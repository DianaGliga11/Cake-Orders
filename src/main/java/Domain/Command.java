package Domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

//extinde clasa abstracta Entity
//foloseste un contor pentru ID
public class Command extends Entity implements Serializable {
    private List<Cake> commands;  //contine torturi deja existente
    private Date date;
    private static int commandIdCount = 99;

//    public Command() {
//        super(++commandIdCount);
//    }

    public Command(List<Cake> commands, Date date) {
        super(++commandIdCount);
        this.commands = commands;
        this.date = date;
    }

    public Command(int id, List<Cake> commands, Date date) {
        super(id);
        this.commands = commands;
        this.date = date;
    }

    public List<Cake> getCommands() {
        return commands;
    }

    public Date getDates() {
        return  date;
    }

    public void setCommands(List<Cake> commands) {
        this.commands = commands;
    }

    public void setDates(Date date) {
        this.date = date;
    }

    public String toString() {
        return "Commands: " +
                "ID-" + this.id +
                ", date-" + this.date +
                ", list of commands-" + this.commands.toString() +
                "\n";
    }

    //am adaugat metoda pentru a fi scrise diferit, mai lizibil in fisier
    public String toFileString() {
        String rez = this.date + "|";
        for (Cake c : this.commands) {
            rez = rez + c.getId() + ":" + c.getType() + ",";
        }
        return rez;
    }
}
