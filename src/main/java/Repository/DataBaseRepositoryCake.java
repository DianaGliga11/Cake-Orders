package Repository;

import Domain.Cake;
import com.github.javafaker.Faker;
import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DataBaseRepositoryCake extends Repository<Cake> {

    private static final String DB_URL = "jdbc:sqlite:D:\\Java programs\\demo\\src\\main\\resources\\Cake Orders";
    private Connection connection = null;
    // Instanță Java Faker pentru generarea datelor
    private static final Faker faker = new Faker();

    public DataBaseRepositoryCake() {
        openConnection();
        createTable();
        loadData();
        //populateDatabase();
    }

    // Deschide conexiunea la baza de date
    private void openConnection() {
        try {
            SQLiteDataSource ds = new SQLiteDataSource();
            ds.setUrl(DB_URL);
            connection = ds.getConnection();
            if (connection != null) {
                System.out.println("Connection to the database established successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Creează tabela Cakes în baza de date
    private void createTable() {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Cakes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +  // Folosește AUTOINCREMENT pentru ID
                    "type VARCHAR(100));");
        } catch (SQLException e) {
            System.err.println("[ERROR] createTable (Cakes): " + e.getMessage());
        }
    }

    // Populează baza de date cu 100 de cake-uri
    private void populateDatabase() {
        Set<String> unigueCakes = new HashSet<>();
        try {
            connection.setAutoCommit(false);
            String sql = "INSERT INTO Cakes (type) VALUES (?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                for (int i = 0; i < 100; i++) {
                    String cakeType = faker.food().fruit();
                    if(!unigueCakes.contains(cakeType)) {
                        unigueCakes.add(cakeType);
                        preparedStatement.setString(1, cakeType);
                        preparedStatement.addBatch();
                    }
                }
                preparedStatement.executeBatch();
                connection.commit();
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                System.err.println("Eroare la rollback: " + rollbackException.getMessage());
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Încarcă datele din baza de date în mapa dataBase
    private void loadData() {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM Cakes");
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                Cake cake = new Cake(rs.getInt("id"), rs.getString("type"));
                dataBase.put(cake.getId(), cake);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Adaugă un cake în baza de date
    @Override
    public void add(Cake cake) throws DuplicateIDException {
        // Validare dacă există deja tortul în baza de date
        try (PreparedStatement statement = connection.prepareStatement("SELECT id FROM Cakes WHERE type = ?")) {
            statement.setString(1, cake.getType());
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    throw new DuplicateIDException("Cake already exists!");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error validating cake", e);
        }

        // Inserare în baza de date
        String sql = "INSERT INTO Cakes (type) VALUES (?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, cake.getType());
            statement.executeUpdate();
            loadData();

            // Obține ID-ul generat
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    cake = new Cake(generatedId, cake.getType());
                    dataBase.put(generatedId, cake);
                }
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException("Error saving cake", e);
        }
    }

    // Căutare după ID
    @Override
    public Cake findById(int id) throws ObjectNotFoundException {
        Cake cake = dataBase.get(id);
        if (cake == null) {
            throw new ObjectNotFoundException("Entity not found");
        }
        return cake;
    }

    // Returnează toate cake-urile
    @Override
    public ArrayList<Cake> getAll() {
        return new ArrayList<>(dataBase.values());
    }

    // Actualizează un cake în baza de date
    @Override
    public void update(int id, Cake cake) throws ObjectNotFoundException {
        if (!dataBase.containsKey(id)) {
            throw new ObjectNotFoundException("Cake not found");
        }
        String sql = "UPDATE Cakes SET type = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, cake.getType());
            statement.setInt(2, id);
            statement.executeUpdate();
            // Actualizare în mapa locală
            dataBase.put(id, cake);
        } catch (SQLException e) {
            throw new IllegalArgumentException("Error updating cake", e);
        }
    }

    // Șterge un cake din baza de date
    @Override
    public void delete(int id) throws ObjectNotFoundException {
        if (!dataBase.containsKey(id)) {
            throw new ObjectNotFoundException("Cake not found");
        }
        String sql = "DELETE FROM Cakes WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
            // Eliminare din mapa locală
            dataBase.remove(id);
        } catch (SQLException e) {
            throw new IllegalArgumentException("Error deleting cake", e);
        }
    }

}
