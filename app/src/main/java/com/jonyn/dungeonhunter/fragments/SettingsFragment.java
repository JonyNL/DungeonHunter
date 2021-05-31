package com.jonyn.dungeonhunter.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.jonyn.dungeonhunter.DbUtils;
import com.jonyn.dungeonhunter.R;
import com.jonyn.dungeonhunter.models.Hero;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    private String usrUID;
    private int pos;
    private Hero hero;

    private ImageButton ibSettClose;
    private Button btnSettSignOut;
    private Button btnHeroSelect;
    private Button btnDeleteHero;
    private SeekBar sbMusicVol;
    private SeekBar sbSfxVol;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param usrUID Id del usuario en FireBase.
     * @param pos Posicion del heroe en la lista.
     * @param hero Heroe actual del usuario.
     * @return A new instance of fragment SettingsFragment.
     */
    public static SettingsFragment newInstance(String usrUID, int pos, Hero hero) {
        SettingsFragment fragment = new SettingsFragment();
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
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        // Recogemos el bundle y buscamos los atributos que necesitamos
        Bundle b = getArguments();
        assert b != null;
        usrUID = b.getString(DbUtils.FB_USER_UID);
        pos = b.getInt(DbUtils.HERO_POS);
        hero = (Hero) b.getSerializable(DbUtils.HERO);

        // Buscamos los componentes de la vista y los asignamos a sus variables correspondientes
        ibSettClose = v.findViewById(R.id.ibSettClose);
        btnSettSignOut = v.findViewById(R.id.btnSettSignOut);
        btnHeroSelect = v.findViewById(R.id.btnHeroSelect);
        btnDeleteHero = v.findViewById(R.id.btnDeleteHero);
        sbMusicVol = v.findViewById(R.id.sbMusicVol);
        sbSfxVol = v.findViewById(R.id.sbSfxVol);

        // Devolvemos la vista
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        assert getFragmentManager() != null;

        // Asignamos un listener al click del boton
        ibSettClose.setOnClickListener(v -> {
            // Cargamos un DungeonFragment y lo lanzamos
            DungeonFragment fragment = DungeonFragment.newInstance(usrUID, pos, hero);
            DbUtils.loadFragment(getFragmentManager(), fragment);
        });

        // Asignamos el progreso de las seekbars al valor del volumen guardado en DbUtils * 100
        sbMusicVol.setProgress((int) (DbUtils.getMusicVol()*100));
        sbSfxVol.setProgress((int) (DbUtils.getSfxVol()*100));

        // Asignamos listeners a las seekbars para cuando las manipulemos
        sbMusicVol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Cuando cambiamos el progreso, cogemos el valor actual, convertido en float, y lo
                // dividimos entre 100 para asignarlo a las variables correspondientes
                float vol = (float) progress / 100;
                DbUtils.setMusicVol(vol);
                DbUtils.getMediaPlayer().setVolume(vol, vol);
            }
            // region metodos no utilizados
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
            // endregion
        });
        sbSfxVol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Cuando cambiamos el progreso, cogemos el valor actual, convertido en float, y lo
                // dividimos entre 100 para asignarlo a las variables correspondientes
                float vol = (float) progress / 100;
                DbUtils.setSfxVol(vol);
            }
            // region metodos no utilizados
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
            // endregion
        });

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

        // Creamos el listener para el click de los botones
        View.OnClickListener listener = v -> {
            switch (v.getId()) {
                case R.id.btnSettSignOut:
                    // Si seleccionamps el boton de cerrar sesion, creamos una nueva instancia de
                    // MainFragment avisando que estamos cerrando sesion, limpiamos la lista de
                    // heroes local y cargamos el fragment
                    MainFragment mFragment = MainFragment.newInstance(true);
                    DbUtils.clearHeroList();
                    DbUtils.loadFragment(getFragmentManager(), mFragment);
                    break;
                case R.id.btnDeleteHero:
                    // Si decidimos eliminar un heroe, asignamos un valor null a su posicion,
                    // actualizamos la lista de la base de datos, avisamos al usuario y saltamos
                    // a la siguiente opcion
                    DbUtils.updateHeroPos(pos, null);
                    DbUtils.updateDbHeroList(usrUID);
                    Toast.makeText(getContext(), "Hero "+ hero.getName() + " removed",
                            Toast.LENGTH_SHORT).show();
                case R.id.btnHeroSelect:
                    // En esta opcion cargamos un HeroSelectFragment con el usrUID del usuario
                    // actual
                    HeroSelectFragment heroSelectFragment = HeroSelectFragment.newInstance(usrUID);
                    DbUtils.loadFragment(getFragmentManager(), heroSelectFragment);
                    break;
            }
        };

        // Asignamos los listeners a los botones
        btnSettSignOut.setOnClickListener(listener);
        btnSettSignOut.setOnTouchListener(tListener);
        btnHeroSelect.setOnClickListener(listener);
        btnHeroSelect.setOnTouchListener(tListener);
        btnDeleteHero.setOnClickListener(listener);
        btnDeleteHero.setOnTouchListener(tListener);
    }
}