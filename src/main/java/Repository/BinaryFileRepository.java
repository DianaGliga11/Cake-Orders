package Repository;

import Domain.Entity;

import java.io.*;
import java.util.ArrayList;

//cand rulezi, incearca sa adaugi ceva, nu sa citesti ca sa
//nu dea crash
public class BinaryFileRepository<T extends Entity> extends Repository<T> {
    private final String fileName;

    public BinaryFileRepository(String fileName) {
        this.fileName = fileName;
        loadEntities();
    }

    private void loadEntities() {
        //creez fisierul
        File file = new File(fileName);
        if (file.exists() && file.length() > 0) {
            //deserializez entitatile pentru a le putea citi
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
               //introduc entitatile intr-o lista
                ArrayList<T> entities = (ArrayList<T>) ois.readObject();
                for (T entity : entities) {
                    super.add(entity);
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RepositoryException("Error while loading entities", e);
            }
        } else {
            System.out.println("File don't exist or empty.");
        }
    }

    private void saveEntities() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            //analog scrie obiectele si le serializeaza
            oos.writeObject(getAll());
            oos.flush();
        } catch (IOException e) {
            throw new RuntimeException("Error saving entities", e);
        }
    }


    @Override
    public void add(T entity) {
        super.add(entity);
        saveEntities();
    }

    @Override
    public void update(int id, T entity) {
        super.update(id, entity);
        saveEntities();
    }

    @Override
    public void delete(int id) {
        super.delete(id);
        saveEntities();
    }

}
