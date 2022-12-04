package de.uhh.detectives.frontend.ui.clues_and_guesses;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import de.uhh.detectives.frontend.database.AppDatabase;
import de.uhh.detectives.frontend.model.Solution;

public class SolutionVerifier {

    final private List<String> solution;

    private AppDatabase db;
    private Solution solutionFromDatabase;


    public SolutionVerifier(Context context){
        db = AppDatabase.getDatabase(context);
        solutionFromDatabase = db.getSolutionRepository().findFirst();

        solution = solutionFromDatabase.getSolutionList();
        solution.replaceAll(element -> element.toLowerCase(Locale.ROOT));
    }

    public List<String> getSolution() {
        return solution;
    }

    public List<String> getSolutionWithAmongus() {
        List<String> result = new ArrayList<>(solution);
        result.replaceAll(
                element -> {
                    switch (element) {
                        case "dennis gatow":
                            return "cyan";
                        case "felix bloom":
                            return "yellow";
                        case "tom gruen":
                            return "red";
                        case "klara porz":
                            return "orange";
                        case "gloria roth":
                            return "pink";
                        case "diana weiss":
                            return "green";
                        default:
                            return element;
                    }
                }
        );
        return result;
    }

    public SuspicionResult compareToSolution(String[] suspicion) {
        Log.i("Solution", String.valueOf(solution));
        if (suspicion[0].equals("default") || suspicion[1].equals("default") || suspicion[2].equals("default"))
            return SuspicionResult.INVALID;

        if (solution.get(0).equals(suspicion[0]) && solution.get(1).equals(suspicion[1]) && solution.get(2).equals(suspicion[2])) {
            return SuspicionResult.SUCCESS;
        } else if (!solution.get(0).equals(suspicion[0]) && !solution.get(1).equals(suspicion[1]) && !solution.get(2).equals(suspicion[2])) {
            return SuspicionResult.FAILED;
        } else {
            return SuspicionResult.SEMIFAILED;
        }
    }
}
