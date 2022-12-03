package de.uhh.detectives.frontend.ui.comms;

import android.transition.TransitionManager;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import de.uhh.detectives.frontend.R;

public class CommsAnimation {
    private boolean isOn;
    private boolean isOff;

    private ConstraintLayout constraintLayout;
    private ConstraintLayout constraintLayoutTransition;
    private ConstraintSet constraintSet1;
    private ConstraintSet constraintSet2;

    private View divider;

    CommsAnimation(View root, View rootTransitition) {
        this.constraintLayout = root.findViewById(R.id.constraint_comms);
        this.constraintLayoutTransition = rootTransitition.findViewById(R.id.constraint_comms_transition);
        this.constraintSet1 = new ConstraintSet();
        this.constraintSet2 = new ConstraintSet();
        this.isOff = true;
        this.isOn = false;

        constraintSet1.clone(constraintLayout);
        constraintSet2.clone(constraintLayoutTransition);

        divider = constraintLayout.findViewById(R.id.divider);
    }

    public void setKeyboardOn() {
        this.isOn = true;
        if (isOff) {
            isOff = false;
            TransitionManager.beginDelayedTransition(constraintLayout);
            constraintSet2.applyTo(constraintLayout);
            TransitionManager.endTransitions(constraintLayout);
            divider.setVisibility(View.INVISIBLE);
        }
    }

    public void setKeyboardOff() {
        isOff = true;
        if (isOn) {
            isOn = false;
            TransitionManager.beginDelayedTransition(constraintLayout);
            constraintSet1.applyTo(constraintLayout);
            TransitionManager.endTransitions(constraintLayout);
            divider.setVisibility(View.VISIBLE);
        }
    }
}
