package Domain;

//interfata care imi permite crearea de multiple entitati, utilizand design pattern-ul factory
public interface I_EntityFactory<T extends Entity> {
    T createEntity(String line);
}
