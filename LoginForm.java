package com.Form;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.util.DB;

public class LoginForm extends JFrame implements ActionListener{
	// Form components
	JTextField userTxt = new JTextField("Enter Username");
	JPasswordField passTxt = new JPasswordField("password");
	JButton loginbtn = new JButton("Login");
	JButton cancelbtn = new JButton("Cancel");

	/**
	 * Constructor - Initialize login form
	 */
	public LoginForm() {
		setTitle("EMMS - Login");
		setBounds(100, 100, 500, 400);
		setLayout(null);

		// Set component bounds
		userTxt.setBounds(50, 30, 120, 25);
		passTxt.setBounds(50, 70, 120, 25);
		loginbtn.setBounds(50, 120, 100, 25);
		cancelbtn.setBounds(170, 120, 100, 25);

		// Add components to frame
		add(userTxt);
		add(passTxt);
		add(loginbtn);
		add(cancelbtn);

		// Add action listeners
		loginbtn.addActionListener(this);
		cancelbtn.addActionListener(this);

		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	/**
	 * Handle button click events
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == loginbtn) {
			try (Connection con = DB.getConnection()) {
				// Query to check user credentials
				String sql = "SELECT * FROM users WHERE username=? AND passwordHash=?";
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setString(1, userTxt.getText());
				ps.setString(2, new String(passTxt.getPassword()));
				ResultSet rs = ps.executeQuery();

				if (rs.next()) {
					// Login successful
					String role = rs.getString("role");
					dispose(); // Close login form
					new EMMS(role, getDefaultCloseOperation()); // Open main form
				} else {
					// Login failed
					JOptionPane.showMessageDialog(this, "Invalid Login");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
			}
		} else if (e.getSource() == cancelbtn) {
			System.exit(0); // Exit application
		}
	}
}




