//MUKAMA UYISENGA LEA-223018803_IndividualProject
package com.Form;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import com.Panel.AssignmentPanel;
import com.Panel.CoursePanel;
import com.Panel.EnrollmentPanel;
import com.Panel.GradePanel;
import com.Panel.InstructorPanel;
import com.Panel.StudentPanel;

public class EMMS extends JFrame {
	 JTabbedPane tabs = new JTabbedPane();
	    
	    /**
	     * Constructor - Initialize main form based on user role
	     * @param role User role (admit\staff)
	     * @param userid User ID
	     */
	    public EMMS(String role, int userid) {
	        setTitle("Education Monitoring Management System");
	        setSize(1000, 700);
	        setLayout(new BorderLayout());
	        
	        // Add tabs based on user role
	        if (role.equalsIgnoreCase("admin")) {
	            // Admit has access to all modules
	            tabs.add("Student", new StudentPanel());
	            tabs.add("Course", new CoursePanel());
	            tabs.add("Instructors", new InstructorPanel());
	            tabs.add("Assignments", new AssignmentPanel());
	            tabs.add("Enrollments", new EnrollmentPanel());
	            tabs.add("Assignment Enrollment", new AssignmentEnrollmentPanel());
	            tabs.add("Grades", new GradePanel());
	        } else if (role.equalsIgnoreCase("staff")) {
	            // Staff has limited access
	            tabs.add("Students", new StudentPanel());
	            tabs.add("Courses", new CoursePanel());
	            tabs.add("Grades", new GradePanel());
	        }
	        
	        add(tabs, BorderLayout.CENTER);
	        setVisible(true);
	        setDefaultCloseOperation(EXIT_ON_CLOSE);
	    }
	}