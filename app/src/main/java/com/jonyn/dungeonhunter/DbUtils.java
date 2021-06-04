package com.jonyn.dungeonhunter;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jonyn.dungeonhunter.fragments.HeroSelectFragment;
import com.jonyn.dungeonhunter.models.Ability;
import com.jonyn.dungeonhunter.models.Active;
import com.jonyn.dungeonhunter.models.Character;
import com.jonyn.dungeonhunter.models.DungeonProgress;
import com.jonyn.dungeonhunter.models.Enemy;
import com.jonyn.dungeonhunter.models.Hero;
import com.jonyn.dungeonhunter.models.Item;
import com.jonyn.dungeonhunter.models.Passive;
import com.jonyn.dungeonhunter.models.Potion;
import com.jonyn.dungeonhunter.models.Sword;
import com.jonyn.dungeonhunter.models.Wand;
import com.jonyn.dungeonhunter.models.Warrior;
import com.jonyn.dungeonhunter.models.Weapon;
import com.jonyn.dungeonhunter.models.Wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Posible clase para la gestion de la conexion con la BBDD.
 * Funciones aun no integradas completamente con el resto de la app.
 *
 * */
public class DbUtils {

    // Etiquetas para la transferencia de info entre fragments y con la BBDD
    public static final String TAG = "DB_UTILS";
    public static final String FB_USER_UID = "FB_USER_UID";
    public static final String HERO = "HERO";
    public static final String ENEMY = "ENEMY";
    public static final String HERO_POS = "HERO_POS";
    public static final String HERO_CLASS = "HERO_CLASS";
    public static final String HERO_NAME = "HERO_NAME";
    public static final String USER_LOGOUT = "USER_LOGOUT";

    // Atributos estaticos necesarios para la gestion de la clase
    //private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static List<Hero> heroes = new ArrayList<>(3);
    private static List<Enemy> enemies = new ArrayList<>();
    private static float musicVol = 1;
    private static float sfxVol = 1;
    private static MediaPlayer mediaPlayer;

    private static final int statsUp = 4;
    private static final int defVal = 100;

    private static final int[] soundIds = new int[10];
    private static final AudioAttributes attrs = new AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build();
    private static final SoundPool sp = new SoundPool.Builder()
            .setMaxStreams(10)
            .setAudioAttributes(attrs)
            .build();

    /**
     * Metodo para cargar los heroes del usuario al listado local.
     *
     * @param context Contexto necesario para algunas operaciones.
     * @param usrUID UID del usuario logueado.
     * @param fromStart Boolean que nos avisa si estamos cargando desde el boton de start
     * @param manager FragmentManager para cargar el HeroSelectFragment si fromStart es true
     * */
    public static void loadHeroList(final Context context, final String usrUID,
                                    final boolean fromStart, final FragmentManager manager) {

        // Agregamos elementos null al listado para mantener el size en la cantidad deseada (3)
        do {
            heroes.add(null);
        } while (heroes.size() < 3);

        // Creamos un ProgressDialog para indicar que se estan cargando elementos desde Firebase
        final ProgressDialog dialog = new ProgressDialog(context);
        //ProgressDialog.show(context, "Loading", "Loading");
        dialog.setTitle("Loading data");
        dialog.setCancelable(false);
        dialog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Creamos una referencia al documento que almacena los heroes del usuario a traves del
        // path del documento de la coleccion users.
        DocumentReference docRef = db.collection("users").document(usrUID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    assert doc != null;
                    if (doc.exists()){
                        // Convertimos el documento en un listado de Map<String, Object>, que cada
                        // uno representara un listado de los atributos de cada uno de los heroes.
                        List<Map<String, Object>> dbHeroes = (List<Map<String, Object>>) doc.get("heroes");

                        // Recorremos la lista de elementos y por cada Map recogemos los diferentes
                        // atributos pertenecientes al heroe.
                        // Creamos un int iterator que nos ayudara a mantener el size de la lista
                        // en 3
                        int iterator = 0;
                        assert dbHeroes != null;
                        for (Map<String, Object> element: dbHeroes) {
                            if (element == null){
                                // Si el elemento que comprobamos es null, vamos al siguiente
                                // incrementando el iterator para cambiar la posicion en la lista
                                iterator++;
                                continue;
                            }
                            // Si el elemento no es null, buscamos los atributos y los usamos para
                            // crear el heroe y asignarlo a su posicion correspondiente indicada
                            // por el iterator
                            try {

                                int luck = ((Long) element.get("luck")).intValue();
                                int lvl = ((Long) element.get("lvl")).intValue();
                                int mp = ((Long) element.get("mp")).intValue();
                                int lp = ((Long) element.get("lp")).intValue();
                                int strength = ((Long) element.get("strength")).intValue();
                                int maxLp = ((Long) element.get("maxLp")).intValue();
                                int maxMp = ((Long) element.get("maxMp")).intValue();

                                List<Item> inventory = getDBItemList(
                                        (List<Map<String, Object>>) element.get("inventory"));
                                List<Ability> actives = getDBActivesList(
                                        (List<Map<String, Object>>) element.get("actives"));
                                List<Ability> passives = getDBPassivesList(
                                        (List<Map<String, Object>>) element.get("passives"));
                                Weapon weapon;
                                Map weaponMap = (Map) element.get("weapon");

                                switch ((String) weaponMap.get("weaponType")){
                                    case "SWORD":
                                        weapon = new Sword((String) weaponMap.get("name"),
                                                (String) weaponMap.get("description"),
                                                ((Long) weaponMap.get("durability")).intValue(),
                                                (boolean) weaponMap.get("equipped"),
                                                ((Long) weaponMap.get("damage")).intValue(),
                                                Weapon.WeaponType.SWORD);
                                        break;
                                    case "WAND":
                                        weapon = new Wand((String) weaponMap.get("name"),
                                                (String) weaponMap.get("description"),
                                                ((Long) weaponMap.get("durability")).intValue(),
                                                (boolean) weaponMap.get("equipped"),
                                                ((Long) weaponMap.get("magic")).intValue(),
                                                Weapon.WeaponType.WAND);
                                        break;
                                    default: weapon = null;
                                }

                                int reqExp = ((Long) element.get("reqExp")).intValue();
                                int defense = ((Long) element.get("defense")).intValue();
                                String name = (String) element.get("name");
                                int agility = ((Long) element.get("agility")).intValue();
                                int exp = ((Long) element.get("exp")).intValue();
                                Map dungeonProgressMap = (Map) element.get("dungeonProgress");

                                int run = ((Long) dungeonProgressMap.get("run")).intValue();
                                int floor = ((Long) dungeonProgressMap.get("floor")).intValue();
                                int stagePos = ((Long) dungeonProgressMap.get("stagePos")).intValue();

                                DungeonProgress dp = new DungeonProgress(floor,
                                        stagePos, run, new ArrayList<>());
                                Hero h;

                                switch ((String)element.get("heroClass")){
                                    case "WARRIOR":
                                        h = new Warrior(name, lvl, maxMp, maxLp, lp, mp, strength,
                                                defense, agility, luck, reqExp, exp, actives,
                                                passives, inventory, weapon, Hero.HeroClass.WARRIOR,
                                                dp);
                                        break;
                                    case "WIZARD":
                                        h = new Wizard(name, lvl, maxMp, maxLp, lp, mp, strength,
                                                defense, agility, luck, reqExp, exp,
                                                actives, passives, inventory, weapon,
                                                ((Long) element.get("intelligence")).intValue(),
                                                Hero.HeroClass.WIZARD, dp);
                                        break;
                                    default:
                                        h = null;
                                }
                                heroes.set(iterator++, h);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                                heroes.set(iterator++, null);
                            }
                        }
                    } else {
                        // Si el documento no existe,lo informamos con Toast y mostramos en el log.
                        Toast.makeText(context, "You have no heroes.", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "No Hero list document exists.");

                    }

                    dialog.dismiss();

                    if (fromStart && manager != null)
                        loadFragment(manager, HeroSelectFragment.newInstance(usrUID));
                } else {
                    // Si da un error la task, lo informamos con Toast y mostramos el error en el log
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Task error: " + task.getException());

                    dialog.dismiss();
                }
            }

        });
    }

    /**
     * Metodo para cargar los enemigos de la base de datos al listado local.
     *
     * */
    public static void loadEnemies(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Creamos una referencia al documento que almacena los heroes del usuario a traves del
        // path del documento de la coleccion users.
        DocumentReference docRef = db.collection("enemies").document("demons");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    Enemy enemy;
                    assert doc != null;
                    if (doc.exists()){

                        Map<String, Object> enemyMap = (Map<String, Object>) doc.get("Kappa");
                        assert enemyMap != null;
                        String name = (String) enemyMap.get("name");
                        String type = (String) enemyMap.get("type");
                        int agility = ((Long) enemyMap.get("agility")).intValue();
                        int strength = ((Long) enemyMap.get("strength")).intValue();
                        int defense = ((Long) enemyMap.get("defense")).intValue();
                        int luck = ((Long) enemyMap.get("luck")).intValue();
                        List<Ability> passives = new ArrayList<>();
                        List<Ability> actives = new ArrayList<>();

                        switch (type){
                            case "DEMON":
                                enemy = new Enemy(name, actives ,passives, Enemy.Types.DEMON,
                                        strength, defense, agility, luck);
                                break;
                            case "HUMAN":
                                enemy = new Enemy(name, actives ,passives, Enemy.Types.HUMAN,
                                    strength, defense, agility, luck);
                                break;
                            default: enemy = null;
                        }

                        if (!enemies.contains(enemy))
                            enemies.add(enemy);
                        assert enemy != null;
                        Log.i(TAG, enemy.toString());
                    }
                }

            }
        });
    }

    /**
     * Metodo para parsear los items del inventario recogidos de la BBDD.
     *
     * @param dbItemList Listado de items recibido de la BBDD.
     * @return listado de objetos Item parseados.
     * */
    private static List<Item> getDBItemList(List<Map<String, Object>> dbItemList) {
        // creamos la lista definitiva que vamos a devolver
        List<Item> itemList = new ArrayList<>();

        // Recorremos el listado de la base de datos recogiendo los valores y agregando los objetos
        // a la lista
        for (Map<String, Object> element: dbItemList) {
            String description = (String) element.get("description");
            String effect = (String) element.get("effect");
            String name = (String) element.get("name");
            int quantity = ((Long) element.get("quantity")).intValue();
            int value = ((Long) element.get("value")).intValue();
            Potion.Types type;
            switch ((String)element.get("type")){
                case "LIFE_POTION":
                    type = Potion.Types.LIFE_POTION;
                    break;
                case "MANA_POTION":
                    type = Potion.Types.MANA_POTION;
                    break;
                case "ELIXIR":
                    type = Potion.Types.ELIXIR;
                    break;
                default:type = null;
            }

            itemList.add(new Potion(name, description, quantity, effect, type, value));
        }
        // Devolvemos la lista
        return itemList;
    }

    /**
     * Metodo para parsear las habilidades activas recogidas de la BBDD.
     *
     * @param dbActives Listado de habilidades recibido de la BBDD.
     * @return listado de objetos Item parseados.
     * */
    private static List<Ability> getDBActivesList(List<Map<String, Object>> dbActives) {
        // Creamos la lista definitiva que vamos a devolver
        List<Ability> actives = new ArrayList<>();

        // Recorremos el listado de la base de datos recogiendo los valores y agregando las
        // habilidades a la lista
        for (Map<String, Object> element : dbActives) {
            String definition = (String) element.get("definition");
            String effect = (String) element.get("effect");
            String ability = (String) element.get("ability");
            int cost = ((Long) element.get("cost")).intValue();
            boolean unlocked;
            if (element.get("unlocked").equals("true"))
                unlocked = true;
            else
                unlocked = false;
            Ability.EffectType effectType;
            switch ((String) element.get("effectType")) {
                case "REGEN":
                    effectType = Ability.EffectType.REGEN;
                    break;
                case "DMG":
                    effectType = Ability.EffectType.DMG;
                    break;
                case "STATUS_UP":
                    effectType = Ability.EffectType.STATUS_UP;
                    break;
                default:
                    effectType = null;
            }

            actives.add(new Active(ability, definition, effect, cost, unlocked,
                    Ability.AbilityType.ACTIVE, effectType));
        }
        // Devolvemos la lista
        return actives;
    }

    /**
     * Metodo para parsear las habilidades pasivas recibidas de la BBDD.
     *
     * @param dbPassives Listado de habilidades recibido de la BBDD.
     * @return listado de habilidades parseadas.
     **/
    private static List<Ability> getDBPassivesList(List<Map<String, Object>> dbPassives) {
        // Creamos la lista definitiva que vamos a devolver
        List<Ability> passives = new ArrayList<>();

        // Recorremos el listado de la base de datos recogiendo los valores y agregando las
        // habilidades a la lista
        for (Map<String, Object> element: dbPassives) {
            String definition = (String) element.get("definition");
            String effect = (String) element.get("effect");
            String ability = (String) element.get("ability");
            boolean unlocked = element.get("unlocked").equals("true");

            Ability.EffectType effectType;
            switch ((String)element.get("effectType")){
                case "REGEN":
                    effectType = Ability.EffectType.REGEN;
                    break;
                case "DMG":
                    effectType = Ability.EffectType.DMG;
                    break;
                case "STATUS_UP":
                    effectType = Ability.EffectType.STATUS_UP;
                    break;
                default:effectType = null;
            }

            passives.add(new Passive(ability, definition, effect, unlocked,
                    Ability.AbilityType.ACTIVE, effectType));
        }
        // Devolvemos la lista
        return passives;
    }

    /**
     * Metodo para actualizar el listado de heroes del usuario.
     *
     * @param usrUID UID del usuario logueado.
     * */
    public static void updateDbHeroList(String usrUID){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> docData = new HashMap<>();
        docData.put("heroes", heroes);
        db.collection("users").document(usrUID).set(docData);
    }

    /**
     * Metodo para modificar un heroe en una posicion concreta.
     *
     * @param pos Posicion del heroe en la lista.
     * @param hero Heroe a insertar en la lista.
     * */
    public static void updateHeroPos(int pos, Hero hero){
        heroes.set(pos, hero);
    }

    /**
     * Metodo para vaciar la lista local de heroes
     *
     * */
    public static void clearHeroList(){
        heroes.clear();
    }

    /**
     * Metodo que devuelve un enemigo del listado local.
     *
     * @return Primer enemigo del listado.
     * */
    public static Enemy getEnemy(int floor, int stagePos){
        // Preparamos el enemigo en base al progreso y lo devolvemos.
        return prepareEnemy(floor);
    }

    /**
     * Metodo que prepara un enemigo del listado local.
     *
     * @return Primer enemigo del listado.
     * */
    private static Enemy prepareEnemy(int floor) {
        // Recogemos el primer enemigo del listado y ascendemos su nivel hasta que cuadre con el
        // progreso del heroe
        Enemy enemy = enemies.get(0);
        while (enemy.getLvl() < floor){
            enemy.lvlUp();
            statusLvlUp(enemy);
        }
        // Seteamos los LP y MP del enemigo al maximo y lo devolvemos
        enemy.setMp(enemy.getMaxMp());
        enemy.setLp(enemy.getMaxLp());
        return enemy;
    }

    /**
     * Metodo para cargar un fragment
     *
     * @param manager Manager para gestionar los fragments
     * @param fragment Fragment a usar
     * */
    public static void loadFragment(FragmentManager manager, Fragment fragment) {

            // A traves del FragmentManager realizamos la transaccion
            String newFragment = fragment.getClass().getName();
            manager.beginTransaction()
                .replace(R.id.container, fragment, newFragment)
                .commit();
    }

    /**
     * Metodo que gestiona la mejora de las caracteristicas de un personaje
     *
     * @param ch Personaje al que mejorar
     * */
    public static String statusLvlUp(Character ch) {

        // Comprobamos si es un heroe o un enemigo y aumentamos alearoriamente una cantidad
        // especifica de caracteristicas del personaje
        if (ch instanceof Hero) {
            Hero hero = (Hero) ch;
            StringBuilder builder = new StringBuilder();
            int ag = 0, def = 0, intel = 0, str = 0, lck = 0,
                lp = defVal + hero.getLvl() * 20, mp = defVal + hero.getLvl() * 20;
            for (int i = 0; i < statsUp; i++) {
                switch(randomNumber(1, statsUp)){
                    case 1:
                        hero.setAgility(hero.getAgility()+1);
                        ag++;
                        break;
                    case 2:
                        hero.setDefense(hero.getDefense()+1);
                        def++;
                        break;
                    case 3:
                        if (hero instanceof Wizard) {
                            ((Wizard)hero).setIntelligence(((Wizard)hero).getIntelligence()+1);
                            intel++;
                        } else {
                            hero.setStrength(hero.getStrength() + 1);
                            str++;
                        }
                        break;
                    case 4:
                        hero.setLuck(hero.getLuck()+1);
                        lck++;
                        break;
                }
            }
            if (hero instanceof Wizard){
                int extraLP = (lp/2), extraMP = (int)(mp*1.5);
                hero.setMaxLp(hero.getMaxLp() + extraLP);
                hero.setMaxMp(hero.getMaxMp() + extraMP);
                builder.append("LP + ").append(extraLP).append("\nMP + ").append(extraMP)
                        .append("\nAgility + ").append(ag).append("\nDefense + ").append(def)
                        .append("\nIntelligence + ").append(intel).append("\nLuck + ")
                        .append(lck).append("\n");
            } else {
                int extraMP = (int)(mp*.75);
                hero.setMaxLp(hero.getMaxLp() + lp);
                hero.setMaxMp(hero.getMaxMp() + extraMP);
                builder.append("LP + ").append(lp).append("\nMP + ").append(extraMP)
                        .append("\nAgility + ").append(ag).append("\nDefense + ").append(def)
                        .append("\nStrength + ").append(str).append("\nLuck + ").append(lck).append("\n");
            }
            // Devolvemos un StringBuilder que indica las caracteristicas mejoradas
                return builder.toString();
        } else if (ch instanceof Enemy) {
            Enemy enemy = (Enemy) ch;
            StringBuilder builder = new StringBuilder();
            int ag = 0, def = 0, intel = 0, str = 0, lck = 0,
                    lp = defVal + enemy.getLvl() * 20, mp = defVal + enemy.getLvl() * 20;
            for (int i = 0; i < statsUp; i++) {
                switch (randomNumber(1, statsUp)) {
                    case 1:
                        enemy.setAgility(enemy.getAgility() + 1);
                        ag++;
                        break;
                    case 2:
                        enemy.setDefense(enemy.getDefense() + 1);
                        def++;
                        break;
                    case 3:
                        enemy.setStrength(enemy.getStrength() + 1);
                        str++;
                        break;
                    case 4:
                        enemy.setLuck(enemy.getLuck() + 1);
                        lck++;
                        break;
                }
            }
            int extraMP = (int)(mp*.75);
            enemy.setMaxLp(enemy.getMaxLp() + lp);
            enemy.setMaxMp(enemy.getMaxMp() + extraMP);

            builder.append("LP + ").append(lp).append("\nMP + ").append(extraMP)
                    .append("\nAgility + ").append(ag).append("\nDefense + ").append(def)
                    .append("\nStrength + ").append(str).append("\nLuck + ").append(lck).append("\n");
            // Devolvemos un StringBuilder que indica las caracteristicas mejoradas
            return builder.toString();
        }
        return null;
    }

    /**
     * Metodo que devuelve el listado de habilidades de Guerrero
     *
     * @return Listado de habilidades activas y pasivas del Guerrero
     * */
    public static List<List<Ability>> getWarriorAbilities() {
        // Listado con los listados de habilidades
        List<List<Ability>> abilities = new ArrayList<>();
        // Listados de habilidades
        List<Ability> actives = new ArrayList<>();
        List<Ability> passives = new ArrayList<>();

        // Agregamos las habilidades activas a la lista
        actives.add(
                new Active(
                        "Big slash", "The hero brandishes his sword strongly towards his enemy.",
                        "physDmg 120", 30, true, Ability.AbilityType.ACTIVE,
                        Ability.EffectType.DMG));
        actives.add(
                new Active(
                        "Heroic thrust", "The hero uses his sword to thrust the enemy.",
                        "physDmg 500", 200, true, Ability.AbilityType.ACTIVE,
                        Ability.EffectType.DMG));

        // Agregamos las habilidades pasivas a la lista
        passives.add(new Passive("High speed", "The benefits of a constant, tough " +
                "trainig has granted the hero great speed, letting him get multiple hits to the " +
                "enemy at once.", "ATKS+1", true, Ability.AbilityType.PASSIVE,
                Ability.EffectType.STATUS_UP));

        // Agregamos las listas a la lista principal
        abilities.add(actives);
        abilities.add(passives);
        // Devolvemos la lista principal
        return abilities;
    }

    /**
     * Metodo que devuelve el listado de habilidades de Mago
     *
     * @return Listado de habilidades activas y pasivas del Mago
     * */
    public static List<List<Ability>> getWizardAbilities() {
        // Listado con los listados de habilidades
        List<List<Ability>> abilities = new ArrayList<>();
        // Listados de habilidades
        List<Ability> actives = new ArrayList<>();
        List<Ability> passives = new ArrayList<>();

        // Agregamos las habilidades activas a la lista
        actives.add(
                new Active(
                        "Fireball", "The hero uses his magic to launch a fireball " +
                        "towards the enemy.", "fireDmg 60", 20, true,
                        Ability.AbilityType.ACTIVE, Ability.EffectType.DMG));
        actives.add(
                new Active(
                        "Almighty Beam", "The hero uses his sword to thrust the enemy.",
                        "divineDmg 1000", 1500, false, Ability.AbilityType.ACTIVE,
                        Ability.EffectType.DMG));

        // Agregamos las habilidades pasivas a la lista
        passives.add(new Passive("High knowledge", "A full life studying and reading" +
                " about the ancient secrets of magic arts granted this hero with a strong and solid " +
                "mind, giving him great intelligence.", "Intel+1", true,
                Ability.AbilityType.PASSIVE, Ability.EffectType.STATUS_UP));

        // Agregamos las listas a la lista principal
        abilities.add(actives);
        abilities.add(passives);
        // Devolvemos la lista principal
        return abilities;
    }

    //// Generic Utils ////

    /**
     * Metodo para cargar los sonidos para el juego
     *
     * @param context Contexto para acceder a los recursos
     * */
    public static void loadSounds(Context context){
        soundIds[0] = sp.load(context, R.raw.hit, 1);
        soundIds[1] = sp.load(context, R.raw.dodge, 1);
    }

    /**
     * Metodo que ejecuta el sonido indicado
     *
     * @param pos Posicion en la lista del sonido
     * @param vol Nivel de volumen para el sonido a reproducir
     * */
    public static void playSound(int pos, float vol) {
        sp.play(soundIds[pos], vol, vol, 1, 0, 1.0f);
    }

    /**
     * Metodo para generar un numero aleatorio
     *
     * @param min Numero minimo del aleatorio
     * @param max Numero maximo del aleatorio
     * */
    public static int randomNumber(int min, int max){
        Random rnd = new Random();
        return rnd.nextInt(max-min+1)+min;
    }

    /**  GETTERS Y SETTERS  **/
    public static List<Hero> getHeroes(){
        return heroes;
    }

    public static List<Enemy> getEnemies() {
        return enemies;
    }

    public static float getMusicVol() {
        return musicVol;
    }

    public static void setMusicVol(float musicVol) {
        DbUtils.musicVol = musicVol;
    }

    public static float getSfxVol() {
        return sfxVol;
    }

    public static void setSfxVol(float sfxVol) {
        DbUtils.sfxVol = sfxVol;
    }

    public static MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public static void setMediaPlayer(MediaPlayer mediaPlayer) {
        DbUtils.mediaPlayer = mediaPlayer;
    }
}


