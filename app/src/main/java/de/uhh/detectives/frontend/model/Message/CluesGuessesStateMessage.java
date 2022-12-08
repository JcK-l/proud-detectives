package de.uhh.detectives.frontend.model.Message;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;

import de.uhh.detectives.frontend.model.CluesGuessesState;
import de.uhh.detectives.frontend.model.Message.api.Message;
import de.uhh.detectives.frontend.ui.clues_and_guesses.Cell;

public class CluesGuessesStateMessage implements Message {
    private CluesGuessesState cluesGuessesState;
    private String cellString;

    // Create ChatMessage
    public CluesGuessesStateMessage(final CluesGuessesState cluesGuessesState) {
        this.cluesGuessesState = cluesGuessesState;
        Gson gson = new Gson();
        this.cellString = gson.toJson(cluesGuessesState.getCells());
    }

    // Parse ChatMessage
    public CluesGuessesStateMessage(String messageToParse) {
        String[] tokens = messageToParse.split(";");
        for (int i = 1; i < tokens.length; i++) {
            tokens[i] = tokens[i].substring(tokens[i].indexOf("=") + 1);
        }
        cluesGuessesState = new CluesGuessesState();
        cluesGuessesState.setPlayerId(Long.parseLong(tokens[1]));
        cluesGuessesState.setCardColor(Integer.parseInt(tokens[2]));
        cluesGuessesState.setNumberOfTries(Integer.parseInt(tokens[3]));
        cluesGuessesState.setSuspicionLeft(Integer.parseInt(tokens[4]));
        cluesGuessesState.setSuspicionMiddle(Integer.parseInt(tokens[5]));
        cluesGuessesState.setSuspicionRight(Integer.parseInt(tokens[6]));

        Type listType = new TypeToken<ArrayList<Cell>>() {}.getType();
        cluesGuessesState.setCells(new Gson().fromJson(tokens[7], listType));
    }

    public CluesGuessesState getCluesGuessesState() {
        return cluesGuessesState;
    }

    @Override
    public MessageType getType() {
        return MessageType.CLUES_GUESSES_STATE_MESSAGE;
    }

    @Override
    public String toString() {
        return "TYPE:CLUES_GUESSES_STATE_MESSAGE" +
                ";playerId=" + cluesGuessesState.getPlayerId() +
                ";cardColor=" + cluesGuessesState.getCardColor() +
                ";numberOfTries=" + cluesGuessesState.getNumberOfTries() +
                ";suspicionLeft=" + cluesGuessesState.getSuspicionLeft() +
                ";suspicionMiddle=" + cluesGuessesState.getSuspicionMiddle() +
                ";suspicionRight=" + cluesGuessesState.getSuspicionRight() +
                ";cellJson=" + cellString;
    }
}
