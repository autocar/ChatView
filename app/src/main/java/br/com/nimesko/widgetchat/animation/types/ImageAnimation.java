package br.com.nimesko.widgetchat.animation.types;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.widget.ImageButton;

import br.com.nimesko.widgetchat.R;
import br.com.nimesko.widgetchat.animation.AbstractAnimation;

public class ImageAnimation {

    public static void imageAnimation(final ImageButton view, final int resImgTo) {

        final Context context = view.getContext();

        final Animator startAnimator = AnimatorInflater.loadAnimator(context, R.animator
                .drawable_change_out);
        final Animator endAnimator = AnimatorInflater.loadAnimator(context, R.animator
                .drawable_change_in);

        startAnimator.setTarget(view);
        endAnimator.setTarget(view);

        startAnimator.addListener(new AbstractAnimation() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setImageDrawable(context.getResources().getDrawable(resImgTo));
                endAnimator.start();
            }
        });

        startAnimator.start();

    }

}
