package com.jonyn.dungeonhunter.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.jonyn.dungeonhunter.DbUtils;
import com.jonyn.dungeonhunter.R;
import com.jonyn.dungeonhunter.models.Hero;

import java.util.Arrays;
import java.util.List;

import static com.jonyn.dungeonhunter.DbUtils.FB_USER_UID;
import static com.jonyn.dungeonhunter.DbUtils.HERO_POS;


/**
 *
 * Metodo para agregar un nuevo heroe a la lista del usuario en una posicion especifica
 */
public class NewHeroFragment extends Fragment {

    // Variables de utilidad
    private String usrUID;
    private int heroPos;

    // Componentes de la vista
    private EditText etHeroName;
    private Spinner spClasses;
    private Button btnConfirm;
    private TextView tvLpStat;
    private TextView tvMpStat;
    private TextView tvStrStat;
    private TextView tvDefStat;
    private TextView tvIntelStat;
    private TextView tvAgStat;
    private TextView tvLckStat;
    private ImageView ivNewHero;
    private ImageButton ibNhClose;

    public NewHeroFragment() {
        // Required empty public constructor
    }

    /**
     * Instancia para crear un nuevo heroe para la lista del usuario en una posicion especifica.
     *
     * @param usrUID ID del usuario.
     * @param pos Posicion del heroe en la lista.
     * @return Una nueva instancia de NewHeroFragment.
     */
    public static NewHeroFragment newInstance(String usrUID, int pos) {
        NewHeroFragment fragment = new NewHeroFragment();
        Bundle args = new Bundle();
        args.putString(FB_USER_UID, usrUID);
        args.putInt(HERO_POS, pos);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflamos la vista y la guardamos en una variable
        View v = inflater.inflate(R.layout.fragment_new_hero, container, false);

        // Recogemos el bundle y buscamos los atributos que necesitamos
        Bundle b = getArguments();
        assert b != null;
        usrUID = b.getString(FB_USER_UID);
        heroPos = b.getInt(HERO_POS);

        // Buscamos los componentes de la vista y los asignamos a sus variables correspondientes
        etHeroName = v.findViewById(R.id.etHeroName);
        spClasses = v.findViewById(R.id.spClasses);
        btnConfirm = v.findViewById(R.id.btnConfirm);
        tvStrStat = v.findViewById(R.id.tvStrStat);
        tvDefStat = v.findViewById(R.id.tvDefStat);
        tvIntelStat = v.findViewById(R.id.tvIntelStat);
        tvAgStat = v.findViewById(R.id.tvAgStat);
        tvLckStat = v.findViewById(R.id.tvLckStat);
        tvLpStat = v.findViewById(R.id.tvLpStat);
        tvMpStat = v.findViewById(R.id.tvMpStat);
        ivNewHero = v.findViewById(R.id.ivNewHero);
        ibNhClose = v.findViewById(R.id.ibNhClose);

        // Devolvemos la lista
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        ibNhClose.setOnClickListener(v -> {

            // Cuando el usuario le de click al boton, lanzamos una HeroSelectFragment.
            FragmentManager manager = getFragmentManager();

            HeroSelectFragment fragment = HeroSelectFragment.newInstance(usrUID);
            assert manager != null;
            DbUtils.loadFragment(manager, fragment);
        });

        // Creamos una lista que muestra
        List<String> classesList = Arrays.asList(Hero.HeroClass.getNames(Hero.HeroClass.class));

        // Adaptador para el spinner con los distintos tipos de personajes disponibles
        final ArrayAdapter<String> spAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, classesList);
        // Asignamos el recurso para el dropDown del adapter y lo asignamos al spinner
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spClasses.setAdapter(spAdapter);

        // Asignamos un listener al seleccionar un item
        spClasses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Comprobamos el valor del item seleccionado y asignamos los valores
                // correspondientes
                switch ((String)spClasses.getSelectedItem()) {
                    case "WARRIOR":
                        tvLpStat.setText(getString(R.string.lp_stat, "100", "100"));
                        tvMpStat.setText(getString(R.string.mp_stat, "20", "20"));
                        tvStrStat.setText(getString(R.string.str_stat, "8"));
                        tvDefStat.setText(getString(R.string.def_stat, "5"));
                        tvIntelStat.setText(getString(R.string.intel_stat, "N/A"));
                        tvAgStat.setText(getString(R.string.ag_stat, "6"));
                        tvLckStat.setText(getString(R.string.lck_stat, "8"));
                        break;
                    case "WIZARD":
                        tvLpStat.setText(getString(R.string.lp_stat, "100", "100"));
                        tvMpStat.setText(getString(R.string.mp_stat, "20", "20"));
                        tvStrStat.setText(getString(R.string.str_stat, "3"));
                        tvDefStat.setText(getString(R.string.def_stat, "2"));
                        tvIntelStat.setText(getString(R.string.intel_stat, "2"));
                        tvAgStat.setText(getString(R.string.ag_stat, "8"));
                        tvLckStat.setText(getString(R.string.lck_stat, "10"));
                        break;

                }
                // Asignamos la imagen correspondiente a la clase seleccionada
                ivNewHero.setImageResource(
                        getResources().getIdentifier(((String) spClasses.getSelectedItem())
                                        .toLowerCase(), "drawable",
                                getContext().getPackageName()));
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // En este metodo por ahora no nos interesa realizar ninguna accion especifica
            }
        });

        // Asignamos listener a btnConfirm
        btnConfirm.setOnClickListener(v -> {
            // Creamos Strings que guarden el nombre y la clase seleccionada
            String name = etHeroName.getText().toString();
            String hClass = spClasses.getSelectedItem().toString();

            // comprobamos la longitud del nombre introducido para el heroe
            if (name.length() < 3) {
                // Si es inferior a 3, indicamos que el nombre tiene que ser mayor a traves de un
                // error del EditText
                etHeroName.setError("Name must be 3 characters or more");
            } else {
                // Si tiene 3 caracteres o mas, lanzamos el heroSelectFragment indicandole los
                // parametros necesarios para crear el nuevo heroe
                assert getFragmentManager() != null;
                DbUtils.loadFragment(getFragmentManager(),
                        HeroSelectFragment.newInstance(usrUID, heroPos, name, hClass));
            }
        });
    }
}
