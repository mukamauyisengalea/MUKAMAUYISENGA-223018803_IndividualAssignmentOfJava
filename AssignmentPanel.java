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

public class AssignmentPanel extends JPanel implements ActionListener{
	private JTextField idTxt, refIdTxt, descTxt, dateTxt, remarksTxt;
	private JComboBox<String> statusCmb;
	private JButton addBtn, updateBtn, deleteBtn, loadBtn;
	private JTable table;
	private DefaultTableModel model;

	public AssignmentPanel() {
		setLayout(null);

		idTxt = new JTextField();
		refIdTxt = new JTextField();
		descTxt = new JTextField();
		dateTxt = new JTextField();
		remarksTxt = new JTextField();

		statusCmb = new JComboBox<>(new String[]{"Submitted", "Pending", "Graded", "Late Submission"});

		addBtn = new JButton("Add");
		updateBtn = new JButton("Update");
		deleteBtn = new JButton("Delete");
		loadBtn = new JButton("Load");

		String[] labels = {"Assignment ID", "Reference ID", "Description", "Date", "Status", "Remarks"};
		model = new DefaultTableModel(labels, 0);
		table = new JTable(model);
		JScrollPane sp = new JScrollPane(table);
		sp.setBounds(20, 250, 750, 200);
		add(sp);

		int y = 20;
		addField("Assignment ID", idTxt, y); y += 30;
		addField("Reference ID", refIdTxt, y); y += 30;
		addField("Description", descTxt, y); y += 30;
		addField("Date (YYYY-MM-DD)", dateTxt, y); y += 30;
		addField("Status", statusCmb, y); y += 30;
		addField("Remarks", remarksTxt, y); y += 30;

		addButtons();

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = table.getSelectedRow();
				if (row >= 0) {
					idTxt.setText(model.getValueAt(row, 0).toString());
					refIdTxt.setText(model.getValueAt(row, 1).toString());
					descTxt.setText(model.getValueAt(row, 2).toString());
					dateTxt.setText(model.getValueAt(row, 3).toString());
					statusCmb.setSelectedItem(model.getValueAt(row, 4).toString());
					remarksTxt.setText(model.getValueAt(row, 5).toString());
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

	public void actionPerformed(ActionEvent e) {
		try (Connection con = DB.getConnection()) {
			if (e.getSource() == addBtn) {
				String sql = "INSERT INTO assignment(referenceID, description, date, status, remarks) VALUES(?,?,?,?,?)";
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setInt(1, Integer.parseInt(refIdTxt.getText()));
				ps.setString(2, descTxt.getText());
				ps.setString(3, dateTxt.getText());
				ps.setString(4, statusCmb.getSelectedItem().toString());
				ps.setString(5, remarksTxt.getText());
				ps.executeUpdate();
				JOptionPane.showMessageDialog(this, "Assignment added successfully!");
				clearFields();
				loadAssignments(con);

			} else if (e.getSource() == updateBtn) {
				if (idTxt.getText().isEmpty()) {
					JOptionPane.showMessageDialog(this, "⚠ Please select an assignment to update!");
					return;
				}
				String sql = "UPDATE assignment SET referenceID=?, description=?, date=?, status=?, remarks=? WHERE AssignmentID=?";
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setInt(1, Integer.parseInt(refIdTxt.getText()));
				ps.setString(2, descTxt.getText());
				ps.setString(3, dateTxt.getText());
				ps.setString(4, statusCmb.getSelectedItem().toString());
				ps.setString(5, remarksTxt.getText());
				ps.setInt(6, Integer.parseInt(idTxt.getText()));
				ps.executeUpdate();
				JOptionPane.showMessageDialog(this, "Assignment updated successfully!");
				clearFields();
				loadAssignments(con);

			} else if (e.getSource() == deleteBtn) {
				if (idTxt.getText().isEmpty()) {
					JOptionPane.showMessageDialog(this, "⚠ Please select an assignment to delete!");
					return;
				}
				int confirm = JOptionPane.showConfirmDialog(this,
						"Are you sure you want to delete this assignment?",
						"Confirm Delete",
						JOptionPane.YES_NO_OPTION);
				if (confirm == JOptionPane.YES_OPTION) {
					String sql = "DELETE FROM assignment WHERE AssignmentID=?";
					PreparedStatement ps = con.prepareStatement(sql);
					ps.setInt(1, Integer.parseInt(idTxt.getText()));
					ps.executeUpdate();
					JOptionPane.showMessageDialog(this, "Assignment deleted successfully!");
					clearFields();
					loadAssignments(con);
				}

			} else if (e.getSource() == loadBtn) {
				loadAssignments(con);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
		}
	}

	private void loadAssignments(Connection con) throws SQLException {
		model.setRowCount(0);
		String sql = "SELECT * FROM assignment ORDER BY AssignmentID";
		ResultSet rs = con.createStatement().executeQuery(sql);
		while (rs.next()) {
			model.addRow(new Object[]{
					rs.getInt("AssignmentID"),
					rs.getInt("referenceID"),
					rs.getString("description"),
					rs.getDate("date"),
					rs.getString("status"),
					rs.getString("remarks")
			});
		}
	}

	private void clearFields() {
		idTxt.setText("");
		refIdTxt.setText("");
		descTxt.setText("");
		dateTxt.setText("");
		statusCmb.setSelectedIndex(0);
		remarksTxt.setText("");
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Assignment Panel");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.add(new AssignmentPanel());
		frame.setVisible(true);
	}
}

