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
import com.app.bahokrider.pojos.TargetObj;

import java.util.List;

/**
 * @author Amit
 */

public class TargetPayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<TargetObj> mList;
    int points;
    IRecyclerViewListener listener;

    public TargetPayAdapter(IRecyclerViewListener listener, List<TargetObj> mList, int points) {
        this.listener = listener;
        this.mList = mList;
        this.points = points;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.target_pay_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        ViewHolder hlr = (ViewHolder) holder;

        TargetObj oObj = mList.get(position);

        hlr.tvAmount.setText("BDT " + oObj.getAmount());
        hlr.tvPoints.setText(oObj.getRange());

        String[] range = oObj.getRange().split("-");

        if (Integer.parseInt(range[0]) <= this.points) {
            hlr.imgTP.setBackground(hlr.itemView.getContext().getDrawable(R.drawable.colorprimary_circular));
        } else {
            hlr.imgTP.setBackground(hlr.itemView.getContext().getDrawable(R.drawable.gray_circular));
        }
    }

    @Override
    public int getItemCount() {

        if (mList == null)
            return 0;

        return mList.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvAmount;
        TextView tvPoints;
        ImageView imgTP;

        ViewHolder(View view) {
            super(view);
            tvAmount = view.findViewById(R.id.tvAmount);
            tvPoints = view.findViewById(R.id.tvPoints);
            imgTP = view.findViewById(R.id.imgTP);
        }
    }

    public void setOrderList(List<TargetObj> list, int value) {
        this.mList = list;
        this.points = value;
        notifyDataSetChanged();
    }

}
