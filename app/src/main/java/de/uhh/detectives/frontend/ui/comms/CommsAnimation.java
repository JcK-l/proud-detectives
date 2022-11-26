package de.uhh.detectives.frontend.ui.comms;

import android.animation.ObjectAnimator;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import java.util.List;

public class CommsAnimation {
    private boolean isOn;
    private boolean isOff;
    private View background;
    private Drawable background_flat;
    private Drawable background_bubble;

    final private List<ObjectAnimator> animations;

    CommsAnimation(List<ObjectAnimator> animations, View background, Drawable background_flat, Drawable background_bubble) {
        this.animations = animations;
        this.background = background;
        this.background_flat = background_flat;
        this.background_bubble = background_bubble;
        this.isOff = true;
        this.isOn = false;

        animations.forEach(animation -> animation.setDuration(500));
    }

    public void setKeyboardOn() {
        this.isOn = true;
        if (isOff) {
            isOff = false;
            animations.forEach(ObjectAnimator::start);
            background.setBackground(background_bubble);
        }
    }

    public void setKeyboardOff() {
        isOff = true;
        if (isOn) {
            isOn = false;
            animations.forEach(ObjectAnimator::reverse);
            background.setBackground(background_flat);
        }
    }
}
