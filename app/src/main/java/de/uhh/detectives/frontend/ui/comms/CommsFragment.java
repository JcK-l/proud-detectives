package de.uhh.detectives.frontend.ui.comms;

import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.uhh.detectives.frontend.R;
import de.uhh.detectives.frontend.database.AppDatabase;
import de.uhh.detectives.frontend.databinding.FragmentCommsBinding;
import de.uhh.detectives.frontend.event.ChatMessageEvent;
import de.uhh.detectives.frontend.model.ChatMessage;
import de.uhh.detectives.frontend.model.UserData;
import de.uhh.detectives.frontend.service.TcpMessageService;

public class CommsFragment extends Fragment {
    private TcpMessageService tcpMessageService;

    private final ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            tcpMessageService = ((TcpMessageService.LocalBinder)service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            tcpMessageService = null;
        }
    };

    private AppDatabase db;
    private UserData user;
    private FragmentCommsBinding binding;

    private List<ChatMessage> chatMessages;

    private CommsAdapter commsAdapter;
    private BottomNavigationView navBar;
    private CommsAnimation animate;

    private static final Long DUMMY_USER_ID = 123456789L;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        navBar = getActivity().findViewById(R.id.nav_view);

        Intent intent = new Intent(getActivity(), TcpMessageService.class);
        getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
        EventBus.getDefault().register(this);

        binding = FragmentCommsBinding.inflate(getLayoutInflater());

        View root = binding.getRoot();

        db = AppDatabase.getDatabase(getContext());
        user = db.getUserDataRepository().findFirst();
        chatMessages = db.getChatMessageRepository().getAll();
        initChat(root);

        return root;
    }

    private void initChat(View root){
        commsAdapter = new CommsAdapter(chatMessages, user.getUserId());
        binding.chatRecyclerView.setAdapter(commsAdapter);

        ObjectAnimator animationInput = ObjectAnimator.ofFloat(root.findViewById(R.id.inputMessage), "translationY", 130f);
        ObjectAnimator animationSend = ObjectAnimator.ofFloat(root.findViewById(R.id.layoutSend), "translationY", 130f);
        ObjectAnimator animationBackground = ObjectAnimator.ofFloat(root.findViewById(R.id.viewBackground), "translationY", 130f);
        ObjectAnimator animationRecycler = ObjectAnimator.ofFloat(root.findViewById(R.id.chatRecyclerView), "translationY", 130f);

        List<ObjectAnimator> animations = new ArrayList<>(Arrays.asList(animationInput, animationSend, animationBackground, animationRecycler));
        Drawable background_flat = ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.background_content_chat, null);
        Drawable background_bubble = ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.background_content_chat_bubble, null);

        animate = new CommsAnimation(animations, root.findViewById(R.id.viewBackground), background_flat, background_bubble);

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
        binding.layoutSend.setOnClickListener(v -> {
            final String input = binding.inputMessage.getText().toString();
            if (!input.isEmpty()){
                ChatMessage chatMessage = new ChatMessage(user.getUserId(), input);
                tcpMessageService.sendMessageToServer(chatMessage);
                binding.inputMessage.setText(null);
            }
        });
    }

    private void sendMessageToUi(final ChatMessage chatMessage) {
        chatMessages.add(chatMessage);
        commsAdapter.notifyItemInserted(chatMessages.size());
        binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size());
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessage(final ChatMessageEvent chatMessageEvent) {
        ChatMessage chatMessage = chatMessageEvent.getMessage();
        sendMessageToUi(chatMessage);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unbindService(connection);
        EventBus.getDefault().unregister(this);
    }
}