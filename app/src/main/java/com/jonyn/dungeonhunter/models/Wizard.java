package com.jonyn.dungeonhunter.models;

import java.util.ArrayList;
import java.util.List;

public class Wizard extends Hero {

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

    public int getIntelligence() {
        return intelligence;
    }

    /** Metodos de utilidad */
    @Override
    public int attack() {
        return ((Wand) weapon).getMagic()*intelligence;
    }
}
