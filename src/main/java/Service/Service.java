package Service;

import Domain.Cake;
import Domain.Command;
import Repository.Repository;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

//contine cele doua Repository
public class Service {
    private final Repository<Cake> cakeRepository;
    private final Repository<Command> commandRepository;

    public Service(Repository<Cake> cakeRepository, Repository<Command> commandRepository) {
        this.cakeRepository = cakeRepository;
        this.commandRepository = commandRepository;
    }

    public void addCake(String type) {

        Cake cake = new Cake(type);
        cakeRepository.add(cake);
    }

    public void addCommand(List<Integer> cakeIDs, Date date) {
        List<Cake> cakes = new ArrayList<>();
        for (int id : cakeIDs) {
            Cake cake = cakeRepository.getAll().stream()
                    .filter(c -> c.getId() == id)
                    .findFirst()
                    .orElse(null);

            if (cake != null && cake.getType() != null && !cake.getType().trim().isEmpty()) {
                cakes.add(cake);
            } else {
                throw new RuntimeException("Cake with ID " + id + " does not exist or has an empty type.");
            }
        }

        Command command = new Command(cakes, date);
        commandRepository.add(command);
    }

    public void updateCake(int id, String type) {
        Cake cake = new Cake(id, type);
        cakeRepository.update(id, cake);
    }

    public void updateCommand(int id, List<Integer> cakeIDs, Date date) {
        List<Cake> cakes = new ArrayList<>();
        for (int cakeID : cakeIDs) {
            Cake cake = cakeRepository.getAll().stream()
                    .filter(c -> c.getId() == cakeID)
                    .findFirst()
                    .orElse(null);

            if (cake != null && cake.getType() != null && !cake.getType().trim().isEmpty()) {
                cakes.add(cake);
            } else {
                throw new RuntimeException("Cake with ID " + cakeID + " does not exist or has an empty type.");
            }
        }

        Command command = new Command(id, cakes, date);
        commandRepository.update(id, command);
    }


    public void deleteCake(int id) {
        cakeRepository.delete(id);
    }

    public void deleteCommand(int id) {
        commandRepository.delete(id);
    }

    public ArrayList<Cake> getAllCakes() {
        return cakeRepository.getAll();
    }

    public ArrayList<Command> getAllCommands() {
        return commandRepository.getAll();
    }

    public boolean getCakeById(int id) {
        return cakeRepository.getById(id);
    }

    public boolean getCommandById(int id) {
        return commandRepository.getById(id);
    }


    public Map<String, Integer> nrTorturiComandatePeZi() {
        // Map pentru stocarea numărului de torturi pe zi
        Map<String, Integer> torturiPeZi = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (Command command : commandRepository.getAll()) {
            String dataComanda = dateFormat.format(command.getDates());
            int nrTorturi = command.getCommands().size();
            // Actualizăm numărul de torturi pentru data respectivă
            torturiPeZi.put(dataComanda, torturiPeZi.getOrDefault(dataComanda, 0) + nrTorturi);
        }
        // Sortăm după numărul de torturi, descrescător
        return torturiPeZi.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, _) -> oldValue,
                        LinkedHashMap::new
                ));
    }

    public Map<String, Integer> nrTorturiComandatePeLuna() {
        // Map pentru stocarea numărului de torturi pe lună
        Map<String, Integer> torturiPeLuna = new HashMap<>();
        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
        for (Command command : commandRepository.getAll()) {
            String lunaComanda = monthFormat.format(command.getDates());
            int nrTorturi = command.getCommands().size();
            // Actualizăm numărul de torturi pentru luna respectivă
            torturiPeLuna.put(lunaComanda, torturiPeLuna.getOrDefault(lunaComanda, 0) + nrTorturi);
        }
        // Sortăm după numărul de torturi, descrescător
        return torturiPeLuna.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, _) -> oldValue,
                        LinkedHashMap::new
                ));
    }

    public Map<String, Integer> celeMaiDesComandateTorturi() {
        // Map pentru numărarea torturilor
        Map<String, Integer> torturiComandate = new HashMap<>();
        for (Command command : commandRepository.getAll()) {
            for (Cake cake : command.getCommands()) {
                String tipTort = cake.getType();
                // Incrementăm numărul de comenzi pentru fiecare tip de tort
                torturiComandate.put(tipTort, torturiComandate.getOrDefault(tipTort, 0) + 1);
            }
        }
        // Găsim valoarea maximă
        int maxComenzi = torturiComandate.values().stream()
                .max(Integer::compareTo)
                .orElse(0);
        // Filtrăm torturile cu acest număr maxim de comenzi
        return torturiComandate.entrySet().stream()
                .filter(entry -> entry.getValue() == maxComenzi)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }
}
