package de.uhh.detectives.frontend.ui.comms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.uhh.detectives.frontend.database.AppDatabase;
import de.uhh.detectives.frontend.databinding.FragmentCommsBinding;
import de.uhh.detectives.frontend.model.ChatMessage;
import de.uhh.detectives.frontend.model.UserData;

public class CommsFragment extends Fragment {

    private AppDatabase db;
    private UserData user;
    private FragmentCommsBinding binding;

    private List<ChatMessage> chatMessages;
    private CommsAdapter commsAdapter;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("hh:mm", Locale.ROOT);

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCommsBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        db = AppDatabase.getDatabase(getContext());
        user = db.getUserDataRepository().findFirst();
        initChat();

        return root;
    }

    private void initChat(){
        chatMessages = new ArrayList<>();
        commsAdapter = new CommsAdapter(chatMessages, user.getUserId());
        binding.chatRecyclerView.setAdapter(commsAdapter);
        binding.layoutSend.setOnClickListener(v -> sendMessage(binding.inputMessage.getText().toString()));
        //listenForMessage();
    }

    private void sendMessage(final String message){
        final long currentTime = System.currentTimeMillis();
        final ChatMessage chatMessage = new ChatMessage(user.getUserId(), currentTime);
//        chatMessage.receiverId = receiverId.getUserId();
        chatMessage.setDateTime(SDF.format(new Date(currentTime)));
        chatMessage.setMessage(message);
        chatMessages.add(chatMessage);
        //commsAdapter.notifyDataSetChanged();
        commsAdapter.notifyItemRangeInserted(chatMessage.messageSize(), chatMessage.messageSize());
        binding.chatRecyclerView.smoothScrollToPosition(chatMessage.messageSize() - 1);
        binding.chatRecyclerView.setVisibility(View.VISIBLE);
        binding.inputMessage.setText(null);
    }

    private void listenForMessage(){
        // TODO: subscribe to Backend for messages
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}