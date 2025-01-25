package de.uhh.detectives.frontend.ui.clues_and_guesses;

import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.Locale;

import de.uhh.detectives.frontend.database.AppDatabase;
import de.uhh.detectives.frontend.model.Solution;

public class SolutionVerifier {

    final private List<String> solution;


    public SolutionVerifier(Context context){
        AppDatabase db = AppDatabase.getDatabase(context);
        Solution solutionFromDatabase = db.getSolutionRepository().findFirst();

        solution = solutionFromDatabase.getSolutionList();
        solution.replaceAll(element -> element.toLowerCase(Locale.ROOT));
    }

    public List<String> getSolution() {
        return solution;
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
