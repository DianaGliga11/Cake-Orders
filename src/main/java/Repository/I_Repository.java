package Repository;

import Domain.Entity;

import java.util.ArrayList;

//interfata pentru Repository
public interface I_Repository<T extends Entity> {
    void add(T entity) throws Exception;

    void update(int id, T entity) throws Exception;

    void delete(int id) throws Exception;

    ArrayList<T> getAll();

    boolean getById(int id);

    T findById(int id);
}
