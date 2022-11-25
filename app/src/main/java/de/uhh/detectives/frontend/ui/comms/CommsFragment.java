package de.uhh.detectives.frontend.ui.comms;

import android.annotation.SuppressLint;
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

import de.uhh.detectives.frontend.databinding.FragmentCommsBinding;
import de.uhh.detectives.frontend.model.UserData;
import de.uhh.detectives.frontend.ui.comms.values.ChatMessage;

public class CommsFragment extends Fragment {

    private FragmentCommsBinding binding;
    private UserData receiverId;
    private List<ChatMessage> chatMessages;
    private CommsAdapter commsAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCommsBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        //setListeners();
        //init();
        //loadReceiverDetails();
        //sendMessage();



        return root;
    }

    private void init(){
        chatMessages = new ArrayList<>();
        commsAdapter = new CommsAdapter(chatMessages, 12L);
        binding.chatRecyclerView.setAdapter(commsAdapter);
    }

    private void sendMessage(){
        UserData sender = new UserData(); // ToDo: ersetzen mit dem beim Start erstellten Nutzer
//        HashMap<String, Object> message = new HashMap<>();
//        message.put("sender Id",sender.getUserId());
//        message.put("reciever Id",receiverUser.getUserId());
//        message.put("sender Id",sender.getUserId());
        //message.put("message", binding.)
        ChatMessage chatMessage = new ChatMessage();
//        chatMessage.senderId = sender.getUserId();
//        chatMessage.receiverId = receiverId.getUserId();
        chatMessage.dateTime = String.valueOf(new SimpleDateFormat("hh:mm", Locale.GERMAN));
        chatMessage.message = "Ich bin eine Nachricht!";
        chatMessages.add(chatMessage);
        //commsAdapter.notifyDataSetChanged();
        commsAdapter.notifyItemRangeInserted(chatMessage.size(), chatMessage.size());
        binding.chatRecyclerView.smoothScrollToPosition(chatMessage.size() - 1);
        binding.chatRecyclerView.setVisibility(View.VISIBLE);
        binding.inputMessage.setText(null);
    }

    private void listenMessage(){
        // Todo
    }

    private void loadReceiverDetails(){
        //ToDo: Schnitstelle für Daten von Empfängern
        receiverId = new UserData();
        binding.textName.setText(String.valueOf(receiverId.getUserId()));
    }

    private void setListeners(){
        binding.layoutSend.setOnClickListener(v -> sendMessage());
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}