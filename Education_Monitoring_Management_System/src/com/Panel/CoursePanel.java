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

public class CoursePanel extends JPanel implements ActionListener{

	private JTextField idTxt, nameTxt, codeTxt, createdAtTxt;
	private JButton addBtn, updateBtn, deleteBtn, loadBtn;
	private JTable table;
	private DefaultTableModel model;

	public CoursePanel() {
		setLayout(null);

		idTxt = new JTextField();
		nameTxt = new JTextField();
		codeTxt = new JTextField();
		createdAtTxt = new JTextField();

		addBtn = new JButton("Add");
		updateBtn = new JButton("Update");
		deleteBtn = new JButton("Delete");
		loadBtn = new JButton("Load");

		String[] labels = {"Course ID", "Course Name", "Course Code", "Created At"};
		model = new DefaultTableModel(labels, 0);
		table = new JTable(model);
		JScrollPane sp = new JScrollPane(table);
		sp.setBounds(20, 200, 750, 250);
		add(sp);

		int y = 20;
		addField("Course ID", idTxt, y); y += 30;
		addField("Course Name", nameTxt, y); y += 30;
		addField("Course Code", codeTxt, y); y += 30;
		addField("Created At (YYYY-MM-DD)", createdAtTxt, y); y += 30;

		addButtons();

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = table.getSelectedRow();
				if (row >= 0) {
					idTxt.setText(model.getValueAt(row, 0).toString());
					nameTxt.setText(model.getValueAt(row, 1).toString());
					codeTxt.setText(model.getValueAt(row, 2).toString());
					createdAtTxt.setText(model.getValueAt(row, 3).toString());
				}
			}
		});
	}

	private void addField(String lbl, JComponent txt, int y) {
		JLabel l = new JLabel(lbl);
		l.setBounds(20, y, 150, 25);
		txt.setBounds(180, y, 150, 25);
		add(l);
		add(txt);
	}

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

	@Override
	public void actionPerformed(ActionEvent e) {
		try (Connection con = DB.getConnection()) {
			if (e.getSource() == addBtn) {
				String sql = "INSERT INTO course(courseName, courseCode, createdAt) VALUES(?,?,?)";
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setString(1, nameTxt.getText());
				ps.setString(2, codeTxt.getText());
				ps.setString(3, createdAtTxt.getText());
				ps.executeUpdate();
				JOptionPane.showMessageDialog(this, "Course added successfully!");
				clearFields();
				loadCourses(con);

			} else if (e.getSource() == updateBtn) {
				if (idTxt.getText().isEmpty()) {
					JOptionPane.showMessageDialog(this, "Please select a course to update!");
					return;
				}
				String sql = "UPDATE course SET courseName=?, courseCode=?, createdAt=? WHERE CourseID=?";
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setString(1, nameTxt.getText());
				ps.setString(2, codeTxt.getText());
				ps.setString(3, createdAtTxt.getText());
				ps.setInt(4, Integer.parseInt(idTxt.getText()));
				ps.executeUpdate();
				JOptionPane.showMessageDialog(this, "Course updated successfully!");
				clearFields();
				loadCourses(con);

			} else if (e.getSource() == deleteBtn) {
				if (idTxt.getText().isEmpty()) {
					JOptionPane.showMessageDialog(this, "Please select a course to delete!");
					return;
				}
				int confirm = JOptionPane.showConfirmDialog(this,
						"Are you sure you want to delete this course?",
						"Confirm Delete",
						JOptionPane.YES_NO_OPTION);
				if (confirm == JOptionPane.YES_OPTION) {
					String sql = "DELETE FROM course WHERE CourseID=?";
					PreparedStatement ps = con.prepareStatement(sql);
					ps.setInt(1, Integer.parseInt(idTxt.getText()));
					ps.executeUpdate();
					JOptionPane.showMessageDialog(this, "Course deleted successfully!");
					clearFields();
					loadCourses(con);
				}

			} else if (e.getSource() == loadBtn) {
				loadCourses(con);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "�?� Error: " + ex.getMessage());
		}
	}

	private void loadCourses(Connection con) throws SQLException {
		model.setRowCount(0);
		String sql = "SELECT * FROM course ORDER BY CourseID";
		ResultSet rs = con.createStatement().executeQuery(sql);
		while (rs.next()) {
			model.addRow(new Object[]{
					rs.getInt("CourseID"),
					rs.getString("courseName"),
					rs.getString("courseCode"),
					rs.getDate("createdAt")
			});
		}
	}

	private void clearFields() {
		idTxt.setText("");
		nameTxt.setText("");
		codeTxt.setText("");
		createdAtTxt.setText("");
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Course Panel");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.add(new CoursePanel());
		frame.setVisible(true);
	}
}
