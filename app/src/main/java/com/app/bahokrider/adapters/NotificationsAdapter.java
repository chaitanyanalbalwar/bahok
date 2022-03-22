package com.app.bahokrider.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.bahokrider.R;
import com.app.bahokrider.interfaces.IRecyclerViewListener;
import com.app.bahokrider.pojos.Notification;

import java.util.List;

/**
 * @author Amit
 */

public class NotificationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Notification> mList;
    IRecyclerViewListener listener;

    public NotificationsAdapter(IRecyclerViewListener listener, List<Notification> mList) {
        this.listener = listener;
        this.mList = mList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.notif_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        ViewHolder hlr = (ViewHolder) holder;

        Notification oObj = mList.get(position);

        hlr.tv_message.setText(oObj.getMessage());
        hlr.tv_date_time.setText(oObj.getDateTime());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onItemClickListener(v, position);
            }
        });

    }

    @Override
    public int getItemCount() {

        if (mList == null)
            return 0;

        return mList.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_message;
        TextView tv_date_time;

        ViewHolder(View view) {
            super(view);
            tv_message = view.findViewById(R.id.tv_message);
            tv_date_time = view.findViewById(R.id.tv_date_time);
        }
    }

    public void setOrderList(List<Notification> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

}
