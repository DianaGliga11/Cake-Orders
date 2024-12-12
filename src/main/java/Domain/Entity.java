package Domain;

import java.io.Serializable;

//clasa abstracta cu un singur atribut (id)
public abstract class Entity implements Serializable {
    protected final int id;
//    private static int idCount = 0;

    public Entity(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public abstract String toFileString();
}

