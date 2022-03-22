package com.app.bahokrider.adapters;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.bahokrider.R;
import com.app.bahokrider.interfaces.IRecyclerViewListener;
import com.app.bahokrider.managers.GlideManager;
import com.app.bahokrider.pojos.Order;

import java.util.List;

/**
 * @author Amit
 */

public class OrdersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Order> mOrderList;
    IRecyclerViewListener listener;

    public OrdersAdapter(IRecyclerViewListener listener, List<Order> orderList) {
        this.listener = listener;
        this.mOrderList = orderList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.order_item, viewGroup, false);
        return new OrdersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        OrdersViewHolder hlr = (OrdersViewHolder) holder;

        Order oObj = mOrderList.get(position);

        hlr.tv_id.setText(holder.itemView.getContext().getString(R.string.order_tag) + oObj.getOrderId());
        hlr.tv_name.setText(oObj.getUserName());
        hlr.tv_date_time.setText(oObj.getDateTime());
        hlr.tv_location.setText(oObj.getUserAddress());
        hlr.tv_cost.setText(oObj.getCurrency() + " " + oObj.getCost());
        hlr.tv_payment.setText(oObj.getPaymentMode());
        hlr.tv_delivery.setText(oObj.getDeliveryTime() + " mins");

        if (oObj.getOrderStatus().equalsIgnoreCase("confirmed"))
            hlr.llButtons.setVisibility(View.VISIBLE);
        else
            hlr.llButtons.setVisibility(View.GONE);

        GlideManager.setUserImage(holder.itemView.getContext(), oObj.getUserImage(),
                hlr.imgProfile);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onItemClickListener(v, position);
            }
        });

        hlr.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onItemClickListener(v, position);
            }
        });

//        hlr.btnReject.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (listener != null)
//                    listener.onItemClickListener(v, position);
//            }
//        });

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
        TextView tv_date_time;
        TextView tv_location;
        TextView tv_cost;
        TextView tv_payment;
        TextView tv_delivery;
        LinearLayout llButtons;
        Button btnAccept;

        OrdersViewHolder(View view) {
            super(view);
            imgProfile = view.findViewById(R.id.imgProfile);
            tv_id = view.findViewById(R.id.tv_id);
            tv_name = view.findViewById(R.id.tv_name);
            tv_date_time = view.findViewById(R.id.tv_date_time);
            tv_location = view.findViewById(R.id.tv_location);
            tv_cost = view.findViewById(R.id.tv_cost);
            tv_payment = view.findViewById(R.id.tv_payment);
            tv_delivery = view.findViewById(R.id.tv_delivery);
            btnAccept = view.findViewById(R.id.btnAccept);
//            btnReject = view.findViewById(R.id.btnReject);
            llButtons = view.findViewById(R.id.llButtons);
        }
    }

    public void setOrderList(List<Order> orderList) {
        this.mOrderList = orderList;
        notifyDataSetChanged();
    }

}
