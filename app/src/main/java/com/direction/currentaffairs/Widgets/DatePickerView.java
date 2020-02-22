package com.direction.currentaffairs.Widgets;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.direction.currentaffairs.R;

import java.util.Calendar;
import java.util.Date;

public class DatePickerView extends LinearLayout implements View.OnTouchListener, DatePickerDialog.OnDateSetListener {
    private static final int LEFT = 0;
    private static final int RIGHT = 2;
    private static String[] MONTHS = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private TextView textView;
    private Calendar calendar;
    private Date today;
    private DatePickerDialog datePickerDialog;

    private OnDateChangedListener dateChangedListener;

    public DatePickerView(Context context) {
        super(context);
        init(context);
    }

    public DatePickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DatePickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context){
        calendar = Calendar.getInstance();
        today = calendar.getTime();
        datePickerDialog = new DatePickerDialog(context,R.style.DialogTheme,this,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        View view = LayoutInflater.from(context).inflate(R.layout.date_picker_view,this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.textView = this.findViewById(R.id.date_picker_view_textView);
        textView.setOnTouchListener(this);
        setDate();

    }

    private void setDate() {
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        textView.setText(new StringBuilder()
                .append(dd).append(" ").append("-").append(MONTHS[mm]).append(", ")
                .append(yy));
    }

    public void setDate(Calendar calendar){
        this.calendar = calendar;
        setDate();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Drawable[] drawables = textView.getCompoundDrawables();
        if(hasDrawableLeft(drawables) && hasDrawableRight(drawables)){
            Rect leftBounds = drawables[LEFT].getBounds();
            Rect rightBounds = drawables[RIGHT].getBounds();
            float x = event.getX();
            float y = event.getY();
            if(decrementTouched(textView,leftBounds.width(),x,y)){
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    calendar.add(Calendar.DATE,-1);
                    setDate();
                    if(dateChangedListener != null) dateChangedListener.onDateChanged(calendar);
                }
            }
            else if(incrementTouched(textView,rightBounds.width(),x,y)){
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if(today.compareTo(calendar.getTime()) > 0){
                        calendar.add(Calendar.DATE,1);
                        setDate();
                        if(dateChangedListener != null) dateChangedListener.onDateChanged(calendar);

                    }
                }
            }else {
                datePickerDialog.show();
            }

        }
        return true;
    }

    private boolean hasDrawableLeft(Drawable[] drawables){
        return drawables[LEFT] != null;
    }

    private boolean hasDrawableRight(Drawable[] drawables){
        return drawables[RIGHT] != null;
    }

    private boolean decrementTouched(TextView textView, int width, float x, float y){
        int xmin = textView.getPaddingLeft();
        int xmax = textView.getPaddingLeft() + width;
        int ymin = textView.getPaddingTop();
        int ymax = textView.getHeight() - textView.getPaddingBottom();

        return x > xmin && x < xmax && y > ymin && y < ymax;
    }

    private boolean incrementTouched(TextView textView, int width, float x, float y){
        int xmin = textView.getWidth() - textView.getPaddingRight() - width;
        int xmax = textView.getWidth() - textView.getPaddingRight();
        int ymin = textView.getPaddingTop();
        int ymax = textView.getHeight() - textView.getPaddingBottom();

        return x > xmin && x < xmax && y > ymin && y < ymax;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar.set(year,month,dayOfMonth);
        setDate();
        if(dateChangedListener != null) dateChangedListener.onDateChanged(calendar);
    }

    public interface OnDateChangedListener{
        void onDateChanged(Calendar date);
    }

    public void setDateChangedListener(OnDateChangedListener dateChangedListener) {
        this.dateChangedListener = dateChangedListener;
    }
}
