
package gestion;

import hibernate.HibernateUtil;
import hibernate.Keep;
import hibernate.Usuario;
import java.math.BigInteger;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.json.JSONArray;
import org.json.JSONObject;

public class GestorKeep {
    
    public static JSONObject addKeep(Keep k, String usuario){
        Configuration configuration = new Configuration().configure();
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().
                applySettings(configuration.getProperties());
        
        SessionFactory factory = configuration.buildSessionFactory(builder.build());
        Session sesion = factory.openSession();
        sesion.beginTransaction();
        
        Usuario u = (Usuario)sesion.get(Usuario.class, usuario);
        k.setUsuario(u);
        sesion.save(k);
        
        Long id = ((BigInteger) sesion.createSQLQuery
            ("select last_insert_id()").uniqueResult())
            .longValue();       
        
        sesion.getTransaction().commit();
        sesion.flush();
        sesion.close();
        
        JSONObject obj = new JSONObject();
        obj.put("r", id);
        return obj;
    }
    
    public static JSONObject getKeeps(String usuario){
        Configuration configuration = new Configuration().configure();
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().
                applySettings(configuration.getProperties());
        
        SessionFactory factory = configuration.buildSessionFactory(builder.build());
        Session sesion = factory.openSession();
        sesion.beginTransaction();
        
        String hql = "from Keep where login = :login";
        Query q = sesion.createQuery(hql);
        q.setString("login", usuario);
        List<Keep> keeps = q.list();
        
        sesion.getTransaction().commit();
        sesion.flush();
        sesion.close();
        //{"r": true}
        //{"r": false}
        
        JSONArray array= new JSONArray();
        for(Keep k: keeps){
            JSONObject obj = new JSONObject();
            obj.put("ida", k.getIdAndroid());
            obj.put("cont", k.getContenido());
            obj.put("est", k.getEstado());
            array.put(obj);
        }
        
        JSONObject obj2 = new JSONObject();
        obj2.put("r", array);
        return obj2;
    }
    
    public static void addKeep(Keep k){
        Configuration configuration = new Configuration().configure();
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().
                applySettings(configuration.getProperties());
        SessionFactory factory = configuration.buildSessionFactory(builder.build());
        Session sesion = factory.openSession();
        sesion.beginTransaction();
        sesion.save(k);
        sesion.getTransaction().commit();
        sesion.flush();
        sesion.close();
    }
    
    public static JSONObject removeKeep(Keep k, String usuario){
        Configuration configuration = new Configuration().configure();
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().
                applySettings(configuration.getProperties());
        
        SessionFactory factory = configuration.buildSessionFactory(builder.build());
        Session sesion = factory.openSession();
        sesion.beginTransaction();
        
        Usuario u = (Usuario)sesion.get(Usuario.class, usuario);
        k.setUsuario(u);
        String hql = "delete from Keep where login = :login and idAndroid= :idan";
        Query q = sesion.createQuery(hql);
        q.setString("login", usuario);
        q.setInteger("idan", k.getIdAndroid());
        q.executeUpdate();
        /*
        sesion.delete(k);      
        sesion.
*/
        sesion.getTransaction().commit();
        sesion.flush();
        sesion.close();
        return new JSONObject();
    }
    
    public static List<Keep> listKeeps(String usuario){
        Configuration configuration = new Configuration().configure();
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().
                applySettings(configuration.getProperties());
        
        SessionFactory factory = configuration.buildSessionFactory(builder.build());
        Session sesion = factory.openSession();
        sesion.beginTransaction();
        
        String hql = "from Keep where login = :login";
        Query q = sesion.createQuery(hql);
        q.setString("login", usuario);
        List<Keep> keeps = q.list();
        
        sesion.getTransaction().commit();
        sesion.flush();
        sesion.close();
        
        return keeps;
    }
    
    public static JSONObject removeKeepWeb(int id){
        Configuration configuration = new Configuration().configure();
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().
                applySettings(configuration.getProperties());
        
        SessionFactory factory = configuration.buildSessionFactory(builder.build());
        Session sesion = factory.openSession();
        sesion.beginTransaction();
        
        String hql = "delete from Keep where id= :idan";
        Query q = sesion.createQuery(hql);
        q.setInteger("idan", id);
        q.executeUpdate();
        /*
        sesion.delete(k);      
        sesion.
*/
        sesion.getTransaction().commit();
        sesion.flush();
        sesion.close();
        return new JSONObject();
    }
    
    /** 
     * WEB*/
    public static void updateKeep(int id, String cont){
         Configuration configuration = new Configuration().configure();
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().
                applySettings(configuration.getProperties());
        
        SessionFactory factory = configuration.buildSessionFactory(builder.build());
        Session sesion = factory.openSession();
        sesion.beginTransaction();
        
        Keep k= (Keep) sesion.get(Keep.class, id);
        k.setContenido(cont);
        
        sesion.getTransaction().commit();
        sesion.flush();
        sesion.close();
    }
    
    /**
     *Android*/
    public static JSONObject updateKeep(Keep newk,String u){                
        Configuration configuration = new Configuration().configure();
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().
                applySettings(configuration.getProperties());
        
        SessionFactory factory = configuration.buildSessionFactory(builder.build());
        Session sesion = factory.openSession();
        sesion.beginTransaction();
        
        Usuario usu = (Usuario)sesion.get(Usuario.class, u);
        newk.setUsuario(usu);
        
        String hql = "Select k from Keep k where login = :login and idAndroid= :idan";
        Query q = sesion.createQuery(hql);
        q.setString("login", u);
        q.setInteger("idan", newk.getIdAndroid());
        Keep id=(Keep) q.uniqueResult();
        
        Keep k= (Keep) sesion.get(Keep.class, id.getId());
        k.setContenido(newk.getContenido());
        k.setEstado(newk.getEstado());
        
        sesion.getTransaction().commit();
        sesion.flush();
        sesion.close();
        return new JSONObject();
    }
}
