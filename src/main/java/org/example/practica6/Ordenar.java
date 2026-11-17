
package org.example.practica6;

import java.util.Arrays;

public class Ordenar<T extends Comparable<T>> {

    public void seleccion(T[] a, int n) {
        T menor;
        int k = 0;

        for (int i = 0; i < n - 1; i++) {
            menor = a[i];
            k = i;

            for (int j = i + 1; j < n; j++) {
                if (a[j].compareTo(menor) < 0) {
                    menor = a[j];
                    k = j;
                }
            }

            a[k] = a[i];
            a[i] = menor;
        }
    }

    public void Shell(T[] a, int n) {
        int inter = n + 1;
        T aux;

        while (inter > 1) {
            inter = inter / 2;
            int bandera = 1;

            while (bandera == 1) {
                bandera = 0;
                int i = 0;

                while ((i + inter) < n) {
                    if (a[i].compareTo(a[i + inter]) > 0) {
                        aux = a[i];
                        a[i] = a[i + inter];
                        a[i + inter] = aux;
                        bandera = 1;
                    }

                    i = i + 1;
                }
            }
        }
    }

    public void recursivo(T[] a, int inicio, int fin) {
        int izquierda = inicio;
        int derecha = fin;
        int pos = inicio;
        T aux;
        boolean bander = true;

        if (inicio >= fin) {
            return;
        }

        while (bander == true) {
            bander = false;

            while (pos != derecha &&
                    a[pos].compareTo(a[derecha]) <= 0) {
                derecha = derecha - 1;
            }

            if (pos != derecha) {
                aux = a[pos];
                a[pos] = a[derecha];
                a[derecha] = aux;
                pos = derecha;
            }

            while (pos != izquierda &&
                    a[pos].compareTo(a[izquierda]) >= 0) {
                izquierda = izquierda + 1;
            }

            if (pos != izquierda) {
                bander = true;
                aux = a[pos];
                a[pos] = a[izquierda];
                a[izquierda] = aux;
                pos = izquierda;
            }
        }

        if (inicio < pos - 1) {
            recursivo(a, inicio, pos - 1);
        }

        if (pos + 1 < fin) {
            recursivo(a, pos + 1, fin);
        }
    }

    public void MergeSort(T[] a, int inicio, int fin) {
        if (inicio < fin) {
            int medio = (inicio + fin) / 2;

            MergeSort(a, inicio, medio);
            MergeSort(a, medio + 1, fin);

            Mezclar(a, inicio, medio, fin);
        }
    }

    public void Mezclar(T[] a, int inicio, int medio, int fin) {
        int n1 = medio - inicio + 1;
        int n2 = fin - medio;

        T[] Izq = Arrays.copyOfRange(a, inicio, medio + 1);
        T[] Der = Arrays.copyOfRange(a, medio + 1, fin + 1);

        int i = 0;
        int j = 0;
        int k = inicio;

        while (i < n1 && j < n2) {
            if (Izq[i].compareTo(Der[j]) <= 0) {
                a[k] = Izq[i];
                i++;
            } else {
                a[k] = Der[j];
                j++;
            }

            k++;
        }

        while (i < n1) {
            a[k] = Izq[i];
            i++;
            k++;
        }

        while (j < n2) {
            a[k] = Der[j];
            j++;
            k++;
        }
    }

    public void sort(T[] a) {
        Arrays.sort(a);
    }

    public void parallelSort(T[] a) {
        Arrays.parallelSort(a);
    }
}
