package com.jonyn.dungeonhunter.models;

public class Active extends Ability{

    // Variable propia de la clase.
    private int cost;

    // Constructor sin parametros
    public Active(){}

    // Constructor con parametros.
    public Active(String ability, String definition, int cost, AbilityType abilityType) {
        super(ability, definition, abilityType);
        this.cost = cost;
    }

    /** GETTERS Y SETTERS */
    public int getCost() {
        return cost;
    }
}
