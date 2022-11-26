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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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
import de.uhh.detectives.frontend.ui.hints.HintAdapter;

public class CommsFragment extends Fragment {

    private AppDatabase db;
    private UserData user;
    private FragmentCommsBinding binding;

    private CommsAdapter commsAdapter;
    private CommsViewModel viewModel;
    private BottomNavigationView navBar;
    private CommsAnimation animate;

    private static final int NOTIFICATION_DELAY = 10000;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("hh:mm", Locale.ROOT);

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
            sendMessage(binding.inputMessage.getText().toString(), user.getUserId());
            // for now we'll put it here, because the thread uses the same method
            binding.inputMessage.setText(null);
        });

        final Handler handler = new Handler();
        handler.post(listenForMessage(commsAdapter, handler));
    }

    private void sendMessage(final String message, final Long userId){
        if (message.isEmpty()) return;

        final long currentTime = System.currentTimeMillis();
        final ChatMessage chatMessage = new ChatMessage(userId, currentTime);
//        chatMessage.receiverId = receiverId.getUserId();

        chatMessage.setDateTime(SDF.format(new Date(currentTime)));
        chatMessage.setMessage(message);

        viewModel.chatMessages.add(chatMessage);
        commsAdapter.notifyItemInserted(viewModel.chatMessages.size());
        binding.chatRecyclerView.smoothScrollToPosition(viewModel.chatMessages.size());
    }

    private Runnable listenForMessage(final CommsAdapter adapter, final Handler handler) {
        // TODO: subscribe to Backend for messages
        // Thread to update messages for presentation purposes
        return new Runnable() {
            @Override
            public void run() {
                sendMessage("this is a test!", user.getUserId() + 1);
                handler.postDelayed(this, NOTIFICATION_DELAY);
            }
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}