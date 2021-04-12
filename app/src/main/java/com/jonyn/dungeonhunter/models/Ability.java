package com.jonyn.dungeonhunter.models;

import java.util.Arrays;

public abstract class Ability {

    public enum AbilityType {
        ACTIVE,
        PASSIVE;

        public static String[] getNames(Class<? extends Enum<?>> e) {
            return Arrays.toString(e.getEnumConstants())
                    .replaceAll("^.|.$", "").split(", ");
        }
    }

    // Variables heredables de la clase.
    protected String ability;
    protected String definition;
    protected AbilityType abilityType;

    // Constructor sin parametros
    public Ability(){}

    // Constructor con parametros.
    public Ability(String ability, String definition, AbilityType abilityType){
        this.ability = ability;
        this.definition = definition;
        this.abilityType = abilityType;
    }

    /** GETTERS Y SETTERS */
    public String getAbility() {
        return ability;
    }

    public String getDefinition() {
        return definition;
    }

    public AbilityType getAbilityType() {
        return abilityType;
    }
}
