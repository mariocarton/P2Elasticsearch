/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inicializacion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author mariomatesanz
 */
public class Maraton {

    private static String path = "src/datos/";
    private static String archivo = "maraton.json";

    public static void main(String[] args) throws MalformedURLException, IOException {
        
        //Lee los datos del fichero y se transforman a un array de JSON
        JSONArray maratones = leerDatos(path + archivo);

        String contenido = "";        
        //Se crea el contenido para la petición _bulk
        for (int i = 0; i < maratones.size(); i++) {
            //Se extra el objeto 
            JSONObject maraton = (JSONObject) maratones.get(i);
            contenido += "{ \"index\": {\"_id\":" + maraton.get("Place") + " }}\n"
                    + maraton.toJSONString() + "\n";
        }

        //Ruta del indice 
        String indexUrl = "http://localhost:9200/maraton/participante/";

        //Se crea el indice del contenido
        creaIndice(indexUrl, contenido);

        //Ruta para eliminar el indice completo
        String deleteUrl = "http://localhost:9200/maraton";

        //Eliminar el indice completo
        //eliminaIndice(deleteUrl, contenido);
        
    }

    private static JSONArray leerDatos(String path) {
        JSONParser parser = new JSONParser();
        try {
            return (JSONArray) parser.parse(new FileReader(path));
        } catch (IOException | org.json.simple.parser.ParseException e) {
            System.err.println("Error en la lectura de datos: " + e);
        }
        return null;
    }

    private static void creaIndice(String indexUrl, String contenido) {
        try {
            //Se crea la url para indearlo
            URL url = new URL(indexUrl + "_bulk");
            
            //Se conecta via http
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
            StringBuilder response = new StringBuilder(); 
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
            //Se crea la url para eliminar el indice
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

