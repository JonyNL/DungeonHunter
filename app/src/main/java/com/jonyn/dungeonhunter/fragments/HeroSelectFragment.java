package com.jonyn.dungeonhunter.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.jonyn.dungeonhunter.DbUtils;
import com.jonyn.dungeonhunter.R;
import com.jonyn.dungeonhunter.models.DungeonProgress;
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

    // Variables de utilidad
    private String usrUID;

    // Componentes del Layout
    private View heroSel1;
    private View heroSel2;
    private View heroSel3;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_hero_select, container, false);

        // Buscamos las vistas y las asignamos a sus variables correspondientes
        heroSel1 = v.findViewById(R.id.heroSel1);
        heroSel2 = v.findViewById(R.id.heroSel2);
        heroSel3 = v.findViewById(R.id.heroSel3);

        // Recibimos los argumentos del fragment, comprobamos que no sea null y asignamos el UID
        // del usuario
        Bundle b = getArguments();
        assert b != null;
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
        }

        // Devolvemos la lista
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Recibimos el listado de heroes desde la clase DbUtils, que ya los debe haber cargado y
        // actualizamos el texto de los botones de heroes
        heroes = DbUtils.getHeroes();
        updateButtonsMsg();

        // Creamos un listener que asinaremos a los botones de los heroes
        View.OnClickListener listener = v -> {
            switch (v.getId()){
                case R.id.heroSel1:
                    checkButtonHero(0);
                    break;
                case R.id.heroSel2:
                    checkButtonHero(1);
                    break;
                case R.id.heroSel3:
                    checkButtonHero(2);
                    break;
            }
        };

        // Asignamos el listener a las vistas
        heroSel1.setOnClickListener(listener);
        heroSel2.setOnClickListener(listener);
        heroSel3.setOnClickListener(listener);
    }

    /**
     * Metodo en el que comprobamos el heroe seleccionado para lanzar el siguiente fragment
     *
     * @param pos Posicion en la lista del heroe seleccionado
     * */
    private void checkButtonHero(int pos) {
        // Guardamos el FragmentManager y comprobamos que no sea null
        FragmentManager manager = getFragmentManager();
        assert manager != null;
        Fragment fragment;
        if (heroes.get(pos) != null && !heroes.isEmpty() && heroes.size() >= pos+1){
            // Asignamos un DungeonFragment pasandole como parametros el usrUID, heroe y su
            // posicion en la lista.
            fragment = DungeonFragment.newInstance(usrUID, pos, heroes.get(pos));
        } else {
            // Asignamos un BattleFragment a fragment pasandole como parametros el heroe y el
            // enemigo.
            fragment = NewHeroFragment.newInstance(usrUID, pos);

        }
        // A traves del FragmentManager que guardamos antes, realizamos la transaccion
        // del fragment.
        DbUtils.loadFragment(manager, fragment);
    }

    /**
     * Metodo que actualiza el texto de los botones de los heroes
     *
     * */
    private void updateButtonsMsg(){
        // Comprobamos si el usuario tiene heroes, y asignamos la informacion de cada uno a la vista
        // que le corresponde.
        if (!heroes.isEmpty()) {
            // Recorremos el listado de heroes y comrpobamos
            for (int i = 0; i < heroes.size(); i++) {
                // Usamos un switch para identificar que vista es la que tenemos que editar
                switch (i) {
                    case 0:
                        if (heroes.get(i)!= null) {
                            // Si el heroe no es nulo, recogemos la vista, buscamos sus componentes
                            // y asignamos sus valores correspondientes
                            heroSel1.findViewById(R.id.llHeroView).setVisibility(View.VISIBLE);
                            heroSel1.findViewById(R.id.tvListProgress).setVisibility(View.VISIBLE);
                            heroSel1.findViewById(R.id.tvNoHero).setVisibility(View.GONE);
                            TextView tvListHeroName =  heroSel1.findViewById(R.id.tvListHeroName);
                            TextView tvListHeroLvl =  heroSel1.findViewById(R.id.tvListHeroLvl);
                            TextView tvListProgress =  heroSel1.findViewById(R.id.tvListProgress);
                            tvListHeroName.setText(heroes.get(i).getName());
                            tvListHeroLvl.setText(getString(R.string.level, heroes.get(i).getLvl()));
                            DungeonProgress progress = heroes.get(i).getDungeonProgress();
                            tvListProgress.setText(getString(R.string.floor_stage_run_hsel,
                                    progress.getRun(), progress.getFloor(), progress.getStagePos()));

                        }
                        break;
                    case 1:
                        if (heroes.get(i)!= null) {
                            // Si el heroe no es nulo, recogemos la vista, buscamos sus componentes
                            // y asignamos sus valores correspondientes
                            heroSel2.findViewById(R.id.llHeroView).setVisibility(View.VISIBLE);
                            heroSel2.findViewById(R.id.tvListProgress).setVisibility(View.VISIBLE);
                            heroSel2.findViewById(R.id.tvNoHero).setVisibility(View.GONE);
                            TextView tvListHeroName =  heroSel2.findViewById(R.id.tvListHeroName);
                            TextView tvListHeroLvl =  heroSel2.findViewById(R.id.tvListHeroLvl);
                            TextView tvListProgress =  heroSel2.findViewById(R.id.tvListProgress);
                            tvListHeroName.setText(heroes.get(i).getName());
                            tvListHeroLvl.setText(getString(R.string.level, heroes.get(i).getLvl()));
                            DungeonProgress progress = heroes.get(i).getDungeonProgress();
                            tvListProgress.setText(getString(R.string.floor_stage_run_hsel,
                                    progress.getRun(), progress.getFloor(), progress.getStagePos()));
                        }
                        break;
                    case 2:
                        if (heroes.get(i)!= null){
                            // Si el heroe no es nulo, recogemos la vista, buscamos sus componentes
                            // y asignamos sus valores correspondientes
                            heroSel3.findViewById(R.id.llHeroView).setVisibility(View.VISIBLE);
                            heroSel3.findViewById(R.id.tvListProgress).setVisibility(View.VISIBLE);
                            heroSel3.findViewById(R.id.tvNoHero).setVisibility(View.GONE);
                            TextView tvListHeroName =  heroSel3.findViewById(R.id.tvListHeroName);
                            TextView tvListHeroLvl =  heroSel3.findViewById(R.id.tvListHeroLvl);
                            TextView tvListProgress =  heroSel3.findViewById(R.id.tvListProgress);
                            tvListHeroName.setText(heroes.get(i).getName());
                            tvListHeroLvl.setText(getString(R.string.level, heroes.get(i).getLvl()));
                            DungeonProgress progress = heroes.get(i).getDungeonProgress();
                            tvListProgress.setText(getString(R.string.floor_stage_run_hsel,
                                    progress.getRun(), progress.getFloor(), progress.getStagePos()));
                        }
                        break;
                }
            }
        } else
            // Si la lista esta vacia, avisamos con un Toast
            Toast.makeText(getContext(), "You have no heroes.", Toast.LENGTH_SHORT).show();
    }

}
