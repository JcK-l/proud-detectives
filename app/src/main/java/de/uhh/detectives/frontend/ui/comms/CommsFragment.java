package de.uhh.detectives.frontend.ui.comms;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.uhh.detectives.frontend.database.AppDatabase;
import de.uhh.detectives.frontend.databinding.FragmentCommsBinding;
import de.uhh.detectives.frontend.model.ChatMessage;
import de.uhh.detectives.frontend.model.UserData;
import de.uhh.detectives.frontend.ui.clues_and_guesses.CluesGuessesViewModel;

public class CommsFragment extends Fragment {

    private AppDatabase db;
    private UserData user;
    private FragmentCommsBinding binding;

    private CommsAdapter commsAdapter;
    private CommsViewModel viewModel;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("hh:mm", Locale.ROOT);

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(CommsViewModel.class);

        setUpDefaults();

        binding = FragmentCommsBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        db = AppDatabase.getDatabase(getContext());
        user = db.getUserDataRepository().findFirst();
        initChat();

        return root;
    }

    public void setUpDefaults() {
       if (viewModel.chatMessages == null) {
           viewModel.chatMessages = new ArrayList<>();
       }
    }

    private void initChat(){
        commsAdapter = new CommsAdapter(viewModel.chatMessages, user.getUserId());
        binding.chatRecyclerView.setAdapter(commsAdapter);
        binding.chatRecyclerView.setVisibility(View.VISIBLE);
        binding.layoutSend.setOnClickListener(v -> sendMessage(binding.inputMessage.getText().toString()));
        //listenForMessage();
    }

    private void sendMessage(final String message){
        if (message.isEmpty()) return;

        final long currentTime = System.currentTimeMillis();
        final ChatMessage chatMessage = new ChatMessage(user.getUserId(), currentTime);
//        chatMessage.receiverId = receiverId.getUserId();
        chatMessage.setDateTime(SDF.format(new Date(currentTime)));
        chatMessage.setMessage(message);
        viewModel.chatMessages.add(chatMessage);
        Log.d("tag", viewModel.chatMessages.toString());
        commsAdapter.notifyItemInserted(viewModel.chatMessages.size());
        binding.chatRecyclerView.smoothScrollToPosition(viewModel.chatMessages.size());
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