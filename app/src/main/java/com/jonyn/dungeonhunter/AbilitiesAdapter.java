package com.jonyn.dungeonhunter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jonyn.dungeonhunter.models.Ability;
import com.jonyn.dungeonhunter.models.Active;

import java.util.List;

public class AbilitiesAdapter extends RecyclerView.Adapter<AbilitiesAdapter.AbilitiesViewHolder>
    implements View.OnClickListener {

    List<Ability> actives;
    View.OnClickListener listener;

    public AbilitiesAdapter(List<Ability> actives){
        this.actives = actives;
    }

    @NonNull
    @Override
    public AbilitiesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.abilities_listitem, parent, false);

        itemView.setOnClickListener(listener);

        AbilitiesViewHolder avh = new AbilitiesViewHolder(itemView);
        return avh;
    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }


    @Override
    public void onClick(View v) {
        if (listener!=null)
            listener.onClick(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AbilitiesViewHolder holder, int position) {
        holder.abilityBind((Active) actives.get(position));
    }

    @Override
    public int getItemCount() {
        return actives.size();
    }

    class AbilitiesViewHolder extends RecyclerView.ViewHolder {

        private TextView tvAbility;
        private TextView tvCost;

        public AbilitiesViewHolder(@NonNull View itemView) {
            super(itemView);

            tvAbility = itemView.findViewById(R.id.tvAbility);
            tvCost = itemView.findViewById(R.id.tvCost);
        }

        public void abilityBind(Active a){
            tvAbility.setText(a.getAbility());
            tvCost.setText(String.valueOf(a.getCost()));
        }
    }
}
