package com.jonyn.dungeonhunter.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Character implements Serializable {

    // Variables heredables de la clase.
    protected String name;
    protected int lvl = 1;
    protected int maxLp = 100;
    protected int lp = maxLp;
    protected int maxMp = 20;
    protected int mp = maxMp;
    protected int strength;
    protected int defense;
    protected int agility;
    protected int luck;
    protected List<Ability> actives;
    protected List<Ability> passives;

    // Constructor sin parametros
    public Character(){

    }

    // Constructor con parametros.
    public Character (String name, int strength, int defense, int agility, int luck,
                      List<Ability> actives, List<Ability> passives) {
        this.name = name;
        this.actives = actives;
        this.passives = passives;
        this.strength = strength;
        this.defense = defense;
        this.agility = agility;
        this.luck = luck;
    }

    // Constructor con todos los parametros.
    public Character (String name, int lvl, int maxMp, int maxLp, int lp, int mp, int strength,
                      int defense, int agility, int luck, List<Ability> actives, List<Ability> passives) {
        this.name = name;
        this.lvl = lvl;
        this.maxMp = maxMp;
        this.mp = mp;
        this.maxLp = maxLp;
        this.lp = lp;
        this.actives = actives;
        this.passives = passives;
        this.strength = strength;
        this.defense = defense;
        this.agility = agility;
        this.luck = luck;
    }

    /** GETTERS Y SETTERS */

    // Name
    public String getName() {
        return name;
    }

    // Lvl
    public int getLvl() {
        return lvl;
    }


    // MaxLp
    public int getMaxLp() {
        return maxLp;
    }

    public void setMaxLp(int maxLp) {
        this.maxLp = maxLp;
    }

    // Lp
    public int getLp() {
        return lp;
    }

    public void setLp(int lp) {
        if (lp > maxLp)
            this.lp = this.maxLp;
        else if (lp < 0)
            this.lp = 0;
        else this.lp = lp;
    }

    public void recoverLp(int val){
        lp+=val;
        if (lp > maxLp)
            lp = maxLp;
    }

    // MaxMp
    public int getMaxMp() {
        return maxMp;
    }

    public void setMaxMp(int maxMp) {
        this.maxMp = maxMp;
    }

    // Mp
    public void setMp(int mp) {
        if (mp > maxMp)
            this.mp = this.maxMp;
        else if (mp < 0)
            this.mp = 0;
        else this.mp = mp;
    }

    public void recoverMp(int val){
        mp+=val;
        if (mp > maxMp)
            mp = maxMp;
    }

    public int getMp() {
        return mp;
    }

    // Strength
    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    // Defense
    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    // Agility
    public int getAgility() {
        return agility;
    }

    public void setAgility(int agility) {
        this.agility = agility;
    }

    // Luck
    public int getLuck() {
        return luck;
    }

    public void setLuck(int luck) {
        this.luck = luck;
    }

    // Actives
    public List<Ability> getActives() {
        return actives;
    }

    // Passives
    public List<Ability> getPassives() {
        return passives;
    }

    /** Metodos de utilidad */

    public void lvlUp() {
        this.lvl++;
    }

    public abstract String attack(Character character);

    public void addPassive(Ability passive) {
        this.passives.add(passive);
    }

    public void addActive(Ability active){
        this.actives.add(active);
    }

    @Override
    public String toString() {
        return "Character{" +
                "name='" + name + '\'' +
                ", lvl=" + lvl +
                ", maxLp=" + maxLp +
                ", lp=" + lp +
                ", maxMp=" + maxMp +
                ", mp=" + mp +
                ", strength=" + strength +
                ", defense=" + defense +
                ", agility=" + agility +
                ", luck=" + luck +
                ", actives=" + actives +
                ", passives=" + passives +
                '}';
    }
}
