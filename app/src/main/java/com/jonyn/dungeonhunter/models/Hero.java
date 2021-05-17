package com.jonyn.dungeonhunter.models;

import java.util.Arrays;
import java.util.List;

public abstract class Hero extends Character{

    public enum HeroClass{
        WARRIOR,
        WIZARD;

        public static String[] getNames(Class<? extends Enum<?>> e) {
            return Arrays.toString(e.getEnumConstants())
                    .replaceAll("^.|.$", "").split(", ");
        }
    }

    // Variables heredables de la clase
    protected int reqExp = 100;
    protected int exp = 0;
    protected Weapon weapon;
    protected List<Item> inventory;
    protected HeroClass heroClass;

    // Constructor sin parametros
    public Hero(){

    }

    // Constructor con parametros.
    public Hero(String name, int strength, int defense, int agility, int luck,
                List<Ability> actives, List<Ability> passives, List<Item> inventory, Weapon weapon,
                HeroClass heroClass) {
        super(name, strength, defense, agility, luck, actives, passives);
        this.weapon = weapon;
        this.inventory = inventory;
        this.heroClass = heroClass;
    }

    // Constructor con todos los parametros.
    public Hero(String name, int lvl, int maxMp, int maxLp, int lp, int mp, int strength, int defense,
                int agility, int luck, int reqExp, int exp,
                List<Ability> actives, List<Ability> passives, List<Item> inventory, Weapon weapon,
                HeroClass heroClass) {
        super(name, lvl, maxMp, maxLp, lp, mp, strength, defense, agility, luck, actives, passives);
        this.reqExp = reqExp;
        this.exp = exp;
        this.weapon = weapon;
        this.inventory = inventory;
        this.heroClass = heroClass;
    }

    /** GETTERS Y SETTERS */

    // Exp
    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    // ReqExp
    public int getReqExp() {
        return reqExp;
    }

    public void setReqExp(int reqExp) {
        this.reqExp = reqExp;
    }

    // Weapon
    public Weapon getWeapon() {
        return weapon;
    }

    // Inventory
    public List<Item> getInventory() {
        return inventory;
    }

    public void setInventory(List<Item> inventory) {
        this.inventory = inventory;
    }

    // HeroClass
    public HeroClass getHeroClass() {
        return heroClass;
    }

    public void setHeroClass(HeroClass heroClass) {
        this.heroClass = heroClass;
    }

    /** Metodos de utilidad */

    public String useItem(int pos){
        StringBuilder res = new StringBuilder();

        // Comprobamos si el inventario tiene objetos dentro.
        if (!inventory.isEmpty()) {

            // Creamos un Consumable asignandole el objeto que vamos a usar.
            Consumable c = (Consumable) inventory.get(pos);

            // Reducimos la cantidad del objeto utilizado en 1.
            ((Consumable) inventory.get(pos)).removeQuantity(1);

            // Usamos el objeto.
            if (c instanceof Potion){
                switch (((Potion) c).getType()) {
                    case LIFE_POTION:
                        recoverLp(((Potion) c).getValue());
                        break;
                    case ELIXIR:
                        recoverLp(maxLp);
                        recoverMp(maxMp);
                    case MANA_POTION:
                        recoverMp(((Potion) c).getValue());
                }
            }

            // Actualizamos el Log para que nos muestre el resultado de la operacion.
            res.append(this.name + " used " + c.getName()
                    + ".\n----------------------------------------\n");

            // En caso de que la cantidad del objeto consumido sea 0, lo eliminamos del
            // inventario.
            if (c.getQuantity() <= 0)
                inventory.remove(pos);
            return res.toString();
        } else return "Empty";
    }
}
