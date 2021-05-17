package com.jonyn.dungeonhunter.models;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Wizard extends Hero {
    public static String TAG = "package com.jonyn.dungeonhunter.models.WIZARD";

    private int intelligence = 2;

    // Constructor sin parametros.
    public Wizard(){}

    // Constructor con parametros.
    public Wizard(String name, int strength, int defense, int agility, int luck,
                  List<Ability> actives, List<Ability> passives, List<Item> inventory,
                  Weapon weapon, int intelligence, HeroClass heroClass) {
        super(name, strength, defense, agility, luck, actives, passives, inventory, weapon, heroClass);
        this.intelligence =  intelligence;
    }

    // Constructor con todos los parametros
    public Wizard(String name, int lvl, int maxMp, int maxLp, int lp, int mp, int strength,
                  int defense, int agility, int luck, int reqExp, int exp,
                  List<Ability> actives, List<Ability> passives, List<Item> inventory,
                  Weapon weapon, int intelligence, HeroClass heroClass) {
        super(name, lvl, maxMp, maxLp, lp, mp, strength, defense, agility, luck, reqExp, exp,
                actives, passives, inventory, weapon, heroClass);
        this.intelligence = intelligence;
    }


    // Constructor con un parametro.
    public Wizard(String name) {
        super(name, 3, 2, 8, 10, new ArrayList<Ability>(),
                new ArrayList<Ability>(), new ArrayList<Item>(),
                new Wand("Wooden wand", "Simple wand made of wood",
                        100, true, 10, Weapon.WeaponType.WAND),
                HeroClass.WIZARD);
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }

    public int getIntelligence() {
        return intelligence;
    }

    /** Metodos de utilidad */
    @Override
    public String attack(Character enemy) {

        int hitValue = (int) (Math.random()*100 + 1) + this.luck;
        Log.i(TAG, Integer.toString(hitValue));

        if (hitValue < 10) {
            return this.name + " Casted basic spell to " + enemy.getName() +
                    " but missed.\n----------------------------------------\n";

        } else if (hitValue > 90) {
            int hitDmg = (int) (((Wand)this.weapon).getMagic()*(Math.random()+1) +
                    this.strength * this.intelligence + 10 - (enemy.defense * .25));

            enemy.setLp(enemy.getLp() - hitDmg);

            return this.name + " Casted basic spell and dealt "+ hitDmg +" critical damage to "
                    + enemy.getName()+ ".\n----------------------------------------\n";
        } else {
            if (hitValue - (enemy.agility + enemy.luck) > 25) {
                int hitDmg = (int) (((Wand)this.weapon).getMagic()*(Math.random()+1) +
                        this.strength * this.intelligence - (enemy.defense * .5));

                enemy.setLp(enemy.getLp() - hitDmg);

                return this.name + " Casted basic spell and dealt "+ hitDmg +" damage to "
                        + enemy.getName()+ ".\n----------------------------------------\n";
            } else {
                return (this.name + " Casted basic spell to " + enemy.getName() +
                        " but missed.\n----------------------------------------\n");
            }
        }
    }

}
