package com.jonyn.dungeonhunter.models;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Enemy extends Character {
    public static String TAG = "package com.jonyn.dungeonhunter.models.ENEMY";
    // Enumerado que indica el tipo de enemigo.
    public enum Types{
        DEMON,
        HUMAN
    }

    // Variables de la clase.
    private Types type;

    // Constructor sin parametros
    public Enemy(){}

    // Constructor con todos los parametros parametros.
    public Enemy(String name, List<Ability> actives, List<Ability> passives, Types type,
                 int strength, int defense, int agility, int luck) {
        super(name, strength, defense, agility, luck, actives, passives);
        this.type = type;
    }

    // Constructor con parametros minimos.
    public Enemy(String name, Types type){
        super(name, 8, 5, 7, 4, new ArrayList<Ability>(), new ArrayList<Ability>());
        this.type = type;
    }

    // Sobreescribimos el metodo de atacar para personalizarlo.
    @Override
    public String attack(Character hero) {
        int hitValue = (int) (Math.random()*100 + 1) + this.luck;
        Log.i(TAG, Integer.toString(hitValue));

        if (hitValue < 10){
            return this.name + " tried to attack " + hero.getName()
                    +" but failed.\n----------------------------------------\n";
        } else if (hitValue > 90) {
            int hitDmg = (int)
                    ((this.strength + 20 - (hero.defense * .25))*(Math.random()+1));
            hero.setLp(hero.getLp() - hitDmg);
            return this.name + " attacked and dealt "+ hitDmg +" critical damage to "
                    + hero.getName() +".\n----------------------------------------\n";
        } else {
            if (hitValue - (hero.agility + hero.luck) > 25) {
                int hitDmg = (int)
                        ((this.strength + 10 - (hero.defense * .5))*(Math.random()+1));
                hero.setLp(hero.getLp() - hitDmg);

                return this.name + " attacked and dealt "+ hitDmg +" damage to " + hero.getName()
                        +".\n----------------------------------------\n";
            } else
                return this.name + " tried to attack " + hero.getName()
                        +" but failed.\n----------------------------------------\n";


        }
    }

    /** GETTERS Y SETTERS */
    public Types getType() {
        return type;
    }

}
