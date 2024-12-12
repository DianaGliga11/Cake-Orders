package Repository;

import Domain.Cake;
import Domain.Command;
import com.github.javafaker.Faker;
import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataBaseRepositoryCommand extends Repository<Command> {

    private static final String URL = "jdbc:sqlite:D:\\Java programs\\demo\\src\\main\\resources\\Cake Orders";  // Path to database
    private Connection conn = null;
    // Java Faker instance for generating data
    private static final Faker faker = new Faker();
    private static final Random random = new Random();

    public DataBaseRepositoryCommand() {
        openConnection();
        createTable();
        loadData();
        //populateDatabase();
    }

    private void openConnection() {
        try {
            SQLiteDataSource ds = new SQLiteDataSource();
            ds.setUrl(URL);
            conn = ds.getConnection();
            if (conn != null) {
                System.out.println("Connection to the database established successfully!");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to the database", e);
        }
    }

    private void createTable() {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Commands (" +
                    "id INTEGER PRIMARY KEY, " +
                    "commandDate BIGINT, " +
                    "cakes TEXT);"); // The cakes field will store serialized cakes
        } catch (SQLException e) {
            System.err.println("[ERROR] createTable (Commands): " + e.getMessage());
        }
    }

    private List<Cake> getCakesFromDatabase() {
        List<Cake> cakes = new ArrayList<>();
        try (PreparedStatement statement = conn.prepareStatement("SELECT * FROM Cakes");
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String type = rs.getString("type");
                cakes.add(new Cake(id, type));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cakes;
    }

    public void populateDatabase() throws SQLException {
        conn.setAutoCommit(false);
        List<Cake> cakes = getCakesFromDatabase();
        String sqlInsertCommand = "INSERT INTO Commands (id, commandDate, cakes) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sqlInsertCommand)) {
            for (int i = 0; i < 100; i++) {
                Date randomDate = generateRandomDate();
                int numOfCakesInOrder = random.nextInt(3) + 1;
                List<Cake> selectedCakes = new ArrayList<>();
                for (int j = 0; j < numOfCakesInOrder; j++) {
                    Cake randomCake = cakes.get(faker.random().nextInt(cakes.size()));
                    selectedCakes.add(randomCake);
                }
                // Serializăm torturile alese într-un string
                String serializedCakes = serializeCakes(selectedCakes);
                preparedStatement.setInt(1, i + 1);
                preparedStatement.setLong(2, randomDate.getTime());
                preparedStatement.setString(3, serializedCakes);
                preparedStatement.addBatch();
                if (i % 10 == 0) {
                    preparedStatement.executeBatch();
                    conn.commit();
                    System.out.println("Inserted " + (i + 1) + " commands.");
                }
            }
            preparedStatement.executeBatch();
            conn.commit();
            System.out.println("Insertion of 100 commands completed.");
        } catch (SQLException e) {
            try {
                conn.rollback();
                System.out.println("Rollback executed.");
            } catch (SQLException rollbackException) {
                System.err.println("Error during rollback: " + rollbackException.getMessage());
            }
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    //generam o data random (din 2022 aprox)
    private Date generateRandomDate() {
        long currentTime = System.currentTimeMillis();
        long randomTime = currentTime - (random.nextInt(365 * 2) * 24L * 60L * 60L * 1000L);
        return new Date(randomTime);
    }


    // serializam lista de torturi pentru a o putea folosi in baza de date
    private String serializeCakes(List<Cake> cakes) {
        StringBuilder builder = new StringBuilder();
        for (Cake cake : cakes) {
            builder.append(cake.getId()).append(":").append(cake.getType()).append(";");
        }
        return builder.toString();
    }

    // deserializam lista de torturi pentru a o putea folosi in baza de date
    private List<Cake> deserializeCakes(String serializedCakes) {
        List<Cake> cakes = new ArrayList<>();
        String[] pairs = serializedCakes.split(";");
        for (String pair : pairs) {
            if (!pair.isEmpty()) {
                String[] parts = pair.split(":");
                int id = Integer.parseInt(parts[0]);
                String type = parts[1];
                cakes.add(new Cake(id, type));
            }
        }
        return cakes;
    }

    private void loadData() {
        try (PreparedStatement statement = conn.prepareStatement("SELECT * FROM Commands");
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                List<Cake> cakes = deserializeCakes(rs.getString("cakes"));
                Date commandDate = new Date(rs.getLong("commandDate"));
                Command command = new Command(rs.getInt("id"), cakes, commandDate);
                entities.add(command);
                dataBase.put(command.getId(), command);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void add(Command entity) throws DuplicateIDException {
        validateIdUnique(entity.getId());
        super.add(entity);
        String sql = "INSERT INTO Commands (id, commandDate, cakes) VALUES (?, ?, ?)";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, entity.getId());
            statement.setLong(2, entity.getDates().getTime());
            statement.setString(3, serializeCakes(entity.getCommands()));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalArgumentException("Error saving to the database", e);
        }
    }

    private void validateIdUnique(int id) throws DuplicateIDException {
        try (PreparedStatement statement = conn.prepareStatement("SELECT COUNT(*) FROM Commands WHERE id = ?")) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new DuplicateIDException("Entity already exists");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error validating ID.", e);
        }
    }

    @Override
    public Command findById(int id) throws ObjectNotFoundException {
        Command command = dataBase.get(id);
        if (command == null) {
            throw new ObjectNotFoundException("Entity not found");
        }
        return command;
    }

    @Override
    public void delete(int id) throws ObjectNotFoundException {
        if (!dataBase.containsKey(id)) {
            throw new ObjectNotFoundException("Entity not found");
        }
        super.delete(id);
        String sql = "DELETE FROM Commands WHERE id = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
            dataBase.remove(id);
        } catch (SQLException e) {
            throw new IllegalArgumentException("Error deleting from the database", e);
        }
    }

    @Override
    public void update(int id, Command command) {
        if (!dataBase.containsKey(id)) {
            throw new ObjectNotFoundException("Command not found");
        }
        super.update(id, command);
        String sql = "UPDATE Commands SET commandDate = ?, cakes = ? WHERE id = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setLong(1, command.getDates().getTime());
            statement.setString(2, serializeCakes(command.getCommands()));
            statement.setInt(3, id);
            statement.executeUpdate();
            dataBase.put(id, command);
        } catch (SQLException e) {
            throw new IllegalArgumentException("Error updating command", e);
        }
    }

    @Override
    public ArrayList<Command> getAll() {
        return new ArrayList<>(dataBase.values());
    }
}

