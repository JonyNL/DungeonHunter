package com.jonyn.dungeonhunter.models;

public class Passive extends Ability {


    // Constructor sin parametros
    public Passive(){}

    // Constructor con parametros.
    public Passive(String ability, String definition, String effect, boolean unlocked,
                   AbilityType abilityType, EffectType effectType) {

        super(ability, definition, effect, unlocked, abilityType, effectType);
    }
}
