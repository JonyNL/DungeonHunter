package com.jonyn.dungeonhunter.models;

public class Sword extends Weapon {

    // Variables de la clase
    private int damage;

    // Constructor sin parametros
    private Sword(){}

    // Constructor con parametros
    public Sword(String name, String description, int durability, boolean equipped, int damage) {
        super(name, description, durability, equipped);
        this.damage = damage;
    }

    /** GETTERS Y SETTERS */
    public int getDamage() {
        return damage;
    }
}
