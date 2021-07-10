package my.project.nostalgia.supplementary;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

public class transformationViewPager implements ViewPager.PageTransformer {
    @Override
    public void transformPage(@NonNull View page, float position) {
        final float normalizedposition = Math.abs(Math.abs(position) - 1);
        page.setScaleX(normalizedposition / 2 + 0.5f);
        page.setScaleY(normalizedposition / 2 + 0.5f);
    }
}
