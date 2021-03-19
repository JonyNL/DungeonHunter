package com.jonyn.dungeonhunter.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jonyn.dungeonhunter.R;
import com.jonyn.dungeonhunter.models.Enemy;
import com.jonyn.dungeonhunter.models.Warrior;

/**
 * Fragment inicial para iniciar sesion y comenzar el juego.
 *
 * */
public class MainFragment extends Fragment {
    // Etiqueta del fragment.
    public static final String TAG = "MAIN_FRAGMENT";

    private static final int RC_SIGN_IN = 9001;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // Metodos para gestionar el login a traves de Google a Firebase.
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // Elementos del layout.
    private TextView tvUser;
    private Button btnStart;
    private Button btnSignOut;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /** Metodo vacio por si es necesario */
    public MainFragment() {
    }

    /**
     * Metodo para crear una instancia de MainFragment con los parametros especificados.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return Una nueva instancia de MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // Inicializamos Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (account== null)
            signIn();
        else Toast.makeText(getActivity(), "Already signed in", Toast.LENGTH_SHORT).show();

    }

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

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(),
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getActivity(), "Signed Out", Toast.LENGTH_SHORT).show();
                            tvUser.setText("No active User");
                    }
                });
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
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    /** Metodo que comprueba el usuario en Firebase. */
    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            tvUser.setText(user.getDisplayName());
                            Toast.makeText(getActivity(), "Sign in Success", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }
    // [END auth_with_google]


    @Override
    public void onStart() {
        super.onStart();
        // Recibimos el usuario abierto en Firebase, o null en caso que no lo haya.
        final FirebaseUser currentUser = mAuth.getCurrentUser();

        // Si el usuario no es null mostramos el nombre en un TextView.
        if (currentUser!= null)
        tvUser.setText(currentUser.getDisplayName());

        // Asignamos un listener al boton Start para ir a una batalla.
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Comprobamos que haya un usuario logueado.
                if (mAuth.getCurrentUser() != null){

                    db = FirebaseFirestore.getInstance();

                    // En caso afirmativo, guardamos el fragment manager para asignar el nuevo
                    // fragment que vamos a crear.
                    FragmentManager manager = getFragmentManager();

                    // Creamos un BattleFragment pasandole como parametros el heroe y el enemigo.
                    Fragment fragment = HeroSelectFragment.newInstance(currentUser.getUid());

                    // A traves del FragmentManager que guardamos antes, realizamos la transaccion
                    // del fragment agregandolo al BackStack.
                    String newFragment = fragment.getClass().getName();
                    manager.beginTransaction()
                            .replace(R.id.container, fragment, newFragment)
                            .addToBackStack(newFragment)
                            .commit();

                    /*// En caso afirmativo, guardamos el fragment manager para asignar el nuevo
                    // fragment que vamos a crear.
                    FragmentManager manager = getFragmentManager();

                    // Creamos un BattleFragment pasandole como parametros el heroe y el enemigo.
                    Fragment fragment = BattleFragment.newInstance(new Warrior(mAuth.getCurrentUser()
                            .getDisplayName()), new Enemy("Kappa", Enemy.Types.DEMON));

                    // A traves del FragmentManager que guardamos antes, realizamos la transaccion
                    // del fragment agregandolo al BackStack.
                    String battleFragment = fragment.getClass().getName();
                    manager.popBackStack();
                    manager.beginTransaction()
                            .replace(R.id.container, fragment, battleFragment)
                            .addToBackStack(battleFragment)
                            .commit();*/
                } else
                    // En caso contrario, abriremos el dialogo de inicio de sesion.
                    signIn();
            }
        });

        // Agregamos al boton btnSignOut un listener para cerrar la sesion.
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // al hacer click en el boton cerrara la sesion abierta
                // tanto en Firebase como en Google.
                // Comprobamos si hay un usuario logueado, y solo si no lo hay llamamos a signIn().
               if (mAuth.getCurrentUser()== null)
                   signIn();
               else
                    signOut();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflamos la vista con el layout de batalla y la guardamos en una variable.
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        // Buscamos en la vista los diferentes componentes y los asignamos a las variables.
        tvUser = v.findViewById(R.id.tvUser);
        btnStart = v.findViewById(R.id.btnStart);
        btnSignOut = v.findViewById(R.id.btnSignOut);

        // Devolvemos la vista.
        return v;
    }
}
