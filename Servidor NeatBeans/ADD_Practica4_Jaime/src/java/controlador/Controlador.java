/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import gestion.GestorKeep;
import gestion.GestorUsuario;
import hibernate.Keep;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

/**
 *
 * @author izv
 */
@WebServlet(name = "Controlador", urlPatterns = {"/go"})
public class Controlador extends HttpServlet {

    enum Camino {
        forward, redirect, print;
    }

    class Destino {

        public Camino camino;
        public String url;
        public String texto;

        public Destino() {
        }

        public Destino(Camino camino, String url, String texto) {
            this.camino = camino;
            this.url = url;
            this.texto = texto;
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String tabla = request.getParameter("tabla");//persona, idioma, etc.
        String op = request.getParameter("op");//login, create, read, update, delete
        String accion = request.getParameter("accion");//view, do
        String origen = request.getParameter("origen");//android, web
        
        Destino destino = handle(request, response, tabla, op, accion, origen);
        
        if (destino == null) {
            destino = new Destino(Camino.forward, "/WEB-INF/index.jsp", "");
        }
        if (destino.camino == Camino.forward) {
            request.getServletContext().
                    getRequestDispatcher(destino.url).forward(request, response);
        } else if (destino.camino == Camino.redirect) {
            response.sendRedirect(destino.url);
        } else {
            response.setContentType("text/html;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                out.println(destino.texto);
            }
        }
    }

    private Destino handle(HttpServletRequest request, HttpServletResponse response,
            String tabla, String op, String accion, String origen) {
        if (origen == null) {
            origen = "";
        }
        if (tabla == null || op == null) {
            tabla = "usuario";
            op = "read";            
        }
        switch (tabla) {
            case "usuario":
                return handleUsuario(request, response, op, origen);
            case "keep":
                return handleKeep(request, response, op, origen);
            default:
        }
        return null;
    }

    private Destino handleUsuario(HttpServletRequest request, HttpServletResponse response,
            String op, String origen) {

        switch (op) {
            case "login"://autentifica el login
                if (origen.equals("android")) {
                    //Devuelve un json true:bien logueado, false:no existe o mal logueado
                    JSONObject obj = GestorUsuario.getLogin(request.getParameter("login"),
                                                            request.getParameter("pass"));
                    return new Destino(Camino.print, "", obj.toString());
                    //http://192.168.208.208:8080/Keep/go?tabla=usuario&op=login&login=pepe&pass=pepe&origen=android&accion=
                }
                if (origen.equals("web")) {
                    //Devuelve un json true:bien logueado, false:no existe o mal logueado
                    JSONObject obj = GestorUsuario.getLogin(request.getParameter("login"),
                                                            request.getParameter("pass"));
                    if (obj.getBoolean("r")) {
                        //devuelve la lista de keep del usuario
                        List<Keep> keeps = GestorKeep.listKeeps(request.getParameter("login"));
                        //guardas la lista de notas en un atributo
                        request.setAttribute("listado", keeps);
                        //guarda al usuario del login
                        request.setAttribute("login", GestorUsuario.getUserbyName(request.getParameter("login")));
                        return new Destino(Camino.forward, "/WEB-INF/viewkeeps.jsp", keeps.toString());
                    } else {
                        return new Destino(Camino.print, "index.html", obj.toString());
                    }
                }

        }
        return null;
    }

    private Destino handleKeep(HttpServletRequest request, HttpServletResponse response,
            String op, String origen) {

        switch (op) {
            case "create":
                if (origen.equals("android")) {
                    //crea la nota como estable y con id de android
                    Keep k = new Keep(null, Integer.parseInt(request.getParameter("idAndroid")),
                            request.getParameter("contenido"), null, "estable");
                    //inserta la nota del usuario, y devuelve un json con el id
                    JSONObject obj = GestorKeep.addKeep(k,request.getParameter("login"));
                    // GestorKeep.addKeep(k);
                    return new Destino(Camino.print, "", obj.toString());
                }
                if (origen.equals("web")) {
                    //lista de notas del usuario
                    List<Keep> keeps = GestorKeep.listKeeps(request.getParameter("login"));
                    int max = -1;
                    max++;
                    //busca el id mas grande
                    for (Keep k : keeps) {
                        if (k.getIdAndroid() >= max) {
                            max = k.getIdAndroid() + 1;
                        }
                    }
                    //creamos la nota(idAndroid=max???)
                    Keep k = new Keep(null, max, request.getParameter("contenido"), null, "estable");
                    //Añadimos la nota al usuario
                    JSONObject obj = GestorKeep.addKeep(k, request.getParameter("login"));
                    //Volvemos a cargar la lista??? Mas facil hacer un add a la lista y mas eficiente
                    keeps = GestorKeep.listKeeps(request.getParameter("login"));
                    request.setAttribute("listado", keeps);
                    return new Destino(Camino.forward, "/WEB-INF/viewkeeps.jsp", keeps.toString());

                }
            case "read":
                if (origen.equals("android")) {
                    //json de la lista de keep
                    JSONObject obj = GestorKeep.getKeeps(request.getParameter("login"));
                    return new Destino(Camino.print, "", obj.toString());
                }
            case "delete":
                if (origen.equals("android")) {
                    //keep que llega desde android,junto con el usuario
                    Keep k = new Keep(null, Integer.parseInt(request.getParameter("idAndroid")),
                            request.getParameter("contenido"), null, "estable");
                    //keep borrado del usuario,json vacio
                    JSONObject obj = GestorKeep.removeKeep(k, request.getParameter("login"));
                    return new Destino(Camino.print, "", obj.toString());
                }
                if (origen.equals("web")){
                    //borra el keep por el identificador
                    GestorKeep.removeKeepWeb(Integer.parseInt(request.getParameter("id")));
                    //lista de keep del usuario????
                    List<Keep> keeps = GestorKeep.listKeeps(request.getParameter("login"));
                    request.setAttribute("listado", keeps);
                    return new Destino(Camino.forward, "/WEB-INF/viewkeeps.jsp", keeps.toString());
                }
            case "update":                
                if(origen.equals("android")){
                    //keep que llega desde android,junto con el usuario
                    Keep k = new Keep(null, Integer.parseInt(request.getParameter("idAndroid")),
                            request.getParameter("contenido"), null, "estable");
                    //keep borrado del usuario,json vacio
                    JSONObject obj = GestorKeep.updateKeep(k,request.getParameter("login"));
                    return new Destino(Camino.print, "", obj.toString());
                }
                if(origen.equals("web")){
                    //actualizamos el keep id, con nuevo contenido
                    GestorKeep.updateKeep(Integer.parseInt(request.getParameter("id")), request.getParameter("contenido"));
                    //lista de keep
                    List<Keep> keeps = GestorKeep.listKeeps(request.getParameter("login"));
                    request.setAttribute("listado", keeps);
                    return new Destino(Camino.forward, "/WEB-INF/viewkeeps.jsp", keeps.toString());
                }
        }

        return null;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    /*
        1 tabla :  keep, usuario
        2 op: login, create, read, update, delete
        3 accion: view, do
        4 origen: android, web
    
    
    
    
     */
}
