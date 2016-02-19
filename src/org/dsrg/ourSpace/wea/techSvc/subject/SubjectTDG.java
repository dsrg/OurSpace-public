package org.dsrg.ourSpace.wea.techSvc.subject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.dsrg.ourSpace.wea.techSvc.TdgSupertypeForLongId;
import org.dsrg.soenea.service.threadLocal.DbRegistry;

/**
 * Subject TDG implemented as a singleton (using Bill Pugh's idiom).
 * 
 * @author Patrice Chalin, Copyright (c) 2013.
 */
public class SubjectTDG extends TdgSupertypeForLongId {

	private SubjectTDG() {
	}

	private static class SingletonHolder {
		public static final SubjectTDG INSTANCE = new SubjectTDG();
	}

	public static SubjectTDG it() {
		return SingletonHolder.INSTANCE;
	}

	public final String BASE_NAME = "Subject";
	public final String TABLE = getTableName();

	public final String UPDATE_SQL = "UPDATE " + TABLE
			+ " SET version=version+1 WHERE id=? and version=?";
	public final String INSERT_SQL = "INSERT INTO " + TABLE
			+ " (id,version) VALUES (?,?);";
	public final String CREATE_SQL = "CREATE TABLE " + TABLE
			+ "(	id			BIGINT NOT NULL," //
			+ "	version		BIGINT NOT NULL," //
			+ "	PRIMARY KEY (id)" //
			+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8";

	@Override
	protected String getTableBaseName() {
		return BASE_NAME;
	}

	public void insert(long id, long version) throws SQLException {
		logger.debug("SubjectTDG.insert(id={}, version={})", id, version);
		Connection con = DbRegistry.getDbConnection();
		PreparedStatement ps = con.prepareStatement(INSERT_SQL);
		ps.setLong(1, id);
		ps.setLong(2, version);
		ps.executeUpdate();
		ps.close();
	}

	public int update(long id, long version) throws SQLException //
	{
		// logger.debug("[{}] SubjectTDG.update(id={}, version={} start)",
		// Thread.currentThread().getId(), id, version);
		Connection con = DbRegistry.getDbConnection();
		PreparedStatement ps = con.prepareStatement(UPDATE_SQL);
		ps.setLong(1, id);
		ps.setLong(2, version);
		int c = ps.executeUpdate();
		logger.debug("{} = {}", c, ps);
		// logger.debug("[{}] SubjectTDG.update(id={}, version={} finish)",
		// Thread.currentThread().getId(), id, version);
		/*
		 * executeUpdate returns a count of the number of rows actually updated
		 * in the DB. Generally speaking, an update count of 0 is not an error.
		 * It simply means that no records were affected (and hence, the row in
		 * question already had the desired values).
		 */
		return c;
	}

	@Override
	public String createTableSQL() throws SQLException {
		return CREATE_SQL;
	}

	/** Also creates dependent table. */
	@Override
	public void createTable() throws SQLException {
		super.createTable();
		SubjectInfoTDG.it().createTable();
	}

	/** Also drops dependent table. */
	@Override
	public void dropTableIfExists() throws SQLException {
		super.dropTableIfExists();
		SubjectInfoTDG.it().dropTableIfExists();
	}

	/** Grant public access to selected features of dependent table */
	public ISubjectInfoTDG dependent() {
		return SubjectInfoTDG.it();
	}
}
