package com.jonyn.dungeonhunter.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jonyn.dungeonhunter.DbUtils;
import com.jonyn.dungeonhunter.R;
import com.jonyn.dungeonhunter.models.Hero;
import com.jonyn.dungeonhunter.models.Wizard;

/**
 * Fragment que muestra los Stats del heroe
 */
public class StatusFragment extends Fragment {

    private String usrUID;
    private int pos;
    private Hero hero;

    private TextView tvStatsHeroName;
    private TextView tvLevel;
    private TextView tvClass;
    private TextView tvLpStat;
    private TextView tvMpStat;
    private TextView tvStrStat;
    private TextView tvIntelStat;
    private TextView tvDefStat;
    private TextView tvAgStat;
    private TextView tvLckStat;
    private ImageView ivHeroImg;
    private Button btnConfirm;

    public StatusFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param usrUID Id del usuario.
     * @param pos Posicion del heroe en la lista.
     * @param hero Heroe a inspeccionar.
     * @return A new instance of fragment StatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatusFragment newInstance(String usrUID, int pos, Hero hero) {
        StatusFragment fragment = new StatusFragment();
        Bundle args = new Bundle();
        args.putString(DbUtils.FB_USER_UID, usrUID);
        args.putInt(DbUtils.HERO_POS, pos);
        args.putSerializable(DbUtils.HERO, hero);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        tvStatsHeroName.setText(hero.getName());
        tvLevel.setText(getString(R.string.hero_lvl, Integer.toString(hero.getLvl())));
        tvClass.setText(hero.getClass().getSimpleName());
        tvLpStat.setText(getString(R.string.lp_stat, Integer.toString(hero.getLp()),
                Integer.toString(hero.getMaxLp())));
        tvMpStat.setText(getString(R.string.mp_stat, Integer.toString(hero.getMp()),
                Integer.toString(hero.getMaxMp())));
        tvStrStat.setText(getString(R.string.str_stat, Integer.toString(hero.getStrength())));
        if (hero instanceof Wizard)
            tvIntelStat.setText(getString(
                    R.string.intel_stat, Integer.toString(((Wizard)hero).getIntelligence())));
        else
            tvIntelStat.setText(getString(R.string.intel_stat, "N/A"));
        tvDefStat.setText(getString(R.string.def_stat, Integer.toString(hero.getDefense())));
        tvAgStat.setText(getString(R.string.ag_stat, Integer.toString(hero.getAgility())));
        tvLckStat.setText(getString(R.string.lck_stat, Integer.toString(hero.getLuck())));
        btnConfirm.setOnClickListener(v -> {
            // Cuando el usuario le de click a Confirmar, lanzamos una DungeonFragment.
            FragmentManager manager = getFragmentManager();

            DungeonFragment fragment = DungeonFragment.newInstance(usrUID, pos, hero);
            assert manager != null;
            DbUtils.loadFragment(manager, fragment);
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_status, container, false);

        Bundle b = getArguments();
        assert b != null;
        usrUID = b.getString(DbUtils.FB_USER_UID);
        pos = b.getInt(DbUtils.HERO_POS);
        hero = (Hero) b.getSerializable(DbUtils.HERO);

        tvStatsHeroName = v.findViewById(R.id.tvStatsHeroName);
        tvLevel = v.findViewById(R.id.tvLevel);
        tvClass = v.findViewById(R.id.tvClass);
        tvLpStat = v.findViewById(R.id.tvLpStat);
        tvMpStat = v.findViewById(R.id.tvMpStat);
        tvStrStat = v.findViewById(R.id.tvStrStat);
        tvIntelStat = v.findViewById(R.id.tvIntelStat);
        tvDefStat = v.findViewById(R.id.tvDefStat);
        tvAgStat = v.findViewById(R.id.tvAgStat);
        tvLckStat= v.findViewById(R.id.tvLckStat);
        ivHeroImg = v.findViewById(R.id.ivHeroImg);
        btnConfirm = v.findViewById(R.id.btnReturn);

        return v;
    }
}