//MUKAMA UYISENGA LEA-223018803_IndividualProject
package com.Panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.util.DB;


public class StudentPanel extends JPanel implements ActionListener {
	// Form fields
	private JTextField idTxt, fnameTxt, lnameTxt, bdateTxt, createdAtTxt;
	private JComboBox<String> sexCmb;

	// Buttons
	private JButton addBtn, updateBtn, deleteBtn, loadBtn;

	// Table
	private JTable table;
	private DefaultTableModel model;

	/**
	 * Constructor - Initialize panel
	 */
	public StudentPanel() {
		setLayout(null);

		// Initialize fields
		idTxt = new JTextField();
		fnameTxt = new JTextField();
		lnameTxt = new JTextField();
		bdateTxt = new JTextField();
		createdAtTxt = new JTextField();

		// Sex dropdown
		sexCmb = new JComboBox<>(new String[]{"M", "F"});

		// Initialize buttons
		addBtn = new JButton("Add");
		updateBtn = new JButton("Update");
		deleteBtn = new JButton("Delete");
		loadBtn = new JButton("Load");

		// Table setup
		String[] labels = {"Student ID", "First Name", "Last Name", "Sex", "Birth Date", "Created At"};
		model = new DefaultTableModel(labels, 0);
		table = new JTable(model);
		JScrollPane sp = new JScrollPane(table);
		sp.setBounds(20, 250, 750, 200);
		add(sp);

		// Layout fields
		int y = 20;
		addField("Student ID", idTxt, y); y += 30;
		addField("First Name", fnameTxt, y); y += 30;
		addField("Last Name", lnameTxt, y); y += 30;
		addField("Sex", sexCmb, y); y += 30;
		addField("Birth Date (YYYY-MM-DD)", bdateTxt, y); y += 30;
		addField("Created At (YYYY-MM-DD)", createdAtTxt, y); y += 30;

		// Add buttons
		addButtons();

		// Table row click listener
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = table.getSelectedRow();
				if (row >= 0) {
					idTxt.setText(model.getValueAt(row, 0).toString());
					fnameTxt.setText(model.getValueAt(row, 1).toString());
					lnameTxt.setText(model.getValueAt(row, 2).toString());
					sexCmb.setSelectedItem(model.getValueAt(row, 3).toString());
					bdateTxt.setText(model.getValueAt(row, 4).toString());
					createdAtTxt.setText(model.getValueAt(row, 5).toString());
				}
			}
		});
	}

	/**
	 * Add label and field to panel
	 */
	private void addField(String lbl, JComponent txt, int y) {
		JLabel l = new JLabel(lbl);
		l.setBounds(20, y, 150, 25);
		txt.setBounds(180, y, 150, 25);
		add(l);
		add(txt);
	}

	/**
	 * Add and position buttons
	 */
	private void addButtons() {
		addBtn.setBounds(370, 20, 100, 30);
		updateBtn.setBounds(370, 60, 100, 30);
		deleteBtn.setBounds(370, 100, 100, 30);
		loadBtn.setBounds(370, 140, 100, 30);

		add(addBtn);
		add(updateBtn);
		add(deleteBtn);
		add(loadBtn);

		addBtn.addActionListener(this);
		updateBtn.addActionListener(this);
		deleteBtn.addActionListener(this);
		loadBtn.addActionListener(this);
	}

	/**
	 * Handle button click events
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		try (Connection con = DB.getConnection()) {
			if (e.getSource() == addBtn) {
				// Add new student
				String sql = "INSERT INTO student(First_name, last_name, sex, birthdate, createdAt) VALUES(?,?,?,?,?)";
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setString(1, fnameTxt.getText());
				ps.setString(2, lnameTxt.getText());
				ps.setString(3, sexCmb.getSelectedItem().toString());
				ps.setString(4, bdateTxt.getText());
				ps.setString(5, createdAtTxt.getText());
				ps.executeUpdate();
				JOptionPane.showMessageDialog(this, "Student added successfully!");
				clearFields();
				loadStudents(con);

			} else if (e.getSource() == updateBtn) {
				// Update existing student
				if (idTxt.getText().isEmpty()) {
					JOptionPane.showMessageDialog(this, "⚠ Please select a student to update!");
					return;
				}
				String sql = "UPDATE student SET First_name=?, last_name=?, sex=?, birthdate=?, createdAt=? WHERE studentID=?";
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setString(1, fnameTxt.getText());
				ps.setString(2, lnameTxt.getText());
				ps.setString(3, sexCmb.getSelectedItem().toString());
				ps.setString(4, bdateTxt.getText());
				ps.setString(5, createdAtTxt.getText());
				ps.setInt(6, Integer.parseInt(idTxt.getText()));
				ps.executeUpdate();
				JOptionPane.showMessageDialog(this, "Student updated successfully!");
				clearFields();
				loadStudents(con);

			} else if (e.getSource() == deleteBtn) {
				// Delete student
				if (idTxt.getText().isEmpty()) {
					JOptionPane.showMessageDialog(this, "⚠ Please select a student to delete!");
					return;
				}
				int confirm = JOptionPane.showConfirmDialog(this,
						"Are you sure you want to delete this student?",
						"Confirm Delete",
						JOptionPane.YES_NO_OPTION);
				if (confirm == JOptionPane.YES_OPTION) {
					String sql = "DELETE FROM student WHERE studentID=?";
					PreparedStatement ps = con.prepareStatement(sql);
					ps.setInt(1, Integer.parseInt(idTxt.getText()));
					ps.executeUpdate();
					JOptionPane.showMessageDialog(this, "Student deleted successfully!");
					clearFields();
					loadStudents(con);
				}

			} else if (e.getSource() == loadBtn) {
				// Load all students
				loadStudents(con);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
		}
	}

	/**
	 * Load students from database into table
	 */
	private void loadStudents(Connection con) throws SQLException {
		model.setRowCount(0);
		String sql = "SELECT * FROM student ORDER BY studentID";
		ResultSet rs = con.createStatement().executeQuery(sql);
		while (rs.next()) {
			model.addRow(new Object[]{
					rs.getInt("studentID"),
					rs.getString("First_name"),
					rs.getString("last_name"),
					rs.getString("sex"),
					rs.getDate("birthdate"),
					rs.getDate("createdAt")
			});
		}
	}

	/**
	 * Clear all input fields
	 */
	private void clearFields() {
		idTxt.setText("");
		fnameTxt.setText("");
		lnameTxt.setText("");
		sexCmb.setSelectedIndex(0);
		bdateTxt.setText("");
		createdAtTxt.setText("");
	}

	/**
	 * Main method for standalone testing
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame("Student Panel");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.add(new StudentPanel());
		frame.setVisible(true);
	}
}

