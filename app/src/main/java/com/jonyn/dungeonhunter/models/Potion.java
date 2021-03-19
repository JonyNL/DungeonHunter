package com.jonyn.dungeonhunter.models;

public class Potion extends Consumable {

    // Enum con los diferentes tipos de pocion.
    public enum Types {
        HEALTH_POTION,
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

    /** Metodos de utilidad */
    public void use(Hero hero, Types type){
        switch (type){
            case HEALTH_POTION:
                hero.setLp(hero.getLp()+value);
                break;
            case MANA_POTION:
                hero.setMp(hero.getMp()+value);
                break;
            case ELIXIR:
                hero.setLp(hero.maxLp);
                hero.setMp(hero.maxMp);
                break;
        }
    }
}
