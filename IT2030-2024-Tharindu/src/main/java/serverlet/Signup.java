
package serverlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Servlet implementation class Signup
 */
@WebServlet("/Signup")
public class Signup extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/edoc";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Signup() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        response.getWriter().append("Served at: ").append(request.getContextPath());
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieve form parameters
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        // Validate passwords
        if (password == null || !password.equals(confirmPassword)) {
            response.sendRedirect("signup.html?error=password_mismatch");
            return;
        }

        // Insert user into database
        if (registerUser(firstName, lastName, email, phoneNumber, password)) {
            response.sendRedirect("login.html?success=registration_successful");
        } else {
            response.sendRedirect("signup.html?error=registration_failed");
        }
    }

    // Method to register user
    private boolean registerUser(String firstName, String lastName, String email, String phoneNumber, String password) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            // Load JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Prepare SQL statement
            String sql = "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, 'patient')";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, firstName + " " + lastName); // Assuming username is full name
            preparedStatement.setString(2, password); // In a real application, hash the password
            preparedStatement.setString(3, email);

            // Execute update
            int rows = preparedStatement.executeUpdate();

            // Check if insert was successful
            return rows > 0;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("JDBC driver not found: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database connection error: " + e.getMessage());
        } finally {
            // Close resources
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Failed to close database connection: " + e.getMessage());
            }
        }
        return false;
    }
}