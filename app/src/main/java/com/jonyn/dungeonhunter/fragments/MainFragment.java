package com.jonyn.dungeonhunter.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.jonyn.dungeonhunter.DbUtils;
import com.jonyn.dungeonhunter.R;

import java.util.Objects;

/**
 * Fragment inicial para iniciar sesion y comenzar el juego.
 *
 * */
public class MainFragment extends Fragment {
    // Etiqueta del fragment.
    public static final String TAG = "MAIN_FRAGMENT";

    private static final int RC_SIGN_IN = 9001;

    // Metodos para gestionar el login a traves de Google a Firebase.
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private boolean signInFromStart = false;
    private Bundle b;

    // Componentes de la vista.
    private TextView tvUser;
    private Button btnStart;
    private Button btnSignInOut;


    /** Constructor vacio por si es necesario */
    public MainFragment() {
    }

    /**
     * Metodo para crear una instancia de MainFragment.
     * @param signOut Boolean que indica si venimos de cerrar sesion o por abrir la app
     *
     * @return Una nueva instancia de MainFragment.
     */
    public static MainFragment newInstance(boolean signOut) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putBoolean(DbUtils.USER_LOGOUT, signOut);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflamos la vista con el layout de batalla y la guardamos en una variable.
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        // Buscamos en la vista los diferentes componentes y los asignamos a las variables.
        tvUser = v.findViewById(R.id.tvUser);
        btnStart = v.findViewById(R.id.btnStart);
        btnSignInOut = v.findViewById(R.id.btnSignInOut);

        // Devolvemos la vista.
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializamos Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(Objects.requireNonNull(getActivity()), gso);

        b = getArguments();
        assert b != null;
        if (b.getBoolean(DbUtils.USER_LOGOUT))
            signOut();
        else {
            // Check for existing Google Sign In account, if the user is already signed in
            // the GoogleSignInAccount will be non-null.
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
            if (account == null){
                signInFromStart = false;
                signIn();
            }
            else {
                Toast.makeText(getActivity(), "Already signed in", Toast.LENGTH_SHORT).show();
                firebaseAuthWithGoogle(account);
            }
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onStart() {
        super.onStart();
        // Recibimos el usuario abierto en Firebase, o null en caso que no lo haya.
        DbUtils.loadEnemies();
        assert b != null;
        if (b.getBoolean(DbUtils.USER_LOGOUT)){
            tvUser.setVisibility(View.VISIBLE);
            btnSignInOut.setVisibility(View.VISIBLE);
            btnStart.setVisibility(View.VISIBLE);
        }

        // Si el usuario no es null mostramos el nombre en un TextView y cargamos los heroes.
        if (currentUser != null) {
            tvUser.setText(currentUser.getDisplayName());
            if (DbUtils.getHeroes().isEmpty())
                DbUtils.loadHeroList(getContext(), currentUser.getUid(), signInFromStart, null);
        }

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

        // Asignamos un listener al boton Start para ir a la seleccion de heroe.
        btnStart.setOnClickListener(v -> {

            // Comprobamos que haya un usuario logueado.
            if (currentUser != null){
                launchFragment();
            } else {
                // En caso contrario, abriremos el dialogo de inicio de sesion.
                signInFromStart = true;
                signIn();
            }
        });
        // Asignamos el onTouchListener
        btnStart.setOnTouchListener(tListener);

        // Agregamos al boton btnSignInOut un listener para cerrar o abrir la sesion.
        btnSignInOut.setOnClickListener(v -> {
            // al hacer click en el boton cerrara la sesion abierta tanto en Firebase como en Google.
            // Comprobamos si hay un usuario logueado
           if (mAuth.getCurrentUser() == null) {
               // Si no lo hay llamamos a signIn(), indicando que no es desde el boton Start
               signInFromStart = false;
               signIn();
           } else {
               // En caso contrario llamamos a signOut() y vaciamos la lista de heroes
                signOut();
                DbUtils.clearHeroList();
           }
        });
        // Asignamos el onTouchListener
        btnSignInOut.setOnTouchListener(tListener);
    }

    /** Recibimos la respuesta del login. */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            tvUser.setVisibility(View.VISIBLE);
            btnSignInOut.setVisibility(View.VISIBLE);
            btnStart.setVisibility(View.VISIBLE);
        }
    }

    /** Metodo que comprueba el usuario en Firebase. */
    // region [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(Objects.requireNonNull(getActivity()), task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        currentUser = mAuth.getCurrentUser();
                        assert currentUser != null;
                        if (DbUtils.getHeroes().isEmpty()){
                            DbUtils.loadHeroList(getContext(), currentUser.getUid(),
                                    signInFromStart, getFragmentManager());
                        }
                        tvUser.setText(currentUser.getDisplayName());
                        btnSignInOut.setText(R.string.sign_out);
                        tvUser.setVisibility(View.VISIBLE);
                        btnSignInOut.setVisibility(View.VISIBLE);
                        btnStart.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(), "Sign in Success", Toast.LENGTH_SHORT).show();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        signOut();
                        tvUser.setVisibility(View.VISIBLE);
                        btnSignInOut.setVisibility(View.VISIBLE);
                        btnStart.setVisibility(View.VISIBLE);
                    }
                });
    }
    // endregion [END auth_with_google]

    /**
     * Metodo para iniciar sesion.
     *
     * */
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * Metodo para cerrar la sesion.
     *
     * */
    private void signOut(){
        // Firebase sign out
        mAuth.signOut();
        currentUser = null;

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(Objects.requireNonNull(getActivity()),
                task -> {
                    Toast.makeText(getActivity(), "Signed Out", Toast.LENGTH_SHORT).show();
                    tvUser.setText(R.string.no_user);
                    btnSignInOut.setText(R.string.common_signin_button_text);
                });
    }

    /**
     * Metodo para lanzar un HeroSelectFragment
     *
     * */
    private void launchFragment(){
        // Creamos un BattleFragment pasandole como parametros el heroe y el enemigo.
        Fragment fragment = HeroSelectFragment.newInstance(currentUser.getUid());

        // Lanzamos el fragment
        assert getFragmentManager() != null;
        DbUtils.loadFragment(getFragmentManager(), fragment);
    }
}
