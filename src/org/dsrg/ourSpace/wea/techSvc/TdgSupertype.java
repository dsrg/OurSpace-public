package org.dsrg.ourSpace.wea.techSvc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dsrg.soenea.service.threadLocal.DbRegistry;
import org.jmlspecs.annotation.NonNull;

public abstract class TdgSupertype {

	@SuppressWarnings("null")
	protected static Logger logger = LogManager.getLogger();
	private final String DROP_SQL = "DROP TABLE IF EXISTS " + getTableName();
	private final String FIND_ALL_SQL = "SELECT * from " + getTableName();

	protected abstract String getTableBaseName();

	public String getTableName() {
		return DbRegistry.getTablePrefix() + getTableBaseName();
	}

	public ResultSet findAll() throws SQLException {
		Connection con = DbRegistry.getDbConnection();
		@SuppressWarnings("null")
		@NonNull
		ResultSet rs = con.createStatement().executeQuery(FIND_ALL_SQL);
		return rs;
	}

	public void dropTableIfExists() throws SQLException {
		PreparedStatement ps = DbRegistry.getDbConnection().prepareStatement(
				DROP_SQL);
		ps.executeUpdate();
		ps.close();
	}

	public abstract String createTableSQL() throws SQLException;

	public void createTable() throws SQLException {
		PreparedStatement ps = DbRegistry.getDbConnection().prepareStatement(
				createTableSQL());
		ps.executeUpdate();
		ps.close();
	}

}
