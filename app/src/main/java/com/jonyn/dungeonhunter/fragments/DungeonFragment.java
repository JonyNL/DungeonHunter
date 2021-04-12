package com.jonyn.dungeonhunter.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jonyn.dungeonhunter.DbUtils;
import com.jonyn.dungeonhunter.R;
import com.jonyn.dungeonhunter.models.Enemy;
import com.jonyn.dungeonhunter.models.Hero;

import java.util.List;
import java.util.Map;

import static com.jonyn.dungeonhunter.DbUtils.FB_USER_UID;
import static com.jonyn.dungeonhunter.DbUtils.HERO;
import static com.jonyn.dungeonhunter.DbUtils.HERO_POS;
import static com.jonyn.dungeonhunter.DbUtils.updateDbHeroList;
import static com.jonyn.dungeonhunter.DbUtils.updateHeroPos;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DungeonFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DungeonFragment extends Fragment {
    public static final String TAG = "DUNGEON_FRAGMENT";

    private String usrUID;
    private FirebaseFirestore db;
    private int heroPos;
    private Hero hero;
    private Enemy enemy;

    private TextView tvHeroName;
    private TextView tvHeroLvl;
    private TextView tvHeroExp;
    private Button btnDLeft;
    private Button btnDFront;
    private Button btnDRight;
    private Button btnMenu1;
    private Button btnMenu2;
    private Button btnMenu3;
    private Button btnMenu4;

    public DungeonFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userID ID del usuario logueado.
     * @return A new instance of fragment DungeonFragment.
     */
    public static DungeonFragment newInstance(String userID, int pos, Hero hero) {
        DungeonFragment fragment = new DungeonFragment();
        Bundle args = new Bundle();
        args.putString(FB_USER_UID, userID);
        args.putInt(HERO_POS, pos);
        args.putSerializable(HERO, hero);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        tvHeroName.setText(hero.getName());
        tvHeroLvl.setText(getString(R.string.hero_lvl, String.valueOf(hero.getLvl())));
        tvHeroExp.setText(getString(R.string.exp_value,
                String.valueOf(hero.getExp()), String.valueOf(hero.getReqExp())));

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO implementar el onClick
                switch (v.getId()){
                    case R.id.btnDLeft:case R.id.btnDFront:case R.id.btnDRight:
                        FragmentManager manager = getFragmentManager();

                        enemy = DbUtils.getEnemy();

                        BattleFragment fragment;
                        if (enemy!= null){
                            enemy.setLp(enemy.getMaxLp());
                            fragment = BattleFragment.newInstance(usrUID, heroPos, hero, enemy);
                        } else {
                            fragment = BattleFragment
                                    .newInstance(usrUID, heroPos, hero, new Enemy("Kappa", Enemy.Types.DEMON));
                        }

                        Log.i(TAG, enemy.toString());

                        String newFragment = fragment.getClass().getName();

                        manager.beginTransaction()
                                .replace(R.id.container, fragment, newFragment)
                                .commit();
                        break;
                    case R.id.btnMenu1: case R.id.btnMenu2: case R.id.btnMenu3:
                    case R.id.btnMenu4:
                        Toast.makeText(getContext(), getResources().getResourceEntryName(v.getId()),
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        btnDLeft.setOnClickListener(listener);
        btnDFront.setOnClickListener(listener);
        btnDRight.setOnClickListener(listener);
        btnMenu1.setOnClickListener(listener);
        btnMenu2.setOnClickListener(listener);
        btnMenu3.setOnClickListener(listener);
        btnMenu4.setOnClickListener(listener);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dungeon, container, false);

        Bundle b = getArguments();
        usrUID = b.getString(FB_USER_UID);
        heroPos = b.getInt(HERO_POS);
        hero = (Hero) b.getSerializable(HERO);
        enemy = null;
        db = FirebaseFirestore.getInstance();

        tvHeroName = v.findViewById(R.id.tvHeroName);
        tvHeroLvl = v.findViewById(R.id.tvHeroLvl);
        tvHeroExp = v.findViewById(R.id.tvHeroExp);
        btnDLeft = v.findViewById(R.id.btnDLeft);
        btnDFront = v.findViewById(R.id.btnDFront);
        btnDRight = v.findViewById(R.id.btnDRight);
        btnMenu1 = v.findViewById(R.id.btnMenu1);
        btnMenu2 = v.findViewById(R.id.btnMenu2);
        btnMenu3 = v.findViewById(R.id.btnMenu3);
        btnMenu4 = v.findViewById(R.id.btnMenu4);

        return v;
    }

}