package de.uhh.detectives.frontend.ui.start_game;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Objects;

import de.uhh.detectives.frontend.WaitingRoomActivity;
import de.uhh.detectives.frontend.database.AppDatabase;
import de.uhh.detectives.frontend.databinding.FragmentStartGameBinding;
import de.uhh.detectives.frontend.location.api.LocationHandler;
import de.uhh.detectives.frontend.model.Message.ChatMessage;
import de.uhh.detectives.frontend.model.Message.JoinGameMessage;
import de.uhh.detectives.frontend.model.Message.StartGameMessage;
import de.uhh.detectives.frontend.model.Player;
import de.uhh.detectives.frontend.model.UserData;
import de.uhh.detectives.frontend.model.event.ChatMessageEvent;
import de.uhh.detectives.frontend.model.event.JoinGameMessageEvent;
import de.uhh.detectives.frontend.pushmessages.services.PushMessageService;
import de.uhh.detectives.frontend.service.TcpMessageService;

public class StartGameFragment extends Fragment {

    private FragmentStartGameBinding binding;

    private UserData user;

    private LocationHandler locationHandler;
    private List<Player> players;

    private StartGameAdapter startGameAdapter;

    private TcpMessageService tcpMessageService;
    private Location location;

    private final ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            tcpMessageService = ((TcpMessageService.LocalBinder)service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            tcpMessageService = null;
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Intent intent = new Intent(getActivity(), TcpMessageService.class);
        requireActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);

        EventBus.getDefault().register(this);


        binding = FragmentStartGameBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        AppDatabase db = AppDatabase.getDatabase(getContext());
        user = db.getUserDataRepository().findFirst();
        players = db.getPlayerRepository().getAll();


        RecyclerView recyclerView = binding.recyclerViewPlayers;

        startGameAdapter = new StartGameAdapter(this.getContext(), players);
        recyclerView.setAdapter(startGameAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        final Context context = this.getContext();
        if (context == null) {
            return root;
        }

        final WaitingRoomActivity activity = (WaitingRoomActivity) getActivity();
        location = Objects.requireNonNull(activity).getLocation();

        binding.buttonStartGame.setOnClickListener(
                view -> tcpMessageService.sendMessageToServer(new StartGameMessage(user,
                        location.getLongitude(), location.getLatitude(), 1000))
        );

        return root;
    }


    // TODO: NICHT DIE BESTE SOLUTION, ABER FÜR DIE PRÄSENTATION IST ES GUT GENUG
    @Subscribe
    public void listenForChatPushMessage(ChatMessageEvent chatMessageEvent) {
        ChatMessage chatMessage = chatMessageEvent.getMessage();
        PushMessageService pushMessageService = new PushMessageService(requireContext());
        if (Objects.requireNonNull(chatMessage.getReceiverId()).equals(user.getUserId())) {
            pushMessageService.pushChatNotification(chatMessage.getPseudonym(), chatMessage.getMessage());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessageJoinGame(final JoinGameMessageEvent joinGameMessageEvent) {
        JoinGameMessage joinGameMessage = joinGameMessageEvent.getMessage();

        if (joinGameMessage.getPlayerNames() == null) return;

        players.clear();
        for (final String name : joinGameMessage.getPlayerNames()){
            players.add(new Player(System.currentTimeMillis(), name));
        }

        startGameAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        requireActivity().unbindService(connection);
        EventBus.getDefault().unregister(this);
        binding = null;
    }
}
