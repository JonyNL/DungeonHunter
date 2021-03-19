package com.jonyn.dungeonhunter.fragments;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.jonyn.dungeonhunter.R;
import com.jonyn.dungeonhunter.models.Consumable;
import com.jonyn.dungeonhunter.models.Enemy;
import com.jonyn.dungeonhunter.models.Hero;
import com.jonyn.dungeonhunter.models.Item;
import com.jonyn.dungeonhunter.models.Potion;

import java.util.List;


/**
 * Fragment para gestionar las batallas
 *
 */
public class BattleFragment extends Fragment {

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

    // Heroe y enemigo a enfrentarse
    private Hero hero;
    private Enemy enemy;

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
    public static BattleFragment newInstance(Hero hero, Enemy enemy) {
        BattleFragment fragment = new BattleFragment();
        Bundle args = new Bundle();
        args.putSerializable(HERO, hero);
        args.putSerializable(ENEMY, enemy);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        // TODO - Boton no clickable porque aun no esta implementado el sistema de habilidades
        btnAbilities.setClickable(false);

        // TODO - Con el objetivo de testear, agregamos un item al inventario del heroe

        //hero = new Warrior("Jony");
        hero.getInventory().add(new Potion("Health Potion",
                "Potions that restores 20 points of your life points",
                5, "LP +20", Potion.Types.HEALTH_POTION, 20));

        /** Establecemos el valor maximo y actual de las barras que representan la salud y la
         *  energia de los personajes, tanto el heroe como el enemigo.*/

        // [START progress_bars_assignment]
        // Establecemos el valor maximo de la vida del heroe.
        pbHeroLP.setMax(hero.getMaxLp());

        // Comprobamos si el SDK de adnroid es el 24 para realizar la asignacion del progreso de la
        // barra a traves del nuevo metodo.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            pbHeroLP.setProgress(hero.getLp(), true);
        else
            //En caso contrario, tendremos que usar el metodo tradicional.
            pbHeroLP.post(new Runnable() {
                @Override
                public void run() {
                    pbHeroLP.setProgress(hero.getLp());
                }
            });

        // Establecemos el valor maximo de la energia del heroe.
        pbHeroMP.setMax(hero.getMaxMp());

        // Realizamos la misma comprobacion que con la vida.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            pbHeroMP.setProgress(hero.getMp(), true);
        else
            pbHeroMP.post(new Runnable() {
                @Override
                public void run() {
                    pbHeroMP.setProgress(hero.getMp());
                }
            });

        //enemy = new Enemy("Kappa", Enemy.Types.DEMON);

        // Establecemos la vida maxima del enemigo en la barra de progreso que la representa.
        pbEnemyLP.setMax(enemy.getMaxLp());

        // Realizaremos la misma comprobacion del SDK y las mismas operaciones.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            pbEnemyLP.setProgress(enemy.getMaxLp(), true);
        else
            pbEnemyLP.post(new Runnable() {
                @Override
                public void run() {
                    pbEnemyLP.setProgress(enemy.getMaxLp());
                }
            });

        // Asignamos la energia maxima del enemigo a la barra de progreso que la representa.
        pbEnemyMP.setMax(enemy.getMaxMp());

        // Realizaremos la misma comprobacion del SDK y las mismas operaciones.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            pbEnemyMP.setProgress(enemy.getMaxMp(),true);
        else {
            pbEnemyMP.post(new Runnable() {
                @Override
                public void run() {
                    pbEnemyMP.setProgress(enemy.getMaxMp());
                }
            });
        }
        // [END progress_bars_assignment]

        // Vamos describiendo los acontecimientos en un TextView, encabezado por este texto
        tvLog.append("========"+hero.getName() + " VS " + enemy.getName()+"========\n");
        scroll.fullScroll(View.FOCUS_DOWN);

        // Empezamos la partida jugando un turno.
        playTurn();

        // Establecemos los listeners a los botones para realizar las acciones correspondientes.
        // [START buttons_click_listeners]
        btnAttack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Recogemos el valor del ataque del heroe y lo guardamos en una variable.
                int heroDmg = hero.attack();

                // Reducimos la vida del enemigo en un valor igual al del ataque del heroe.
                enemy.setLp(enemy.getLp()-heroDmg);

                // Realizamos de nuevo la comprobacion del SDK y realizamos la asignacion correspondiente.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    pbEnemyLP.setProgress(enemy.getLp(), true);
                } else
                    pbEnemyLP.post(new Runnable() {
                    @Override
                    public void run() {
                        pbEnemyLP.setProgress(enemy.getLp());
                    }
                });

                // Agregamos al TextView que muestra el progreso del juego el resultado del ataque.
                tvLog.append(hero.getName() + " attacked and dealt " + heroDmg + " damage to "
                        + enemy.getName()+ ".\n----------------------------------------\n");
                scroll.fullScroll(View.FOCUS_DOWN);

                // Comprobamos si ambos personajes aun tienen vida.
                if (hero.getLp()>0 && enemy.getLp()>0){

                    // En caso afirmativo, jugamos el turno y llamamos a la jugada del enemigo
                    playTurn();
                    enemyPlay();
                } else {

                    // En caso contrario, llamamos al metodo gameEnd.
                    gameEnd();
                }

            }
        });
        //TODO implement btnAbilities.onClick()

        btnItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Creamos una lista de items en la que guardaremos el inventario del heroe.
                List<Item> inventory = hero.getInventory();

                // Comprobamos si el inventario tiene objetos dentro.
                if (!inventory.isEmpty()){

                    // Creamos un Consumable asignandole el objeto que vamos a usar.
                    Consumable c = (Consumable) inventory.get(0);

                    // Reducimos la cantidad del objeto utilizado en 1.
                    ((Consumable) inventory.get(0)).removeQuantity(1);

                    // Usamos el objeto en el heroe.
                    ((Potion)c).use(hero, Potion.Types.HEALTH_POTION);

                    // Realizamos la comprobacion del SDK para modificar la salud del heroe.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        pbHeroLP.setProgress(hero.getLp(), true);
                    } else
                        pbHeroLP.post(new Runnable() {
                            @Override
                            public void run() {
                                pbHeroLP.setProgress(hero.getLp());
                            }
                        });

                    // Actualizamos el Log para que nos muestre el resultado de la operacion.
                    tvLog.append(hero.getName()+" used " + c.getName()
                            + ".\n----------------------------------------\n");

                    // En caso de que la cantidad del objeto consumido sea 0, lo eliminamos del
                    // inventario.
                    if (c.getQuantity()<= 0)
                        inventory.remove(0);

                    // Reasignamos el inventario para que el consumo del objeto sea efectivo.
                    hero.setInventory(inventory);

                    // Jugamos el turno y llamamos a la jugada del enemigo.
                    playTurn();
                    enemyPlay();
                } else
                    // Si el inventario esta vacio, lo avisamos y no se realiza la jugada.
                    Toast.makeText(getContext(), "No items in inventory", Toast.LENGTH_SHORT).show();
            }
        });

        // [END buttons_click_listeners]
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
        int enemyDmg = enemy.attack();

        // Reducimos la vida del heroe en una cantidad igual al ataque del enemigo.
        hero.setLp(hero.getLp() - enemyDmg);

        // Realizamos la comprobacion del SDK de android y realizamos la accion correspondiente.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            pbHeroLP.setProgress(hero.getLp(), true);
        else
            pbHeroLP.post(new Runnable() {
                @Override
                public void run() {
                    pbHeroLP.setProgress(hero.getLp());
                }
            });

        // Agregamos el resultado de la accion al Log.
        tvLog.append(enemy.getName() + " attacked and dealt " + enemyDmg + " damage to " + hero.getName()
                +".\n----------------------------------------\n");

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

        // Creamos un dialogo con un titulo avisando del resultado de la partida y un mensaje que
        // nos avisa de que vamos a volver a la pantalla de login.
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(message)
                .setMessage("Press OK to go to login screen.");

        // Asignamos al dialogo el boton OK con el click listener.
        alertDialog.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Cuando el usuario le de click al boton OK, recreamos la activity para reiniciar
                // el juego y volver al menu de login.
                getActivity().recreate();
            }
        });

        // creamos el dialogo y hacemos que no se pueda cancelar pulsando fuera y lo mostramos.
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
        String message;
        // Comprobamos si el heroe tiene mas vida que el enemigo, en cuyo caso ganaria
        if (hero.getLp()>enemy.getLp()){
            message = "=========You Win!=========";
            showAlertDialog(message);
        } else {
            // En caso contrario habria perdido
            message = "=========You Lose=========";
            showAlertDialog(message);
        }

        // Agregamos el mensaje al Log de la partida y movemos el scroll al final
        tvLog.append(message);
        scroll.fullScroll(View.FOCUS_DOWN);

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

        // Recibimos los argumentos del Fragment para recibir al heroe y al enemigo/
        Bundle b = getArguments();
        hero = (Hero) b.getSerializable(HERO);
        enemy = (Enemy) b.getSerializable(ENEMY);



        // Devolvemos la vista.
        return v;
    }
}
