package com.jonyn.dungeonhunter.models;

public abstract class Weapon extends Item {

    // Variables heredables de la clase.
    private int durability;
    private boolean equipped;

    // Constructor sin parametros
    public Weapon(){}

    // Constructor con parametros.
    public Weapon(String name, String description,
                  int durability, boolean equipped) {
        super(name, description);
        this.durability = durability;
        this.equipped = equipped;
    }


    /** GETTERS Y SETTERS */

    // Durability
    public int getDurability() {
        return durability;
    }

    // Equipped
    public boolean isEquipped() {
        return equipped;
    }

    public void setEquipped(boolean equipped) {
        this.equipped = equipped;
    }
}
