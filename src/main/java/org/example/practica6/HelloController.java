
package org.example.practica6;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.stage.Stage;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;

public class HelloController {

    private String ruta = "C:\\Users\\LUIS GABRIEL VILLA\\Downloads\\billboard_hot_100_spotify_audio_features_enriched.csv";

    private TableView<String[]> tabla;

    private String[] enteros = {
            "spotify_track_duration_ms", "key", "mode",
            "time_signature", "spotify_track_popularity",
            "peak_position", "weeks_on_chart"
    };

    private String[] decimales = {
            "danceability", "energy", "loudness",
            "speechiness", "acousticness", "instrumentalness",
            "liveness", "valence", "tempo", "confidence_score"
    };

    private String[] booleanos = {
            "spotify_track_explicit"
    };

    public BorderPane crearVentana() {

        tabla = new TableView<>();

        cargarTabla();

        Label mensaje = new Label(
                "Haz clic en una columna para ver su gráfica.");

        VBox arriba = new VBox(mensaje);
        arriba.setPadding(new Insets(10));

        BorderPane ventana = new BorderPane();

        ventana.setTop(arriba);
        ventana.setCenter(tabla);

        return ventana;
    }

    private void cargarTabla() {

        try {

            FileReader lector = new FileReader(ruta);

            CSVParser archivo = CSVFormat.RFC4180.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .get()
                    .parse(lector);

            String[] columnas = archivo.getHeaderMap()
                    .keySet().toArray(new String[0]);

            // Crear columnas
            for (int i = 0; i < columnas.length; i++) {

                final int posicion = i;
                String nombre = columnas[i];

                TableColumn<String[], String> columna =
                        new TableColumn<>();

                Button boton = new Button(nombre);

                boton.setOnAction(e -> mostrarGrafica(nombre));

                columna.setGraphic(boton);

                columna.setCellValueFactory(celda ->
                        new ReadOnlyStringWrapper(
                                celda.getValue()[posicion]));

                columna.setPrefWidth(160);

                tabla.getColumns().add(columna);
            }

            // Leer los datos
            for (CSVRecord fila : archivo) {

                String[] datos = new String[columnas.length];

                for (int i = 0; i < columnas.length; i++) {
                    datos[i] = fila.get(columnas[i]);
                }

                tabla.getItems().add(datos);
            }

            archivo.close();
            lector.close();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void mostrarGrafica(String columna) {

        try {

            if (contiene(enteros, columna)) {

                Integer[] datos =
                        CargarDatos.cargarEnteros(ruta, columna);

                calcularTiempos(columna, datos);

            } else if (contiene(decimales, columna)) {

                Double[] datos =
                        CargarDatos.cargarDecimales(ruta, columna);

                calcularTiempos(columna, datos);

            } else if (contiene(booleanos, columna)) {

                Boolean[] datos =
                        CargarDatos.cargarBooleanos(ruta, columna);

                calcularTiempos(columna, datos);

            } else {

                String[] datos =
                        CargarDatos.cargarTexto(ruta, columna);

                calcularTiempos(columna, datos);
            }

        } catch (Exception e) {

            e.printStackTrace();

            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setContentText("Error al generar la gráfica.");
            alerta.showAndWait();
        }
    }

    private boolean contiene(String[] arreglo, String nombre) {

        for (int i = 0; i < arreglo.length; i++) {

            if (arreglo[i].equals(nombre)) {
                return true;
            }
        }

        return false;
    }

    private <T extends Comparable<T>> void calcularTiempos(
            String columna, T[] datos) {

        double t1 = medir("seleccion", datos);
        double t2 = medir("shell", datos);
        double t3 = medir("quicksort", datos);
        double t4 = medir("mergeSort", datos);
        double t5 = medir("sort", datos);
        double t6 = medir("parallelSort", datos);

        PieChart grafica = new PieChart();

        grafica.setTitle("Tiempos de " + columna);
        grafica.setLegendSide(Side.RIGHT);

        grafica.setData(FXCollections.observableArrayList(

                new PieChart.Data("Selección: " + t1 + " ms",
                        t1),

                new PieChart.Data("Shell: " + t2 + " ms",
                        t2),

                new PieChart.Data("Quicksort: " + t3 + " ms",
                        t3 < 0 ? 0 : t3),

                new PieChart.Data("MergeSort: " + t4 + " ms",
                        t4),

                new PieChart.Data("Arrays.sort(): " + t5 + " ms",
                        t5),

                new PieChart.Data("ParallelSort: " + t6 + " ms",
                        t6)
        ));

        Stage ventana = new Stage();

        VBox contenido = new VBox(grafica);
        contenido.setPadding(new Insets(10));

        ventana.setTitle("Gráfica de " + columna);
        ventana.setScene(new Scene(contenido, 900, 600));

        ventana.show();
    }

    private <T extends Comparable<T>> double medir(
            String metodo, T[] datos) {

        T[] copia = datos.clone();

        Ordenar<T> ordenar = new Ordenar<>();

        long inicio = System.nanoTime();

        if (metodo.equals("seleccion")) {

            ordenar.seleccion(copia, copia.length);

        } else if (metodo.equals("shell")) {

            ordenar.Shell(copia, copia.length);

        } else if (metodo.equals("quicksort")) {

            try {

                ordenar.recursivo(copia, 0, copia.length - 1);

            } catch (StackOverflowError e) {

                return -1;
            }

        } else if (metodo.equals("mergeSort")) {

            ordenar.MergeSort(copia, 0, copia.length - 1);

        } else if (metodo.equals("sort")) {

            ordenar.sort(copia);

        } else if (metodo.equals("parallelSort")) {

            ordenar.parallelSort(copia);
        }

        long fin = System.nanoTime();

        return (fin - inicio) / 1000000.0;
    }
}
