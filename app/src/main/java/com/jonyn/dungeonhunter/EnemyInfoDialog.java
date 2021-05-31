package com.jonyn.dungeonhunter;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.jonyn.dungeonhunter.models.Enemy;

public class EnemyInfoDialog extends Dialog {

    private ImageView ivEnemyRecImg;
    private TextView tvEnemyInfo;
    private TextView tvEnemyDesc;

    private Enemy enemy;

    public EnemyInfoDialog(@NonNull Context context, Enemy enemy){
        super(context);
        this.enemy = enemy;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.enemy_rec_dialog);
        ivEnemyRecImg = findViewById(R.id.ivEnemyRecImg);
        tvEnemyInfo = findViewById(R.id.tvEnemyInfo);
        tvEnemyDesc = findViewById(R.id.tvEnemyDesc);

        tvEnemyInfo.setText(getContext().getString(
                R.string.enemy_info, enemy.getName(), enemy.getType().toString(), 0));
    }
}
