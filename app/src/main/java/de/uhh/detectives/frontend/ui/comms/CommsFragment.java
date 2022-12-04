package de.uhh.detectives.frontend.ui.comms;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;

import de.uhh.detectives.frontend.R;
import de.uhh.detectives.frontend.database.AppDatabase;
import de.uhh.detectives.frontend.databinding.FragmentCommsBinding;
import de.uhh.detectives.frontend.databinding.FragmentCommsSoftkeyboardBinding;
import de.uhh.detectives.frontend.model.Message.ChatMessage;
import de.uhh.detectives.frontend.model.UserData;
import de.uhh.detectives.frontend.model.event.ChatMessageEvent;
import de.uhh.detectives.frontend.service.TcpMessageService;

public class CommsFragment extends Fragment {

    private AppDatabase db;
    private  UserData user;

    private FragmentCommsBinding binding;
    private FragmentCommsSoftkeyboardBinding bindingTransition;

    private List<ChatMessage> chatMessages;

    private BottomNavigationView navBar;
    private CommsAnimation animate;
    private ViewPager2 viewPager2;
    private ViewPagerAdapter viewPagerAdapter;

    private TcpMessageService tcpMessageService;

    private final ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            tcpMessageService = ((TcpMessageService.LocalBinder)service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            tcpMessageService = null;
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        navBar = getActivity().findViewById(R.id.nav_view);

        Intent intent = new Intent(getActivity(), TcpMessageService.class);
        getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);

        EventBus.getDefault().register(this);

        binding = FragmentCommsBinding.inflate(getLayoutInflater());
        bindingTransition = FragmentCommsSoftkeyboardBinding.inflate(getLayoutInflater());

        View root = binding.getRoot();
        View rootTransition = bindingTransition.getRoot();

        animate = new CommsAnimation(root, rootTransition);

        db = AppDatabase.getDatabase(getContext());
        user = db.getUserDataRepository().findFirst();
        chatMessages = db.getChatMessageRepository().getAll();
        initChat(root);

        return root;
    }

    private void initChat(View root){

        viewPager2 = root.findViewById(R.id.viewPager);

        viewPagerAdapter = new ViewPagerAdapter(getContext(), Arrays.asList(23423L, 324324L), chatMessages, getActivity());

        viewPager2.setAdapter(viewPagerAdapter);
        final float MIN_SCALE = 0.75f;

        viewPager2.setPageTransformer(
                (view, position) -> {
                    int pageWidth = view.getWidth();

                    if (position < -1) { // [-Infinity,-1)
                        // This page is way off-screen to the left.
                        view.setAlpha(0f);

                    } else if (position <= 0) { // [-1,0]
                        // Use the default slide transition when moving to the left page
                        view.setAlpha(1f);
                        view.setTranslationX(0f);
                        view.setTranslationZ(0f);
                        view.setScaleX(1f);
                        view.setScaleY(1f);

                    } else if (position <= 1) { // (0,1]
                        // Fade the page out.
                        view.setAlpha(1 - position);

                        // Counteract the default slide transition
                        view.setTranslationX(pageWidth * -position);
                        // Move it behind the left page
                        view.setTranslationZ(-1f);

                        // Scale the page down (between MIN_SCALE and 1)
                        float scaleFactor = MIN_SCALE
                                + (1 - MIN_SCALE) * (1 - Math.abs(position));
                        view.setScaleX(scaleFactor);
                        view.setScaleY(scaleFactor);

                    } else { // (1,+Infinity]
                        // This page is way off-screen to the right.
                        view.setAlpha(0f);
                    }
                }
        );

        binding.layoutSend.setOnClickListener(
            view -> {
                final String input = binding.inputMessage.getText().toString();
                if (!input.isEmpty()){
                    binding.inputMessage.setText(null);
                    ChatMessage chatMessage = new ChatMessage(user,null,input);
                    tcpMessageService.sendMessageToServer(chatMessage);
                }
            });


        root.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            //r will be populated with the coordinates of your view that area still visible.
            root.getWindowVisibleDisplayFrame(r);

            int heightDiff = root.getRootView().getHeight() - (r.bottom - r.top);
            if (heightDiff > 500) { // if more than 100 pixels, its probably a keyboard...
                navBar.setVisibility(View.INVISIBLE);
                animate.setKeyboardOn();
            } else {
                animate.setKeyboardOff();
                navBar.setVisibility(View.VISIBLE);
            }
        });
    }

    private void sendMessageToUi(final ChatMessage chatMessage) {
        chatMessages.add(chatMessage);
        Log.d("test", String.valueOf(viewPager2.getCurrentItem()));
//        viewPagerAdapter.notifyItemChanged(viewPager2.getCurrentItem());
        viewPagerAdapter.notifyDataSetChanged();
//        binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size());
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessage(final ChatMessageEvent chatMessageEvent) {
        ChatMessage chatMessage = chatMessageEvent.getMessage();
        sendMessageToUi(chatMessage);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        getActivity().unbindService(connection);
    }
}