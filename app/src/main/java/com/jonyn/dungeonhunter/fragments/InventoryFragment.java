package com.jonyn.dungeonhunter.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jonyn.dungeonhunter.DbUtils;
import com.jonyn.dungeonhunter.ItemsAdapter;
import com.jonyn.dungeonhunter.R;
import com.jonyn.dungeonhunter.models.Hero;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InventoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InventoryFragment extends Fragment {

    // Elementos comunes recibidos del anterior fragment
    private String usrUID;
    private int pos;
    private Hero hero;

    // Elementos del Layout
    private TextView tvHeroInv;
    private RecyclerView rvItems;
    private Button btnInvReturn;

    public InventoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param usrUID Id del usuario.
     * @param pos Posicion del heroe en la lista.
     * @param hero Heroe a inspeccionar.
     * @return A new instance of fragment InventoryFragment.
     */
    public static InventoryFragment newInstance(String usrUID, int pos, Hero hero) {
        InventoryFragment fragment = new InventoryFragment();
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

        // Asignamos a la vista el texto correspondiente para mostrar el propietario del inventario
        tvHeroInv.setText(getString(R.string.hero_inventory, hero.getName()));

        // Creamos un adaptador con la lista de objetos del heroe
        ItemsAdapter adapter = new ItemsAdapter(hero.getInventory());

        // Asignamos el adaptador al RecyclerView y le asignamos un GridLayoutManager para que se
        // muestren como una tabla
        rvItems.setAdapter(adapter);
        rvItems.setLayoutManager(
                new GridLayoutManager(getContext(), 1,
                        RecyclerView.HORIZONTAL, false));

        // Asignamos un listener al boton para regresar al fragment anterior
        btnInvReturn.setOnClickListener(v -> {
            // Cuando el usuario le de click al boton, obtenemos el FragmentManager y el
            // DungeonFragment
            FragmentManager manager = getFragmentManager();
            DungeonFragment fragment = DungeonFragment.newInstance(usrUID, pos, hero);

            // Comprobamos que el FragmentManager no sea nulo y lo cargamos desde DbUtils
            assert manager != null;
            DbUtils.loadFragment(manager, fragment);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle b = getArguments();
        assert b != null;
        usrUID = b.getString(DbUtils.FB_USER_UID);
        pos = b.getInt(DbUtils.HERO_POS);
        hero = (Hero) b.getSerializable(DbUtils.HERO);

        // Inflamos la vista del fragment y lo guardamos en una variable
        View v = inflater.inflate(R.layout.fragment_inventory, container, false);

        // Asignamos las distintas vistas a sus elementos locales
        tvHeroInv = v.findViewById(R.id.tvHeroInv);
        rvItems = v.findViewById(R.id.rvItems);
        btnInvReturn = v.findViewById(R.id.btnInvReturn);

        // Devolvemos la vista
        return v;
    }
}