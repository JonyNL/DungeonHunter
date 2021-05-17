package com.jonyn.dungeonhunter.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jonyn.dungeonhunter.DbUtils;
import com.jonyn.dungeonhunter.R;
import com.jonyn.dungeonhunter.models.Enemy;
import com.jonyn.dungeonhunter.models.Hero;

import static com.jonyn.dungeonhunter.DbUtils.FB_USER_UID;
import static com.jonyn.dungeonhunter.DbUtils.HERO;
import static com.jonyn.dungeonhunter.DbUtils.HERO_POS;

/**
 * fragment que representa la pantalla principal una vez seleccionado un heroe
 *
 */
public class DungeonFragment extends Fragment {
    public static final String TAG = "DUNGEON_FRAGMENT";

    // Elementos comunes recibidos del anterior fragment
    private String usrUID;
    private int heroPos;
    private Hero hero;
    private Enemy enemy;

    // Elementos de la vista
    private TextView tvHeroName;
    private TextView tvHeroLvl;
    private TextView tvHeroExp;
    private Button btnDLeft;
    private Button btnDFront;
    private Button btnDRight;
    private Button btnMenuStats;
    private Button btnMenuBag;
    private Button btnEnemies;
    private Button btnSettings;

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

        // Asignamos el texto correspondiente a los elementos de la vista
        tvHeroName.setText(hero.getName());
        tvHeroLvl.setText(getString(R.string.hero_lvl, String.valueOf(hero.getLvl())));
        tvHeroExp.setText(getString(R.string.exp_value,
                String.valueOf(hero.getExp()), String.valueOf(hero.getReqExp())));

        // Creamos el listener a agregar a los botones.
        View.OnClickListener listener = v -> {
            // Asignamos el FragmentManager a una variable para usarlo mas adelante.
            // comprobamos que no sea null y realizamos una accion dependiendo del ID de la vista
            FragmentManager manager = getFragmentManager();
            assert manager != null;
            switch (v.getId()){
                case R.id.btnDLeft:case R.id.btnDFront:case R.id.btnDRight:

                    // Obtenemos un enemigo desde DbUtils y lo asignamos a la variable local.
                    enemy = DbUtils.getEnemy();

                    // Creamos una variable BattleFragment y dependiendo de si el enemigo es o no 
                    // null lo lanzamos usando esa variable o creamos un enemigo nuevo.
                    BattleFragment bFragment;
                    if (enemy!= null){
                        enemy.setLp(enemy.getMaxLp());
                        bFragment = BattleFragment.newInstance(usrUID, heroPos, hero, enemy);
                    } else {
                        bFragment = BattleFragment
                                .newInstance(usrUID, heroPos, hero, new Enemy("Kappa", Enemy.Types.DEMON));
                    }

                    // Cargamos el fragment desde DbUtils
                    DbUtils.loadFragment(manager, bFragment);
                    break;
                case R.id.btnMenuStatus:
                    // Creamos un StatusFragment y la lanzamos a traves de DbUtils.
                    StatusFragment sFragment = StatusFragment.newInstance(usrUID, heroPos, hero);
                    DbUtils.loadFragment(manager, sFragment);
                    break;
                case R.id.btnMenuBag:
                    // Creamos un InventoryFragment y la lanzamos a traves de DbUtils.
                    InventoryFragment iFragment = InventoryFragment.newInstance(usrUID, heroPos, hero);
                    DbUtils.loadFragment(manager, iFragment);
                    break;

                    // TODO Crear e implementar fragments para el resto de opciones
                case R.id.btnEnemies: case R.id.btnSettings:
                    Toast.makeText(getContext(), getResources().getResourceEntryName(v.getId()),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        };

        // Asignamos el listener a los botones
        btnDLeft.setOnClickListener(listener);
        btnDFront.setOnClickListener(listener);
        btnDRight.setOnClickListener(listener);
        btnMenuStats.setOnClickListener(listener);
        btnMenuBag.setOnClickListener(listener);
        btnEnemies.setOnClickListener(listener);
        btnSettings.setOnClickListener(listener);

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

        // Buscamos y asignamos las vistas a sus elementos locales
        tvHeroName = v.findViewById(R.id.tvHeroName);
        tvHeroLvl = v.findViewById(R.id.tvHeroLvl);
        tvHeroExp = v.findViewById(R.id.tvHeroExp);
        btnDLeft = v.findViewById(R.id.btnDLeft);
        btnDFront = v.findViewById(R.id.btnDFront);
        btnDRight = v.findViewById(R.id.btnDRight);
        btnMenuStats = v.findViewById(R.id.btnMenuStatus);
        btnMenuBag = v.findViewById(R.id.btnMenuBag);
        btnEnemies = v.findViewById(R.id.btnEnemies);
        btnSettings = v.findViewById(R.id.btnSettings);

        // Devolvemos la vista
        return v;
    }

}