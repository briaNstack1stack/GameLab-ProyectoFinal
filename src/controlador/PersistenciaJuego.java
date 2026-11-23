package controlador;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import modelo.DatosPartida;

public class PersistenciaJuego {

    private static final String ARCHIVO_RECORD = "record.txt";
    private static final String ARCHIVO_HISTORIAL = "records.txt";

    // Cargar la puntuación máxima
    public static int cargarRecord() {

        int mayor = 0;

        ArrayList<String[]> registros = leerRecords();

        for (String[] registro : registros) {
            int puntos = Integer.parseInt(registro[1]);

            if (puntos > mayor) {
                mayor = puntos;
            }
        }

        // Compatibilidad con el archivo antiguo
        File archivo = new File(ARCHIVO_RECORD);

        if (archivo.exists()) {
            try (BufferedReader reader = new BufferedReader(
                    new FileReader(archivo))) {

                String linea = reader.readLine();

                if (linea != null) {
                    try {
                        int puntos = Integer.parseInt(linea.trim());

                        if (puntos > mayor) {
                            mayor = puntos;
                        }
                    } catch (NumberFormatException e) {
                        String segundaLinea = reader.readLine();

                        if (segundaLinea != null) {
                            int puntos = Integer.parseInt(
                                segundaLinea.trim()
                            );

                            if (puntos > mayor) {
                                mayor = puntos;
                            }
                        }
                    }
                }

            } catch (IOException | NumberFormatException e) {
                System.err.println("Error al cargar récord: " + e.getMessage());
            }
        }

        return mayor;
    }

    // Cargar las iniciales del mejor jugador
    public static String cargarNombreRecord() {

        ArrayList<String[]> registros = leerRecords();

        if (!registros.isEmpty()) {
            return registros.get(0)[0];
        }

        return "---";
    }

    // Guardar una partida nueva
    public static void guardarRecord(String nombre, int puntuacion) {

        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(ARCHIVO_HISTORIAL, true))) {

            writer.write(nombre + ";" + puntuacion);
            writer.newLine();

        } catch (IOException e) {
            System.err.println("Error al guardar historial: " + e.getMessage());
        }

        // Actualizar el récord máximo
        int mayor = cargarRecord();

        if (puntuacion >= mayor) {
            try (BufferedWriter writer = new BufferedWriter(
                    new FileWriter(ARCHIVO_RECORD))) {

                writer.write(nombre);
                writer.newLine();
                writer.write(String.valueOf(puntuacion));

            } catch (IOException e) {
                System.err.println("Error al actualizar récord: " + e.getMessage());
            }
        }
    }

    // Leer todos los registros
    private static ArrayList<String[]> leerRecords() {

        ArrayList<String[]> registros = new ArrayList<>();

        File archivo = new File(ARCHIVO_HISTORIAL);

        if (!archivo.exists()) {
            return registros;
        }

        try (BufferedReader reader = new BufferedReader(
                new FileReader(archivo))) {

            String linea;

            while ((linea = reader.readLine()) != null) {

                String[] datos = linea.split(";");

                if (datos.length == 2) {
                    try {
                        Integer.parseInt(datos[1]);
                        registros.add(datos);
                    } catch (NumberFormatException e) {
                        // Ignorar registros inválidos
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Error al leer historial: " + e.getMessage());
        }

        Collections.sort(registros, new Comparator<String[]>() {
            @Override
            public int compare(String[] a, String[] b) {
                return Integer.compare(
                    Integer.parseInt(b[1]),
                    Integer.parseInt(a[1])
                );
            }
        });

        return registros;
    }

    // Mostrar todos los récords ordenados
    public static String cargarHistorialRecords() {

        ArrayList<String[]> registros = leerRecords();

        if (registros.isEmpty()) {
            return "Todavía no hay partidas registradas.";
        }

        StringBuilder texto = new StringBuilder();

        texto.append("POS.     JUGADOR     PUNTUACIÓN\n");
        texto.append("--------------------------------\n");

        for (int i = 0; i < registros.size(); i++) {

            String[] registro = registros.get(i);

            texto.append(String.format(
                "%-9d %-12s %s%n",
                i + 1,
                registro[0],
                registro[1]
            ));
        }

        return texto.toString();
    }
    

    // Archivo donde se guardará la partida
    private static final String ARCHIVO_PARTIDA = "partida_guardada.dat";

    // Guardar una partida
    public static void guardarPartida(DatosPartida datos) {

        try (ObjectOutputStream salida = new ObjectOutputStream(
                new FileOutputStream(ARCHIVO_PARTIDA))) {

            salida.writeObject(datos);

        } catch (IOException e) {
            System.err.println("Error al guardar partida: " + e.getMessage());
        }
    }

    // Cargar una partida
    public static DatosPartida cargarPartida() {

        File archivo = new File(ARCHIVO_PARTIDA);

        if (!archivo.exists()) {
            return null;
        }

        try (ObjectInputStream entrada = new ObjectInputStream(
                new FileInputStream(archivo))) {

            return (DatosPartida) entrada.readObject();

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al cargar partida: " + e.getMessage());
            return null;
        }
    }

    // Comprobar si existe una partida guardada
    public static boolean existePartidaGuardada() {

        return new File(ARCHIVO_PARTIDA).exists();
    }

    // Eliminar partida guardada
    public static void eliminarPartidaGuardada() {

        File archivo = new File(ARCHIVO_PARTIDA);

        if (archivo.exists() && !archivo.delete()) {
            System.err.println("No se pudo eliminar la partida guardada.");
        }
    }

}