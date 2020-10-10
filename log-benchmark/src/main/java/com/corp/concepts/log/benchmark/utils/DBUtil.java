package com.corp.concepts.log.benchmark.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.corp.concepts.log.benchmark.connection.MySQLConnectionFactory;

public class DBUtil {

	public static void emptyTable() {
		Connection connection = null;
		try {
			connection = MySQLConnectionFactory.getConnection();
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("DELETE FROM LOGS");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static int getRecordCount() {
		Connection connection = null;
		try {
			connection = MySQLConnectionFactory.getConnection();
			Statement stmt = connection.createStatement();
			stmt.execute("SELECT COUNT(*) AS total FROM LOGS");

			ResultSet rs = stmt.getResultSet();

			while (rs.next()) {
				return rs.getInt("total");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return 0;
	}
}
