/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gestion;

import hibernate.HibernateUtil;
import hibernate.Usuario;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.json.JSONObject;

/**
 *
 * @author izv
 */
public class GestorUsuario {

    public static JSONObject getLogin(String login, String pass) {
        Configuration configuration = new Configuration().configure();
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().
                applySettings(configuration.getProperties());
        
        SessionFactory factory = configuration.buildSessionFactory(builder.build());
        Session sesion = factory.openSession();
        sesion.beginTransaction();
        
        String hql = "from Usuario where login = :login and pass = :pass";
        Query q = sesion.createQuery(hql);
        q.setString("login", login);
        q.setString("pass", pass);
        
        List<Usuario> usuarios = q.list();
        sesion.getTransaction().commit();
        
        sesion.flush();
        sesion.close();

        JSONObject obj = new JSONObject();
        if (usuarios.isEmpty()) {
            obj.put("r", false);
        } else {
            obj.put("r", true);
        }
        return obj;
    }
    
    public static Usuario getUserbyName(String login){
        Configuration configuration = new Configuration().configure();
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().
                applySettings(configuration.getProperties());
        
        SessionFactory factory = configuration.buildSessionFactory(builder.build());
        Session sesion = factory.openSession();
        sesion.beginTransaction();
        
        Usuario u= (Usuario) sesion.get(Usuario.class, login);
        
        sesion.getTransaction().commit();
        sesion.flush();
        sesion.close();
        return u;
    }
}
