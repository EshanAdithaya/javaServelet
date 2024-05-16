
package serverlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet(name="LoginServlet",urlPatterns ="/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/edoc";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Check if the user is authenticated
        String role = authenticateUser(username, password);
        if (role != null) {
            if ("admin".equals(role)) {
                response.sendRedirect("admin/index.jsp");
            } else {
                response.sendRedirect("index.jsp");
            }
        } else {
            response.sendRedirect("login.jsp?error=authentication_failed");
        }
    }

    // Method to authenticate user and get role
    private String authenticateUser(String username, String password) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Load JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Prepare SQL statement
            String sql = "SELECT role FROM users WHERE username = ? AND password = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            // Execute query
            resultSet = preparedStatement.executeQuery();

            // Check if user exists and return role
            if (resultSet.next()) {
            	
            	 System.out.println("Database is connected");  
            	
                return resultSet.getString("role");
                         }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("JDBC driver not found: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database connection error: " + e.getMessage());
        } finally {
            // Close resources
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Failed to close database connection: " + e.getMessage());
            }
        }
        return null;
    }
}

