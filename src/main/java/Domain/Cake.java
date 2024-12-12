package Domain;

import java.io.Serializable;

//extinde clasa abstracta Entity
//foloseste un contor pentru ID
public class Cake extends Entity implements Serializable {
    private String type_cake;
    private static int cakeIdCount = 99;

//    public Cake() {
//        super(++cakeIdCount);
//    }


    public void setCake(String type_cake) {
        this.type_cake = type_cake;
    }

    public String getType() {
        return type_cake;
    }

    public Cake(String type_cake) {
        super(++cakeIdCount);
        this.type_cake = type_cake;
    }

    public Cake(int id, String type_cake) {
        super(id);
        this.type_cake = type_cake;
    }

    public String toString() {
        return "Cake: " +
                "ID-" + this.id +
                ", type-" + this.type_cake +
                "\n";
    }

    //am adaugat metoda pentru a fi scrise diferit, mai lizibil in fisier
    public String toFileString() {
        return this.type_cake;
    }

    public void setType(String s) {
        this.type_cake = s;
    }
}
