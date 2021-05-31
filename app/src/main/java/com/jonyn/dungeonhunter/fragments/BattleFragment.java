package com.jonyn.dungeonhunter.fragments;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.jonyn.dungeonhunter.IItemsListener;
import com.jonyn.dungeonhunter.ItemsAdapter;
import com.jonyn.dungeonhunter.R;
import com.jonyn.dungeonhunter.models.Ability;
import com.jonyn.dungeonhunter.models.Active;
import com.jonyn.dungeonhunter.models.Consumable;
import com.jonyn.dungeonhunter.models.Enemy;
import com.jonyn.dungeonhunter.models.Hero;
import com.jonyn.dungeonhunter.models.Potion;

import org.w3c.dom.Text;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static com.jonyn.dungeonhunter.DbUtils.FB_USER_UID;
import static com.jonyn.dungeonhunter.DbUtils.HERO_POS;


/**
 * Fragment para gestionar las batallas
 *
 */
public class BattleFragment extends Fragment implements IAbilityListener, IItemsListener {

    // Atributos del layout
    private TextView tvLog;
    private TextView tvEnemyName;
    private TextView tvBtHeroName;
    private TextView tvEnemyLvl;
    private TextView tvHeroStatus;
    private ImageButton ibLog;
    private ImageButton ibClose;
    private ImageView ivEnemy;
    private ImageView ivHero;
    private Button btnAttack;
    private Button btnAbilities;
    private Button btnItems;
    private ProgressBar pbEnemyLP;
    private ProgressBar pbEnemyMP;
    private ProgressBar pbHeroLP;
    private ProgressBar pbHeroMP;
    private RecyclerView rvAbilities;
    private RecyclerView rvItems;
    private LinearLayout llHero;
    private LinearLayout llStatus;

    // Heroe y enemigo a enfrentarse
    private Hero hero;
    private Enemy enemy;

    // Variables auxiliares para el Fragment
    String usrUID;
    int pos;
    private int round = 1;
    private StringBuilder logBuild;
    private StringBuilder tvLogText;
    private String textToLog;
    private boolean playerTurn = true;
    private boolean playTurn = true;
    private boolean animating = false;
    IAbilityListener aListener;
    IItemsListener iListener;


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
        args.putSerializable(DbUtils.HERO, hero);
        args.putSerializable(DbUtils.ENEMY, enemy);
        args.putString(FB_USER_UID, usrUID);
        args.putInt(HERO_POS, pos);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflamos la vista con el layout de batalla y la guardamos en una variable.
        View v = inflater.inflate(R.layout.fragment_battle, container, false);

        // Buscamos en la vista los diferentes componentes y los asignamos a las variables.
        tvLog = v.findViewById(R.id.tvLog);
        tvHeroStatus = v.findViewById(R.id.tvHeroStatus);
        ibLog = v.findViewById(R.id.ibLog);
        ibClose = v.findViewById(R.id.ibClose);
        btnAttack = v.findViewById(R.id.btnAttack);
        btnAbilities = v.findViewById(R.id.btnAbilities);
        btnItems = v.findViewById(R.id.btnItems);
        pbEnemyLP = v.findViewById(R.id.pbEnemyLP);
        pbEnemyMP = v.findViewById(R.id.pbEnemyMP);
        pbHeroLP = v.findViewById(R.id.pbHeroLP);
        pbHeroMP = v.findViewById(R.id.pbHeroMP);
        rvAbilities = v.findViewById(R.id.rvAbilities);
        rvItems = v.findViewById(R.id.rvItems);
        llHero = v.findViewById(R.id.llHero);
        llStatus = v.findViewById(R.id.llStatus);
        tvEnemyLvl = v.findViewById(R.id.tvEnemyLvl);
        tvEnemyName = v.findViewById(R.id.tvEnemyName);
        tvBtHeroName = v.findViewById(R.id.tvBtHeroName);
        ivEnemy = v.findViewById(R.id.ivEnemy);
        ivHero = v.findViewById(R.id.ivHero);

        // Recibimos los argumentos del Fragment para recibir al heroe y al enemigo
        Bundle b = getArguments();

        // Comprobamos que el Bundle no sea nulo y asignamos lo que necesitamos
        assert b != null;
        hero = (Hero) b.getSerializable(DbUtils.HERO);
        enemy = (Enemy) b.getSerializable(DbUtils.ENEMY);
        usrUID = b.getString(FB_USER_UID);
        pos = b.getInt(HERO_POS);

        // Inicializamos los StringBuilders
        tvLogText = new StringBuilder();
        logBuild = new StringBuilder();

        // Devolvemos la vista.
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        // inicializamos los listeners
        aListener = this;
        iListener = this;

        // Establecemos la imagen correspondiente tanto para el heroe como para el enemigo
        ivHero.setImageResource(
                getResources().getIdentifier(hero.getHeroClass().toString().toLowerCase(),
                "drawable", getContext().getPackageName()));
        ivEnemy.setImageResource(
                getResources().getIdentifier(enemy.getName().toLowerCase(),
                        "drawable", getContext().getPackageName()));

        // Asignamos la informacion del heroe y el enemigo a sus vistas correspondientes
        tvEnemyName.setText(enemy.getName());
        tvEnemyLvl.setText(getString(R.string.level, enemy.getLvl()));
        tvBtHeroName.setText(hero.getName());

        /* Establecemos el valor maximo y actual de las barras que representan la salud y la
         *  energia de los personajes, tanto el heroe como el enemigo.*/

        // region [START progress_bars_assignment]
        // Establecemos el valor maximo de la vida del heroe.
        pbHeroLP.setMax(hero.getMaxLp());
        updateHeroLP();

        // Establecemos el valor maximo de la energia del heroe.
        pbHeroMP.setMax(hero.getMaxMp());
        updateHeroMP();

        // Establecemos la vida maxima del enemigo en la barra de progreso que la representa.
        pbEnemyLP.setMax(enemy.getMaxLp());
        updateEnemyLP();

        // Asignamos la energia maxima del enemigo a la barra de progreso que la representa.
        pbEnemyMP.setMax(enemy.getMaxMp());
        updateEnemyMP();
        // endregion [END progress_bars_assignment]

        // Vamos describiendo los acontecimientos en un StringBuilder, encabezado por este texto
        textToLog = "========"+hero.getName() + " VS " + enemy.getName()+"========\n";
        // Agregamos el texto a logBuild y a tvLogText
        logBuild.append(textToLog);
        tvLogText.append(textToLog);

        // Empezamos la partida jugando un turno.
        playTurn();

        // Agregamos un listener a ibClose para cerrar los listados
        ibClose.setOnClickListener(v -> {
            // Escondemos el listado de habilidades u objetos y mostramos otra vez el menu de acciones
            llStatus.setVisibility(View.GONE);
            rvAbilities.setVisibility(View.GONE);
            rvItems.setVisibility(View.GONE);
            llHero.setVisibility(View.VISIBLE);
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

        // Creamos el listener para realizar las diferentes acciones segun el boton pulsado.
        View.OnClickListener btnListener = (v -> {
            if (!animating && playerTurn) {
                // Si al pulsar el boton no hay animacion activa y es el turno del jugador,
                // en principio jugaremos el turno y realizamos la accion que corresponda.
                playTurn = true;
                switch (v.getId()) {
                    case R.id.btnAttack:
                        // Si pulsamos el boton de atacar, guardamos la cantidad de vida del enemigo
                        int enemyLp = enemy.getLp();
                        // Agregamos al StringBuider que muestra el progreso del juego el resultado
                        // del ataque, lo agregamos a logBuild y tvLogText y lo asignamos al tvLog.
                        textToLog = hero.attack(enemy);
                        logBuild.append(textToLog);
                        tvLogText.append(textToLog);
                        tvLog.setText(tvLogText);
                        // Vaciamos tvLogText
                        tvLogText.setLength(0);

                        if (enemyLp != enemy.getLp()) {
                            // Comprobamos si la vida del enemigo ha cambiado, lo que indicaria que
                            // el ataque ha sido efectivo, y en caso afirmativo animamos el golpe
                            // al enemigo
                            animateOnEnemyHit();
                        }
                        break;
                    case R.id.btnAbilities:
                        // Si pulsamos sobre las habilidades, no jugaremos el turno, ya que solo
                        // mostraremos el listado de habilidades.
                        playTurn = false;

                        // Creamos un adaptador para el listado de habilidades
                        AbilitiesAdapter abAdapter = new AbilitiesAdapter(hero.getActives(), true);

                        // Agregamos un listener al adaptador para poder seleccionar las habilidades
                        abAdapter.setListener(v1 -> {
                            if (aListener != null) {
                                // Si el listener no es nulo, ejecutamos el metodo onAbilityClick()
                                aListener.onAbilityClick(hero.getActives().get(rvAbilities.getChildAdapterPosition(v1)));

                                // Escondemos el listado de habilidades y mostramos otra vez el menu de acciones
                                llStatus.setVisibility(View.GONE);
                                rvAbilities.setVisibility(View.GONE);
                                llHero.setVisibility(View.VISIBLE);
                            }
                        });

                        // Asignamos el adaptador al RecyclerView y le asignamos el LayoutManager
                        rvAbilities.setAdapter(abAdapter);
                        rvAbilities.setLayoutManager(new LinearLayoutManager(
                                getContext(), LinearLayoutManager.VERTICAL, false));

                        // Asignamos al TextView que muestra el estado actual del personaje los
                        // valores de LP y MP del heroe
                        tvHeroStatus.setText(getString(R.string.hero_status, hero.getLp(),
                                hero.getMaxLp(), hero.getMp(), hero.getMaxMp()));

                        // Mostramos el LinearLayout del estado del heroe
                        llStatus.setVisibility(View.VISIBLE);
                        // Escondemos el menu de acciones y mostramos el listado de habilidades
                        rvAbilities.setVisibility(View.VISIBLE);
                        llHero.setVisibility(View.GONE);
                        break;
                    case R.id.btnItems:
                        // Si pulsamos sobre los objetos tampoco jugaremos el turno, sino que
                        // mostraremos el listado de objetos.
                        playTurn = false;
                        if (!hero.getInventory().isEmpty()) {
                            // Si el inventario contiene elementos, realizamos las siguientes acciones

                            // Creamos un adaptador para el listado de habilidades
                            ItemsAdapter iAdapter =
                                    new ItemsAdapter(hero.getInventory(), false);

                            // Agregamos un listener al adaptador para poder seleccionar las habilidades
                            iAdapter.setOnClickListener(v1 -> {
                                if (iListener != null) {
                                    // Si el listener no es nulo, ejecutamos el metodo onAbilityClick()
                                    iListener.onItemClick(rvItems.getChildAdapterPosition(v1));

                                    // Escondemos el listado de habilidades y mostramos otra vez el menu de acciones
                                    llStatus.setVisibility(View.GONE);
                                    rvItems.setVisibility(View.GONE);
                                    llHero.setVisibility(View.VISIBLE);
                                }
                            });

                            // Asignamos el adaptador al RecyclerView y le asignamos el LayoutManager
                            rvItems.setAdapter(iAdapter);
                            rvItems.setLayoutManager(new LinearLayoutManager(
                                    getContext(), LinearLayoutManager.VERTICAL, false));

                            // Asignamos al TextView que muestra el estado actual del personaje los
                            // valores de LP y MP del heroe
                            tvHeroStatus.setText(getString(R.string.hero_status, hero.getLp(),
                                    hero.getMaxLp(), hero.getMp(), hero.getMaxMp()));

                            // Mostramos el LinearLayout del estado del heroe
                            llStatus.setVisibility(View.VISIBLE);
                            // Escondemos el menu de acciones y mostramos el listado de habilidades
                            rvItems.setVisibility(View.VISIBLE);
                            llHero.setVisibility(View.GONE);
                            break;
                        } else {
                            // En caso contrario, avisamos que no hay objetos en el inventario
                            Toast.makeText(getContext(), "You have no items",
                                    Toast.LENGTH_SHORT).show();
                        }
                }

                if (playTurn) {
                    // Si jugamos el turno, usamos un postDelayed de la clase Handler para ejecutar
                    // el codigo con una breve pausa y mejorar la fluidez de la partida
                    new Handler().postDelayed(() -> {
                        // Actualizamos la barra de salud del enemigo.
                        updateEnemyLP();
                        // Comprobamos si ambos personajes aun tienen vida.
                        if (hero.getLp() > 0 && enemy.getLp() > 0) {
                            // En caso afirmativo, jugamos el turno e informamos que no es el turno
                            // del jugador
                            playerTurn = false;
                            playTurn();
                        } else {
                            // En caso contrario, llamamos al metodo gameEnd.
                            gameEnd();
                        }
                    }, 500);
                }
            }
        });

        // Creamos un listener para el boton que muestra el log.
        View.OnClickListener ibLogListener = v -> {
            // Creamos un AlertDialog
            AlertDialog.Builder alertDialog;
            // Asignamos un nuevo alertDialog con titulo "BattleLog" y le asignamos el log actual
            // guardado en logBuild
            alertDialog = new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                    .setTitle("Battle Log")
                    .setMessage(logBuild);

            // Creamos el dialogo lo mostramos.
            AlertDialog alert = alertDialog.create();
            alert.show();
        };

        // Establecemos los listeners a los botones para realizar las acciones correspondientes.
        ibLog.setOnClickListener(ibLogListener);
        btnAttack.setOnClickListener(btnListener);
        btnAttack.setOnTouchListener(tListener);
        btnAbilities.setOnClickListener(btnListener);
        btnAbilities.setOnTouchListener(tListener);
        btnItems.setOnClickListener(btnListener);
        btnItems.setOnTouchListener(tListener);
    }

    /**
     * Metodo para mostrar en el Log el avance de los turnos y rondas.
     *
     * */
    public void playTurn(){

        // Comprobamos si es el turno del jugador
        if (playerTurn) {
            // En caso afirmativo, mostramos la ronda y el nombre del jugador.
            textToLog = "Round " + round + ": "+ hero.getName() + "'s turn.\n~~~~~~~~~~~~~~~~~\n";
            // Agregamos el texto anterior a los StringBuilders.
            logBuild.append(textToLog);
            tvLogText.append(textToLog);
            // Asignamos el contenido del tvLogText al tvLog
            tvLog.setText(tvLogText);

        } else {
            // En caso contrario, se realiza lo mismo pero mostrando el nombre del enemigo.
            textToLog = "Round " + round + ": "+ enemy.getName() + "'s turn.\n~~~~~~~~~~~~~~~~~\n";
            // Agregamos el texto anterior a los StringBuilders.
            logBuild.append(textToLog);
            tvLogText.append(textToLog);
            // Asignamos el contenido del tvLogText al tvLog
            tvLog.setText(tvLogText);
            // Ejecutamos la jugada del enemigo
            enemyPlay();
        }
        // Una vez entramos aqui, ya se han terminado las animaciones, asi que animating le
        // asignamos false
        animating = false;

    }

    /**
     * Metodo que gestiona la jugada del enemigo.
     *
     * */
    public void enemyPlay() {
        // Guardamos la vida actual del heroe en una variable.
        int heroLp = hero.getLp();

        // Recogemos el resultado del ataque y lo agregamos al log.
        textToLog = enemy.attack(hero);
        // Agregamos el texto del log a tvLogText y a logBuild
        tvLogText.append(textToLog);
        logBuild.append(textToLog);
        // Asignamos el texto del tvLogText al tvLog y lo vaciamos
        tvLog.setText(tvLogText);
        tvLogText.setLength(0);

        if (heroLp != hero.getLp()) {
            // Si la vida del heroe ha variado, el ataque ha funcionado y animamos el golpe
            animateOnHeroHit();
        }

        // Usamos un postDelayed de la clase Handler
        new Handler().postDelayed(() -> {
            // Actualizamos la vida del heroe.
            updateHeroLP();
            // Comprobamos si los personajes aun tienen vida.
            if (hero.getLp()>0 && enemy.getLp()>0){
                // En caso afirmativo, aumentariamos una ronda porque en principio el enemigo siempre
                // va segundo, se avisa que viene el turno del heroe y se juega el turno.
                round++;
                playerTurn = true;
                playTurn();
            } else {
                // En caso contrario llamamos al metodo gameEnd()
                gameEnd();
            }
        }, 500);

    }

    /**
     *  Metodo para finalizar el juego
     *
     * */
    public void gameEnd(){
        // En caso contrario, hacemos que los botones de accion no puedan pulsar
        btnAttack.setClickable(false);
        btnAbilities.setClickable(false);
        btnItems.setClickable(false);

        // Creamos un String que asignaremos al realizar la comprobacion del ganador
        // con un mensaje acorde al resultado de la partida.
        StringBuilder message = new StringBuilder();
        // Comprobamos si el heroe tiene mas vida que el enemigo, en cuyo caso ganaria
        if (hero.getLp()>enemy.getLp()){
            // Sumamos los puntos de experiencia al heroe
            hero.setExp(hero.getExp() + 50);
            message.append("=========You Win!=========\n");
            // Avanzamos una stage en el progreso
            hero.getDungeonProgress().stagePosUp();
            if (hero.getExp()>= hero.getReqExp()) {
                // Si el heroe ha alcanzado o superado los puntos de experiencia requeridos para
                // subir de nivel, aumenta en 1 su nivel y arrastra los puntos extra para el proximo
                hero.lvlUp();
                // Aumentamos los atributos del heroe y los agregamos al mensaje del log
                message.append(DbUtils.statusLvlUp(hero));
                // Restauramos la salud y la energia del heroe al maximo y guardamos los puntos
                // sobrantes de la experiencia del nivel
                hero.setLp(hero.getMaxLp());
                hero.setMp(hero.getMaxMp());
                hero.setExp(hero.getExp() - hero.getReqExp());
            }
            // Obtenemos el loot de la batalla y lo agregamos al mensaje
            message.append(getLoot());
        } else {
            // En caso contrario habria perdido, por lo que subimos una run y restauramos los LP y
            // MP del heroe
            hero.getDungeonProgress().runUp();
            hero.setLp(hero.getMaxLp());
            hero.setMp(hero.getMaxMp());
            // Agregamos al mensaje un string indicando que se ha perdido
            message.append("=========You Lose=========\n");
        }

        // Mostramos el dialogo
        showAlertDialog(message.toString());

        // Agregamos el mensaje al Log de la partida
        tvLogText.append(message);
        logBuild.append(message);
        tvLog.setText(tvLogText);

    }

    /**
     * Metodo que genera el loot de la batalla al terminar.
     *
     * @return un String con el resultado del loot.
     * */
    private String getLoot() {
        // Creamos un StringBuilder para ir construyendo el String con el resultado.
        StringBuilder lootStr = new StringBuilder();
        // Agregamos un mensaje inicial.
        lootStr.append("You got:");
        // Generamos un numero aleatorio para recibir una cantidad de objetos aleatoria
        switch (DbUtils.randomNumber(1, 6)) {
            case 1:
                // En caso que salga 1, agreagamos un Elixir al inventario.
                Potion elixir = new Potion("Elixir",
                        "Elixir that fills your LP and MP",
                        5, "LP/MP MAX", Potion.Types.ELIXIR, 0);
                // Comprobamos si el heroe ya tiene Elixir en el inventario
                if (hero.getInventory().contains(elixir))
                    // En caso afirmativo, simplemente incrementamos la cantidad del item en 5
                    ((Potion)hero.getInventory()
                            .get(hero.getInventory().indexOf(elixir))).quantityUp(5);
                else
                    // En caso contrario, agregamos el objeto
                    hero.getInventory().add(elixir);
                // Agregamos al StringBuilder el nombre del objeto.
                lootStr.append("\nElixir");
            case 2:
                // En caso que salga 2, o que vengamos de 1, agreagamos una pocion de Mana al inventario.
                Potion manaP = new Potion("Mana Potion",
                        "Potion that restores 20 points of your mana points",
                        5, "MP+20", Potion.Types.MANA_POTION, 20);
                // Comprobamos si el heroe tiene ya el objeto en el inventario
                if (hero.getInventory().contains(manaP))
                    // En caso afirmativo, simplemente incrementamos la cantidad del item en 5
                    ((Potion)hero.getInventory()
                            .get(hero.getInventory().indexOf(manaP))).quantityUp(5);
                else
                    // En caso contrario, agregamos el objeto
                    hero.getInventory().add(manaP);
                // Agregamos al StringBuilder el nombre del objeto.
                lootStr.append("\nMana Potion");
            case 3:
                // En caso que salga 3, o que vengamos de 2, agreagamos una pocion de Vida al inventario.
                Potion lifeP = new Potion("Life Potion",
                        "Potion that restores 20 points of your life points",
                        5, "LP+20", Potion.Types.LIFE_POTION, 20);
                // Comprobamos si el heroe tiene ya el objeto en el inventario
                if (hero.getInventory().contains(lifeP))
                    // En caso afirmativo, simplemente incrementamos la cantidad del item en 5
                    ((Potion)hero.getInventory()
                            .get(hero.getInventory().indexOf(lifeP))).quantityUp(5);
                else
                    // En caso contrario, agregamos el objeto
                    hero.getInventory().add(lifeP);
                // Agregamos al StringBuilder el nombre del objeto.
                lootStr.append("\nLife Potion");
                // Ponemos un break para no continuar en este caso.
                break;
            case 4:case 5:case 6:
                // En el resto de los casos no agregamos ningun objeto, lo informamos en el
                // StringBuilder
                lootStr.append("\nNothing");
                break;
        }
        // Construimos y devolvemos el StringBuilder.
        return lootStr.toString();
    }

    /**
     * Dialogo que avisa que la batalla ha finalizado.
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
     * Metodo para actualizar la barra de vida del heroe
     *
     * */
    void updateHeroLP(){
        // Realizamos la comprobacion del SDK para modificar la salud del heroe.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            pbHeroLP.setProgress(hero.getLp(), true);
        } else
            pbHeroLP.post(() -> pbHeroLP.setProgress(hero.getLp()));
    }

    /**
     * Metodo para actualizar la barra de mana del heroe
     *
     * */
    void updateHeroMP(){
        // Realizamos la comprobacion del SDK y realizamos la asignacion correspondiente.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            pbHeroMP.setProgress(hero.getMp(), true);
        else
            pbHeroMP.post(()-> pbHeroMP.setProgress(hero.getMp()));
    }

    /**
     * Metodo para actualizar la barra de vida del enemigo
     *
     * */
    void updateEnemyLP(){
        // Realizamos la comprobacion del SDK y realizamos la asignacion correspondiente.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            pbEnemyLP.setProgress(enemy.getLp(), true);
        } else
            pbEnemyLP.post(() -> pbEnemyLP.setProgress(enemy.getLp()));
    }

    /**
     * Metodo para actualizar la barra de mana del enemigo
     *
     * */
    void updateEnemyMP(){
        // Realizamos la comprobacion del SDK y realizamos la asignacion correspondiente.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            pbEnemyMP.setProgress(enemy.getMp(),true);
        else
            pbEnemyMP.post(() -> pbEnemyMP.setProgress(enemy.getMp()));
    }

    /**
     * Metodo para animar el golpe al enemigo
     *
     * */
    private void animateOnEnemyHit() {
        // Asignamos a animating el valor true.
        animating = true;
        Animation animation = new AlphaAnimation(1, 0); //to change visibility from visible to invisible
        animation.setDuration(100); //100 millisecond duration for each animation cycle
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(3); //repeating 3 times
        animation.setRepeatMode(Animation.REVERSE); //animation will start from end point once ended.
        ivEnemy.startAnimation(animation); //to start animation
    }

    /**
     * Metodo para animar el golpe al heroe
     *
     * */
    private void animateOnHeroHit() {
        // Asignamos a animating el valor true.
        animating = true;
        Animation animation = new AlphaAnimation(1, 0); //to change visibility from visible to invisible
        animation.setDuration(100); //300 millisecond duration for each animation cycle
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(3); //repeating 3 times
        animation.setRepeatMode(Animation.REVERSE); //animation will start from end point once ended.
        ivHero.startAnimation(animation); //to start animation

    }

    /**
     * Metodo que gestiona la seleccion de un objeto
     *
     * @param pos posicion en la lista del objeto
     * */
    @Override
    public void onItemClick(int pos) {
        playerTurn = false;
        playTurn = true;

        // Usamos el objeto desde el inventario del heroe
        textToLog = hero.useItem(pos);
        tvLogText.append(textToLog);
        logBuild.append(textToLog);
        // Asignamos tvLogText a tvLog y lo vaciamos
        tvLog.setText(tvLogText);
        tvLogText.setLength(0);

        // Actualizamos las barras de vida y mana del heroe
        updateHeroLP();
        updateHeroMP();

        if (playTurn)
            // Si jugamos el turno, lanzamos el postDelayed del nuevo Handler
            new Handler().postDelayed(() -> {
                // Actualizamos la barra de salud del enemigo.
                updateEnemyLP();
                // Comprobamos si ambos personajes aun tienen vida.
                if (hero.getLp() > 0 && enemy.getLp() > 0) {
                    // En caso afirmativo, jugamos el turno e indicamos que es el turno del enemigo
                    playerTurn = false;
                    playTurn();
                } else {
                    // En caso contrario, llamamos al metodo gameEnd.
                    gameEnd();
                }
            }, 500);
    }

    /**
     * Metodo que gestiona la seleccion de una habilidad
     *
     * @param a Habilidad que se ha seleccionado
     * */
    @Override
    public void onAbilityClick(Ability a) {
        playerTurn = false;
        playTurn = true;
        // Comprobamos el tipo de efecto que tiene la habilidad
        switch (a.getEffectType()) {
            case REGEN:
                // En caso de que sea de tipo REGEN, recogemos el atributo effect y le hacemos
                // split para obtener el valor y el atributo al que afecta
                String[] regenEffect = a.getEffect().split("\\+");
                // Si el primer elemento del array es LP, es una habilidad para regenerar LP.
                if (regenEffect[0].equalsIgnoreCase("LP")){
                    // Si el heroe tiene menos MP que el coste de la habilidad, se avisa y no se
                    // juega turno
                    if (hero.getMp() < ((Active)a).getCost()) {
                        playerTurn = true;
                        playTurn = false;
                        Toast.makeText(getContext(), "Not enough MP", Toast.LENGTH_SHORT).show();
                    } else {
                        // En caso contrario, realizamos la accion de regenerar los LP, reducimos
                        // los mp, actualizamos las barras correspondientes y jugamos el turno.
                        Toast.makeText(getContext(), a.getAbility(), Toast.LENGTH_SHORT).show();
                        hero.recoverLp(Integer.parseInt(regenEffect[1]));
                        hero.consumeMp(((Active)a).getCost());

                        // Asignamos el resultado del uso de la habilidad al textToLog
                        textToLog = hero.getName() + " used ability " + a.getAbility() + " to heal "
                                + regenEffect[1] + " " + regenEffect[0] + "\n";

                        // Agregamos el contenido de textToLog a tvLogText y a logBuild
                        tvLogText.append(textToLog);
                        logBuild.append(textToLog);
                        // Asignamos tvLogText a tvLog y lo vaciamos
                        tvLog.setText(tvLogText);
                        tvLogText.setLength(0);


                        // Actualizamos la barra de vida del heroe.
                        updateHeroLP();

                        // Actualizamos la barra de energia del heroe.
                        updateHeroMP();
                    }
                }
                break;

            case DMG:
                // En caso de que sea de tipo DMG, recogemos el atributo effect y le hacemos
                // split para obtener el valor y el atributo al que afecta
                String[] dmgEffect = a.getEffect().split("\\s");

                // Si el heroe tiene menos MP que el coste de la habilidad, se avisa y no se
                // juega turno
                if (hero.getMp() < ((Active)a).getCost()) {
                    playerTurn = true;
                    playTurn = false;
                    Toast.makeText(getContext(), "Not enough MP", Toast.LENGTH_SHORT).show();
                } else {
                    // En caso contrario, realizamos la accion de reducir los LP del enemigo,
                    // reducimos los mp, actualizamos las barras correspondientes y jugamos el turno.
                    Toast.makeText(getContext(), a.getAbility(), Toast.LENGTH_SHORT).show();
                    enemy.damageLp(Integer.parseInt(dmgEffect[1]));
                    hero.consumeMp(((Active)a).getCost());

                    // Animamos el golpe al enemigo
                    animateOnEnemyHit();

                    // Actualizamos la barra de energia del heroe.
                    updateHeroMP();

                    // Comprobamos el tipo de dmg del ataque
                    switch (dmgEffect[0]){
                        case "fireDmg":
                            // En caso de ser fireDmg, asignamos el texto correspondiente
                            textToLog = hero.getName() + " casted a Fireball and dealt "+
                                    dmgEffect[1] + " fire damage to " + enemy.getName()+
                                    ".\n----------------------------------------\n";
                            // Agregamos el contenido de textToLog a tvLogText y a logBuild
                            tvLogText.append(textToLog);
                            logBuild.append(textToLog);
                            // Asignamos tvLogText a tvLog y lo vaciamos
                            tvLog.setText(tvLogText);
                            tvLogText.setLength(0);

                            break;
                        case "divineDmg":
                            // En caso de ser divineDmg, asignamos el texto correspondiente
                            textToLog = hero.getName() + " casted an Almighty Beam and dealt "+
                                    dmgEffect[1] + " magic damage to " + enemy.getName()+
                                    ".\n----------------------------------------\n";
                            // Agregamos el contenido de textToLog a tvLogText y a logBuild
                            tvLogText.append(textToLog);
                            logBuild.append(textToLog);
                            // Asignamos tvLogText a tvLog y lo vaciamos
                            tvLog.setText(tvLogText);
                            tvLogText.setLength(0);
                            break;
                    }
                }
                break;
            default:
        }

        if (playTurn) {
            // Si jugamos el turno, lanzamos el postDelayed del nuevo Handler
            new Handler().postDelayed(() -> {
                // Actualizamos la barra de vida del enemigo.
                updateEnemyLP();
                // Comprobamos si ambos personajes aun tienen vida.
                if (hero.getLp() > 0 && enemy.getLp() > 0) {
                    // En caso afirmativo, jugamos el turno e indicamos que es el turno del enemigo
                    playerTurn = false;
                    playTurn();
                } else {
                    // En caso contrario, llamamos al metodo gameEnd.
                    gameEnd();
                }
            }, 500);
        }
    }

}
