package com.corp.concepts.log.benchmark.connection;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;

public class MySQLConnectionFactory {
	private static BasicDataSource dataSource;

	private MySQLConnectionFactory() {
	}

	public static Connection getConnection() throws SQLException {
		String host = System.getProperty("MYSQL_HOST", "localhost");
		String dbName = System.getProperty("MYSQL_DB_NAME");
		String dbUser = System.getProperty("MYSQL_DB_USER");
		String dbPass = System.getProperty("MYSQL_DB_PASS");

		int port;
		try {
			port = Integer.valueOf(System.getProperty("MYSQL_PORT", "3306"));
		} catch (NumberFormatException nfe) {
			LogManager.getRootLogger().error("Error: ", nfe);
			throw nfe;
		}

		if (dataSource == null) {
			dataSource = new BasicDataSource();
			dataSource.setUrl(String.format("jdbc:mysql://%s:%d/%s?useSSL=false", host, port, dbName));
			dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
			dataSource.setUsername(dbUser);
			dataSource.setPassword(dbPass);
		}
		return dataSource.getConnection();
	}

}
