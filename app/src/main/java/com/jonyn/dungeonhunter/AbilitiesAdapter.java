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

    private final List<Ability> abilities;
    private View.OnClickListener listener;
    private boolean actives;

    public AbilitiesAdapter(List<Ability> abilities, boolean actives){
        this.abilities = abilities;
        this.actives = actives;
    }

    @NonNull
    @Override
    public AbilitiesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.battlelist_listitem, parent, false);

        itemView.setOnClickListener(listener);

        return new AbilitiesViewHolder(itemView, actives);
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
        holder.abilityBind(abilities.get(position));
    }

    @Override
    public int getItemCount() {
        return abilities.size();
    }

    static class AbilitiesViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvAbility;
        private final TextView tvCost;
        private final boolean actives;

        public AbilitiesViewHolder(@NonNull View itemView, boolean actives) {
            super(itemView);
            this.actives = actives;

            tvAbility = itemView.findViewById(R.id.tvName);
            tvCost = itemView.findViewById(R.id.tvCostQtty);
        }

        public void abilityBind(Ability a){
            if (actives){
                tvAbility.setText(a.getAbility());
                tvCost.setText(String.valueOf(((Active)a).getCost()));
            } else {
                tvAbility.setText(a.getAbility());
                tvCost.setText("N/A");
            }
        }
    }
}







