package Domain;

public class CakeFactory implements I_EntityFactory<Cake> {
    @Override
    public Cake createEntity(String line) {
        //fiind doar un String, e mai usor sa imi selectez atributul
        return new Cake(line.trim());
    }

}
