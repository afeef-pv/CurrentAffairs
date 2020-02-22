package com.direction.currentaffairs.Widgets;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.direction.currentaffairs.R;

import java.util.ArrayList;

public class CustomGrid extends BaseAdapter{
    private static Context mContext;
    private ArrayList<QuestionHolder> questionHolders;

    public CustomGrid(Context c,ArrayList<QuestionHolder> holders) {
        mContext = c;
        this.questionHolders = holders;
    }

    @Override
    public int getCount() {

        return questionHolders.size();
    }

    @Override
    public Object getItem(int position) {

        return null;
    }

    @Override
    public long getItemId(int position) {

        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.qstn_grid_itm, parent,false);
            holder = new ViewHolder();
            holder.questionNoView = convertView.findViewById(R.id.grid_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.questionNoView = convertView.findViewById(R.id.grid_tv);
        }
        QuestionHolder h = questionHolders.get(position);
        holder.questionNoView.setText(h.getTitle());
        setColorTV(holder.questionNoView,h.getStatus());
        return convertView;
    }

    static void setColorTV(TextView tv,int status){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            switch (status) {
                case QuestionHolder.UNSEEN:
                    tv.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_qs_no_unseen));
                    break;
                case QuestionHolder.SKIPPED:
                    tv.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_qs_no_skipped));
                    break;
                case QuestionHolder.CORRECT:
                   tv.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_qs_no_correct));
                    break;
                case QuestionHolder.INCORRECT:
                    tv.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_qs_no_incorrect));
                    break;
            }
        }

    }
    static class ViewHolder
    {
        public static TextView questionNoView;
    }
}