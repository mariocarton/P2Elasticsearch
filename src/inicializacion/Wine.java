/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inicializacion;

import java.io.FileReader;
import java.io.IOException;
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
    
    public static void main(String[] args) {
        JSONArray wine = leerDatos(path+file);
         System.out.println("aqui");
        System.out.println(wine.toJSONString());
    }

    private static JSONArray leerDatos(String path) {
        JSONParser parser = new JSONParser();
        try {
            return (JSONArray)  parser.parse(new FileReader(path));           
        } catch (IOException | ParseException e) {
            System.err.println("Error en la lectura de datos: "+e);
        }
        return null;
    }
}
