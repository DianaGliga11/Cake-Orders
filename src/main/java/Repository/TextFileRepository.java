package Repository;

import Domain.Entity;
import Domain.I_EntityFactory;

import java.io.*;
import java.util.Scanner;

public class TextFileRepository<T extends Entity>extends Repository<T> {
    private final String fileName;
    //folosesc un atribut(interfata in cazul meu) care utilizeaza design pattern-ul factory
    private final I_EntityFactory<T> entityFactory;

    public TextFileRepository(String fileName, I_EntityFactory<T> entityFactory) throws FileNotFoundException , DuplicateIDException{
        this.fileName = fileName;
        this.entityFactory = entityFactory;
        loadEntities();
    }

    private void loadEntities() throws FileNotFoundException {
        //creeez fisierul de care am nevoie
        File file = new File(fileName);
        //citesc ce am in fisier
        Scanner scanner = new Scanner(file);
        //atata timp cat am ce citi
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            //creez noua mea entitate(care nu am cu,m sa stiu daca e Cake sau Command
            //si tocmai de aceea folosesc factory
            T entity = entityFactory.createEntity(line);
            //si o adaug in fisier
            super.add(entity);
        }
        scanner.close();
    }

    @Override
    public void add(T entity) {
        super.add(entity);
        saveToFile();
    }

    private void saveToFile() {
        //am nevie de un try care va executa ceea ce are in () pentru a evita ca fisierul sa ramana deschis
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (T entity : entities) {
                //salvez fiecare entitate sub forma de linie (pe care o comnstruiesc cu toFileString()
                writer.println(entity.toFileString());
            }
        } catch (IOException e) {
            throw new RepositoryException("Error writing to file: " + fileName, e);
        }
    }

    @Override
    public void delete(int id) {
        super.delete(id);
        saveToFile();
    }

    @Override
    public void update(int id, T entity) {
        super.update(id, entity);
        saveToFile();
    }
}
