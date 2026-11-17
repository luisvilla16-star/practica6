package org.example.practica6;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.util.ArrayList;

public class CargarDatos {

    // Lee una columna del CSV y guarda sus valores no vacíos como texto.
    private static ArrayList<String> leerColumna(
            String nombreArchivo, String nombreColumna) throws Exception {

        ArrayList<String> valores = new ArrayList<>();

        try (
                FileReader lector = new FileReader(nombreArchivo);
                CSVParser archivo = CSVFormat.RFC4180.builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .get()
                        .parse(lector)
        ) {
            for (CSVRecord fila : archivo) {
                String texto = fila.get(nombreColumna);

                if (texto != null && !texto.trim().isEmpty()) {
                    valores.add(texto.trim());
                }
            }
        }

        return valores;
    }

    // Carga columnas con números enteros, como peak_position.
    public static Integer[] cargarEnteros(
            String nombreArchivo, String nombreColumna) throws Exception {

        ArrayList<String> valores = leerColumna(nombreArchivo, nombreColumna);
        Integer[] resultado = new Integer[valores.size()];

        for (int i = 0; i < valores.size(); i++) {
            resultado[i] = (int) Double.parseDouble(valores.get(i));
        }

        return resultado;
    }


    public static Integer[] cargarPosiciones(String nombreArchivo)
            throws Exception {
        return cargarEnteros(nombreArchivo, "peak_position");
    }

    // Carga columnas decimales, como danceability o tempo.
    public static Double[] cargarDecimales(
            String nombreArchivo, String nombreColumna) throws Exception {

        ArrayList<String> valores = leerColumna(nombreArchivo, nombreColumna);
        Double[] resultado = new Double[valores.size()];

        for (int i = 0; i < valores.size(); i++) {
            resultado[i] = Double.parseDouble(valores.get(i));
        }

        return resultado;
    }

    // Carga columnas de texto y fechas.
    public static String[] cargarTexto(
            String nombreArchivo, String nombreColumna) throws Exception {

        ArrayList<String> valores = leerColumna(nombreArchivo, nombreColumna);
        return valores.toArray(new String[0]);
    }

    // Carga columnas verdadero/falso.
    public static Boolean[] cargarBooleanos(
            String nombreArchivo, String nombreColumna) throws Exception {

        ArrayList<String> valores = leerColumna(nombreArchivo, nombreColumna);
        Boolean[] resultado = new Boolean[valores.size()];

        for (int i = 0; i < valores.size(); i++) {
            resultado[i] = Boolean.parseBoolean(valores.get(i));
        }

        return resultado;
    }
}