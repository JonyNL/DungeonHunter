package com.jonyn.dungeonhunter.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jonyn.dungeonhunter.DbUtils;
import com.jonyn.dungeonhunter.GridAutoFitLayoutManager;
import com.jonyn.dungeonhunter.IItemsListener;
import com.jonyn.dungeonhunter.ItemsAdapter;
import com.jonyn.dungeonhunter.R;
import com.jonyn.dungeonhunter.models.Hero;
import com.jonyn.dungeonhunter.models.Item;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InventoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InventoryFragment extends Fragment implements IItemsListener {

    // Elementos comunes recibidos del anterior fragment
    private String usrUID;
    private int pos;
    private Hero hero;

    // Elementos del Layout
    private TextView tvHeroInv;
    private RecyclerView rvItems;
    private ImageButton ibInvClose;

    // Listener y adapter
    private IItemsListener listener;
    private ItemsAdapter adapter;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Guardamos el Bundle, comprobamos que no sea null y buscamos las variables
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
        ibInvClose = v.findViewById(R.id.ibInvClose);

        // Devolvemos la vista
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Asignamos a la vista el texto correspondiente para mostrar el propietario del inventario
        tvHeroInv.setText(getString(R.string.hero_inventory, hero.getName()));

        listener = this;

        // Creamos un adaptador con la lista de objetos del heroe, avisando que es un GridView
        adapter = new ItemsAdapter(hero.getInventory(), true);

        // Asignamos un onClickListener al adaptador
        adapter.setOnClickListener(v1 -> {
            if (listener != null){
                // Si el listener no es null, llamamos a onItemClick con la posicion del item en la
                // lista
                listener.onItemClick(rvItems.getChildAdapterPosition(v1));
            }

        });

        // Asignamos el adaptador al RecyclerView y le asignamos un GridLayoutManager para que se
        // muestren como una tabla
        rvItems.setAdapter(adapter);
        rvItems.setLayoutManager(new GridAutoFitLayoutManager(getContext(), 53));

        // Asignamos un listener al boton para regresar al fragment anterior
        ibInvClose.setOnClickListener(v -> {
            // Cuando el usuario le de click al boton, obtenemos el FragmentManager y el
            // DungeonFragment
            FragmentManager manager = getFragmentManager();
            DungeonFragment fragment = DungeonFragment.newInstance(usrUID, pos, hero);

            // Comprobamos que el FragmentManager no sea nulo y lo cargamos desde DbUtils
            assert manager != null;
            DbUtils.loadFragment(manager, fragment);
        });
    }

    /**
     * Metodo para gestionar el objeto seleccionado
     *
     * @param pos Posicion del objeto seleccionado en la lista
     * */
    @Override
    public void onItemClick(int pos) {
        // Creamos un dialogo con un titulo avisando del resultado de la partida.
        AlertDialog.Builder alertDialog;

        alertDialog = new AlertDialog.Builder(getContext());

        alertDialog.setTitle(hero.getInventory().get(pos).getName());
        alertDialog.setMessage(hero.getInventory().get(pos).toString());

        // Asignamos al dialogo un boton para usar el objeto.
        alertDialog.setPositiveButton(R.string.use, (dialog, which) -> {
            // Lanzamos un Toast que indique que hemos usado el item y actualizamos el adapter por
            // si hemos consumido completamente el objeto
            Toast.makeText(getContext(), hero.useItem(pos), Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();
        });
        // Asignamos al dialogo un boton para cerrarlo.
        alertDialog.setNegativeButton(R.string.close, (dialog, which) -> {
            dialog.dismiss();
        });


        // Creamos el dialogo, hacemos que no se pueda cancelar pulsando fuera y lo mostramos.
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }
}