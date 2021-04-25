package com.jonyn.dungeonhunter.models;

import java.io.Serializable;
import java.util.Arrays;

public abstract class Ability implements Serializable {

    public enum AbilityType {
        ACTIVE,
        PASSIVE;

        public static String[] getNames(Class<? extends Enum<?>> e) {
            return Arrays.toString(e.getEnumConstants())
                    .replaceAll("^.|.$", "").split(", ");
        }
    }

    public enum EffectType {
        REGEN,
        DMG,
        STATUS_UP;


        public static String[] getNames(Class<? extends Enum<?>> e) {
            return Arrays.toString(e.getEnumConstants())
                    .replaceAll("^.|.$", "").split(", ");
        }
    }

    // Variables heredables de la clase.
    protected String ability;
    protected String definition;
    protected String effect;
    protected AbilityType abilityType;
    protected EffectType effectType;

    // Constructor sin parametros
    public Ability(){}

    // Constructor con parametros.
    public Ability(String ability, String definition, String effect, AbilityType abilityType, EffectType effectType){
        this.ability = ability;
        this.definition = definition;
        this.effect = effect;
        this.abilityType = abilityType;
        this.effectType = effectType;
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

    public String getEffect() {
        return effect;
    }

    public EffectType getEffectType() {
        return effectType;
    }
}
