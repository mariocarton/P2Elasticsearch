/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inicializacion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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
public class Norte {

    private static String path = "D:\\mario\\Dropbox\\Mate\\1\\2Cuatrimestre\\AWBG!\\P2\\Datos\\norte\\";

    public static void main(String[] args) {

        JSONArray norte = leerDatos(path);

        System.out.println("Creando las consultas...");
        ArrayList<String> a = new ArrayList();
        
        //Crea las consultas y las mete en una lista
        for (int i = 0; i < norte.size(); i++) {
            //Se extra el objeto 
            JSONObject n = (JSONObject) norte.get(i);
            a.add("{ \"index\": {\"_id\":" + i + " }}\n"
                    + n.toJSONString() + "\n");

        }
        
        System.out.println("Creando contenido...");
        //Ruta del indice 
        String indexUrl = "http://localhost:9200/norte/noticia/";
        //Cada mil elementos concatenados los introduce en el indice
        String contenido = "";
        for (int i = 0; i < a.size(); i++) {
            //Concataena
            contenido += a.get(i);
            //Introduce 1000            
            if (i % 1000 == 0) {
                
                System.out.println("Creando indice... Introducido: "+i+" de "+a.size());
                //Se crea el indice con el contenido
                creaIndice(indexUrl, contenido);
                contenido = "";
            }

        }
        //Introduce el resto
        System.out.println("Creando indice... Resto...");
        //Se crea el indice con el contenido
        creaIndice(indexUrl, contenido);

        //Ruta para eliminar el indice completo
        String deleteUrl = "http://localhost:9200/norte";

        //Eliminar el indice completo
        //eliminaIndice(deleteUrl, "");
    }

    private static JSONArray leerDatos(String path) {

        System.out.println("Leyendo los ficheros...");

        File dir = new File(path);

        JSONParser parser = new JSONParser();

        JSONArray todo = new JSONArray();

        for (File d : dir.listFiles()) {
            for (File json : d.listFiles()) {
                //System.out.println(json.getName());
                try {
                    JSONArray dia = (JSONArray) parser.parse(new FileReader(json.getAbsolutePath()));
                    for (int i = 0; i < dia.size(); i++) {
                        todo.add(dia.get(i));
                    }

                } catch (IOException | ParseException e) {
                    System.err.println("Error en la lectura de datos: " + e);
                }
            }
        }
        /* Imprime todos los json
        for (int i=0;i<todo.size();i++){
            JSONObject o = (JSONObject)todo.get(i);
            System.out.println(o.toJSONString());
        }
         */

        return todo;
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
            //System.out.println(response.toString());

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
