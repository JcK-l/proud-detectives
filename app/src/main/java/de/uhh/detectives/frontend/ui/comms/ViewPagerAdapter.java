package de.uhh.detectives.frontend.ui.comms;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.uhh.detectives.frontend.R;
import de.uhh.detectives.frontend.database.AppDatabase;
import de.uhh.detectives.frontend.model.Message.ChatMessage;
import de.uhh.detectives.frontend.model.UserData;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {

    final Context context;
    final List<ChatMessage> messages;
    final List<Long> receiverIds;

    public ViewPagerAdapter(final Context context, final List<Long> receiverIds ,
                            final List<ChatMessage> messages, Activity activity) {
        this.context = context;
        this.messages = messages;
        this.receiverIds = receiverIds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.item_directmessage, parent, false);
        return new ViewPagerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(messages);
    }

    @Override
    public int getItemCount() {
        return receiverIds.size() + 1;
    }

    public void newMessage() {

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final AppDatabase db;
        private final UserData user;

        // Ui components
        private final RecyclerView recyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.db = AppDatabase.getDatabase(itemView.getContext());
            this.user = this.db.getUserDataRepository().findFirst();

            this.recyclerView = itemView.findViewById(R.id.chatRecyclerView);
        }

        public void bind(final List<ChatMessage> chatMessages) {

            CommsAdapter commsAdapter = new CommsAdapter(chatMessages, user.getUserId());

            recyclerView.setAdapter(commsAdapter);
        }
    }
}
