
package org.example.practica6;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.stage.Stage;
import javafx.util.Duration;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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
                "Pasa el mouse sobre las barras para ver el conteo. "
                        + "Haz clic en \"Tiempos\" para comparar los algoritmos de ordenamiento.");

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

            List<TableColumn<String[], String>> listaColumnas =
                    new ArrayList<>();

            // Crear columnas
            for (int i = 0; i < columnas.length; i++) {

                final int posicion = i;

                TableColumn<String[], String> columna =
                        new TableColumn<>();

                columna.setCellValueFactory(celda ->
                        new ReadOnlyStringWrapper(
                                celda.getValue()[posicion]));

                columna.setPrefWidth(210);

                tabla.getColumns().add(columna);
                listaColumnas.add(columna);
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

            // Encabezados
            for (int i = 0; i < columnas.length; i++) {

                listaColumnas.get(i)
                        .setGraphic(crearHistograma(columnas[i], i));
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // Números abreviados
    private String compacto(double v) {

        double a = Math.abs(v);

        if (a >= 1_000_000) {
            return new DecimalFormat("#.##").format(v / 1_000_000) + "m";
        }

        if (a >= 10_000) {
            return new DecimalFormat("#.#").format(v / 1_000) + "k";
        }

        return new DecimalFormat("#.##").format(v);
    }

    // Gráfica de pastel para booleanos
    private HBox crearPastel(int posicion) {

        double verdaderos = 0, falsos = 0, nulos = 0;

        for (String[] fila : tabla.getItems()) {

            String v = fila[posicion] == null ? "" : fila[posicion].trim();

            if (v.equalsIgnoreCase("true")) {
                verdaderos++;
            } else if (v.equalsIgnoreCase("false")) {
                falsos++;
            } else {
                nulos++;
            }
        }

        double total = verdaderos + falsos + nulos;

        String[] nombres = {"true", "false", "[null]"};
        double[] valores = {verdaderos, falsos, nulos};

        Color[] colores = {
                Color.web("#4C94FF"),
                Color.web("#1F7BFF"),
                Color.web("#1860C8")
        };

        double radio = 40;

        Pane pastel = new Pane();
        pastel.setPrefSize(radio * 2, radio * 2);
        pastel.setMinSize(radio * 2, radio * 2);

        VBox leyenda = new VBox(4);

        double inicio = 90;

        for (int i = 0; i < valores.length; i++) {

            double angulo = total == 0 ? 0 : valores[i] / total * 360;

            if (angulo > 0) {

                Arc arco = new Arc(radio, radio, radio, radio, inicio, -angulo);
                arco.setType(ArcType.ROUND);
                arco.setFill(colores[i]);
                arco.setStroke(Color.WHITE);

                Tooltip tip = new Tooltip(nombres[i] + "\nCount: "
                        + String.format("%,d", (long) valores[i]));

                tip.setShowDelay(Duration.ZERO);
                Tooltip.install(arco, tip);

                pastel.getChildren().add(arco);

                inicio -= angulo;
            }

            Label nombreLabel = new Label(nombres[i]);
            nombreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13;");

            long porcentaje = total == 0 ? 0 :
                    Math.round(valores[i] / total * 100);

            Label datoLabel = new Label(
                    compacto(valores[i]) + "  " + porcentaje + "%");

            leyenda.getChildren().add(new VBox(0, nombreLabel, datoLabel));
        }

        HBox caja = new HBox(10, pastel, leyenda);
        caja.setAlignment(Pos.CENTER_LEFT);

        return caja;
    }

    // Histograma y valores únicos
    private VBox crearHistograma(String nombre, int posicion) {

        final double ANCHO = 190;
        final double ALTO = 90;

        DecimalFormat formato = new DecimalFormat("#.##");

        Label titulo = new Label("# " + nombre);
        titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 13;");

        Button tiempos = new Button("⏱ Tiempos");
        tiempos.setOnAction(e -> mostrarGrafica(nombre));

        boolean esEntero = contiene(enteros, nombre);
        boolean esDecimal = contiene(decimales, nombre);
        boolean esBooleano = contiene(booleanos, nombre);

        String tipoDato = (esEntero || esDecimal) ? "double"
                : esBooleano ? "boolean" : "string";

        Label tipo = new Label(tipoDato);
        tipo.setStyle("-fx-text-fill: #555;");

        VBox caja = new VBox(6, titulo, tipo);
        caja.setPadding(new Insets(4));

        double[] cuentas;
        String[] rangos;
        String textoMin, textoMax;

        if (esEntero || esDecimal) {

            // Leer valores numéricos
            List<Double> valores = new ArrayList<>();

            for (String[] fila : tabla.getItems()) {

                try {
                    valores.add(Double.parseDouble(fila[posicion].trim()));
                } catch (Exception e) {
                    // Ignorar valores vacíos
                }
            }

            if (valores.isEmpty()) {
                caja.getChildren().add(tiempos);
                return caja;
            }

            double min = Double.MAX_VALUE;
            double max = -Double.MAX_VALUE;

            for (double v : valores) {

                if (v < min) min = v;
                if (v > max) max = v;
            }

            int cubetas;
            double ancho;

            if (esEntero && (max - min) <= 20) {

                cubetas = (int) (max - min) + 1;
                ancho = 1;

            } else {

                cubetas = 20;
                ancho = (max - min) / cubetas;

                if (ancho == 0) ancho = 1;
            }

            cuentas = new double[cubetas];
            rangos = new String[cubetas];

            for (double v : valores) {

                int idx = (int) ((v - min) / ancho);

                if (idx >= cubetas) idx = cubetas - 1;

                cuentas[idx]++;
            }

            for (int i = 0; i < cubetas; i++) {

                if (esEntero && ancho == 1) {

                    rangos[i] = formato.format(min + i);

                } else {

                    rangos[i] = formato.format(min + i * ancho) + " - "
                            + formato.format(min + (i + 1) * ancho);
                }
            }

            textoMin = compacto(min);
            textoMax = compacto(max);

        } else if (esBooleano) {

            // Conservar gráfica de pastel
            caja.getChildren().addAll(crearPastel(posicion), tiempos);
            return caja;

        } else {

            // Mostrar solamente la cantidad de valores únicos
            List<String> valoresUnicos = new ArrayList<>();

            for (String[] fila : tabla.getItems()) {

                String valor = fila[posicion];

                if (valor == null || valor.trim().isEmpty()) {
                    valor = "[null]";
                } else {
                    valor = valor.trim();
                }

                if (!valoresUnicos.contains(valor)) {
                    valoresUnicos.add(valor);
                }
            }

            Label cantidad = new Label(
                    "valore unicos: " + valoresUnicos.size()
            );

            caja.getChildren().addAll(cantidad, tiempos);

            return caja;
        }

        // Dibujar las barras
        double maxCuenta = 0;

        for (double c : cuentas) {
            if (c > maxCuenta) maxCuenta = c;
        }

        HBox barras = new HBox(0);
        barras.setAlignment(Pos.BOTTOM_LEFT);
        barras.setPrefHeight(ALTO);
        barras.setMinHeight(ALTO);

        double anchoBarra = ANCHO / cuentas.length;

        Color azul = Color.web("#4C94FF");
        Color azulOscuro = Color.web("#2F6FD6");

        for (int i = 0; i < cuentas.length; i++) {

            double alto = maxCuenta == 0 ? 0 :
                    (cuentas[i] / maxCuenta) * ALTO;

            Rectangle barra = new Rectangle(
                    anchoBarra, Math.max(alto, 1));

            barra.setFill(azul);

            Tooltip tip = new Tooltip(rangos[i] + "\nCount: "
                    + String.format("%,d", (long) cuentas[i]));

            tip.setShowDelay(Duration.ZERO);
            Tooltip.install(barra, tip);

            barra.setOnMouseEntered(e -> barra.setFill(azulOscuro));
            barra.setOnMouseExited(e -> barra.setFill(azul));

            barras.getChildren().add(barra);
        }

        BorderPane etiquetas = new BorderPane();

        etiquetas.setLeft(new Label(textoMin));
        etiquetas.setRight(new Label(textoMax));

        etiquetas.setPrefWidth(ANCHO);
        etiquetas.setMaxWidth(ANCHO);

        caja.getChildren().addAll(barras, etiquetas, tiempos);

        return caja;
    }

    // Gráfica de tiempos de ordenamiento
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
                        Math.max(0, t1)),

                new PieChart.Data("Shell: " + t2 + " ms",
                        Math.max(0, t2)),

                new PieChart.Data("Quicksort: " + t3 + " ms",
                        Math.max(0, t3)),

                new PieChart.Data("MergeSort: " + t4 + " ms",
                        Math.max(0, t4)),

                new PieChart.Data("Arrays.sort(): " + t5 + " ms",
                        Math.max(0, t5)),

                new PieChart.Data("ParallelSort: " + t6 + " ms",
                        Math.max(0, t6))
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
