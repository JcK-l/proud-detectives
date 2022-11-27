package de.uhh.detectives.frontend.ui.comms;

import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.uhh.detectives.frontend.R;
import de.uhh.detectives.frontend.database.AppDatabase;
import de.uhh.detectives.frontend.databinding.FragmentCommsBinding;
import de.uhh.detectives.frontend.model.ChatMessage;
import de.uhh.detectives.frontend.model.UserData;

public class CommsFragment extends Fragment {

    private AppDatabase db;
    private UserData user;
    private FragmentCommsBinding binding;

    private CommsAdapter commsAdapter;
    private CommsViewModel viewModel;
    private BottomNavigationView navBar;
    private CommsAnimation animate;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("hh:mm", Locale.ROOT);
    private static final Long DUMMY_USER_ID = 123456789L;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(CommsViewModel.class);
        navBar = getActivity().findViewById(R.id.nav_view);

        setUpDefaults();

        binding = FragmentCommsBinding.inflate(getLayoutInflater());

        View root = binding.getRoot();

        db = AppDatabase.getDatabase(getContext());
        user = db.getUserDataRepository().findFirst();
        initChat(root);

        return root;
    }

    public void setUpDefaults() {
       if (viewModel.chatMessages == null) {
           viewModel.chatMessages = new ArrayList<>();
       }
    }

    private void initChat(View root){
        commsAdapter = new CommsAdapter(viewModel.chatMessages, user.getUserId());
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
            final ChatMessage chatMessage = createMessage(binding.inputMessage.getText().toString(), user.getUserId());
            final Thread thread = new Thread(sendMessageToServer(chatMessage));
            thread.start();
            // for now we'll put it here, because the thread uses the same method
            binding.inputMessage.setText(null);
        });

        final Handler handler = new Handler();
        // handler.post(listenForMessage(commsAdapter, handler));
    }

    private ChatMessage createMessage(final String message, final Long userId) {
        if (message.isEmpty()) {
            return null;
        }
        final long currentTime = System.currentTimeMillis();
        final ChatMessage chatMessage = new ChatMessage(userId, currentTime);
//        chatMessage.receiverId = receiverId.getUserId();
        chatMessage.setDateTime(SDF.format(new Date(currentTime)));
        chatMessage.setMessage(message);
        return chatMessage;
    }

    private void sendMessageToUi(final ChatMessage chatMessage) {
        viewModel.chatMessages.add(chatMessage);
        commsAdapter.notifyItemInserted(viewModel.chatMessages.size());
        binding.chatRecyclerView.smoothScrollToPosition(viewModel.chatMessages.size());
    }

    private void listenForMessage(final CommsAdapter adapter, final Handler handler) {
        // TODO: subscribe to Backend for messages
    }

    private Runnable sendMessageToServer(final ChatMessage message) {
        return new Runnable() {
            private Socket socket = null;
            private String host = "dos-wins-04.informatik.uni-hamburg.de";
            private int port = 22527;

            @Override
            public void run() {
                System.out.println("starting thread");
                ObjectOutputStream out = null;
                BufferedReader in = null;
                try {
                    socket = new Socket(host, port);
                    out = new ObjectOutputStream(socket.getOutputStream());
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                } catch (UnknownHostException e) {
                    System.err.println("Don't know about host: " + host);
                    System.exit(1);
                } catch (IOException e) {
                    System.err.println("Couldn't get I/O for host: " + host);
                    System.exit(1);
                }
                try {
                    String serverInput;
                    out.writeObject(message.toString());
                    serverInput = in.readLine();
                    getActivity().runOnUiThread(receiveMessage(serverInput));
                    out.close();
                    in.close();
                    socket.close();
                } catch (IOException ignored) {
                    // handle connection failure
                }
            }
        };
    }

    private Runnable receiveMessage(final String message) {
        return () -> {
            // TODO: create message from server String instead of just sending string to chat
            ChatMessage chatMessage;
            if (message.contains(user.getUserId() + "")) {
                chatMessage = createMessage(message, user.getUserId());
            } else {
                chatMessage = createMessage(message, DUMMY_USER_ID);
            }
            sendMessageToUi(chatMessage);
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}