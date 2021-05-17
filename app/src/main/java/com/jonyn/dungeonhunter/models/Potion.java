package com.jonyn.dungeonhunter.models;

public class Potion extends Consumable {

    // Enum con los diferentes tipos de pocion.
    public enum Types {
        LIFE_POTION,
        MANA_POTION,
        ELIXIR
    }

    // Variables de la clase
    private Types type;
    private int value;

    // Constructor sin parametros
    public Potion(){}

    // Constructor con parametros
    public Potion(String name, String description, int quantity, String effect, Types type, int value) {
        super(name, description, quantity, effect);
        this.type = type;
        this.value = value;
    }

    /** GETTERS Y SETTERS */
    public Types getType() {
        return type;
    }

    public int getValue() {
        return value;
    }
}
