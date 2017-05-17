/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inicializacion;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author mario
 */
public class Wine {

    private static String path = "src/datos/";
    private static String file = "wine.json";

    public static void main(String[] args) throws IOException {

        //Leer los datos del documento
        JSONArray wines = leerDatos(path + file);
        
        String contenido = "";
        //Se crea el contenido para la petición _bulk
        for (int i = 0; i < wines.size(); i++) {
            //Se extra el objeto 
            JSONObject wine = (JSONObject) wines.get(i);
            contenido += "{ \"index\": {\"_id\":" + wine.get("No") + " }}\n"
                    + wine.toJSONString() + "\n";

        }
               
        //Ruta del indice 
        String indexUrl = "http://localhost:9200/wines/wine/";

        //Se crea el indice con el contenido
        creaIndice(indexUrl, contenido);
        
        //Ruta para eliminar el indice completo
        String deleteUrl = "http://localhost:9200/wines";
        
        //Eliminar el indice completo
        eliminaIndice(deleteUrl, contenido);

    }

    private static JSONArray leerDatos(String path) {
        JSONParser parser = new JSONParser();
        try {
            return (JSONArray) parser.parse(new FileReader(path));
        } catch (IOException | ParseException e) {
            System.err.println("Error en la lectura de datos: " + e);
        }
        return null;
    }

    private static void creaIndice(String indexUrl, String contenido) {

        try {
            //Se crea la url para indearlo
            URL url = new URL(indexUrl + "_bulk");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            //Envia la peticion
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(contenido);
            out.close();

            //Captura la respuesta a la peticion
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            //Imprime la respuesta
            System.out.println(response.toString());
        } catch (MalformedURLException ex) {
            System.err.println("Error en la URL: " + ex);
        } catch (IOException ex) {
            System.err.println("Error en la conexion: " + ex);
        }
    }

    private static void eliminaIndice(String deleteUrl, String contenido) {
        try {
            URL url = new URL(deleteUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            //Envia la peticion
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(contenido);
            out.close();

            //Captura la respuesta a la peticion
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            //Imprime la respuesta
            System.out.println(response.toString());
        } catch (MalformedURLException ex) {
            System.err.println("Error en la URL: " + ex);
        } catch (IOException ex) {
            System.err.println("Error en la conexion: " + ex);
        }
    }
}
