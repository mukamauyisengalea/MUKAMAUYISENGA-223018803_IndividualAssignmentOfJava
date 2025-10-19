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

public class InstructorPanel  extends JPanel implements ActionListener  {

	private JTextField idTxt, nameTxt, identifierTxt, locationTxt, contactTxt, assignedSinceTxt, courseIdTxt;
	private JComboBox<String> statusCmb;
	private JButton addBtn, updateBtn, deleteBtn, loadBtn;
	private JTable table;
	private DefaultTableModel model;

	public InstructorPanel() {
		setLayout(null);

		idTxt = new JTextField();
		nameTxt = new JTextField();
		identifierTxt = new JTextField();
		locationTxt = new JTextField();
		contactTxt = new JTextField();
		assignedSinceTxt = new JTextField();
		courseIdTxt = new JTextField();

		statusCmb = new JComboBox<>(new String[]{"Active", "On Leave", "Retired"});

		addBtn = new JButton("Add");
		updateBtn = new JButton("Update");
		deleteBtn = new JButton("Delete");
		loadBtn = new JButton("Load");

		String[] labels = {"Instructor ID", "Name", "Identifier", "Status", "Location", "Contact", "Assigned Since", "Course ID"};
		model = new DefaultTableModel(labels, 0);
		table = new JTable(model);
		JScrollPane sp = new JScrollPane(table);
		sp.setBounds(20, 280, 850, 200);
		add(sp);

		int y = 20;
		addField("Instructor ID", idTxt, y); y += 30;
		addField("Name", nameTxt, y); y += 30;
		addField("Identifier", identifierTxt, y); y += 30;
		addField("Status", statusCmb, y); y += 30;
		addField("Location", locationTxt, y); y += 30;
		addField("Contact", contactTxt, y); y += 30;
		addField("Assigned Since (YYYY-MM-DD)", assignedSinceTxt, y); y += 30;
		addField("Course ID", courseIdTxt, y); y += 30;

		addButtons();

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = table.getSelectedRow();
				if (row >= 0) {
					idTxt.setText(model.getValueAt(row, 0).toString());
					nameTxt.setText(model.getValueAt(row, 1).toString());
					identifierTxt.setText(model.getValueAt(row, 2).toString());
					statusCmb.setSelectedItem(model.getValueAt(row, 3).toString());
					locationTxt.setText(model.getValueAt(row, 4).toString());
					contactTxt.setText(model.getValueAt(row, 5).toString());
					assignedSinceTxt.setText(model.getValueAt(row, 6).toString());
					courseIdTxt.setText(model.getValueAt(row, 7).toString());
				}
			}
		});
	}

	private void addField(String lbl, JComponent txt, int y) {
		JLabel l = new JLabel(lbl);
		l.setBounds(20, y, 160, 25);
		txt.setBounds(190, y, 150, 25);
		add(l);
		add(txt);
	}

	private void addButtons() {
		addBtn.setBounds(380, 20, 100, 30);
		updateBtn.setBounds(380, 60, 100, 30);
		deleteBtn.setBounds(380, 100, 100, 30);
		loadBtn.setBounds(380, 140, 100, 30);

		add(addBtn);
		add(updateBtn);
		add(deleteBtn);
		add(loadBtn);

		addBtn.addActionListener(this);
		updateBtn.addActionListener(this);
		deleteBtn.addActionListener(this);
		loadBtn.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try (Connection con = DB.getConnection()) {
			if (e.getSource() == addBtn) {
				String sql = "INSERT INTO instructor(name, identifier, status, location, contact, assignedSince, courseID) VALUES(?,?,?,?,?,?,?)";
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setString(1, nameTxt.getText());
				ps.setString(2, identifierTxt.getText());
				ps.setString(3, statusCmb.getSelectedItem().toString());
				ps.setString(4, locationTxt.getText());
				ps.setInt(5, Integer.parseInt(contactTxt.getText()));
				ps.setString(6, assignedSinceTxt.getText());
				ps.setInt(7, Integer.parseInt(courseIdTxt.getText()));
				ps.executeUpdate();
				JOptionPane.showMessageDialog(this, "Instructor added successfully!");
				clearFields();
				loadInstructors(con);

			} else if (e.getSource() == updateBtn) {
				if (idTxt.getText().isEmpty()) {
					JOptionPane.showMessageDialog(this, "⚠ Please select an instructor to update!");
					return;
				}
				String sql = "UPDATE instructor SET name=?, identifier=?, status=?, location=?, contact=?, assignedSince=?, courseID=? WHERE instructorID=?";
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setString(1, nameTxt.getText());
				ps.setString(2, identifierTxt.getText());
				ps.setString(3, statusCmb.getSelectedItem().toString());
				ps.setString(4, locationTxt.getText());
				ps.setInt(5, Integer.parseInt(contactTxt.getText()));
				ps.setString(6, assignedSinceTxt.getText());
				ps.setInt(7, Integer.parseInt(courseIdTxt.getText()));
				ps.setInt(8, Integer.parseInt(idTxt.getText()));
				ps.executeUpdate();
				JOptionPane.showMessageDialog(this, "Instructor updated successfully!");
				clearFields();
				loadInstructors(con);

			} else if (e.getSource() == deleteBtn) {
				if (idTxt.getText().isEmpty()) {
					JOptionPane.showMessageDialog(this, "Please select an instructor to delete!");
					return;
				}
				int confirm = JOptionPane.showConfirmDialog(this,
						"Are you sure you want to delete this instructor?",
						"Confirm Delete",
						JOptionPane.YES_NO_OPTION);
				if (confirm == JOptionPane.YES_OPTION) {
					String sql = "DELETE FROM instructor WHERE instructorID=?";
					PreparedStatement ps = con.prepareStatement(sql);
					ps.setInt(1, Integer.parseInt(idTxt.getText()));
					ps.executeUpdate();
					JOptionPane.showMessageDialog(this, "Instructor deleted successfully!");
					clearFields();
					loadInstructors(con);
				}

			} else if (e.getSource() == loadBtn) {
				loadInstructors(con);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
		}
	}

	private void loadInstructors(Connection con) throws SQLException {
		model.setRowCount(0);
		String sql = "SELECT * FROM instructor ORDER BY instructorID";
		ResultSet rs = con.createStatement().executeQuery(sql);
		while (rs.next()) {
			model.addRow(new Object[]{
					rs.getInt("instructorID"),
					rs.getString("name"),
					rs.getString("identifier"),
					rs.getString("status"),
					rs.getString("location"),
					rs.getInt("contact"),
					rs.getDate("assignedSince"),
					rs.getInt("courseID")
			});
		}
	}

	private void clearFields() {
		idTxt.setText("");
		nameTxt.setText("");
		identifierTxt.setText("");
		statusCmb.setSelectedIndex(0);
		locationTxt.setText("");
		contactTxt.setText("");
		assignedSinceTxt.setText("");
		courseIdTxt.setText("");
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Instructor Panel");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(900, 600);
		frame.add(new InstructorPanel());
		frame.setVisible(true);
	}
}

