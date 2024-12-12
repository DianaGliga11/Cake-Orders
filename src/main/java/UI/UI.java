package UI;

import Domain.Cake;
import Domain.Command;
import Service.Service;

import java.util.*;

//are ca atribute Service-ul si un cititor(Scanner)
public class UI {
    private final Service service;
    private final Scanner scanner;

    public UI(Service service) {
        this.service = service;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        boolean running = true;
        while (running) {
            System.out.println("1. Add cake");
            System.out.println("2. Add command");
            System.out.println("3. Read cakes");
            System.out.println("4. Read commands");
            System.out.println("5. Update cake");
            System.out.println("6. Update command");
            System.out.println("7. Delete cake");
            System.out.println("8. Delete command");
            System.out.println("9. Get Cake");
            System.out.println("10. Get command");
            System.out.println("11. Sort commands per days number of cakes");
            System.out.println("12. Sort commands per months number of cakes");
            System.out.println("13. Most ordered cakes");
            int option = scanner.nextInt();
            scanner.nextLine();
            switch (option) {
                case 1 -> addCake();
                case 2 -> addCommand();
                case 3 -> readCakes();
                case 4 -> readCommands();
                case 5 -> updateCake();
                case 6 -> updateCommand();
                case 7 -> deleteCake();
                case 8 -> deleteCommand();
                case 9 -> getCake();
                case 10 -> getCommand();
                case 11 -> afiseazaNrComenziPerTort();
                case 12 -> afisareNrComenziPerLuna();
                case 13 -> afiseazaTorturileCeleMaiComandate();
                case 0 -> running = false;
            }
        }
    }

    public void afiseazaTorturileCeleMaiComandate() {
        Map<String, Integer> rezultat = service.celeMaiDesComandateTorturi();

        if (rezultat.isEmpty()) {
            System.out.println("Nu există torturi comandate.");
            return;
        }

        System.out.println("Torturile cele mai des comandate:");
        rezultat.forEach((tort, numarComenzi) -> {
            System.out.println("Tort: " + tort + ", Număr de comenzi: " + numarComenzi);
        });
    }


    public void afisareNrComenziPerLuna() {
        Map<String, Integer> rezultat = service.nrTorturiComandatePeLuna();
        rezultat.forEach((luna, nrTorturi) ->
                System.out.println("Luna: " + luna + ", Număr de torturi: " + nrTorturi));
    }



    public void afiseazaNrComenziPerTort() {
        Map<String, Integer> rezultat = service.nrTorturiComandatePeZi();
        rezultat.forEach((cake, nrComenzi) ->
                System.out.println("Tort: " + cake+ ", Număr comenzi: " + nrComenzi)
        );
    }
    private void getCommand() {
        System.out.println("Enter command id");
        int id = scanner.nextInt();
        System.out.println(service.getCommandById(id));
    }

    private void getCake() {
        System.out.println("Enter cake id");
        int id = scanner.nextInt();
        System.out.println(service.getCakeById(id));
    }

    private void deleteCommand() {
        System.out.println("Enter command ID");
        int id = scanner.nextInt();
        try {
            service.deleteCommand(id);
            System.out.println("Command deleted");
        } catch (Exception e) {
            System.out.println("Error deleting command" + e.getMessage());
        }
    }

    private void deleteCake() {
        System.out.println("Enter cake ID: ");
        int id = scanner.nextInt();
        try {
            service.deleteCake(id);
            System.out.println("Cake deleted");
        } catch (Exception e) {
            System.out.println("Error deleting cake" + e.getMessage());
        }
    }


    private void updateCommand() {
        System.out.println("Enter command ID to update:");
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a valid command ID:");
            scanner.next();
        }
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Enter the number of cakes in the command:");
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a valid number:");
            scanner.next();
        }
        int number = scanner.nextInt();
        scanner.nextLine();

        List<Integer> cakesIDs = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            System.out.println("Enter cake ID:");
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a valid cake ID:");
                scanner.next();
            }
            cakesIDs.add(scanner.nextInt());
            scanner.nextLine();
        }

        Date date = new Date();
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());

        try {
            service.updateCommand(id, cakesIDs, sqlDate);
            System.out.println("Command updated successfully.");
        } catch (Exception e) {
            System.out.println("Error while updating command: " + e.getMessage());
        }
    }

    private void updateCake() {
        System.out.println("Enter cake ID to update");
        int id = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter cake type: ");
        String cakeType = scanner.nextLine();
        try {
            service.updateCake(id, cakeType);
            System.out.println("Cake updated");
        } catch (Exception e) {
            System.out.println("Error updating cake" + e.getMessage());
        }
    }

    private void readCommands() {
        List<Command> commands = service.getAllCommands();
        System.out.println("Commands are: ");
        commands.forEach(System.out::println);
    }

    private void readCakes() {
        List<Cake> cakes = service.getAllCakes();
        if(cakes == null || cakes.isEmpty()){
            System.out.println("No cakes found");
        }
        else {
            System.out.println("Cakes are: ");
            cakes.forEach(System.out::println);
        }
    }

    private void addCommand() {
        List<Integer> cakesIDs = new ArrayList<>();
        System.out.println("Enter number of cakes in the command:");
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a valid number:");
            scanner.next();
        }
        int number = scanner.nextInt();
        scanner.nextLine();

        for (int i = 0; i < number; i++) {
            System.out.println("Enter cake ID:");
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a valid cake ID:");
                scanner.next();
            }
            cakesIDs.add(scanner.nextInt());
            scanner.nextLine();
        }

        Date date = new Date();
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());

        try {
            service.addCommand(cakesIDs, sqlDate);
            System.out.println("Command added successfully.");
        } catch (Exception e) {
            System.out.println("Error while adding command: " + e.getMessage());
        }
    }

    private void addCake() {
        System.out.println("Enter cake type: ");
        String cakeType = scanner.nextLine();
        try {
            service.addCake(cakeType);
            System.out.println("Cake added successfully");
        } catch (Exception e) {
            System.out.println("Error adding cake" + e.getMessage());
        }
    }
}
