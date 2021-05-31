package com.jonyn.dungeonhunter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jonyn.dungeonhunter.models.Enemy;

import java.util.List;

public class EnemiesAdapter extends RecyclerView.Adapter<EnemiesAdapter.EnemiesViewHolder>
        implements View.OnClickListener {

    private List<Enemy> enemies;
    private View.OnClickListener listener;

    public EnemiesAdapter(List<Enemy> enemies){
        this.enemies = enemies;
    }

    @NonNull
    @Override
    public EnemiesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.enemies_listitem, parent,
        false);

        v.setOnClickListener(listener);

        return new EnemiesViewHolder(v);
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onClick(v);
        }
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull EnemiesViewHolder holder, int position) {
        holder.bindEnemy(enemies.get(position));
    }

    @Override
    public int getItemCount() {
        return enemies.size();
    }

    static class EnemiesViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivEnemyListImg;
        private TextView tvEnemyListName;
        private TextView tvEnemyRec;


        public EnemiesViewHolder(@NonNull View itemView) {
            super(itemView);

            ivEnemyListImg = itemView.findViewById(R.id.ivEnemyListImg);
            tvEnemyListName = itemView.findViewById(R.id.tvEnemyListName);
            tvEnemyRec = itemView.findViewById(R.id.tvEnemyRec);
        }

        void bindEnemy(Enemy e){
            tvEnemyListName.setText(e.getName());
            tvEnemyRec.setText("0");
        }
    }
}
