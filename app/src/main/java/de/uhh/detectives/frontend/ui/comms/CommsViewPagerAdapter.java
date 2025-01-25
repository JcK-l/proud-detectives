package de.uhh.detectives.frontend.ui.comms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.stream.Collectors;

import de.uhh.detectives.frontend.R;
import de.uhh.detectives.frontend.database.AppDatabase;
import de.uhh.detectives.frontend.model.Message.ChatMessage;
import de.uhh.detectives.frontend.model.UserData;

public class CommsViewPagerAdapter extends RecyclerView.Adapter<CommsViewPagerAdapter.ViewHolder> {

    final Context context;
    final List<ChatMessage> messages;
    final List<Long> senderIds;


    private final UserData user;


    public CommsViewPagerAdapter(final Context context, final List<Long> senderIds ,
                                 final List<ChatMessage> messages) {
        this.context = context;
        this.messages = messages;
        this.senderIds = senderIds;
        AppDatabase db = AppDatabase.getDatabase(context);
        this.user = db.getUserDataRepository().findFirst();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.item_directmessage, parent, false);
        return new CommsViewPagerAdapter.ViewHolder(view, user.getUserId());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        List<ChatMessage> result = messages.stream()
                .filter( chatMessage -> {
                    // All Chat
                    if (position == 0 && chatMessage.getReceiverId() == null) {
                        return true;
                    } else if (chatMessage.getReceiverId() == null) {
                        return false;
                        // Messages From someone
                    } else if (position > 0 && chatMessage.getSenderId().equals(senderIds.get(position - 1))
                            && chatMessage.getReceiverId().equals(user.getUserId())) {
                       return true;
                        // Messages To someone
                    } else return position > 0 && chatMessage.getSenderId().equals(user.getUserId())
                            && chatMessage.getReceiverId().equals(senderIds.get(position - 1));
                }).collect(Collectors.toList());
        holder.bind(result);
    }

    @Override
    public int getItemCount() {
        return senderIds.size() + 1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Ui components
        private final RecyclerView recyclerView;
        private final Long userId;

        public ViewHolder(@NonNull View itemView, final Long userId) {
            super(itemView);
            this.recyclerView = itemView.findViewById(R.id.chatRecyclerView);
            this.userId = userId;
        }

        public void bind(final List<ChatMessage> chatMessages) {

            CommsAdapter commsAdapter = new CommsAdapter(chatMessages, userId);

            recyclerView.setAdapter(commsAdapter);
        }
    }
}
