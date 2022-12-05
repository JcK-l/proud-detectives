package de.uhh.detectives.frontend.model.Message;

import java.util.ArrayList;
import java.util.List;

import de.uhh.detectives.frontend.model.Hint;
import de.uhh.detectives.frontend.model.Message.api.Message;
import de.uhh.detectives.frontend.model.Player;
import de.uhh.detectives.frontend.model.Solution;
import de.uhh.detectives.frontend.model.UserData;

public class StartGameMessage implements Message {
    // for sending
    private Long userId;
    private double longitude;
    private double latitude;

    // for receiving
    private int status;
    private Long gameId;
    private String culprit;
    private String location;
    private String weapon;
    private double centerX;
    private double centerY;
    private double radius;

    private List<Player> players;
    private List<Hint> hints;

    public StartGameMessage() {}

    // Create StartGameMessage
    public StartGameMessage(final UserData user, final double longitude, final double latitude) {
        this.userId = user.getUserId();
        this.longitude = longitude;
        this.latitude = latitude;
    }

    // Parse RegisterMessage
    public StartGameMessage(String messageToParse) {
        players = new ArrayList<>();
        hints = new ArrayList<>();

        String arrays = messageToParse.substring(messageToParse.indexOf("["));

        String playerArray = arrays.substring(1, arrays.indexOf("]") - 1);
        String hintArray = arrays.substring(arrays.lastIndexOf("[") + 1, arrays.length() - 2);

        final String[] playerTokens = playerArray.split(";(?=id)");
        for (final String playerString : playerTokens) {
            players.add(new Player(playerString));
        }

        final String[] hintTokens = hintArray.split(";(?=category)");
        for (final String hintString : hintTokens) {
            hints.add(new Hint(hintString));
        }

        String variables = messageToParse.substring(0, messageToParse.indexOf("[") - 8);

        String[] variableTokens = variables.split(";");
        for (int i = 1; i < variableTokens.length; i++) {
            variableTokens[i] = variableTokens[i].substring(variableTokens[i].indexOf("=") + 1);
        }

        this.status = Integer.parseInt(variableTokens[1]);
        this.gameId = (variableTokens[2].equals("null")) ? null : Long.parseLong(variableTokens[2]);
        this.culprit = variableTokens[3];
        this.location = variableTokens[4];
        this.weapon = variableTokens[5];
        this.centerX = Double.parseDouble(variableTokens[6]);
        this.centerY = Double.parseDouble(variableTokens[7]);
        this.radius = Double.parseDouble(variableTokens[8]);
    }

    public Solution getSolution() {
        Solution solution = new Solution();
        solution.setWeapon(weapon);
        solution.setCulprit(culprit);
        solution.setLocation(location);
        return solution;
    }

    public Long getUserId() {
        return userId;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public int getStatus() {
        return status;
    }

    public Long getGameId() {
        return gameId;
    }

    public String getCulprit() {
        return culprit;
    }

    public String getLocation() {
        return location;
    }

    public String getWeapon() {
        return weapon;
    }

    public double getCenterX() {
        return centerX;
    }

    public double getCenterY() {
        return centerY;
    }

    public double getRadius() {
        return radius;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Hint> getHints() {
        return hints;
    }

    @Override
    public String toString() {
        return "TYPE:START_GAME_MESSAGE" +
                ";userId=" + this.userId +
                ";longitude=" + this.longitude +
                ";latitude=" + this.latitude;
    }

    @Override
    public MessageType getType() {
        return MessageType.START_GAME_MESSAGE;
    }

}
