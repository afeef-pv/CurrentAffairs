package com.direction.currentaffairs.Adapters;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.direction.currentaffairs.R;

public class Divider extends RecyclerView.ItemDecoration{
    private int orientation;
    private Drawable divider,background;

    public Divider(Context context, int orientation){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && context != null) {
            divider = context.getDrawable(R.drawable.divider);
        }
        if(orientation != LinearLayoutManager.VERTICAL ) throw new IllegalArgumentException("Vertical Layout Required");
        this.orientation = orientation;
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        drawHorizontalDivider(c,parent,state);
    }

    private void drawHorizontalDivider(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left, top, right, bottom;
        left = parent.getPaddingLeft();
        right = parent.getWidth() - parent.getPaddingRight();
        int count = parent.getChildCount();
        for(int i = 0; i < count; ++i){
            View current = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) current.getLayoutParams();
            top = current.getTop() - params.topMargin;
            bottom = top + divider.getIntrinsicHeight();
            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
    }
}
