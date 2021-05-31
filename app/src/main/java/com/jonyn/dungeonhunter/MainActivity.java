package com.jonyn.dungeonhunter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;

import com.jonyn.dungeonhunter.fragments.MainFragment;

public class MainActivity extends AppCompatActivity {
    public static String TAG = "MAIN_ACTIVITY";
    private final Handler mHandler = new Handler();

    /**Runnable que gestiona el comportamiento de la interfaz del sistema*/
    private final Runnable mHideRunnable = this::hideSystemUI;

    private FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            DbUtils.setMediaPlayer(MediaPlayer.create(getApplicationContext(), R.raw.rollinghard));
            DbUtils.getMediaPlayer().setLooping(true);
            DbUtils.getMediaPlayer().start();


        DbUtils.loadSounds(this);

        setContentView(R.layout.activity_main);

        container = findViewById(R.id.container);

        // Guardamos el fragment manager para asignar el nuevo fragment que vamos a crear.
        FragmentManager manager = getSupportFragmentManager();

        // Creamos un MainFragment pasandole dos Strings vacios como parametros.
        Fragment fragment = MainFragment.newInstance(false);

        // A traves del FragmentManager que guardamos antes, realizamos la transaccion
        // del fragment
        String mainFragment = fragment.getClass().getName();
        manager.beginTransaction()
                .replace(R.id.container, fragment, mainFragment)
                .commit();

        // Ocultamos la UI del dipositivo.
        hideSystemUI();

        // Cada vez que se deslice para mostrar la UI del dispositivo, llamamos al metodo
        // delayedHide() para ocultarla automaticamente.
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener
                (visibility -> {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        delayedHide();
                    }
                });

    }
    /**Oculta la UI del dispositivo 2000 ms desde la llamada*/
    private void delayedHide() {
        mHandler.removeCallbacks(mHideRunnable);
        mHandler.postDelayed(mHideRunnable, 2000);
    }

    /**
     * Metodo para ocultar la UI del dispositivo
     *
     * */
    private void hideSystemUI() {
        int uiOptions;
        uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);
    }

    /**
     * sobreescribimos el metodo para cerrar la app en caso de pulsar back
     * */
    @Override
    public void onBackPressed() {
        // Hacemos que al pulsar back en cualquier parte de la app salga un dialogo que nos
        // pregunte si queremos cerrar la app, y en caso positivo la cerramos
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    MainActivity.this.finish();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Close app?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (DbUtils.getMediaPlayer() == null) {
            DbUtils.setMediaPlayer(MediaPlayer.create(getApplicationContext(), R.raw.rollinghard));
            DbUtils.getMediaPlayer().setLooping(true);
            DbUtils.getMediaPlayer().start();
        }
    }

    public void onPause() {
        super.onPause();
        DbUtils.getMediaPlayer().stop();
        DbUtils.getMediaPlayer().release();
        DbUtils.setMediaPlayer(null);
    }
}