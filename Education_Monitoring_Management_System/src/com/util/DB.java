//MUKAMA UYISENGA LEA-223018803_IndividualProject
package com.util;

import java.sql.Connection;
import java.sql.DriverManager;



public class DB {
	/**
	 * Establishes and returns connection to MySQL database
	 * @return Connection object
	 * @throws Exception if connection fails
	 */
	public static Connection getConnection() throws Exception {
		// Load MySQL JDBC Driver
		Class.forName("com.mysql.cj.jdbc.Driver");

		// Return connection to education_monitoring_management_system database
		return DriverManager.getConnection(
				"jdbc:mysql://localhost/education_monitoring_management_system",
				"root",  
				""       
				);
	}
}


