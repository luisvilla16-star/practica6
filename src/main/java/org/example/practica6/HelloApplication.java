package org.example.practica6;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    @Override
    public void start(Stage ventana) {
        HelloController controlador = new HelloController();

        Scene escena = new Scene(controlador.crearVentana(), 1200, 800);

        ventana.setTitle("Práctica 6 - Tiempos de ordenamiento");
        ventana.setScene(escena);
        ventana.setMaximized(true);
        ventana.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}