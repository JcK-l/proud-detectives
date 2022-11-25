package de.uhh.detectives.frontend.ui.comms;


import android.view.View;

import androidx.lifecycle.ViewModel;

import java.util.List;

import de.uhh.detectives.frontend.model.ChatMessage;


public class CommsViewModel extends ViewModel {
    public List<ChatMessage> chatMessages;
}
