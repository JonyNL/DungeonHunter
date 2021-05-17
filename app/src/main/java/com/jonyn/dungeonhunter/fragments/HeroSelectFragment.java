package com.jonyn.dungeonhunter.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.jonyn.dungeonhunter.DbUtils;
import com.jonyn.dungeonhunter.R;
import com.jonyn.dungeonhunter.models.Hero;
import com.jonyn.dungeonhunter.models.Warrior;
import com.jonyn.dungeonhunter.models.Wizard;

import java.util.ArrayList;
import java.util.List;

import static com.jonyn.dungeonhunter.DbUtils.FB_USER_UID;
import static com.jonyn.dungeonhunter.DbUtils.HERO_CLASS;
import static com.jonyn.dungeonhunter.DbUtils.HERO_NAME;
import static com.jonyn.dungeonhunter.DbUtils.HERO_POS;

/**
 * Fragment seleccion de heroe
 *
 */
public class HeroSelectFragment extends Fragment {
    public static final String TAG = "HERO_SELECT_FRAGMENT";

    private String usrUID;

    // Elementos del Layout
    private Button btnHero1;
    private Button btnHero2;
    private Button btnHero3;

    // Lista de heroes del usuario
    private List<Hero> heroes = new ArrayList<>(3);

    // Constructor vacio
    public HeroSelectFragment() {}

    /**
     * Metodo para crear una instancia de HeroSelectFragment desde MainFragment.
     *
     * @param userID ID del usuario logueado.
     * @return una nueva instancia de HeroSelectFragment.
     * */
    public static HeroSelectFragment newInstance(String userID) {

        Bundle args = new Bundle();
        args.putString(FB_USER_UID, userID);
        HeroSelectFragment fragment = new HeroSelectFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Metodo para crear una instancia de HeroSelectFragment desde NewHeroFragment.
     *
     * @param userID ID del usuario logueado.
     * @param pos Posicion en la lista del nuevo heroe.
     * @param hName Nombre del nuevo heroe.
     * @param hClass Clase del nuevo heroe.
     * @return una nueva instancia de HeroSelectFragment.
     * */
    public static HeroSelectFragment newInstance(String userID, int pos, String hName, String hClass) {

        Bundle args = new Bundle();
        args.putString(FB_USER_UID, userID);
        args.putInt(HERO_POS, pos);
        args.putString(HERO_NAME, hName);
        args.putString(HERO_CLASS, hClass);
        HeroSelectFragment fragment = new HeroSelectFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Recibimos el listado de heroes desde la clase DbUtils, que ya los debe haber cargado y
        // actualizamos el texto de los botones de heroes
        heroes = DbUtils.getHeroes();
        updateButtonsMsg();

        // Creamos un listener que asinaremos a los botones de los heroes
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btnHero1:
                        checkButtonHero(0);
                        break;
                    case R.id.btnHero2:
                        checkButtonHero(1);
                        break;
                    case R.id.btnHero3:
                        checkButtonHero(2);
                        break;
                }
            }
        };

        // Asignamos el listener a los botones
        btnHero1.setOnClickListener(listener);
        btnHero2.setOnClickListener(listener);
        btnHero3.setOnClickListener(listener);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_hero_select, container, false);

        // Asignamos las variables de los botones a sus vistas correspondientes
        btnHero1 = v.findViewById(R.id.btnHero1);
        btnHero2 = v.findViewById(R.id.btnHero2);
        btnHero3 = v.findViewById(R.id.btnHero3);

        // Recibimos los argumentos del fragment y asignamos el UID del usuario
        Bundle b = getArguments();
        usrUID = b.getString(FB_USER_UID);

        // Comprobamos si aparte del usrUID recibimos parametros para un nuevo heroe, en cuyo caso
        // debemos agregarlo al listado
        int hPos = b.getInt(HERO_POS);
        String hName = b.getString(HERO_NAME);
        String hClass = b.getString(HERO_CLASS);

        // En caso de tenerlos, comprobamos la clase del heroe recibido y lo creamos
        if (hClass!= null && hName != null){
            Hero h;
            switch (hClass) {
                case "WARRIOR":
                    h = new Warrior(hName);
                    break;
                case "WIZARD":
                    h = new Wizard(hName);
                    break;
                default:
                    h = null;
            }

            // Si ha ido bien y tenemos el heroe, lo agregamos a la lista y la actualizamos
            if (h!= null){
                DbUtils.updateHeroPos(hPos, h);
                DbUtils.updateDbHeroList(usrUID);
            }

            Log.i(TAG, h.toString());

        }

        return v;
    }

    /**
     * Metodo en el que comprobamos el heroe seleccionado para lanzar el siguiente fragment
     *
     * @param pos Posicion en la lista del heroe seleccionado
     * */
    private void checkButtonHero(int pos) {
        FragmentManager manager = getFragmentManager();
        assert manager != null;
        if (heroes.get(pos) != null && !heroes.isEmpty() && heroes.size() >= pos+1){
            // Creamos un DungeonFragment pasandole como parametros el usrUID, heroe y su posicion
            // en la lista.
            Fragment fragment = DungeonFragment.newInstance(usrUID, pos, heroes.get(pos));

            // A traves del FragmentManager que guardamos antes, realizamos la transaccion
            // del fragment.
            DbUtils.loadFragment(manager, fragment);
        } else {
            Toast.makeText(getContext(), "No hero in Slot", Toast.LENGTH_SHORT).show();


            // Creamos un BattleFragment pasandole como parametros el heroe y el enemigo.
            Fragment fragment = NewHeroFragment.newInstance(usrUID, pos);

            // A traves del FragmentManager que guardamos antes, realizamos la transaccion
            // del fragment.
            DbUtils.loadFragment(manager, fragment);
        }
    }
    /**
     * Metodo que actualiza el texto de los botones de los heroes
     *
     * */
    void updateButtonsMsg(){
        // Comprobamos si el usuario tiene heroes, y asignamos su nombre al boton
        // que le corresponde.
        if (!heroes.isEmpty()) {
            btnHero1.setText(getString(R.string.slot));
            btnHero2.setText(getString(R.string.slot));
            btnHero3.setText(getString(R.string.slot));
            for (int i = 0; i < heroes.size(); i++) {
                switch (i) {
                    case 0:
                        if (heroes.get(i)!= null)
                            btnHero1.setText(heroes.get(i).getName());
                        break;
                    case 1:
                        if (heroes.get(i)!= null)
                            btnHero2.setText(heroes.get(i).getName());
                        break;
                    case 2:
                        if (heroes.get(i)!= null)
                            btnHero3.setText(heroes.get(i).getName());
                        break;
                }
            }
        }else Toast.makeText(getContext(), "You have no heroes.", Toast.LENGTH_SHORT).show();
    }
}
