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
 * Created by Hawkingg on 05/09/2016.
 */
public class CustomArrayAdapterH extends ArrayAdapter<Historico> {

    private static  class ViewHolder
    {
        TextView txtInfo;
    }

    public CustomArrayAdapterH(Context context, int resource, List<Historico> objects) {
        super(context, resource, objects);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Historico info = getItem(position);

        ViewHolder viewHolder;
        if(convertView == null)
        {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.custom_arrayadapter,parent,false);
            viewHolder.txtInfo = (TextView) convertView.findViewById(R.id.txtInfo);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.txtInfo.setText(info.toString());
        if (position % 2 == 0)
        {
            viewHolder.txtInfo.setBackgroundColor(Color.parseColor("#d4d6d4"));
        }
        else
        {
            viewHolder.txtInfo.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        return convertView;
    }
}
