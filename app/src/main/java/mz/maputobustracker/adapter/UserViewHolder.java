package mz.maputobustracker.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class UserViewHolder extends RecyclerView.ViewHolder {

    public TextView text1;
    public TextView text2;

    public UserViewHolder(View itemView) {
        super(itemView);

        text1 = (TextView) itemView.findViewById(android.R.id.text1);
        text2 = (TextView) itemView.findViewById(android.R.id.text2);
    }
}
