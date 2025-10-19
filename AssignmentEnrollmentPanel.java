package com.Form;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.util.DB;

public class AssignmentEnrollmentPanel extends JPanel implements ActionListener  {
	private JTextField idTxt, assignmentIdTxt, enrollmentIdTxt;
	private JButton addBtn, updateBtn, deleteBtn, loadBtn;
	private JTable table;
	private DefaultTableModel model;

	public AssignmentEnrollmentPanel() {
		setLayout(null);

		idTxt = new JTextField();
		assignmentIdTxt = new JTextField();
		enrollmentIdTxt = new JTextField();

		addBtn = new JButton("Add");
		updateBtn = new JButton("Update");
		deleteBtn = new JButton("Delete");
		loadBtn = new JButton("Load");

		String[] labels = {"Assignment Enrollment ID", "Assignment ID", "Enrollment ID"};
		model = new DefaultTableModel(labels, 0);
		table = new JTable(model);
		JScrollPane sp = new JScrollPane(table);
		sp.setBounds(20, 180, 750, 250);
		add(sp);

		int y = 20;
		addField("Assignment Enrollment ID", idTxt, y); y += 30;
		addField("Assignment ID", assignmentIdTxt, y); y += 30;
		addField("Enrollment ID", enrollmentIdTxt, y); y += 30;

		addButtons();

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = table.getSelectedRow();
				if (row >= 0) {
					idTxt.setText(model.getValueAt(row, 0).toString());
					assignmentIdTxt.setText(model.getValueAt(row, 1).toString());
					enrollmentIdTxt.setText(model.getValueAt(row, 2).toString());
				}
			}
		});
	}

	private void addField(String lbl, JComponent txt, int y) {
		JLabel l = new JLabel(lbl);
		l.setBounds(20, y, 180, 25);
		txt.setBounds(210, y, 150, 25);
		add(l);
		add(txt);
	}

	private void addButtons() {
		addBtn.setBounds(400, 20, 100, 30);
		updateBtn.setBounds(400, 60, 100, 30);
		deleteBtn.setBounds(400, 100, 100, 30);
		loadBtn.setBounds(400, 140, 100, 30);

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
				String sql = "INSERT INTO assignment_enrollment(AssignmentID, enrollmentID) VALUES(?,?)";
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setInt(1, Integer.parseInt(assignmentIdTxt.getText()));
				ps.setInt(2, Integer.parseInt(enrollmentIdTxt.getText()));
				ps.executeUpdate();
				JOptionPane.showMessageDialog(this, "Assignment Enrollment added successfully!");
				clearFields();
				loadAssignmentEnrollments(con);

			} else if (e.getSource() == updateBtn) {
				if (idTxt.getText().isEmpty()) {
					JOptionPane.showMessageDialog(this, "Please select a record to update!");
					return;
				}
				String sql = "UPDATE assignment_enrollment SET AssignmentID=?, enrollmentID=? WHERE assignment_enrollmentID=?";
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setInt(1, Integer.parseInt(assignmentIdTxt.getText()));
				ps.setInt(2, Integer.parseInt(enrollmentIdTxt.getText()));
				ps.setInt(3, Integer.parseInt(idTxt.getText()));
				ps.executeUpdate();
				JOptionPane.showMessageDialog(this, "Assignment Enrollment updated successfully!");
				clearFields();
				loadAssignmentEnrollments(con);

			} else if (e.getSource() == deleteBtn) {
				if (idTxt.getText().isEmpty()) {
					JOptionPane.showMessageDialog(this, "Please select a record to delete!");
					return;
				}
				int confirm = JOptionPane.showConfirmDialog(this,
						"Are you sure you want to delete this record?",
						"Confirm Delete",
						JOptionPane.YES_NO_OPTION);
				if (confirm == JOptionPane.YES_OPTION) {
					String sql = "DELETE FROM assignment_enrollment WHERE assignment_enrollmentID=?";
					PreparedStatement ps = con.prepareStatement(sql);
					ps.setInt(1, Integer.parseInt(idTxt.getText()));
					ps.executeUpdate();
					JOptionPane.showMessageDialog(this, "Assignment Enrollment deleted successfully!");
					clearFields();
					loadAssignmentEnrollments(con);
				}

			} else if (e.getSource() == loadBtn) {
				loadAssignmentEnrollments(con);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
		}
	}

	private void loadAssignmentEnrollments(Connection con) throws SQLException {
		model.setRowCount(0);
		String sql = "SELECT * FROM assignment_enrollment ORDER BY assignment_enrollmentID";
		ResultSet rs = con.createStatement().executeQuery(sql);
		while (rs.next()) {
			model.addRow(new Object[]{
					rs.getInt("assignment_enrollmentID"),
					rs.getInt("AssignmentID"),
					rs.getInt("enrollmentID")
			});
		}
	}

	private void clearFields() {
		idTxt.setText("");
		assignmentIdTxt.setText("");
		enrollmentIdTxt.setText("");
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Assignment Enrollment Panel");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 550);
		frame.add(new AssignmentEnrollmentPanel());
		frame.setVisible(true);
	}
}