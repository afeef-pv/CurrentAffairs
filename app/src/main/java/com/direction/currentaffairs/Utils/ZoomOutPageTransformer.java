package com.direction.currentaffairs.Utils;

import android.view.View;

import androidx.viewpager2.widget.ViewPager2;

import static java.lang.Math.min;

public class ZoomOutPageTransformer implements ViewPager2.PageTransformer {

    private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.5f;

    @Override
    public void transformPage( View page, float pos ) {
        final float height = page.getHeight();
        final float width = page.getWidth();
        final float scale = min( pos > 0 ? 1f : Math.abs(1f + pos), 1f );

        page.setScaleX( scale );
        page.setScaleY( scale );
        page.setPivotX( width * 0.5f );
        page.setPivotY( height * 0.5f );
        page.setTranslationY( pos > 0 ? width * pos : -width * pos * 0.25f );
    }
}
