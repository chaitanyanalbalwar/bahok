package com.app.bahokrider.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.bahokrider.R;
import com.app.bahokrider.interfaces.IRecyclerViewListener;
import com.app.bahokrider.managers.GlideManager;
import com.app.bahokrider.pojos.Order;

import java.util.List;

/**
 * @author Amit
 */
public class ActiveOrdersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Order> mOrderList;
    IRecyclerViewListener listener;

    public ActiveOrdersAdapter(IRecyclerViewListener listener, List<Order> orderList) {
        this.listener = listener;
        this.mOrderList = orderList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.active_order_item, viewGroup, false);
        return new OrdersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        OrdersViewHolder hlr = (OrdersViewHolder) holder;

        Order oObj = mOrderList.get(position);

        hlr.tv_id.setText(holder.itemView.getContext().getString(R.string.order_tag) + oObj.getOrderId());
        hlr.tv_name.setText(oObj.getUserName());
        hlr.tv_location.setText(oObj.getUserAddress());
        hlr.tv_cost.setText(oObj.getCurrency() + " " + oObj.getCost());
        hlr.tv_delivery.setText("Delivery In: " + oObj.getDeliveryTime() + " mins");

        GlideManager.setUserImage(holder.itemView.getContext(), oObj.getHotelImage(), hlr.imgProfile);

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

        if (mOrderList == null)
            return 0;

        return mOrderList.size();
    }

    private class OrdersViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProfile;
        TextView tv_id;
        TextView tv_name;
        TextView tv_location;
        TextView tv_cost;
        TextView tv_delivery;

        OrdersViewHolder(View view) {
            super(view);
            imgProfile = view.findViewById(R.id.imgProfile);
            tv_id = view.findViewById(R.id.tv_id);
            tv_name = view.findViewById(R.id.tv_name);
            tv_location = view.findViewById(R.id.tv_location);
            tv_cost = view.findViewById(R.id.tv_cost);
            tv_delivery = view.findViewById(R.id.tv_delivery);
        }
    }

    public void setOrderList(List<Order> orderList) {
        this.mOrderList = orderList;
        notifyDataSetChanged();
    }

}
