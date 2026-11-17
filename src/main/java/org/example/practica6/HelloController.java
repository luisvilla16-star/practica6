package org.example.practica6;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class HelloController {

    private final String ruta =
            "C:\\Users\\LUIS GABRIEL VILLA\\Downloads\\billboard_hot_100_spotify_audio_features_enriched.csv";

    private ComboBox<String> selector;
    private PieChart grafica;
    private Label mensaje;

    private final String[] columnasEnteras = {
            "spotify_track_duration_ms",
            "key",
            "mode",
            "time_signature",
            "spotify_track_popularity",
            "peak_position",
            "weeks_on_chart"
    };

    private final String[] columnasDecimales = {
            "danceability",
            "energy",
            "loudness",
            "speechiness",
            "acousticness",
            "instrumentalness",
            "liveness",
            "valence",
            "tempo",
            "confidence_score"
    };

    private final String[] columnasTexto = {
            "song_id",
            "performer",
            "song",
            "spotify_genre",
            "spotify_track_id",
            "spotify_track_preview_url",
            "spotify_track_album",
            "first_week",
            "last_week",
            "chart_success_tier",
            "hit_longevity_profile",
            "acoustic_mood_cluster",
            "musical_era",
            "evidence_span",
            "cot_rationale"
    };

    public BorderPane crearVentana() {
        selector = new ComboBox<>();
        selector.setPromptText("Selecciona una columna");

        selector.getItems().addAll(columnasEnteras);
        selector.getItems().addAll(columnasDecimales);
        selector.getItems().addAll(columnasTexto);
        selector.getItems().add("spotify_track_explicit");

        mensaje = new Label("Elige una columna para comparar los tiempos.");

        grafica = new PieChart();
        grafica.setTitle("Tiempos de ordenamiento");
        grafica.setMinSize(800, 600);
        grafica.setPrefSize(1000, 700);
        grafica.setLegendSide(Side.RIGHT);
        grafica.setLabelsVisible(false);

        selector.setOnAction(event ->
                seleccionarColumna(selector.getValue())
        );

        VBox parteSuperior = new VBox(10, mensaje, selector);
        parteSuperior.setPadding(new Insets(10));

        BorderPane ventana = new BorderPane();
        ventana.setTop(parteSuperior);
        ventana.setCenter(grafica);

        return ventana;
    }

    private void seleccionarColumna(String columna) {
        if (columna == null) {
            return;
        }

        mensaje.setText("Calculando con todos los valores...");
        selector.setDisable(true);

        try {
            if (contiene(columnasEnteras, columna)) {
                Integer[] datos =
                        CargarDatos.cargarEnteros(ruta, columna);
                mostrarTiempos(columna, datos);

            } else if (contiene(columnasDecimales, columna)) {
                Double[] datos =
                        CargarDatos.cargarDecimales(ruta, columna);
                mostrarTiempos(columna, datos);

            } else if (columna.equals("spotify_track_explicit")) {
                Boolean[] datos =
                        CargarDatos.cargarBooleanos(ruta, columna);
                mostrarTiempos(columna, datos);

            } else {
                String[] datos =
                        CargarDatos.cargarTexto(ruta, columna);
                mostrarTiempos(columna, datos);
            }

        } catch (Exception error) {
            mensaje.setText("No se pudieron cargar los datos.");
        } finally {
            selector.setDisable(false);
        }
    }

    private boolean contiene(String[] columnas, String buscada) {
        for (String columna : columnas) {
            if (columna.equals(buscada)) {
                return true;
            }
        }

        return false;
    }

    private <T extends Comparable<T>> void mostrarTiempos(
            String columna, T[] datos) {

        double seleccion = medir("seleccion", datos);
        double shell = medir("shell", datos);
        double quicksort = medir("quicksort", datos);
        double mergeSort = medir("mergeSort", datos);
        double sort = medir("sort", datos);
        double parallelSort = medir("parallelSort", datos);

        String textoQuickSort;
        double valorQuickSort;

        if (quicksort < 0) {
            textoQuickSort = "Quicksort: no terminó";
            valorQuickSort = 0;
        } else {
            textoQuickSort = "Quicksort: " + quicksort + " ms";
            valorQuickSort = quicksort;
        }

        grafica.setTitle("Tiempos para " + columna);

        grafica.setData(FXCollections.observableArrayList(
                new PieChart.Data(
                        "Selección: " + seleccion + " ms", seleccion),
                new PieChart.Data(
                        "Shell: " + shell + " ms", shell),
                new PieChart.Data(textoQuickSort, valorQuickSort),
                new PieChart.Data(
                        "MergeSort: " + mergeSort + " ms", mergeSort),
                new PieChart.Data(
                        "Arrays.sort(): " + sort + " ms", sort),
                new PieChart.Data(
                        "Arrays.parallelSort(): "
                                + parallelSort + " ms",
                        parallelSort)
        ));

        mensaje.setText(
                "Columna: " + columna
                        + " | Valores cargados: " + datos.length
        );
    }

    private <T extends Comparable<T>> double medir(
            String metodo, T[] datosOriginales) {

        T[] copia = datosOriginales.clone();
        Ordenar<T> ordenador = new Ordenar<>();

        long inicio = System.nanoTime();

        if (metodo.equals("seleccion")) {
            ordenador.seleccion(copia, copia.length);
        } else if (metodo.equals("shell")) {
            ordenador.Shell(copia, copia.length);
        } else if (metodo.equals("quicksort")) {
            try {
                ordenador.recursivo(copia, 0, copia.length - 1);
            } catch (StackOverflowError error) {
                return -1;
            }
        } else if (metodo.equals("mergeSort")) {
            ordenador.MergeSort(copia, 0, copia.length - 1);
        } else if (metodo.equals("sort")) {
            ordenador.sort(copia);
        } else if (metodo.equals("parallelSort")) {
            ordenador.parallelSort(copia);
        }

        long fin = System.nanoTime();

        return (fin - inicio) / 1_000_000.0;
    }
}