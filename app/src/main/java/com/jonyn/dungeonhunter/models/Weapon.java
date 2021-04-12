package com.jonyn.dungeonhunter.models;

import java.util.Arrays;

public abstract class Weapon extends Item {

    public enum WeaponType {
        SWORD,
        WAND;

        public static String[] getNames(Class<? extends Enum<?>> e) {
            return Arrays.toString(e.getEnumConstants())
                    .replaceAll("^.|.$", "").split(", ");
        }
    }

    // Variables heredables de la clase.
    protected int durability;
    protected boolean equipped;
    protected WeaponType weaponType;

    // Constructor sin parametros
    public Weapon(){}

    // Constructor con parametros.
    public Weapon(String name, String description,
                  int durability, boolean equipped, WeaponType weaponType) {
        super(name, description);
        this.durability = durability;
        this.equipped = equipped;
        this.weaponType = weaponType;
    }


    /** GETTERS Y SETTERS */

    // Durability
    public int getDurability() {
        return durability;
    }

    // Equipped
    public boolean isEquipped() {
        return equipped;
    }

    public void setEquipped(boolean equipped) {
        this.equipped = equipped;
    }

    public WeaponType getWeaponType() {
        return weaponType;
    }

    public void setWeaponType(WeaponType weaponType) {
        this.weaponType = weaponType;
    }
}
