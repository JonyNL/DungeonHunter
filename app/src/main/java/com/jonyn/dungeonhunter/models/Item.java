package com.jonyn.dungeonhunter.models;

import java.io.Serializable;

public abstract class Item implements Serializable {

    // Variables heredables de la clase.
    protected String name;
    protected String description;

    public Item(){}

    // Constructor con parametros.
    public Item(String name, String description){
        this.name = name;
        this.description = description;
    }

    /** GETTERS Y SETTERS */
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
