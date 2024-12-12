package Domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class CommandFactory implements I_EntityFactory<Command> {
    //da un pattern datei, pentru a o putea folosi in fisierul text mai usor
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");

    @Override
    public Command createEntity(String line) {
        //separ atributele prin "|"
        String[] parts = line.split("\\|");
        Date date;
        try {
            //imi setez primul atribut
            date = DATE_FORMAT.parse(parts[0].trim());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format:" + parts[0], e);
        }
        //al doilea atribut e lista, deci separ fiecare obiect Cake prin ","
        String[] cakeDetails = parts[1].trim().split(",");
        //si creez cu ele lista
        ArrayList<Cake> cakes = new ArrayList<>();
        for (String cakeDetail : cakeDetails) {
            //torturile vor avea atributele sepatrate prin ":"
            String[] cakeInfo = cakeDetail.trim().split(":");
            int cakeId = Integer.parseInt(cakeInfo[0].trim());
            //daca nu am un tip se va pune automat "Generic"
            String cakeType = cakeInfo.length > 1 ? cakeInfo[1].trim() : "Generic"; // Tipul prăjiturii sau "Generic"
            cakes.add(new Cake(cakeId, cakeType));
        }
        return new Command(cakes, date);
    }

}
