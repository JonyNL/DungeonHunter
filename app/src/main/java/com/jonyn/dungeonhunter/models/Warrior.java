package com.jonyn.dungeonhunter.models;

import android.util.Log;

import com.jonyn.dungeonhunter.DbUtils;

import java.util.ArrayList;
import java.util.List;

public class Warrior extends Hero {
    public static String TAG = "com.jonyn.dungeonhunter.models.WARRIOR";

    // Constructor sin parametros
    public Warrior(){}

    // Constructor con parametros.
    public Warrior(String name, int strength, int defense, int agility, int luck,
                   List<Ability> actives, List<Ability> passives, List<Item> inventory,
                   Weapon weapon, HeroClass heroClass) {
        super(name, strength, defense, agility, luck, actives, passives, inventory, weapon, heroClass);
    }

    // Constructor con todos los parametros
    public Warrior(String name, int lvl, int maxMp, int maxLp, int lp, int mp, int strength,
                   int defense, int agility, int luck, int reqExp, int exp,
                   List<Ability> actives, List<Ability> passives, List<Item> inventory,
                   Weapon weapon, HeroClass heroClass) {
        super(name, lvl, maxMp, maxLp, lp, mp, strength, defense, agility, luck, reqExp, exp,
                actives, passives, inventory, weapon, heroClass);
    }

    // Constructor con un parametro.
    public Warrior(String name) {
        super(name, 8, 5, 6, 8, new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(),
                new Sword("Wooden Sword", "Simple sword made of wood.",
                        100,  true, 10, Weapon.WeaponType.SWORD),
                HeroClass.WARRIOR);
    }

    /** Metodos de utilidad */
    @Override
    public String attack(Character enemy)
    {
        int hitValue = DbUtils.randomNumber(0, 100) + this.luck;
        Log.i(TAG, Integer.toString(hitValue));

        if (hitValue < 10) {
            return (this.name + " tried to attack " + enemy.getName() +
                    "but missed.\n----------------------------------------\n");

        } else if (hitValue > 90) {
            int hitDmg = (int) (((Sword)this.weapon).getDamage()*(DbUtils.randomNumber(0, 10)/10) +
                    this.strength + 10 - (enemy.defense * .25));

            enemy.setLp(enemy.getLp() - hitDmg);

            return this.name + " attacked and dealt " + hitDmg + " critical damage to "
                    + enemy.getName()+ ".\n----------------------------------------\n";
        } else {
            if (hitValue - (enemy.agility + enemy.luck) > 25) {
                int hitDmg = (int)
                        (((Sword)this.weapon).getDamage()*(DbUtils.randomNumber(0, 10)/10) +
                        this.strength - (enemy.defense * .5));

                enemy.setLp(enemy.getLp() - hitDmg);

                return this.name + " attacked and dealt "+ hitDmg + " damage to "
                        + enemy.getName()+ ".\n----------------------------------------\n";
            } else {
                return (this.name + " tried to attack " + enemy.getName() +
                        "but missed.\n----------------------------------------\n");
            }
        }
        //((Sword) weapon).getDamage()+strength;
    }
}
