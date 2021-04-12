package com.jonyn.dungeonhunter.models;

import java.util.ArrayList;
import java.util.List;

public class Warrior extends Hero {

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
        super(name, 8, 5, 6, 8, new ArrayList<Ability>(),
                new ArrayList<Ability>(), new ArrayList<Item>(),
                new Sword("Wooden Sword", "Simple sword made of wood.",
                        100,  true, 10, Weapon.WeaponType.SWORD),
                HeroClass.WARRIOR);
    }

    /** Metodos de utilidad */
    @Override
    public int attack() {
        return ((Sword) weapon).getDamage()+strength;
    }
}
