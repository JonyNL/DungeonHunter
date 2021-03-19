package com.jonyn.dungeonhunter.models;

public class Wand extends Weapon {

    // Variables de la clase.
    private int magic;

    // Constructor sin parametros
    public Wand(){}

    // Constructor con parametros.
    public Wand(String name, String description, int durability, boolean equipped, int magic) {
        super(name, description, durability, equipped);
        this.magic = magic;
    }

    /** GETTERS Y SETTERS */
    public int getMagic() {
        return magic;
    }
}
