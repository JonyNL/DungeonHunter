package com.jonyn.dungeonhunter.models;

public class Passive extends Ability {

    // Variables de la clase
    private String effect;

    // Constructor sin parametros
    public Passive(){}

    // Constructor con parametros.
    public Passive(String ability, String definition, String effect, AbilityType abilityType){
        super(ability, definition, abilityType);
        this.effect = effect;
    }

    /** GETTERS Y SETTERS */
    public String getEffect() {
        return effect;
    }
}
