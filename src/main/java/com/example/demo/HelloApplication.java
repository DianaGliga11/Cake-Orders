package com.example.demo;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import Service.Service;
import Domain.Cake;
import Domain.Command;
import Repository.DataBaseRepositoryCake;
import Repository.DataBaseRepositoryCommand;
import Repository.Repository;
import Repository.ObjectNotFoundException;
import Repository.DuplicateIDException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) {
        Repository<Cake> repositoryCake = new DataBaseRepositoryCake();
        Repository<Command> repositoryCommand = new DataBaseRepositoryCommand();
        Service service = new Service(repositoryCake, repositoryCommand);

        //organizeaza continutul in mai multe sectiuni (in acest caz 2)
        TabPane tabPane = new TabPane();
        Tab cakesTab = new Tab("Cakes");
        Tab commandsTab = new Tab("Commands");
        cakesTab.setClosable(false);
        VBox cakesBox = new VBox();
        cakesBox.setSpacing(10);

        //tabela si coloane pt Cakes
        TableView<Cake> cakesTable = new TableView<>();
        TableColumn<Cake, Integer> idColumn = new TableColumn<>("ID");
        TableColumn<Cake, String> typeColumn = new TableColumn<>("Type");

        idColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getId()));
        typeColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getType()));

        cakesTable.getColumns().addAll(idColumn, typeColumn);
        //design pattern-ul observable (face modificari in  timp real pt a fi la curent)
        ObservableList<Cake> observableListCakes = FXCollections.observableArrayList(service.getAllCakes());
        cakesTable.setItems(observableListCakes);

        //similar cu un tabel, organizeaza pe randuri si colkoane
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        Label idLabel = new Label("ID");
        Label typeLabel = new Label("Type");
        TextField idTextField = new TextField();
        TextField typeTextField = new TextField();

        gridPane.add(idLabel, 0, 0);
        gridPane.add(idTextField, 1, 0);
        gridPane.add(typeLabel, 0, 1);
        gridPane.add(typeTextField, 1, 1);

        //amplasez butoanele orizontal
        HBox cakesButtons = new HBox(10);
        Button addCakeButton = new Button("Add Cake");
        Button deleteCakeButton = new Button("Delete Cake");
        Button updateCakeButton = new Button("Update Cake");
        cakesButtons.getChildren().addAll(addCakeButton, deleteCakeButton, updateCakeButton);
        cakesButtons.setAlignment(Pos.CENTER);

        cakesBox.getChildren().addAll(cakesTable, gridPane, cakesButtons);
        cakesTab.setContent(cakesBox);

        commandsTab.setClosable(false);
        VBox commandsBox = new VBox();
        commandsBox.setSpacing(10);

        //tabela pt Commands
        TableView<Command> commandsTable = new TableView<>();
        TableColumn<Command, Integer> idCommands = new TableColumn<>("ID");
        TableColumn<Command, Date> dateCommands = new TableColumn<>("Date");
        TableColumn<Command, String> cakesCommands = new TableColumn<>("Cakes");

        //organizez coloanele
        idCommands.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getId()));
        dateCommands.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getDates()));
        //organizez torturile astfel incat sa imi ia doar tipul existent
        cakesCommands.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getCommands().stream()
                .map(Cake::getType)
                .collect(Collectors.joining(", "))));

        commandsTable.getColumns().addAll(idCommands, dateCommands, cakesCommands);
        ObservableList<Command> observableListCommands = FXCollections.observableArrayList(service.getAllCommands());
        commandsTable.setItems(observableListCommands);

        GridPane gridPaneCommands = new GridPane();
        gridPaneCommands.setHgap(10);
        gridPaneCommands.setVgap(10);

        Label commandsIdLabel = new Label("ID");
        Label commandsDateLabel = new Label("Date");
        Label commandsCakesLabel = new Label("Cakes");
        //de aici imi iau torturile existente
        ComboBox<Cake> cakeComboBox = new ComboBox<>();
        TextField commandsIdTextField = new TextField();
        TextField commandsDateTextField = new TextField();
        TextField commandsCakesTextField = new TextField();

        cakeComboBox.setItems(FXCollections.observableArrayList(service.getAllCakes()));

        gridPaneCommands.add(commandsIdLabel, 0, 0);
        gridPaneCommands.add(commandsIdTextField, 0, 1);
        gridPaneCommands.add(commandsDateLabel, 1, 0);
        gridPaneCommands.add(commandsDateTextField, 1, 1);
        gridPaneCommands.add(commandsCakesLabel, 2, 0);
        gridPaneCommands.add(commandsCakesTextField, 2, 1);

        HBox commandButtons = new HBox(10);
        Button addCommandButton = new Button("Add Command");
        Button deleteCommandButton = new Button("Delete Command");
        Button updateCommandButton = new Button("Update Command");

        commandButtons.getChildren().addAll(addCommandButton, deleteCommandButton, updateCommandButton);
        commandButtons.setAlignment(Pos.CENTER);

        commandsBox.getChildren().addAll(commandsTable, gridPaneCommands, commandButtons);
        commandsTab.setContent(commandsBox);

        //accesare tabelaa Cakes
        cakesTable.setOnMouseClicked(_ -> {
            Cake selectedCake = cakesTable.getSelectionModel().getSelectedItem();
            if (selectedCake != null) {
                idTextField.setText(String.valueOf(selectedCake.getId()));
                typeTextField.setText(selectedCake.getType());
            }
        });

        //de aici incep sa programez actiunea butoanelor
        addCakeButton.setOnAction(_ -> {
            try {
                String type = typeTextField.getText();
                if (!type.trim().isEmpty()) {
                    service.addCake(type);
                    observableListCakes.setAll(service.getAllCakes());
                }
                idTextField.clear();
                typeTextField.clear();
            } catch (DuplicateIDException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText(ex.getMessage());
                alert.show();
            }
        });

        deleteCakeButton.setOnAction(_ -> {
            try {
                int id = Integer.parseInt(idTextField.getText());
                service.deleteCake(id);
                observableListCakes.setAll(service.getAllCakes());
                idTextField.clear();
                typeTextField.clear();
            } catch (ObjectNotFoundException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText(ex.getMessage());
                alert.show();
            }
        });

        updateCakeButton.setOnAction(_ -> {
            try {
                int id = Integer.parseInt(idTextField.getText());
                String type = typeTextField.getText();
                if (!type.trim().isEmpty()) {
                    service.updateCake(id, type);
                    observableListCakes.setAll(service.getAllCakes());
                }
                idTextField.clear();
                typeTextField.clear();
            } catch (ObjectNotFoundException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Warning");
                alert.setContentText(ex.getMessage());
                alert.show();
            }
        });

        //accesare tabela Commands
        commandsTable.setOnMouseClicked(_ -> {
            Command selectedCommand = commandsTable.getSelectionModel().getSelectedItem();
            if (selectedCommand != null) {
                commandsIdTextField.setText(String.valueOf(selectedCommand.getId()));
                commandsDateTextField.setText(new SimpleDateFormat("yyyy-MM-dd").format(selectedCommand.getDates()));
                // Setează torturile în caseta de text "Cakes"
                String cakesList = selectedCommand.getCommands().stream()
                        .map(Cake::getType)
                        .collect(Collectors.joining(", "));
                commandsCakesTextField.setText(cakesList);
                cakeComboBox.setItems(FXCollections.observableArrayList(service.getAllCakes()));
            }
        });

        //de aici incep sa programez accesarea butoanelor
        addCommandButton.setOnAction(_ -> {
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(commandsDateTextField.getText());
                //String cakesText = commandsCakesTextField.getText();
                List<Integer> selectedCakeIds = service.getAllCakes().stream()
                        .filter(c -> List.of(commandsCakesTextField.getText().split(", ")).contains(c.getType()))
                        .map(Cake::getId)
                        .toList();
                if (!selectedCakeIds.isEmpty()) {
                    service.addCommand(selectedCakeIds, date);
                    observableListCommands.clear();
                    observableListCommands.setAll(service.getAllCommands());
                }
                commandsIdTextField.clear();
                commandsDateTextField.clear();
                commandsCakesTextField.clear();
            } catch (DuplicateIDException | ParseException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Warning");
                alert.setContentText(ex.getMessage());
                alert.show();
            }
        });


        deleteCommandButton.setOnAction(_ -> {
            try {
                int id = Integer.parseInt(commandsIdTextField.getText());
                service.deleteCommand(id);
                observableListCommands.setAll(service.getAllCommands());
                commandsIdTextField.clear();
                commandsDateTextField.clear();
                commandsCakesTextField.clear();
            } catch (ObjectNotFoundException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Warning");
                alert.setContentText(ex.getMessage());
                alert.show();
            }
        });

        updateCommandButton.setOnAction(_ -> {
            try {
                int id = Integer.parseInt(commandsIdTextField.getText());
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(commandsDateTextField.getText());
                String[] cakesTypes = commandsCakesTextField.getText().split(", ");
                List<Cake> allCakes = service.getAllCakes();
                List<Integer> selectedCakeIds = allCakes.stream()
                        .filter(cake -> List.of(cakesTypes).contains(cake.getType()))
                        .map(Cake::getId)
                        .distinct()
                        .collect(Collectors.toList());
                if (!selectedCakeIds.isEmpty()) {
                    service.updateCommand(id, selectedCakeIds, date);
                    observableListCommands.setAll(service.getAllCommands());
                }
                commandsIdTextField.clear();
                commandsDateTextField.clear();
                commandsCakesTextField.clear();
            } catch (ObjectNotFoundException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText(ex.getMessage());
                alert.show();
            } catch (ParseException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Warning");
                alert.setContentText(ex.getMessage());
                alert.show();
            }
        });


        //AM VRUT SA FAC GUI SA ARATE CUTE ^_^

        //personalizare tabele
        String tableStyle = "-fx-background-color: rgba(148, 0, 211, 0.5); " +
                "-fx-border-color: black; " +
                "-fx-border-width: 2px;";
        cakesTable.setStyle(tableStyle);
        commandsTable.setStyle(tableStyle);

        //personalizare tab-uri
        String tabStyle = "-fx-background-color: rgba(0, 0, 139, 0.5); " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-border-color: black; " +
                "-fx-border-width: 2px;" +
                "-fx-padding: 10px;";
        tabPane.setStyle("-fx-tab-min-width: 150px; -fx-tab-max-height: 30px;");
        cakesTab.setStyle(tabStyle);
        commandsTab.setStyle(tabStyle);

        //personalizare butoanele
        String buttonStyle = "-fx-background-color: rgba(173, 216, 230, 0.5); " +
                "-fx-border-color: black; " +
                "-fx-border-width: 2px; " +
                "-fx-text-fill: black; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 5px 15px;";
        addCakeButton.setStyle(buttonStyle);
        deleteCakeButton.setStyle(buttonStyle);
        updateCakeButton.setStyle(buttonStyle);

        addCommandButton.setStyle(buttonStyle);
        deleteCommandButton.setStyle(buttonStyle);
        updateCommandButton.setStyle(buttonStyle);

        //personalizare etichetele
        String labelStyle = "-fx-text-fill: black; -fx-font-size: 14px; -fx-font-weight: bold;";
        idLabel.setStyle(labelStyle);
        typeLabel.setStyle(labelStyle);
        commandsIdLabel.setStyle(labelStyle);
        commandsDateLabel.setStyle(labelStyle);
        commandsCakesLabel.setStyle(labelStyle);

        //personalizare câmpurile de text
        String textFieldStyle = "-fx-background-color: rgba(255, 182, 193, 0.5); " +
                "-fx-border-color: black; " +
                "-fx-border-width: 2px; " +
                "-fx-text-fill: black; " +
                "-fx-padding: 5px;";
        idTextField.setStyle(textFieldStyle);
        typeTextField.setStyle(textFieldStyle);
        commandsIdTextField.setStyle(textFieldStyle);
        commandsDateTextField.setStyle(textFieldStyle);
        commandsCakesTextField.setStyle(textFieldStyle);


        tabPane.getTabs().addAll(cakesTab, commandsTab);
        Scene scene = new Scene(tabPane);
        stage.setTitle("Cake Orders");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
