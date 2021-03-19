package com.jonyn.dungeonhunter.models;

import java.util.ArrayList;
import java.util.List;

public class Enemy extends Character{

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
    public int attack() {
        return strength*2;
    }

    /** GETTERS Y SETTERS */
    public Types getType() {
        return type;
    }

}
