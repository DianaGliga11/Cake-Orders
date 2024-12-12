package Repository;

import Domain.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//Repository generic (genericitate extinsa din Entity) care implementeaza interfata
//operatii CRUD
public class Repository<T extends Entity> implements I_Repository<T> {
    //contine o lista cu entitati generice
    protected final ArrayList<T> entities = new ArrayList<>();
    protected Map<Integer, T> dataBase = new HashMap<>();
    @Override
    public void add(T entity) {
        if (getById(entity.getId())) {
            throw new DuplicateIDException("Entity with this ID already exist" + entity.getId());
        }
        entities.add(entity);

    }

    @Override
    public void update(int id, T entity){
        T found = null;
        for (int i=0;i<entities.size();i++) {
            if (entities.get(i).getId() == id) {
                found = entities.get(i);
                entities.set(i, entity);
                break;
            }
        }
        if (found == null) {
            throw new RepositoryException("Entity not found-" + id);
        }
    }

    @Override
    public void delete(int id)  {
        T found = null;
        for (T e : entities) {
            if (e.getId() == id) {
                found = e;
                break;
            }
        }
        if (found == null) {
            throw new ObjectNotFoundException("Entity not found-" + id );
        }
        entities.remove(found);
    }

    @Override
    public ArrayList<T> getAll() {
        return entities;
    }

    @Override
    public boolean getById(int id) {
        for (T e :entities) {
            if (e.getId() == id) {
                return true;
            }
        }
        return false;
    }

    @Override
    public T findById(int id) {
        for (T e :entities) {
            if (e.getId() == id) {
                return e;
            }
        }
        return null;
    }
}
