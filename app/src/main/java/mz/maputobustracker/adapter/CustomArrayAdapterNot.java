package mz.maputobustracker.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import mz.maputobustracker.R;
import mz.maputobustracker.domain.Historico;

/**
 * Created by Hawkingg on 22/02/2017.
 */

public class CustomArrayAdapterNot extends ArrayAdapter<String> {

    private static  class ViewHolder
    {
        TextView txtInfo;
    }

    public CustomArrayAdapterNot(Context context, int resource, List<String> objects) {
        super(context, resource, objects);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String info = getItem(position);

        CustomArrayAdapterNot.ViewHolder viewHolder;
        if(convertView == null)
        {
            viewHolder = new CustomArrayAdapterNot.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.custom_arrayadapter,parent,false);
            viewHolder.txtInfo = (TextView) convertView.findViewById(R.id.txtInfo);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (CustomArrayAdapterNot.ViewHolder) convertView.getTag();
        }
        viewHolder.txtInfo.setText(info);
        if (position % 2 == 0)
        {
            viewHolder.txtInfo.setBackgroundColor(Color.parseColor("#52CB9E"));
        }
        else
        {
            viewHolder.txtInfo.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        return convertView;
    }

}
