package com.jonyn.dungeonhunter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jonyn.dungeonhunter.models.Consumable;
import com.jonyn.dungeonhunter.models.Item;

import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemViewHolder>
        implements View.OnClickListener {

    private View.OnClickListener listener;
    private final List<Item> items;
    private final boolean gridView;

    public ItemsAdapter(List<Item> items, boolean gridView){
        this.items = items;
        this.gridView = gridView;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (gridView)
            v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.inventory_listitem, parent, false);
        else
            v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.battlelist_listitem, parent, false);
        v.setOnClickListener(listener);

        return new ItemViewHolder(v, gridView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsAdapter.ItemViewHolder holder, int position) {
        holder.bindItem(items.get(position), gridView);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onClick(View v) {
        if (listener != null)
            listener.onClick(v);
    }

    public void setOnClickListener (View.OnClickListener listener){
        this.listener = listener;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView tvItem;
        private TextView tvAbility;
        private TextView tvCost;

        public ItemViewHolder(@NonNull View itemView, boolean gridView) {
            super(itemView);
            if (gridView) {
                tvItem = itemView.findViewById(R.id.tvItem);
            } else {
                tvAbility = itemView.findViewById(R.id.tvName);
                tvCost = itemView.findViewById(R.id.tvCostQtty);
            }
        }

        public void bindItem(Item i, boolean gridView){
            if (gridView) {
                tvItem.setText(String.valueOf(i.getName().charAt(0)));
            } else {
                tvAbility.setText(i.getName());
                tvCost.setText(String.valueOf(((Consumable)i).getQuantity()));
            }
        }
    }
}











