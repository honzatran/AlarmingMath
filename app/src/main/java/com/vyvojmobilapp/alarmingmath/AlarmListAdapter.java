package com.vyvojmobilapp.alarmingmath;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vyvojmobilapp.alarmingmath.alarm.Alarm;

/**
 * Vypis seznamu budiku v main activity
 */
public class AlarmListAdapter extends ArrayAdapter<Alarm> {

    Context context;
    int layoutResourceId;
    AlarmContainer data = null;

     public AlarmListAdapter(Context context, int layoutResourceId, AlarmContainer data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        AlarmHolder holder;

        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new AlarmHolder();
            holder.listItem_time = (TextView)row.findViewById(R.id.listItem_time);
            holder.listItem_alarmName = (TextView)row.findViewById(R.id.listItem_alarmName);
            holder.listItem_repeat = (TextView)row.findViewById(R.id.listItem_repeat);
            holder.listItem_method = (TextView)row.findViewById(R.id.listItem_method);
            holder.listItem_imgActive = (ImageView)row.findViewById(R.id.listItem_imgActive);

            row.setTag(holder);
        }
        else {
            holder = (AlarmHolder)row.getTag();
        }

        Alarm alarm = data.get(position);
        holder.listItem_time.setText(alarm.toString());
        holder.listItem_alarmName.setText(alarm.getName());
        // repeat
        StringBuilder repeat = new StringBuilder();
        String[] d = {context.getString(R.string.sunday),
                context.getString(R.string.monday),
                context.getString(R.string.tuesday),
                context.getString(R.string.wednesday),
                context.getString(R.string.thursday),
                context.getString(R.string.friday),
                context.getString(R.string.saturday)};
        if (alarm.getDays() != null) {
            for (int i = 0; i < 7; i++) {        // zjistim kazdy den (od nedele do soboty)
                if (alarm.getDays().isDaySet(i)) {
                    repeat.append(d[i] + " ");
                }
            }
        }
        if (repeat == null)
            repeat.append(context.getString(R.string.listItem_norepeat));
        holder.listItem_repeat.setText(repeat);
        // method (qr/math/simple)
        String method = null;
        if (alarm.getMethodId() == 1)
            method = context.getString(R.string.listItem_method_math);
        else if (alarm.getMethodId() == 2)
            method = context.getString(R.string.listItem_method_qr);
        holder.listItem_method.setText(method);

        if (alarm.isActive())
            holder.listItem_imgActive.setImageResource(R.drawable.ic_alarm_on_black_48dp);
        else
            holder.listItem_imgActive.setImageResource(R.drawable.ic_alarm_off_grey600_48dp);

        return row;
    }

    static class AlarmHolder {
        TextView listItem_time;
        TextView listItem_alarmName;
        TextView listItem_repeat;
        TextView listItem_method;
        ImageView listItem_imgActive;
    }
}
