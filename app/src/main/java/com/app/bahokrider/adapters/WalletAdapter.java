package com.app.bahokrider.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.bahokrider.R;
import com.app.bahokrider.interfaces.IRecyclerViewListener;
import com.app.bahokrider.pojos.Transaction;

import java.util.List;

/**
 * @author Amit
 */

public class WalletAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Transaction> mList;
    IRecyclerViewListener listener;

    public WalletAdapter(IRecyclerViewListener listener, List<Transaction> mList) {
        this.listener = listener;
        this.mList = mList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.wallet_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        ViewHolder hlr = (ViewHolder) holder;

        Transaction oObj = mList.get(position);

        hlr.tv_id.setText(oObj.getTransId());
        hlr.tv_amount.setText(oObj.getAmount());
        hlr.tv_date_time.setText(oObj.getDateTime());
        hlr.tv_status.setText(oObj.getTranStatus().toUpperCase());
        if (oObj.getTranStatus().equalsIgnoreCase("credit"))
            hlr.tv_status.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.linkColor));
        else
            hlr.tv_status.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.colorPrimary));

    }

    @Override
    public int getItemCount() {

        if (mList == null)
            return 0;

        return mList.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_id;
        TextView tv_date_time;
        TextView tv_amount;
        TextView tv_status;

        ViewHolder(View view) {
            super(view);
            tv_id = view.findViewById(R.id.tv_id);
            tv_date_time = view.findViewById(R.id.tv_date_time);
            tv_amount = view.findViewById(R.id.tv_amount);
            tv_status = view.findViewById(R.id.tv_status);
        }
    }

    public void setOrderList(List<Transaction> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

}
