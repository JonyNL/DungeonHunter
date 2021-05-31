package com.jonyn.dungeonhunter.models;

import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return name.equals(item.name) &&
                description.equals(item.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
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
