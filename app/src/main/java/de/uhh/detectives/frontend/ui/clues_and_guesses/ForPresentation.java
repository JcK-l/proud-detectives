package de.uhh.detectives.frontend.ui.clues_and_guesses;

public class ForPresentation {

    final private String[] solution;

    public ForPresentation(){
        solution = new String[3];
        solution[0] = "pistole";
        solution[1] = "tom gruen";
        solution[2] = "arbeitszimmer";
    }

    public SuspicionResult compareToSolution(String[] suspicion) {
        if (suspicion[0].equals("default") || suspicion[1].equals("default") || suspicion[2].equals("default"))
            return SuspicionResult.INVALID;

        if (solution[0].equals(suspicion[0]) && solution[1].equals(suspicion[1]) && solution[2].equals(suspicion[2])) {
            return SuspicionResult.SUCCESS;
        } else if (!solution[0].equals(suspicion[0]) && !solution[1].equals(suspicion[1]) && !solution[2].equals(suspicion[2])) {
            return SuspicionResult.FAILED;
        } else {
            return SuspicionResult.SEMIFAILED;
        }
    }
}
