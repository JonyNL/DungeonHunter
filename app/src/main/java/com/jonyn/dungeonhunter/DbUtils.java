package com.jonyn.dungeonhunter;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jonyn.dungeonhunter.models.Ability;
import com.jonyn.dungeonhunter.models.Enemy;
import com.jonyn.dungeonhunter.models.Hero;
import com.jonyn.dungeonhunter.models.Item;
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

    // Atributos estaticos necesarios para la gestion de la clase
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static List<Hero> heroes = new ArrayList<>(3);
    private static List<Enemy> enemies = new ArrayList<>();

    /**
     * Metodo para cargar los heroes del usuario al listado local.
     *
     * @param context Contexto necesario para algunas operaciones.
     * @param usrUID UID del usuario logueado.
     * */
    public static void loadHeroList(final Context context, final String usrUID) {

        // Agregamos elementos null al listado para mantener el size en la cantidad deseada (3)
        heroes.add(null);
        heroes.add(null);
        heroes.add(null);

        // Creamos una referencia al documento que almacena los heroes del usuario a traves del
        // path del documento de la coleccion users.
        DocumentReference docRef = db.collection("users").document(usrUID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()){
                        // Convertimos el documento en un listado de Map<String, Object>, que cada
                        // uno representara un listado de los atributos de cada uno de los heroes.
                        List<Map<String, Object>> dbHeroes = (List<Map<String, Object>>) doc.get("heroes");

                        // Recorremos la lista de elementos y por cada Map recogemos los diferentes
                        // atributos pertenecientes al heroe.
                        // Creamos un int iterator que nos ayudara a mantener el size de la lista
                        // en 3
                        int iterator = 0;
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

                                List<Item> inventory = getDBItemList((List<Map<String, Object>>) element.get("inventory"));
                                List<Ability> actives = (List<Ability>) element.get("actives");
                                List<Ability> passives = (List<Ability>) element.get("passives");
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

                                Hero h;

                                switch ((String)element.get("heroClass")){
                                    case "WARRIOR":
                                        h = new Warrior(name, lvl, maxMp, maxLp, lp, mp, strength, defense,
                                                agility, luck, reqExp, exp, actives, passives, inventory, weapon,
                                                Hero.HeroClass.WARRIOR);
                                        break;
                                    case "WIZARD":
                                        h = new Wizard(name, strength, defense, agility, luck, actives,
                                                passives, inventory, weapon, ((Long) element.get("intelligence")).intValue(),
                                                Hero.HeroClass.WIZARD);
                                        break;
                                    default:
                                        h = null;
                                }
                                heroes.set(iterator++, h);
                            } catch (NullPointerException e) {
                                heroes.set(iterator++, null);
                            }
                        }
                    } else {
                        // Si el documento no existe,lo informamos con Toast y mostramos en el log.
                        Toast.makeText(context, "You have no heroes.", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "No Hero list document exists.");

                    }
                } else {
                    // Si da un error la task, lo informamos con Toast y mostramos el error en el log
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Task error: " + task.getException());
                }
            }
        });

    }
    /**
     * Metodo para cargar los enemigos de la base de datos al listado local.
     *
     * */
    public static void loadEnemies(){
        // Creamos una referencia al documento que almacena los heroes del usuario a traves del
        // path del documento de la coleccion users.
        DocumentReference docRef = db.collection("enemies").document("demons");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    Enemy enemy;
                    if (doc.exists()){

                        Map<String, Object> enemyMap = (Map<String, Object>) doc.get("Kappa");
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
                            case "HUMAN":enemy = new Enemy(name, actives ,passives, Enemy.Types.HUMAN,
                                    strength, defense, agility, luck);
                                break;
                            default: enemy = null;
                        }

                        enemies.add(enemy);
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
        List<Item> itemList = new ArrayList<>();

        for (Map<String, Object> element: dbItemList) {
            String description = (String) element.get("description");
            String effect = (String) element.get("effect");
            String name = (String) element.get("name");
            int quantity = ((Long) element.get("quantity")).intValue();
            int value = ((Long) element.get("value")).intValue();
            Potion.Types type;
            switch ((String)element.get("type")){
                case "HEALTH_POTION":
                    type = Potion.Types.HEALTH_POTION;
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

        return itemList;
    }

    /**
     * Metodo para actualizar el listado de heroes del usuario.
     *
     * @param usrUID UID del usuario logueado.
     * */
    public static void updateDbHeroList(String usrUID){
        Map<String, Object> docData = new HashMap<>();
        docData.put("heroes", heroes);
        db.collection("users").document(usrUID).set(docData);
    }

    /**
     * Metodo para agregar un enemigo a la BBDD.
     *
     * @param name Nombre del enemigo.
     * @param type Tipo de enemigo.
     * */
    public static void addEnemy(String name, Enemy.Types type){

        Map<String, Object> enemyDoc = new HashMap<>();
        Enemy enemy = new Enemy(name, type);
        enemyDoc.put(enemy.getName(), enemy);
        switch (enemy.getType()){
            case DEMON:
                db.collection("enemies")
                        .document("demons").set(enemyDoc);
                break;
            case HUMAN:
                db.collection("enemies")
                        .document("humans").set(enemyDoc);
                break;
        }
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
     * Metodo para obtener el listado de heroes local.
     *
     * @return listado de heroes locales.
     * */
    public static List<Hero> getHeroes(){
        return heroes;
    }

    /**
     * Metodo que devuelve un enemigo del listado local.
     *
     * @return Primer enemigo del listado.
     * */
    public static Enemy getEnemy(){
        return enemies.get(0);
    }
}
