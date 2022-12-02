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

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import de.uhh.detectives.frontend.R;
import de.uhh.detectives.frontend.database.AppDatabase;
import de.uhh.detectives.frontend.databinding.FragmentCommsBinding;
import de.uhh.detectives.frontend.databinding.FragmentCommsSoftkeyboardBinding;
import de.uhh.detectives.frontend.model.event.ChatMessageEvent;
import de.uhh.detectives.frontend.model.Message.ChatMessage;
import de.uhh.detectives.frontend.model.UserData;
import de.uhh.detectives.frontend.service.TcpMessageService;

public class CommsFragment extends Fragment {
    private TcpMessageService tcpMessageService;

    private final ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            tcpMessageService = ((TcpMessageService.LocalBinder)service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            tcpMessageService = null;
        }
    };

    private AppDatabase db;
    private UserData user;
    private FragmentCommsBinding binding;
    private FragmentCommsSoftkeyboardBinding bindingTransition;

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
        commsAdapter = new CommsAdapter(chatMessages, user.getUserId());
        binding.chatRecyclerView.setAdapter(commsAdapter);

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
                ChatMessage chatMessage = new ChatMessage(user,null,input);
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