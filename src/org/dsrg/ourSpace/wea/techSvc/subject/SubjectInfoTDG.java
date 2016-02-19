package org.dsrg.ourSpace.wea.techSvc.subject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.dsrg.ourSpace.wea.techSvc.TdgSupertypeForLongId;
import org.dsrg.soenea.service.threadLocal.DbRegistry;

/**
 * SubjectInfo is a dependent mapping from Subject. Hence, in particular, there
 * is no update method since dependent mappings are always handled via a full
 * delete of matching records followed by an insertion.
 */

/*package*/ class SubjectInfoTDG extends TdgSupertypeForLongId implements ISubjectInfoTDG {

	private SubjectInfoTDG() { }
	
	private static class SingletonHolder {
		public static final SubjectInfoTDG INSTANCE = new SubjectInfoTDG();
	}

	public static SubjectInfoTDG it() {
		return SingletonHolder.INSTANCE;
	}

	public final String BASE_NAME = "SubjectInfo";
	public final String TABLE = getTableName();
	
	public final String CREATE_SQL = "CREATE TABLE "
			+ TABLE
			+ "(	id			BIGINT NOT NULL," //
			+ "	attrName	VARCHAR(32) NOT NULL,"
			+ "	attrValue	VARCHAR(1024) NOT NULL,"
			+ "	PRIMARY KEY (id, attrName)"
			+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8";
	public final String INSERT_SQL = "INSERT INTO " + TABLE
			+ " (id,attrName,attrValue) VALUES (?,?,?)";
	public final String DELETE_SQL = "DELETE FROM " + TABLE
			+ " WHERE id=?";

	@Override
	protected String getTableBaseName() {
		return BASE_NAME;
	}

	@Override
	public String createTableSQL() throws SQLException {
		return CREATE_SQL;
	}

	@Override
	public void insert(long id, String attrName, String attrValue)
			throws SQLException {
		logger.trace("SubjectInfoTDG.insert(id={}, attrName={}, attrValue={})",
				id, attrName, attrValue);
		Connection con = DbRegistry.getDbConnection();
		PreparedStatement ps = con.prepareStatement(INSERT_SQL);
		ps.setLong(1, id);
		ps.setString(2, attrName);
		ps.setString(3, attrValue);
		ps.executeUpdate();
		ps.close();
	}

	/*
	 * update method purposefully omitted. See note in header comment.
	 */

	@Override
	public void delete(long id) throws SQLException {
		Connection con = DbRegistry.getDbConnection();
		PreparedStatement ps = con.prepareStatement(DELETE_SQL);
		ps.setLong(1, id);
		ps.executeUpdate();
	}
}
