package com.example.admin.add_practica4_jaime.gestion;

import com.example.admin.add_practica4_jaime.pojo.Keep;
import com.example.admin.add_practica4_jaime.pojo.Usuario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 01/03/2016.
 */
public class GestorHibernate {


    private String urlOrigen = "http://192.168.1.36:28914/ADD_Practica4_Jaime/go";


    public GestorHibernate() {
    }

    /*************************************************************/

    public List<Keep> getKeeps(Usuario u) {
        List<Keep> keeps = new ArrayList<>();
        URL url = null;
        BufferedReader in = null;
        String res = "";
        String login;
        try {
            login = URLEncoder.encode(u.getEmail(), "UTF-8");
            String destino = urlOrigen + "?tabla=keep&op=read&login=" + login + "&origen=android";

            url = new URL(destino);
            in = new BufferedReader(new InputStreamReader(url.openStream()));
            String linea;

            while ((linea = in.readLine()) != null) {
                res += linea;
            }
            in.close();

            JSONObject obj = new JSONObject(res);
            JSONArray array = (JSONArray) obj.get("r");

            for (int i = 0; i < array.length(); i++) {
                JSONObject o = (JSONObject) array.get(i);

                Keep keep = new Keep(o.getInt("ida"), o.getString("cont"), true);
                keeps.add(keep);
            }
            return keeps;
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        } catch (JSONException e) {
        }
        return null;
    }

    public long getId(List<Keep> l) {
        long next = -1;
        for (Keep k : l) {
            if (k.getId() > next) {
                next = k.getId();
            }
        }
        return next+1;
    }

    public void create (Keep k,Usuario u){
        URL url = null;
        BufferedReader in = null;
        String res = "";
        String login;
        try {
            login = URLEncoder.encode(u.getEmail(), "UTF-8");
            String destinor = urlOrigen + "?tabla=keep&op=create&origen=android&login=" + login + "&idAndroid=" + k.getId() + "&contenido=" + k.getContenido();
            url = new URL(destinor);
            in = new BufferedReader(new InputStreamReader(url.openStream()));

        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
    }


    public void update(Keep k, Usuario u) {
        URL url = null;
        BufferedReader in = null;
        String res = "";
        String login;
        try {
            login = URLEncoder.encode(u.getEmail(), "UTF-8");
            String destinor = urlOrigen + "?tabla=keep&op=update&origen=android&login=" + login + "&idAndroid=" + k.getId() + "&contenido=" + k.getContenido();
            url = new URL(destinor);
            in = new BufferedReader(new InputStreamReader(url.openStream()));

        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }

    }

    public void delete(Keep k, Usuario u){
        URL url = null;
        BufferedReader in = null;
        String res = "";
        String login;
        try {
            login = URLEncoder.encode(u.getEmail(), "UTF-8");
            String destinor = urlOrigen + "?tabla=keep&op=delete&origen=android&login=" + login + "&idAndroid=" + k.getId() + "&contenido=" + k.getContenido();
            url = new URL(destinor);
            in = new BufferedReader(new InputStreamReader(url.openStream()));

        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }

    }

    /**************************************************************/
    public boolean validar(Usuario u){
        URL url = null;
        BufferedReader in = null;
        String res = "";
        String login;
        String pass;
        try {
            login = URLEncoder.encode(u.getEmail(), "UTF-8");
            pass = URLEncoder.encode(u.getPass(), "UTF-8");

            String destino = urlOrigen +"?tabla=usuario&op=login&origen=android&login="+login+"&pass="+pass;
            url = new URL(destino);
            in = new BufferedReader(new InputStreamReader(url.openStream()));
            String linea;

            while ((linea = in.readLine()) != null) {
                res += linea;
            }

            in.close();
            JSONObject obj = new JSONObject(res);

            return obj.getBoolean("r");
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }catch (JSONException e){
        }
        return false;
    }
}
