package controlador;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GestorPersistencia {

    private static final String ARCHIVO_RECORD = "record.txt";

    /**
     * Lee la puntuación máxima guardada localmente en el archivo record.txt.
     * Si el archivo no existe o ocurre un error, retorna 0.
     */
    public static int cargarRecord() {
        File archivo = new File(ARCHIVO_RECORD);
        if (!archivo.exists()) {
            return 0;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea = reader.readLine();
            if (linea != null && !linea.trim().isEmpty()) {
                return Integer.parseInt(linea.trim());
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error al cargar el récord guardado: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Guarda la nueva puntuación máxima localmente en el archivo record.txt.
     */
    public static void guardarRecord(int nuevoRecord) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_RECORD))) {
            writer.write(String.valueOf(nuevoRecord));
        } catch (IOException e) {
            System.err.println("Error al guardar el récord: " + e.getMessage());
        }
    }
}