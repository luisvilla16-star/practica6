module org.example.practica6 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.commons.csv;

    opens org.example.practica6 to javafx.fxml;
    exports org.example.practica6;
}