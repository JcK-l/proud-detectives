package de.uhh.detectives.frontend.ui.comms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import de.uhh.detectives.frontend.database.AppDatabase;
import de.uhh.detectives.frontend.databinding.FragmentCommsBinding;
import de.uhh.detectives.frontend.model.UserData;

public class CommsFragment extends Fragment {

    private FragmentCommsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CommsViewModel commsViewModel =
                new ViewModelProvider(this).get(CommsViewModel.class);

        binding = FragmentCommsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textComms;
        commsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        final AppDatabase db = AppDatabase.getDatabase(getContext());
        final UserData userData = db.getUserDataRepository().getAll().get(0);
        Toast.makeText(getContext(), "UserId: " + userData.getUserId(), Toast.LENGTH_LONG).show();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}