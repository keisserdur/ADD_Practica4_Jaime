<%@page import="hibernate.Usuario"%>
<%@page import="hibernate.Keep"%>
<%@page import="java.util.List"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    List<Keep> lista = (List<Keep>)request.getAttribute("listado");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Keep list</h1>
        <a href="addkeep.jsp?login=<%= request.getParameter("login") %>">Add new Keep</a>
        <table border="1">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Usuario</th>
                    <th>Contenido</th>
                    <th>Id Android</th>
                    <th>Estado</th>
                    <th>Editar</th>
                    <th>Borrar</th>
                </tr>
            </thead>
            <tbody>
                <%
                for(Keep p: lista){
                    %>
                    <tr>
                        <td><%= p.getId()%></td>
                        <td><%= p.getUserName()%></td>
                        <td><%= p.getContenido()%></td>
                        <td><%= p.getIdAndroid()%></td>
                        <td><%= p.getEstado()%></td>
                        <td><a href="editkeep.jsp?id=<%= p.getId() %>&login=<%= request.getParameter("login") %>">Editar</a></td>
                        <td><a href="go?tabla=keep&origen=web&op=delete&accion=&id=<%= p.getId() %>&login=<%= request.getParameter("login") %>" class="borrar">Borrar</a></td>
                    </tr>
                    <%
                }
                %>               
            </tbody>
        </table>
    </body>
</html>
