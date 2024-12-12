module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafaker;
    requires org.xerial.sqlitejdbc;
    requires java.desktop;


    opens com.example.demo to javafx.fxml;
    exports com.example.demo;
}