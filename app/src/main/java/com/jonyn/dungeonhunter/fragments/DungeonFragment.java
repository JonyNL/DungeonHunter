package com.jonyn.dungeonhunter.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jonyn.dungeonhunter.DbUtils;
import com.jonyn.dungeonhunter.R;
import com.jonyn.dungeonhunter.models.DungeonProgress;
import com.jonyn.dungeonhunter.models.Enemy;
import com.jonyn.dungeonhunter.models.Hero;

import java.util.ArrayList;

import static com.jonyn.dungeonhunter.DbUtils.FB_USER_UID;
import static com.jonyn.dungeonhunter.DbUtils.HERO;
import static com.jonyn.dungeonhunter.DbUtils.HERO_POS;

/**
 * fragment que representa la pantalla principal una vez seleccionado un heroe
 *
 */
public class DungeonFragment extends Fragment {
    public static final String TAG = "DUNGEON_FRAGMENT";

    // Elementos comunes de utilidad y recibidos del anterior fragment
    private String usrUID;
    private int heroPos;
    private Hero hero;
    private DungeonProgress dungeonProgress;
    private Enemy enemy;

    // Elementos de la vista
    private TextView tvHeroName;
    private TextView tvHeroLvl;
    private TextView tvHeroExp;
    private TextView tvFloorStage;
    private Button btnAdvance;
    private ImageButton ibMenuStats;
    private ImageButton ibMenuBag;
    private ImageButton ibEnemies;
    private ImageButton ibSettings;
    private SeekBar sbDProgress;


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflamos la vista del fragment y lo asignamos a una variable
        View v = inflater.inflate(R.layout.fragment_dungeon, container, false);

        // Comprobamos que el Bundle no sea nulo y asignamos lo que necesitamos
        Bundle b = getArguments();
        assert b != null;
        usrUID = b.getString(FB_USER_UID);
        heroPos = b.getInt(HERO_POS);
        hero = (Hero) b.getSerializable(HERO);
        enemy = null;

        // Actualizamos la lista de usuarios en la base de datos, para que en caso de haber
        // terminado una batalla se guarden los cambios en el heroe
        DbUtils.updateDbHeroList(usrUID);

        // Buscamos y asignamos los componentes a sus variables locales
        tvHeroName = v.findViewById(R.id.tvHeroName);
        tvHeroLvl = v.findViewById(R.id.tvHeroLvl);
        tvHeroExp = v.findViewById(R.id.tvHeroExp);
        btnAdvance = v.findViewById(R.id.btnAdvance);
        ibMenuStats = v.findViewById(R.id.ibMenuStatus);
        ibMenuBag = v.findViewById(R.id.ibMenuBag);
        ibEnemies = v.findViewById(R.id.ibEnemies);
        ibSettings = v.findViewById(R.id.ibSettings);
        tvFloorStage = v.findViewById(R.id.tvFloorStage);
        sbDProgress = v.findViewById(R.id.sbDProgress);

        // Devolvemos la vista
        return v;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onStart() {
        super.onStart();
        /* if (hero.getDungeonProgress() == null)
            hero.setDungeonProgress(new DungeonProgress(
                    1, 1, 1, new ArrayList<>()));*/

        // Obtenemos el progreso del heroe
        dungeonProgress = hero.getDungeonProgress();

        // Asignamos el texto correspondiente a los elementos de la vista
        tvHeroName.setText(hero.getName());
        tvHeroLvl.setText(getString(R.string.level, hero.getLvl()));
        tvHeroExp.setText(getString(R.string.exp_value,
                String.valueOf(hero.getExp()), String.valueOf(hero.getReqExp())));
        tvFloorStage.setText(getString(R.string.floor_stage_run, dungeonProgress.getRun(),
                dungeonProgress.getFloor(), dungeonProgress.getStagePos()));

        // Ajustamos el seekBar al proceso del heroe
        sbDProgress.setMax(hero.getDungeonProgress().getFloorProgress().size() - 1);
        sbDProgress.setProgress(hero.getDungeonProgress().getStagePos() - 1);

        // Asignamos un onTouchListener que directamente devuelva true para que no sea interactivo
        sbDProgress.setOnTouchListener((v, event) -> true);
        // Creamos el btnListener para los botones.
        @SuppressLint("NonConstantResourceId")
        View.OnClickListener btnListener = v -> {
            // Asignamos el FragmentManager a una variable para usarlo mas adelante.
            // comprobamos que no sea null y realizamos una accion dependiendo del ID de la vista
            FragmentManager manager = getFragmentManager();
            assert manager != null;
            switch (v.getId()){
                case R.id.btnAdvance:
                    // Obtenemos un enemigo desde DbUtils y lo asignamos a la variable local.
                    enemy = DbUtils.getEnemy(dungeonProgress.getFloor(), dungeonProgress.getStagePos());

                    // Creamos una variable BattleFragment y lo lanzamos con el enemigo
                    BattleFragment bFragment;
                    enemy.setLp(enemy.getMaxLp());
                    bFragment = BattleFragment.newInstance(usrUID, heroPos, hero, enemy);

                    // Cargamos el fragment desde DbUtils
                    DbUtils.loadFragment(manager, bFragment);
                    break;
                case R.id.ibMenuStatus:
                    // Creamos un StatusFragment y la lanzamos a traves de DbUtils.
                    StatusFragment sFragment = StatusFragment.newInstance(usrUID, heroPos, hero);
                    DbUtils.loadFragment(manager, sFragment);
                    break;
                case R.id.ibMenuBag:
                    // Creamos un InventoryFragment y la lanzamos a traves de DbUtils.
                    InventoryFragment iFragment = InventoryFragment.newInstance(usrUID, heroPos, hero);
                    DbUtils.loadFragment(manager, iFragment);
                    break;
                case R.id.ibEnemies:
                    // Creamos un EnemiesFragment y la lanzamos a traves de DbUtils.
                    EnemiesFragment eFragment = EnemiesFragment.newInstance(usrUID, heroPos, hero);
                    DbUtils.loadFragment(manager, eFragment);
                    break;
                case R.id.ibSettings:
                    // Creamos un SettingsFragment y la lanzamos a traves de DbUtils.
                    SettingsFragment settFragment = SettingsFragment.newInstance(usrUID, heroPos, hero);
                    DbUtils.loadFragment(manager, settFragment);
                    break;
            }
        };

        // Creamos un OnTouchListener para remarcar la pulsacion de los botones
        @SuppressLint("ClickableViewAccessibility")
        View.OnTouchListener tListener = (v, e) -> {
            if(e.getAction() == MotionEvent.ACTION_DOWN) {
                // Si el boton esta siendo pulsado, agregamos tinte al background color negro
                v.setBackgroundTintList(getResources().getColorStateList(android.R.color.black));
            } else if (e.getAction() == MotionEvent.ACTION_UP) {
                // Si el boton esta dejando de pulsarse, establecemos el tinte del background a null
                v.setBackgroundTintList(null);
            }
            return false;
        };

        // Asignamos el los listeners a los botones
        btnAdvance.setOnClickListener(btnListener);
        btnAdvance.setOnTouchListener(tListener);
        ibMenuStats.setOnClickListener(btnListener);
        ibMenuBag.setOnClickListener(btnListener);
        ibEnemies.setOnClickListener(btnListener);
        ibSettings.setOnClickListener(btnListener);

    }
}