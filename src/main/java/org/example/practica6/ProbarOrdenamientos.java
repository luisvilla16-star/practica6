package org.example.practica6;

public class ProbarOrdenamientos {

    static final int REPETICIONES = 5;

    public static void main(String[] args) throws Exception {

        String ruta = "C:\\Users\\LUIS GABRIEL VILLA\\Downloads\\billboard_hot_100_spotify_audio_features_enriched.csv";

        // Columnas que contienen números enteros.
        String[] enteros = {
                "spotify_track_duration_ms",
                "key",
                "mode",
                "time_signature",
                "spotify_track_popularity",
                "peak_position",
                "weeks_on_chart"
        };

        // Columnas que contienen números decimales.
        String[] decimales = {
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

        // Columnas de texto y fechas.
        // Las fechas están en formato AAAA-MM-DD y se ordenan como texto.
        String[] textos = {
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

        // Columna de verdadero/falso.
        String[] booleanos = {
                "spotify_track_explicit"
        };

        // Prueba todas las columnas enteras.
        for (String columna : enteros) {
            Integer[] datos =
                    CargarDatos.cargarEnteros(ruta, columna);
            probarColumna(columna, datos);
        }

        // Prueba todas las columnas decimales.
        for (String columna : decimales) {
            Double[] datos =
                    CargarDatos.cargarDecimales(ruta, columna);
            probarColumna(columna, datos);
        }

        // Prueba todas las columnas de texto y fechas.
        for (String columna : textos) {
            String[] datos =
                    CargarDatos.cargarTexto(ruta, columna);
            probarColumna(columna, datos);
        }

        // Prueba las columnas verdadero/falso.
        for (String columna : booleanos) {
            Boolean[] datos =
                    CargarDatos.cargarBooleanos(ruta, columna);
            probarColumna(columna, datos);
        }
    }

    public static <T extends Comparable<T>> void probarColumna(
            String columna, T[] datosOriginales) {

        System.out.println();
        System.out.println("Columna: " + columna);
        System.out.println("Valores cargados: " + datosOriginales.length);
        System.out.println("Método                 Promedio (ms)     Ordenado");

        medir("Selección", datosOriginales);
        medir("Shell", datosOriginales);
        medir("Quicksort", datosOriginales);
        medir("MergeSort", datosOriginales);
        medir("Arrays.sort()", datosOriginales);
        medir("Arrays.parallelSort()", datosOriginales);
    }

    public static <T extends Comparable<T>> void medir(
            String nombre, T[] originales) {

        Ordenar<T> ordenador = new Ordenar<>();

        // Ejecución previa; no se cuenta en el promedio.
        ordenar(nombre, originales.clone(), ordenador);

        long sumaTiempos = 0;
        T[] ultimoResultado = originales.clone();

        for (int i = 0; i < REPETICIONES; i++) {
            T[] copia = originales.clone();

            // El cronómetro rodea únicamente la llamada al ordenamiento.
            long inicio = System.nanoTime();
            ordenar(nombre, copia, ordenador);
            long fin = System.nanoTime();

            sumaTiempos += fin - inicio;
            ultimoResultado = copia;
        }

        double promedio =
                (sumaTiempos / (double) REPETICIONES) / 1_000_000.0;

        System.out.printf("%-22s %-16.4f %s%n",
                nombre, promedio, estaOrdenado(ultimoResultado));
    }

    public static <T extends Comparable<T>> void ordenar(
            String nombre, T[] datos, Ordenar<T> ordenador) {

        if (nombre.equals("Selección")) {
            ordenador.seleccion(datos, datos.length);
        } else if (nombre.equals("Shell")) {
            ordenador.Shell(datos, datos.length);
        } else if (nombre.equals("Quicksort")) {
            ordenador.recursivo(datos, 0, datos.length - 1);
        } else if (nombre.equals("MergeSort")) {
            ordenador.MergeSort(datos, 0, datos.length - 1);
        } else if (nombre.equals("Arrays.sort()")) {
            ordenador.sort(datos);
        } else if (nombre.equals("Arrays.parallelSort()")) {
            ordenador.parallelSort(datos);
        }
    }

    public static <T extends Comparable<T>> boolean estaOrdenado(T[] datos) {
        for (int i = 0; i < datos.length - 1; i++) {
            if (datos[i].compareTo(datos[i + 1]) > 0) {
                return false;
            }
        }

        return true;
    }
}