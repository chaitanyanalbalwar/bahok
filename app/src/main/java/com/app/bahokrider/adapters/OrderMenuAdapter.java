package com.app.bahokrider.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.bahokrider.R;
import com.app.bahokrider.pojos.OrderMenu;

import java.util.List;

/**
 * @author
 */
public class OrderMenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<OrderMenu> menuList;

    public OrderMenuAdapter(List<OrderMenu> menuList) {
        this.menuList = menuList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.order_menu_item, viewGroup, false);
        return new OrdersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        OrdersViewHolder hlr = (OrdersViewHolder) holder;

        OrderMenu oObj = menuList.get(position);

        hlr.tvName.setText(oObj.getMenuName());
        hlr.tvQuantity.setText("Items: " + oObj.getQty() + " x " + oObj.getMenuPrice());
      //  hlr.tvCost.setText(oObj.getCurrency() + " " + oObj.getMenuTotalPrice());
        hlr.tvCost.setText("BDT" + " " + oObj.getMenuTotalPrice());

    }

    @Override
    public int getItemCount() {

        if (menuList == null)
            return 0;

        return menuList.size();
    }

    private class OrdersViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvQuantity;
        TextView tvCost;

        OrdersViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvName);
            tvQuantity = view.findViewById(R.id.tvQuantity);
            tvCost = view.findViewById(R.id.tvCost);
        }
    }

}
