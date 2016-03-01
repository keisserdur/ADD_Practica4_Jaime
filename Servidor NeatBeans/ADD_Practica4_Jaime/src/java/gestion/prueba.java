package gestion;

import hibernate.Keep;

public class prueba {
    
    
    public static void main(String[] args) {
        Keep k = new Keep(null, 317, "contenido", null, "inestable");
        GestorKeep.addKeep(k, "pepe");
        
    }
    
    
}
