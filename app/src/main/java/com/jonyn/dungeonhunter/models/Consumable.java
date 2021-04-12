package com.jonyn.dungeonhunter.models;

public abstract class Consumable extends Item {

    // Variables heredables de la clase.
    protected String effect;
    protected int quantity;

    // Constructor sin parametros
    public Consumable(){}

    // Constructor con parametros.
    public Consumable(String name, String description, int quantity, String effect) {
        super(name, description);
        this.effect = effect;
        this.quantity = quantity;
    }

    /** GETTERS Y SETTERS */

    // Effect
    public String getEffect() {
        return effect;
    }

    // Quantity
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    /** Metodos de utilidad */

    public void removeQuantity(int i) {
        this.quantity -= i;
    }
}
