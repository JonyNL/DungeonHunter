package com.jonyn.dungeonhunter.models;

public class Sword extends Weapon {

    // Variables de la clase
    private int damage;

    // Constructor sin parametros
    private Sword(){}

    // Constructor con parametros
    public Sword(String name, String description, int durability, boolean equipped, int damage,
                 WeaponType weaponType) {
        super(name, description, durability, equipped, weaponType);
        this.damage = damage;
    }

    /** GETTERS Y SETTERS */
    public int getDamage() {
        return damage;
    }
}
