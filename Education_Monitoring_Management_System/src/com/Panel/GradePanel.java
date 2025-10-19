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

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.util.DB;

public class GradePanel extends JPanel implements ActionListener {
	private JTextField idTxt, assignmentIdTxt, studentIdTxt, scoreTxt, remarksTxt, createdAtTxt, courseIdTxt;
	private JButton addBtn, updateBtn, deleteBtn, loadBtn;
	private JTable table;
	private DefaultTableModel model;

	public GradePanel() {
		setLayout(null);

		idTxt = new JTextField();
		assignmentIdTxt = new JTextField();
		studentIdTxt = new JTextField();
		scoreTxt = new JTextField();
		remarksTxt = new JTextField();
		createdAtTxt = new JTextField();
		courseIdTxt = new JTextField();

		addBtn = new JButton("Add");
		updateBtn = new JButton("Update");
		deleteBtn = new JButton("Delete");
		loadBtn = new JButton("Load");

		String[] labels = {"Grade ID", "Assignment ID", "Student ID", "Score", "Remarks", "Created At", "Course ID"};
		model = new DefaultTableModel(labels, 0);
		table = new JTable(model);
		JScrollPane sp = new JScrollPane(table);
		sp.setBounds(20, 280, 850, 200);
		add(sp);

		int y = 20;
		addField("Grade ID", idTxt, y); y += 30;
		addField("Assignment ID", assignmentIdTxt, y); y += 30;
		addField("Student ID", studentIdTxt, y); y += 30;
		addField("Score", scoreTxt, y); y += 30;
		addField("Remarks", remarksTxt, y); y += 30;
		addField("Created At (YYYY-MM-DD)", createdAtTxt, y); y += 30;
		addField("Course ID", courseIdTxt, y); y += 30;

		addButtons();

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = table.getSelectedRow();
				if (row >= 0) {
					idTxt.setText(model.getValueAt(row, 0).toString());
					assignmentIdTxt.setText(model.getValueAt(row, 1).toString());
					studentIdTxt.setText(model.getValueAt(row, 2).toString());
					scoreTxt.setText(model.getValueAt(row, 3).toString());
					remarksTxt.setText(model.getValueAt(row, 4).toString());
					createdAtTxt.setText(model.getValueAt(row, 5).toString());
					courseIdTxt.setText(model.getValueAt(row, 6).toString());
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
				String sql = "INSERT INTO grade(AssignmentId, studentID, score, remarks, createdAt, courseID) VALUES(?,?,?,?,?,?)";
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setInt(1, Integer.parseInt(assignmentIdTxt.getText()));
				ps.setInt(2, Integer.parseInt(studentIdTxt.getText()));
				ps.setFloat(3, Float.parseFloat(scoreTxt.getText()));
				ps.setString(4, remarksTxt.getText());
				ps.setString(5, createdAtTxt.getText());
				ps.setInt(6, Integer.parseInt(courseIdTxt.getText()));
				ps.executeUpdate();
				JOptionPane.showMessageDialog(this, "Grade added successfully!");
				clearFields();
				loadGrades(con);

			} else if (e.getSource() == updateBtn) {
				if (idTxt.getText().isEmpty()) {
					JOptionPane.showMessageDialog(this, "Please select a grade to update!");
					return;
				}
				String sql = "UPDATE grade SET AssignmentId=?, studentID=?, score=?, remarks=?, createdAt=?, courseID=? WHERE gradeID=?";
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setInt(1, Integer.parseInt(assignmentIdTxt.getText()));
				ps.setInt(2, Integer.parseInt(studentIdTxt.getText()));
				ps.setFloat(3, Float.parseFloat(scoreTxt.getText()));
				ps.setString(4, remarksTxt.getText());
				ps.setString(5, createdAtTxt.getText());
				ps.setInt(6, Integer.parseInt(courseIdTxt.getText()));
				ps.setInt(7, Integer.parseInt(idTxt.getText()));
				ps.executeUpdate();
				JOptionPane.showMessageDialog(this, "Grade updated successfully!");
				clearFields();
				loadGrades(con);

			} else if (e.getSource() == deleteBtn) {
				if (idTxt.getText().isEmpty()) {
					JOptionPane.showMessageDialog(this, "Please select a grade to delete!");
					return;
				}
				int confirm = JOptionPane.showConfirmDialog(this,
						"Are you sure you want to delete this grade?",
						"Confirm Delete",
						JOptionPane.YES_NO_OPTION);
				if (confirm == JOptionPane.YES_OPTION) {
					String sql = "DELETE FROM grade WHERE gradeID=?";
					PreparedStatement ps = con.prepareStatement(sql);
					ps.setInt(1, Integer.parseInt(idTxt.getText()));
					ps.executeUpdate();
					JOptionPane.showMessageDialog(this, "Grade deleted successfully!");
					clearFields();
					loadGrades(con);
				}

			} else if (e.getSource() == loadBtn) {
				loadGrades(con);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
		}
	}

	private void loadGrades(Connection con) throws SQLException {
		model.setRowCount(0);
		String sql = "SELECT * FROM grade ORDER BY gradeID";
		ResultSet rs = con.createStatement().executeQuery(sql);
		while (rs.next()) {
			model.addRow(new Object[]{
					rs.getInt("gradeID"),
					rs.getInt("AssignmentId"),
					rs.getInt("studentID"),
					rs.getFloat("score"),
					rs.getString("remarks"),
					rs.getDate("createdAt"),
					rs.getInt("courseID")
			});
		}
	}

	private void clearFields() {
		idTxt.setText("");
		assignmentIdTxt.setText("");
		studentIdTxt.setText("");
		scoreTxt.setText("");
		remarksTxt.setText("");
		createdAtTxt.setText("");
		courseIdTxt.setText("");
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Gradepanel");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 550);
		frame.add(new GradePanel());
		frame.setVisible(true);
	}
}
