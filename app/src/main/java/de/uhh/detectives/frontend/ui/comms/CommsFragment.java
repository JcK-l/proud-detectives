package de.uhh.detectives.frontend.ui.comms;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import de.uhh.detectives.frontend.R;
import de.uhh.detectives.frontend.database.AppDatabase;
import de.uhh.detectives.frontend.databinding.FragmentCommsBinding;
import de.uhh.detectives.frontend.databinding.FragmentCommsSoftkeyboardBinding;
import de.uhh.detectives.frontend.model.Message.ChatMessage;
import de.uhh.detectives.frontend.model.Message.DirectMessage;
import de.uhh.detectives.frontend.model.UserData;
import de.uhh.detectives.frontend.model.event.ChatMessageEvent;
import de.uhh.detectives.frontend.model.event.DirectMessageEvent;
import de.uhh.detectives.frontend.service.TcpMessageService;

public class CommsFragment extends Fragment {

    private AppDatabase db;
    private  UserData user;

    private FragmentCommsBinding binding;

    private List<ChatMessage> chatMessages;

    private List<Long> playerIds;
    private List<String> playerNames;

    private BottomNavigationView navBar;
    private CommsAnimation animate;
    private ViewPager2 viewPager2;
    private CommsViewPagerAdapter commsViewPagerAdapter;

    private TcpMessageService tcpMessageService;

    private final float MIN_SCALE = 0.75f;

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
        navBar = requireActivity().findViewById(R.id.nav_view);

        Intent intent = new Intent(getActivity(), TcpMessageService.class);
        requireActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);

        EventBus.getDefault().register(this);

        binding = FragmentCommsBinding.inflate(getLayoutInflater());
        FragmentCommsSoftkeyboardBinding bindingTransition = FragmentCommsSoftkeyboardBinding.inflate(getLayoutInflater());

        View root = binding.getRoot();
        View rootTransition = bindingTransition.getRoot();

        animate = new CommsAnimation(root, rootTransition);

        db = AppDatabase.getDatabase(getContext());
        user = db.getUserDataRepository().findFirst();
        chatMessages = db.getChatMessageRepository().getAll();
        List<DirectMessage> directMessages = db.getDirectMessageRepository().getAll();

        directMessages.sort(Comparator.comparing(DirectMessage::getPosition));

        playerIds = new ArrayList<>();
        playerNames = new ArrayList<>();
        for (final DirectMessage directMessage : directMessages) {
            playerIds.add(directMessage.getId());
            playerNames.add(directMessage.getPseudonym());
        }


        initChat(root);

        return root;
    }

    private void initChat(View root){

        viewPager2 = root.findViewById(R.id.viewPager);

        commsViewPagerAdapter = new CommsViewPagerAdapter(getContext(), playerIds, chatMessages);

        viewPager2.setAdapter(commsViewPagerAdapter);

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

        TabLayout tabLayout = binding.viewTabs.findViewById(R.id.tabLayout);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText("All Chat");
                    } else {
                        tab.setText(playerNames.get(position - 1));
                    }
                }
        );
        tabLayoutMediator.attach();

        binding.layoutSend.setOnClickListener(
            view -> {
                final String input = binding.inputMessage.getText().toString();
                if (!input.isEmpty()){
                    binding.inputMessage.setText(null);
                    int currentPosition = viewPager2.getCurrentItem();
                    Long receiverId = (currentPosition == 0) ? null : playerIds.get(currentPosition - 1);
                    ChatMessage chatMessage = new ChatMessage(user, receiverId, input);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessage(final ChatMessageEvent chatMessageEvent) {
        ChatMessage chatMessage = chatMessageEvent.getMessage();
        chatMessages.add(chatMessage);
        commsViewPagerAdapter.notifyDataSetChanged();

        if (chatMessage.getReceiverId() != null && chatMessage.getReceiverId().equals(user.getUserId())
        && !playerIds.contains(chatMessage.getSenderId())) {
            playerIds.add(0, chatMessage.getSenderId());
            playerNames.add(0, chatMessage.getPseudonym());
            commsViewPagerAdapter.notifyItemInserted(1);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveDirectMessage(final DirectMessageEvent directMessageEvent) {
        DirectMessage directMessage = directMessageEvent.getMessage();

        if (!playerIds.contains(directMessage.getId())) {
            playerIds.add(0, directMessage.getId());
            playerNames.add(0, directMessage.getPseudonym());

            directMessage.setPosition(0);

            db.getDirectMessageRepository().prepareForInsertion();
            db.getDirectMessageRepository().insert(directMessage);

            commsViewPagerAdapter.notifyItemInserted(1);
            viewPager2.setCurrentItem(1);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        requireActivity().unbindService(connection);
    }
}