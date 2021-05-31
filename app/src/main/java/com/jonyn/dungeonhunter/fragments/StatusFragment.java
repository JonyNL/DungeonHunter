package com.jonyn.dungeonhunter.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jonyn.dungeonhunter.AbilitiesAdapter;
import com.jonyn.dungeonhunter.DbUtils;
import com.jonyn.dungeonhunter.R;
import com.jonyn.dungeonhunter.models.Hero;
import com.jonyn.dungeonhunter.models.Wizard;

/**
 * Fragment que muestra el estado actual del heroe
 */
public class StatusFragment extends Fragment {

    // Variables de utilidad
    private String usrUID;
    private int pos;
    private Hero hero;

    // Componentes de la vista
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
    private ImageButton ibStatusClose;
    private Button btnActives;
    private Button btnPassives;
    private RecyclerView rvStAbilities;

    public StatusFragment() {
        // Required empty public constructor
    }

    /**
     * Metodo para crear una instancia de StatusFragment
     *
     * @param usrUID Id del usuario.
     * @param pos Posicion del heroe en la lista.
     * @param hero Heroe a inspeccionar.
     * @return A new instance of fragment StatsFragment.
     */
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflamos la vista y la guardamos en una variable
        View v =inflater.inflate(R.layout.fragment_status, container, false);

        // Recogemos el bundle y buscamos los atributos que necesitamos
        Bundle b = getArguments();
        assert b != null;
        usrUID = b.getString(DbUtils.FB_USER_UID);
        pos = b.getInt(DbUtils.HERO_POS);
        hero = (Hero) b.getSerializable(DbUtils.HERO);

        // Buscamos los componentes de la vista y los asignamos a sus variables correspondientes
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
        ibStatusClose = v.findViewById(R.id.ibStatusClose);
        btnActives = v.findViewById(R.id.btnActives);
        btnPassives = v.findViewById(R.id.btnPassives);
        rvStAbilities = v.findViewById(R.id.rvAbilities);

        // Devolvemos la vista
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Cargamos la imagen correspondiente a la clase del heroe
        ivHeroImg.setImageResource(
                getResources().getIdentifier(hero.getHeroClass().toString().toLowerCase(),
                        "drawable", getContext().getPackageName()));

        // Asignamos los valores correspondientes a los componentes de la vista
        tvStatsHeroName.setText(hero.getName());
        tvLevel.setText(getString(R.string.level, hero.getLvl()));
        tvClass.setText(hero.getClass().getSimpleName());
        tvLpStat.setText(getString(R.string.lp_stat, Integer.toString(hero.getLp()),
                Integer.toString(hero.getMaxLp())));
        tvMpStat.setText(getString(R.string.mp_stat, Integer.toString(hero.getMp()),
                Integer.toString(hero.getMaxMp())));
        tvStrStat.setText(getString(R.string.str_stat, Integer.toString(hero.getStrength())));

        if (hero instanceof Wizard)
            // Si es un mago cargamos el atributo inteligencia
            tvIntelStat.setText(getString(
                    R.string.intel_stat, Integer.toString(((Wizard)hero).getIntelligence())));
        else
            // En caso contrario es irrelevante
            tvIntelStat.setText(getString(R.string.intel_stat, "N/A"));

        tvDefStat.setText(getString(R.string.def_stat, Integer.toString(hero.getDefense())));
        tvAgStat.setText(getString(R.string.ag_stat, Integer.toString(hero.getAgility())));
        tvLckStat.setText(getString(R.string.lck_stat, Integer.toString(hero.getLuck())));

        // Agregamos un listener a ibStatusClose para cerrar la ventana actual y volver a
        // DungeonFragment
        ibStatusClose.setOnClickListener(v -> {
            FragmentManager manager = getFragmentManager();

            // Cargamos el fragment y lo lanzamos
            DungeonFragment fragment = DungeonFragment.newInstance(usrUID, pos, hero);
            assert manager != null;
            DbUtils.loadFragment(manager, fragment);
        });

        // Creamos un listener para los botones de las habilidades
        View.OnClickListener listener = v -> {
            switch (v.getId()) {
                  case R.id.btnActives:
                      // Si hemos seleccionado activas, cargamos una lista de las activas
                      AbilitiesAdapter aAdapter = new AbilitiesAdapter(hero.getActives(), true);
                      rvStAbilities.setAdapter(aAdapter);
                      break;
                  case R.id.btnPassives:
                      // En caso contrario, cargamos una lista de pasivas
                      AbilitiesAdapter pAdapter = new AbilitiesAdapter(hero.getPassives(), false);
                      rvStAbilities.setAdapter(pAdapter);
                      break;
            }
            // Asignamos el LayoutManager vertical al RecyclerView
            rvStAbilities.setLayoutManager(new LinearLayoutManager(getContext(),
                    LinearLayoutManager.VERTICAL, false));
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

        // Asignamos los listeners a los botones
        btnActives.setOnClickListener(listener);
        btnActives.setOnTouchListener(tListener);
        btnPassives.setOnClickListener(listener);
        btnPassives.setOnTouchListener(tListener);
    }
}