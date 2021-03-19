package com.jonyn.dungeonhunter.models;

public abstract class Ability {

    // Variables heredables de la clase.
    protected String ability;
    protected String definition;

    // Constructor sin parametros
    public Ability(){}

    // Constructor con parametros.
    public Ability(String ability, String definition){
        this.ability = ability;
        this.definition = definition;
    }

    /** GETTERS Y SETTERS */
    public String getAbility() {
        return ability;
    }

    public String getDefinition() {
        return definition;
    }
}
