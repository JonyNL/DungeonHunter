package com.jonyn.dungeonhunter.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jonyn.dungeonhunter.R;
import com.jonyn.dungeonhunter.models.Ability;
import com.jonyn.dungeonhunter.models.Hero;
import com.jonyn.dungeonhunter.models.Item;
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
 * Fragment seleccion de heroe
 *
 * */
public class HeroSelectFragment extends Fragment {

    public static final String TAG = "HERO_SELECT_FRAGMENT";
    public static final String FB_USER_UID = "FB_USER_UID";
    public static final String HERO_POS = "HERO_POS";
    public static final String HERO_CLASS = "HERO_CLASS";
    public static final String HERO_NAME = "HERO_NAME";

    private FirebaseFirestore db;
    private String usrUID;

    // Elementos del Layout
    private Button btnHero1;
    private Button btnHero2;
    private Button btnHero3;

    // Lista de heroes del usuario
    private final List<Hero> heroes = new ArrayList<>(3);

    // Constructor vacio
    public HeroSelectFragment() {};

    /**
     * Metodo para crear una instancia de HeroSelectFragment con los parametros especificados.
     *
     * @param userID ID del usuario logueado.
     * @return una nueva instancia de BattleFragment.
     * */
    public static HeroSelectFragment newInstance(String userID) {

        Bundle args = new Bundle();
        args.putString(FB_USER_UID, userID);
        HeroSelectFragment fragment = new HeroSelectFragment();
        fragment.setArguments(args);
        return fragment;
    }

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

        heroes.add(null);
        heroes.add(null);
        heroes.add(null);

        // Asignamos las variables de los botones a sus vistas correspondientes
        btnHero1 = v.findViewById(R.id.btnHero1);
        btnHero2 = v.findViewById(R.id.btnHero2);
        btnHero3 = v.findViewById(R.id.btnHero3);

        // Recibimos los argumentos del fragment y asignamos el UID del usuario
        Bundle b = getArguments();
        usrUID = b.getString(FB_USER_UID);
        db = FirebaseFirestore.getInstance();

        int hPos = b.getInt(HERO_POS);
        String heroName = b.getString(HERO_NAME);
        if (heroName!= null){
            switch (b.getString(HERO_CLASS)) {
                case "Warrior":
                    heroes.set(hPos, new Warrior(heroName));
                    break;
                case "Wizard":
                    heroes.set(hPos, new Wizard(heroName));
                    break;
                default:
                    Toast.makeText(getContext(), "Hero class switch Error", Toast.LENGTH_SHORT).show();
            }

        }

        // Map<String, Hero> heroes = new HashMap<String, Hero>(3);

        /*
        heroes.add(new Warrior("Warrior"));
        heroes.add(new Wizzard("Wizzard"));
        Map<String, Object> docData = new HashMap<>();
        docData.put("heroes", heroes);
        db.collection("users").document("Heroes").set(docData);*/

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

                        //if (!dbHeroes.isEmpty())
                        // Recorremos la lista de elementos y por cada Map recogemos los diferentes
                        // atributos pertenecientes al heroe.
                        int iterator = 0;
                        for (Map<String, Object> element: dbHeroes) {
                            if (element == null){
                                iterator++;
                                continue;
                            }
                            try {

                                int luck = ((Long) element.get("luck")).intValue();
                                int lvl = ((Long) element.get("lvl")).intValue();
                                int mp = ((Long) element.get("mp")).intValue();
                                int lp = ((Long) element.get("lp")).intValue();
                                int strength = ((Long) element.get("strength")).intValue();
                                int maxLp = ((Long) element.get("maxLp")).intValue();
                                int maxMp = ((Long) element.get("maxMp")).intValue();
                                List<Item> inventory = (List<Item>) element.get("inventory");
                                List<Ability> actives = (List<Ability>) element.get("actives");
                                List<Ability> passives = (List<Ability>) element.get("passives");
                                Weapon weapon = null;
                                Map weaponMap = (Map) element.get("weapon");
                                if (weaponMap.get("damage") != null) {
                                    weapon = new Sword((String) weaponMap.get("name"),
                                            (String) weaponMap.get("description"),
                                            ((Long) weaponMap.get("durability")).intValue(),
                                            (boolean) weaponMap.get("equipped"),
                                            ((Long) weaponMap.get("damage")).intValue());
                                }

                                if (weaponMap.get("magic") != null) {
                                    weapon = new Wand((String) weaponMap.get("name"),
                                            (String) weaponMap.get("description"),
                                            ((Long) weaponMap.get("durability")).intValue(),
                                            (boolean) weaponMap.get("equipped"),
                                            ((Long) weaponMap.get("magic")).intValue());
                                }

                                int reqExp = ((Long) element.get("reqExp")).intValue();
                                int defense = ((Long) element.get("defense")).intValue();
                                String name = (String) element.get("name");
                                int agility = ((Long) element.get("agility")).intValue();
                                int exp = ((Long) element.get("exp")).intValue();

                                Hero h;
                                if (element.get("intelligence") != null) {
                                    h = new Wizard(name, strength, defense, agility, luck, actives,
                                            passives, inventory, weapon, ((Long) element.get("intelligence")).intValue());
                                } else {
                                    h = new Warrior(name, lvl, maxMp, maxLp, lp, mp, strength, defense,
                                            agility, luck, reqExp, exp, actives, passives, inventory, weapon);
                                }

                                heroes.set(iterator++, h);
                            } catch (NullPointerException e) {
                                heroes.set(iterator++, null);
                            }
                            /*
                            for (Map.Entry entry :element.entrySet()) {
                                Log.i("ENTRY_KEY", ""+entry.getKey());
                                Log.i("ENTRY_VALUE", ""+entry.getValue());
                            }*/
                        }

                        Map<String, Object> docData = new HashMap<>();
                        docData.put("heroes", heroes);
                        db.collection("users").document(usrUID).set(docData);
                        Toast.makeText(getContext(), "Hero(es) added to DB", Toast.LENGTH_SHORT).show();


                        updateButtonMsg();

                        //List<Hero> heroes = doc.toObject(HeroDocument.class).heroes;
                    } else {
                        // Si el documento no existe, creamos un listado Map desde una lista de
                        // heroes vacia y la asignamos al usuario.
                        Toast.makeText(getContext(), "You have no heroes.", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "No Hero list document exists.");
                        Map<String, Object> docData = new HashMap<>();
                        docData.put("heroes", heroes);
                        db.collection("users").document(usrUID).set(docData);
                        Log.i(TAG, "Hero list document created.");

                    }
                    
                    //Map<String, Object> heroes = doc.getData();
                    /*for (Map.Entry entry :heroes.entrySet()) {
                        Hero h = (Hero)entry.getValue();
                        Log.i("HEROES", entry.getKey()+" - "+ entry.getValue());

                    }*/
                    //Hero hr = heroes.get(0);
                    //Log.i("HEROES", hr.getName());


                } else {
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Task error: " + task.getException());
                }
            }
        });

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btnHero1:
                        checkButtonHero(0);
                        break;
                    case R.id.btnHero2:
                        checkButtonHero(1);
                        break;
                    case R.id.btnHero3:
                        checkButtonHero(2);
                        break;
                }
            }
        };


        btnHero1.setOnClickListener(listener);
        btnHero2.setOnClickListener(listener);
        btnHero3.setOnClickListener(listener);


        return v;
    }

    void checkButtonHero(int pos) {
        if (!heroes.isEmpty() && heroes.size() >= pos+1 && heroes.get(pos) != null){
            // TODO: Implementar fragment de mazmorra y lanzarlo

            // region Testing purposes

            Toast.makeText(getContext(), heroes.get(pos).getName() + " -- " +
                    heroes.get(pos).getClass().getSimpleName(), Toast.LENGTH_SHORT).show();
            /*heroes.set(pos, null);
            updateButtonMsg();*/

            // endregion

        } else {
            // TODO: Lanzar fragment de creacion de heroe

            Toast.makeText(getContext(), "No hero in Slot", Toast.LENGTH_SHORT).show();

            FragmentManager manager = getFragmentManager();

            // Creamos un BattleFragment pasandole como parametros el heroe y el enemigo.
            Fragment fragment = NewHeroFragment.newInstance(usrUID, pos);

            // A traves del FragmentManager que guardamos antes, realizamos la transaccion
            // del fragment agregandolo al BackStack.
            String newFragment = fragment.getClass().getName();
            manager.popBackStack();
            manager.beginTransaction()
                    .replace(R.id.container, fragment, newFragment)
                    //.addToBackStack(null)
                    .commit();

            // region Testing purposes
            /*Hero h = new Warrior("Paco"+Math.random());
            heroes.set(pos, h);
            Toast.makeText(getContext(), heroes.get(pos).getName() + " added", Toast.LENGTH_SHORT).show();
            updateButtonMsg();
            Map<String, Object> docData = new HashMap<>();
            docData.put("heroes", heroes);
            db.collection("users").document(usrUID).set(docData);
            Log.i(TAG, "Hero list document updated.");*/

            // endregion

        }
    }

    void updateButtonMsg(){
        // Comprobamos si el usuario tiene heroes, y asignamos su nombre al boton
        // que le corresponde.
        if (!heroes.isEmpty()) {
            btnHero1.setText(getString(R.string.slot));
            btnHero2.setText(getString(R.string.slot));
            btnHero3.setText(getString(R.string.slot));
            for (int i = 0; i < heroes.size(); i++) {
                switch (i) {
                    case 0:
                        if (heroes.get(i)!= null)
                            btnHero1.setText(heroes.get(i).getName());
                        break;
                    case 1:
                        if (heroes.get(i)!= null)
                            btnHero2.setText(heroes.get(i).getName());
                        break;
                    case 2:
                        if (heroes.get(i)!= null)
                            btnHero3.setText(heroes.get(i).getName());
                        break;
                }
            }
        }else Toast.makeText(getContext(), "You have no heroes.", Toast.LENGTH_SHORT).show();
    }
}
