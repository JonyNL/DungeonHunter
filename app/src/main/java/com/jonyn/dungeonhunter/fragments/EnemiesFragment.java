package com.jonyn.dungeonhunter.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.jonyn.dungeonhunter.EnemyInfoDialog;
import com.jonyn.dungeonhunter.DbUtils;
import com.jonyn.dungeonhunter.EnemiesAdapter;
import com.jonyn.dungeonhunter.IEnemiesListener;
import com.jonyn.dungeonhunter.R;
import com.jonyn.dungeonhunter.models.Enemy;
import com.jonyn.dungeonhunter.models.Hero;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EnemiesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EnemiesFragment extends Fragment implements IEnemiesListener {

    // Variables de utilidad
    private String usrUID;
    private int pos;
    private Hero hero;
    private IEnemiesListener listener;

    // Variables de la vista
    private RecyclerView rvEnemies;
    private ImageButton ibEnemiesClose;


    public EnemiesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param usrUID Id del Usuario en FireBase.
     * @param pos Posicion del heroe en la lista.
     * @return Nueva instancia de EnemiesFragment.
     */
    public static EnemiesFragment newInstance(String usrUID, int pos, Hero hero) {
        EnemiesFragment fragment = new EnemiesFragment();
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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_enemies, container, false);

        // Recibimos el bundle, comprobamos que no sea null y recogemos sus elementos
        Bundle b = getArguments();
        assert b != null;
        usrUID = b.getString(DbUtils.FB_USER_UID);
        pos = b.getInt(DbUtils.HERO_POS);
        hero = (Hero) b.getSerializable(DbUtils.HERO);

        // Buscamos los componentes en la vista y los asignamos a sus variables.
        rvEnemies = v.findViewById(R.id.rvEnemies);
        ibEnemiesClose = v.findViewById(R.id.ibEnemiesClose);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Inicializamos el listener
        listener = this;

        // Creamos un nuevo adaptador para el listado de enemigos y se lo asignamos al RecyclerView
        EnemiesAdapter adapter = new EnemiesAdapter(DbUtils.getEnemies());
        rvEnemies.setAdapter(adapter);

        // Asignamos el listener al adaptador
        adapter.setOnClickListener(v -> {
            if (listener != null)
                // Lanzamos el onEnemyClick y le pasamos el enemigo seleccionado
                onEnemyClick(DbUtils.getEnemies().get(rvEnemies.getChildAdapterPosition(v)));
        });

        // Asignamos un LinearLayout vertical al RecyclerView
        rvEnemies.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL, false));

        // Asignamos un listener al ibEnemiesClose
        ibEnemiesClose.setOnClickListener(v -> {
            // Cerramos el Fragment actual y volvemos a abrir el DungeonFragment
            DungeonFragment dFragment = DungeonFragment.newInstance(usrUID, pos, hero);
            DbUtils.loadFragment(getFragmentManager(), dFragment);
        });
    }

    /**
     * Metodo que gestiona la seleccion de un enemigo
     *
     * @param enemy Enemigo seleccionado en la lista
     * */
    @Override
    public void onEnemyClick(Enemy enemy) {
        // Creamos un EnemyInfoDialog pasandole context y enemy y lo mostramos.
        EnemyInfoDialog eid = new EnemyInfoDialog(getContext(), enemy);
        eid.show();


    }
}