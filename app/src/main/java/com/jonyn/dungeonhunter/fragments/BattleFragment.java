package com.jonyn.dungeonhunter.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jonyn.dungeonhunter.AbilitiesAdapter;
import com.jonyn.dungeonhunter.DbUtils;
import com.jonyn.dungeonhunter.IAbilityListener;
import com.jonyn.dungeonhunter.R;
import com.jonyn.dungeonhunter.models.Ability;
import com.jonyn.dungeonhunter.models.Active;
import com.jonyn.dungeonhunter.models.Enemy;
import com.jonyn.dungeonhunter.models.Hero;
import com.jonyn.dungeonhunter.models.Potion;

import java.util.Objects;

import static com.jonyn.dungeonhunter.DbUtils.FB_USER_UID;
import static com.jonyn.dungeonhunter.DbUtils.HERO_POS;


/**
 * Fragment para gestionar las batallas
 *
 */
public class BattleFragment extends Fragment implements IAbilityListener {

    // Etiquetas para buscar los argumentos del fragment.
    private static final String HERO = "hero";
    private static final String ENEMY = "enemy";

    // Atributos del layout
    private TextView tvLog;
    private ImageButton ibLog;
    private Button btnAttack;
    private Button btnAbilities;
    private Button btnItems;
    private ProgressBar pbEnemyLP;
    private ProgressBar pbEnemyMP;
    private ProgressBar pbHeroLP;
    private ProgressBar pbHeroMP;
    private ScrollView scroll;
    private RecyclerView rvAbilities;
    private LinearLayout llHero;


    // Heroe y enemigo a enfrentarse
    private Hero hero;
    private Enemy enemy;
    String usrUID;
    int pos;
    IAbilityListener listener;

    // Variables auxiliares para la batalla
    private int round = 1;
    private boolean playerTurn = true;


    // Creamos un constructor vacio por si llega a ser necesario.
    public BattleFragment() {}

    /**
     * Metodo para crear una instancia de BattleFragment con los parametros especificados.
     *
     * @param hero Heroe que utiliza el jugador en la partida.
     * @param enemy Enemigo al que se enfrenta el jugador.
     * @return una nueva instancia de BattleFragment.
     * */
    public static BattleFragment newInstance(String usrUID, int pos, Hero hero, Enemy enemy) {
        BattleFragment fragment = new BattleFragment();
        Bundle args = new Bundle();
        args.putSerializable(HERO, hero);
        args.putSerializable(ENEMY, enemy);
        args.putString(FB_USER_UID, usrUID);
        args.putInt(HERO_POS, pos);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        listener = this;

        // TODO - Con el objetivo de testear, agregamos un item al inventario del heroe

        if (hero.getActives().isEmpty())
            hero.addActive(new Active("Healing", "Heals 30 LP", "LP+30",
                10, Ability.AbilityType.ACTIVE, Ability.EffectType.REGEN));

        if (hero.getInventory().isEmpty()) {
            hero.getInventory().add(new Potion("Life Potion",
                    "Potion that restores 20 points of your life points",
                    5, "LP+20", Potion.Types.LIFE_POTION, 20));
            hero.getInventory().add(new Potion("Elixir",
                    "Elixir that fills your LP and MP",
                    5, "LP/MP MAX", Potion.Types.ELIXIR, 0));
            hero.getInventory().add(new Potion("Mana Potion",
                    "Potion that restores 20 points of your mana points",
                    5, "MP+20", Potion.Types.MANA_POTION, 20));
        }
        /* Establecemos el valor maximo y actual de las barras que representan la salud y la
         *  energia de los personajes, tanto el heroe como el enemigo.*/

        // region [START progress_bars_assignment]
        // Establecemos el valor maximo de la vida del heroe.
        pbHeroLP.setMax(hero.getMaxLp());

        // Comprobamos si el SDK de adnroid es el 24 para realizar la asignacion del progreso de la
        // barra a traves del nuevo metodo.
        updateHeroLP();

        // Establecemos el valor maximo de la energia del heroe.
        pbHeroMP.setMax(hero.getMaxMp());

        // Realizamos la misma comprobacion que con la vida.
        updateHeroMP();

        // Establecemos la vida maxima del enemigo en la barra de progreso que la representa.
        pbEnemyLP.setMax(enemy.getMaxLp());

        // Realizaremos la misma comprobacion del SDK y las mismas operaciones.
        updateEnemyLP();

        // Asignamos la energia maxima del enemigo a la barra de progreso que la representa.
        pbEnemyMP.setMax(enemy.getMaxMp());

        // Realizaremos la misma comprobacion del SDK y las mismas operaciones.
        updateEnemyMP();
        // endregion [END progress_bars_assignment]

        // Vamos describiendo los acontecimientos en un TextView, encabezado por este texto
        tvLog.append("========"+hero.getName() + " VS " + enemy.getName()+"========\n");
        scroll.fullScroll(View.FOCUS_DOWN);

        // Empezamos la partida jugando un turno.
        playTurn();

        // Establecemos los listeners a los botones para realizar las acciones correspondientes.
        // region [START buttons_click_listeners]
        btnAttack.setOnClickListener(v -> {

            // Recogemos el valor del ataque del heroe y lo guardamos en una variable.
            String attackRes = hero.attack(enemy);

            // Agregamos al TextView que muestra el progreso del juego el resultado del ataque.
            tvLog.append(attackRes);
            scroll.fullScroll(View.FOCUS_DOWN);

            // Reducimos la vida del enemigo en un valor igual al del ataque del heroe.
            //enemy.setLp(enemy.getLp()-heroDmg);

            // Actualizamos la barra de salud del enemigo.
            updateEnemyLP();

            // Comprobamos si ambos personajes aun tienen vida.
            if (hero.getLp()>0 && enemy.getLp()>0){

                // En caso afirmativo, jugamos el turno y llamamos a la jugada del enemigo
                playTurn();
                enemyPlay();
            } else {

                // En caso contrario, llamamos al metodo gameEnd.
                gameEnd();
            }

        });

        btnAbilities.setOnClickListener(v -> {
            // Creamos un adaptador para el listado de habilidades
            AbilitiesAdapter adapter = new AbilitiesAdapter(hero.getActives());

            // Agregamos un listener al adaptador para poder seleccionar las habilidades
            adapter.setListener(v1 -> {
                if (listener!= null){
                    // Si el listener no es nulo, ejecutamos el metodo onAbilityClick()
                    listener.onAbilityClick(hero.getActives().get(rvAbilities.getChildAdapterPosition(v1)));

                    // Escondemos el listado de habilidades y mostramos otra vez el menu de acciones
                    rvAbilities.setVisibility(View.GONE);
                    llHero.setVisibility(View.VISIBLE);
                }
            });

            // Asignamos el adaptador al RecyclerView y le asignamos el LayoutManager
            rvAbilities.setAdapter(adapter);
            rvAbilities.setLayoutManager(new LinearLayoutManager(
                    getContext(), LinearLayoutManager.VERTICAL, false));

            // Escondemos el menu de acciones y mostramos el listado de habilidades
            rvAbilities.setVisibility(View.VISIBLE);
            llHero.setVisibility(View.GONE);

        });

        btnItems.setOnClickListener(v -> {

            // Usamos el objeto desde el inventario del heroe
            hero.useItem(0);

            // Jugamos el turno y llamamos a la jugada del enemigo.
            playTurn();
            enemyPlay();
        });

        // endregion [END buttons_click_listeners]
    }

    /**
     * Metodo para mostrar en el Log el avance de los turnos y rondas.
     *
     * */
    public void playTurn(){

        // Comprobamos si es el turno del jugador
        if (playerTurn) {
            // En caso afirmativo, mostramos la ronda y el nombre del jugador.
            tvLog.append("Round " + round + ": "+ hero.getName() + "'s turn.\n~~~~~~~~~~~~~~~~~~\n");

            // Hacemos el scroll hacia abajo para tener siempre a la vista lo ultimo ocurrido
            scroll.fullScroll(View.FOCUS_DOWN);

            // Establecemos la variable playerTurn a false, para que al llamar a playTurn() de nuevo
            // muestre la linea del enemigo.
            playerTurn = false;
        } else {
            // En caso contrario, se realiza lo mismo pero mostrando el nombre del enemigo.
            tvLog.append("Round " + round + ": "+ enemy.getName() + "'s turn.\n~~~~~~~~~~~~~~~~~~\n");
            scroll.fullScroll(View.FOCUS_DOWN);
            playerTurn = true;
        }
    }

    /**
     * Metodo que gestiona la jugada del enemigo.
     *
     * */
    public void enemyPlay() {
        // Recogemos el valor del ataque del enemigo y lo guardamos en una variable.
        // Agregamos el resultado de la accion al Log.
        tvLog.append(enemy.attack(hero));
        updateHeroLP();

        // Comprobamos si los personajes aun tienen vida,
        if (hero.getLp()>0 && enemy.getLp()>0){
            // En caso afirmativo, aumentariamos una ronda porque en principio el enemigo siempre
            // va segundo y se juega el turno.
            round++;
            playTurn();
        } else {
            // En caso contrario llamamos al metodo gameEnd()
            gameEnd();
        }
    }

    /**
     * Dialogo que avisa que el juego ha finalizado y que volveremos a la ventana de login.
     *
     * */
    private void showAlertDialog(String message) {

        // Creamos un dialogo con un titulo avisando del resultado de la partida.
        AlertDialog.Builder alertDialog;

        // HAcemos un split del mensaje para comprobar la informacion que contiene.
        String[] messageArray = message.split("\n");
        if (messageArray.length > 2)
            // Si el array guardado contiene mas de 2 elementos contiene informacion
            // extra por la subida de nivel del heroe, por lo que mostramos el titulo desde el
            // array y la informacion extra desde un substring a partir del final del titulo.
            alertDialog = new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                    .setTitle(messageArray[0])
                    .setMessage(message.substring(messageArray[0].length()+1));
        else
            // En caso contrario no hay ninguna informacion extra, por lo que mostramos el mensaje
            // directamente
            alertDialog  = new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                    .setTitle(message);

        // Asignamos al dialogo el boton OK con el click listener.
        alertDialog.setNeutralButton(android.R.string.ok, (dialog, which) -> {
            // Cuando el usuario le de click al boton OK, cargamos de nuevo el DungeonFragment
            FragmentManager manager = getFragmentManager();
            DungeonFragment fragment = DungeonFragment.newInstance(usrUID, pos, hero);

            // Comprobamos que el manager no sea null y cargamos el fragment
            assert manager != null;
            DbUtils.loadFragment(manager, fragment);
        });

        // Creamos el dialogo, hacemos que no se pueda cancelar pulsando fuera y lo mostramos.
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    /**
     *  Metodo para finalizar el juego
     *
     * */
    public void gameEnd(){
        // En caso contrario, hacemos que los botones de accion no puedan pulsar
        btnAttack.setClickable(false);
        btnItems.setClickable(false);

        // Creamos un String que asignaremos al realizar la comprobacion del ganador
        // con un mensaje acorde al resultado de la partida.
        StringBuilder message = new StringBuilder();
        // Comprobamos si el heroe tiene mas vida que el enemigo, en cuyo caso ganaria
        if (hero.getLp()>enemy.getLp()){
            // Sumamos los puntos de experiencia al heroe
            hero.setExp(hero.getExp() + 50);
            message.append("=========You Win!=========\n");

            if (hero.getExp()>= hero.getReqExp()) {
                // Si el heroe ha alcanzado o superado los puntos de experiencia requeridos para
                // subir de nivel, aumenta en 1 su nivel y arrastra los puntos extra para el proximo
                hero.lvlUp();
                // Aumentamos los atributos del heroe y los agregamos al mensaje del log
                message.append(DbUtils.statusLvlUp(hero));
                // Restauramos la salud y la energia del heroe al maximo
                hero.setLp(hero.getMaxLp());
                hero.setMp(hero.getMaxMp());
                hero.setExp(hero.getExp() - hero.getReqExp());
            }
        } else {
            // En caso contrario habria perdido
            hero.setLp(hero.getMaxLp());
            message.append("=========You Lose=========\n");
        }

        // Mostramos el dialogo
        showAlertDialog(message.toString());

        // Agregamos el mensaje al Log de la partida y movemos el scroll al final
        tvLog.append(message);
        scroll.fullScroll(View.FOCUS_DOWN);

    }

    void updateHeroLP(){
        // Realizamos la comprobacion del SDK para modificar la salud del heroe.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            pbHeroLP.setProgress(hero.getLp(), true);
        } else
            pbHeroLP.post(() -> pbHeroLP.setProgress(hero.getLp()));
                    /* -- Solucion sin usar Lambda --
                    pbHeroLP.post(new Runnable() {
                        @Override
                        public void run() {
                            pbHeroLP.setProgress(hero.getLp());
                        }
                    });
                    */
    }
    void updateHeroMP(){
        // Realizamos la comprobacion del SDK y realizamos la asignacion correspondiente.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            pbHeroMP.setProgress(hero.getMp(), true);
        else
            pbHeroMP.post(()-> pbHeroMP.setProgress(hero.getMp()));
    }
    void updateEnemyLP(){
        // Realizamos la comprobacion del SDK y realizamos la asignacion correspondiente.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            pbEnemyLP.setProgress(enemy.getLp(), true);
        } else
            pbEnemyLP.post(() -> pbEnemyLP.setProgress(enemy.getLp()));
    }
    void updateEnemyMP(){
        // Realizamos la comprobacion del SDK y realizamos la asignacion correspondiente.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            pbEnemyMP.setProgress(enemy.getMp(),true);
        else {
            pbEnemyMP.post(() -> pbEnemyMP.setProgress(enemy.getMp()));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflamos la vista con el layout de batalla y la guardamos en una variable.
        View v = inflater.inflate(R.layout.fragment_battle, container, false);

        // Buscamos en la vista los diferentes componentes y los asignamos a las variables.
        tvLog = v.findViewById(R.id.tvLog);
        ibLog = v.findViewById(R.id.ibLog);
        btnAttack = v.findViewById(R.id.btnAttack);
        btnAbilities = v.findViewById(R.id.btnAbilities);
        btnItems = v.findViewById(R.id.btnItems);
        pbEnemyLP = v.findViewById(R.id.pbEnemyLP);
        pbEnemyMP = v.findViewById(R.id.pbEnemyMP);
        pbHeroLP = v.findViewById(R.id.pbHeroLP);
        pbHeroMP = v.findViewById(R.id.pbHeroMP);
        scroll = v.findViewById(R.id.scroll);
        rvAbilities = v.findViewById(R.id.rvAbilities);
        llHero = v.findViewById(R.id.llHero);

        // Recibimos los argumentos del Fragment para recibir al heroe y al enemigo
        Bundle b = getArguments();

        // Comprobamos que el Bundle no sea nulo y asignamos lo que necesitamos
        assert b != null;
        hero = (Hero) b.getSerializable(HERO);
        enemy = (Enemy) b.getSerializable(ENEMY);
        usrUID = b.getString(FB_USER_UID);
        pos = b.getInt(HERO_POS);

        // Devolvemos la vista.
        return v;
    }

    @Override
    public void onAbilityClick(Ability a) {
        // Comprobamos el tipo de efecto que tiene la habilidad
        switch (a.getEffectType()) {
            case REGEN:
                // En caso de que sea de tipo REGEN, recogemos el atributo effect y le hacemos
                // split para obtener el valor y el atributo al que afecta
                String[] effect = a.getEffect().split("\\+");
                // Si el primer elemento del array es LP, es una habilidad para regenerar LP.
                if (effect[0].equalsIgnoreCase("LP")){
                    // Si el heroe tiene menos MP que el coste de la habilidad, se avisa y no se
                    // juega turno
                    if (hero.getMp() < ((Active)a).getCost()){
                        Toast.makeText(getContext(), "Not enough MP", Toast.LENGTH_SHORT).show();
                    } else {
                        // En caso contrario, realizamos la accion de regenerar los LP, reducimos
                        // los mp, actualizamos las barras correspondientes y jugamos el turno.
                        Toast.makeText(getContext(), a.getAbility(), Toast.LENGTH_SHORT).show();
                        hero.setLp(hero.getLp() + Integer.parseInt(effect[1]));
                        hero.setMp(hero.getMp() - ((Active)a).getCost());

                        // Actualizamos la barra de vida del heroe.
                        updateHeroLP();

                        // Actualizamos la barra de energia del heroe.
                        updateHeroMP();

                        playTurn();
                    }
                }

                // TODO Implementar los otros tipos de habilidades.
                break;case STATUS_UP:break;case DMG:break; default:
        }


    }
}
