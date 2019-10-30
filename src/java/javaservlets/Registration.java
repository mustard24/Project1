/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaservlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author Dustin Moody
 */
public class Registration extends HttpServlet {
    public String getResultSetTable(ResultSet resultset) throws ServletException, IOException {
        
        ResultSetMetaData metadata = null;
        
        String table = "";
        String tableheading;
        String tablerow;
        
        String key;
        String value;
        
        try {
            
            System.out.println("*** Getting Query Results ... ");

            metadata = resultset.getMetaData();

            int numberOfColumns = metadata.getColumnCount();
            
            table += "<table border=\"1\">";
            tableheading = "<tr>";
            
            System.out.println("*** Number of Columns: " + numberOfColumns);
            
            for (int i = 1; i <= numberOfColumns; i++) {
            
                key = metadata.getColumnLabel(i);
                
                tableheading += "<th>" + key + "</th>";
            
            }
            
            tableheading += "</tr>";
            
            table += tableheading;
                        
            while(resultset.next()) {
                
                tablerow = "<tr>";
                
                for (int i = 1; i <= numberOfColumns; i++) {

                    value = resultset.getString(i);

                    if (resultset.wasNull()) {
                        tablerow += "<td></td>";
                    }

                    else {
                        tablerow += "<td>" + value + "</td>";
                    }
                    
                }
                
                tablerow += "</tr>";
                
                table += tablerow;
                
            }
            
            table += "</table><br />";

        }
        
        catch (Exception e) {}
        
        return table;
        
    }
    // End getResultSetTable()
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */

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
        response.setContentType("text/html");
        System.err.println("*** " + request.getParameter("session"));
        int getSession = Integer.parseInt(request.getParameter("session"));
        PrintWriter out = response.getWriter();
        String server = "jdbc:mysql://localhost:3306/registration_db";
        //String username ="jdbc/db_pool";
        //String password = "CS425!Lab3B";
        PreparedStatement pstatement = null;
        String username = "root";
        String password = "CS310";
        Boolean hasresults;
        ResultSet resultset = null;
        String table = "";
        String query = "Select * FROM registrations WHERE sessionid = ?"; 
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance(); 
            Connection conn = DriverManager.getConnection(server, username, password);
            pstatement = conn.prepareStatement(query);
            pstatement.setInt(1, getSession);
            
            hasresults = pstatement.execute();
            
            while ( hasresults || pstatement.getUpdateCount() != -1 ) {
                
                if ( hasresults ) {
                    resultset = pstatement.getResultSet();
                    table += getResultSetTable(resultset);
                }
                
                else {
                    
                    if ( pstatement.getUpdateCount() == -1 ) {
                        break;
                    }
                    
                }

                hasresults = pstatement.getMoreResults();
            
            }

            out.println(table);
            //out.println("<p>Search Parameter: " + "</p>" + table);
        }
        
        catch (Exception e) {
            System.out.println(e.toString());
        }
        
        finally {
            
            out.close();
            
            if (resultset != null) { try { resultset.close(); resultset = null; } catch (Exception e) {} }
            
            if (pstatement != null) { try { pstatement.close(); pstatement = null; } catch (Exception e) {} }
            
            
            
        }
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
        response.setContentType("application/json");
        String server = "jdbc:mysql://localhost:3306/registration_db";
        //String username ="jdbc/db_pool";
        //String password = "CS425!Lab3B";
        String firstName = request.getParameter("first");
        String sessionIDString = request.getParameter("session");
        String lastName = request.getParameter("last");
        String displayName = request.getParameter("display");
        
        
        int sessionID = Integer.parseInt(sessionIDString);
        
        PreparedStatement pstatement = null;
        PreparedStatement inputStatement = null;
        String username = "root";
        String password = "CS310";
        String countQuery = "SELECT COUNT(*) FROM registrations";
        /*
        String inputQuery = "INSERT INTO registrations (id,firstname,lastname,displayname,sessionid)\n" +
        "VALUES (?,\'?\',\'?\',\'?\',?)";
        */
        String inputQuery = "INSERT INTO registrations (id,firstname,lastname,displayname,sessionid)\n" +
        "VALUES (?,?,?,?,?)";
        boolean hasresults;
        ResultSet resultset = null;
        Integer nextID;
        JSONObject json = new JSONObject();
        String results;
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance(); 
            Connection conn = DriverManager.getConnection(server, username, password);
            pstatement = conn.prepareStatement(countQuery);
            hasresults = pstatement.execute();
            resultset = pstatement.getResultSet();
            resultset.next();
            nextID = resultset.getInt("COUNT(*)");
            nextID++;
            pstatement.close();
            System.out.println("Made it to the preparation of the query for registration");
            inputStatement = conn.prepareStatement(inputQuery);
            inputStatement.setInt(1, nextID);
            inputStatement.setString(2, firstName);
            inputStatement.setString(3, lastName);
            inputStatement.setString(4, displayName);
            inputStatement.setInt(5, sessionID);
            inputStatement.execute();
            System.out.println("Completed SQL work");
            int length = String.valueOf(nextID).length();
            int lengthOfZeroes = 6 - length;
            String registrationID = "R";
            for(int i = 0; i < lengthOfZeroes; i++){
                registrationID = registrationID + "0";
            }
            registrationID = registrationID + nextID.toString();
            json.put("name", displayName);
            json.put("registration", registrationID);
            PrintWriter out = response.getWriter();
            
            out.println(JSONValue.toJSONString(json).trim());
            
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
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

}
