package serverlet;

import java.io.*;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; // Import HttpSession class

import java.sql.*;


public class AppointmentServlet extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Extract form data
        String patientName = request.getParameter("name");
        String patientEmail = request.getParameter("email");
        String patientPhone = request.getParameter("phone");
        String department = request.getParameter("department");
        String doctorName = request.getParameter("doctor");
        String appointmentDate = request.getParameter("date");
        String message = request.getParameter("message");
        String status = message; // Set status to the value of the message input

        // Database connection parameters
        String url = "jdbc:mysql://localhost:3306/edoc";
        String user = "root";
        String password = "";

        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
            
            // Establish the database connection
            Connection conn = DriverManager.getConnection(url, user, password);
            
            // Create a SQL INSERT statement
            String sql = "INSERT INTO appointments (patient_id, patientEmail, patientPhone, department, DoctorsName, appointment_date, message, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            // Prepare the statement
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, patientName); // Assuming patient_name is stored in patient_id field
            statement.setString(2, patientEmail);
            statement.setString(3, patientPhone);
            statement.setString(4, department);
            statement.setString(5, doctorName);
            statement.setString(6, appointmentDate);
            statement.setString(7, message);
            statement.setString(8, status);
            
            // Execute the statement
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                // Store a success message in session
                HttpSession session = request.getSession();
                session.setAttribute("successMessage", "Appointment booked successfully!");
                
                System.out.println("A new appointment was inserted successfully!");
            }
            
            // Close the connection
            conn.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        
        // Forward the request to a confirmation page
        RequestDispatcher dispatcher = request.getRequestDispatcher("confirmation.jsp");
        dispatcher.forward(request, response);
    }
}
