package org.dsrg.ourSpace.wea.techSvc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.dsrg.ourSpace.wea.domLogic.experiment.Experiment.Kind;
import org.dsrg.soenea.service.threadLocal.DbRegistry;
import org.jmlspecs.annotation.NonNull;
import org.jmlspecs.annotation.Nullable;

/**
 * Experiment TDG implemented as a singleton (using Bill Pugh's idiom).
 * 
 * @author Patrice Chalin, Copyright (c) 2013.
 */
public class ExperimentTDG extends TdgSupertypeForLongId {

	private ExperimentTDG() {}
	
	private static class SingletonHolder {
		public static final ExperimentTDG INSTANCE = new ExperimentTDG();
	}

	public static ExperimentTDG it() {
		return SingletonHolder.INSTANCE;
	}

	public final String BASE_NAME = "Experiment";
	public final String TABLE = getTableName();
	
	public final String FIND_ACTIVE_SQL = "SELECT * from " + TABLE
			+ " WHERE finish IS NULL ORDER BY start";
	public final String INSERT_SQL = "INSERT INTO " + TABLE
			+ " (id,version,kind,start,finish,subject1,subject2) VALUES (?,?,?,?,?,?,?);";
	public final String UPDATE_SQL = "UPDATE "
			+ TABLE
			+ " SET version=version+1, kind=?, start=?, finish=?, subject1=?, subject2=? WHERE id=? and version=?";
	public final String CREATE_SQL = "CREATE TABLE " + TABLE
			+ "(id			BIGINT NOT NULL,"
			+ "	version		BIGINT NOT NULL,"
			+ " kind        VARCHAR(3) NOT NULL,"
			+ "	start		TIMESTAMP NOT NULL,"
			+ "	finish		TIMESTAMP NULL,"
			+ "	subject1	BIGINT NOT NULL,"
			+ "	subject2	BIGINT NOT NULL,"
			+ "	PRIMARY KEY (id)"
			+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";


	@Override
	public String createTableSQL() throws SQLException {
		return CREATE_SQL;
	}

	@Override
	protected String getTableBaseName() {
		return BASE_NAME;
	}

	public ResultSet findActive() throws SQLException {
		logger.debug("ExperimentTDG.findActive()");
		Connection con = DbRegistry.getDbConnection();
		@SuppressWarnings("null")
		@NonNull
		PreparedStatement ps = con.prepareStatement(FIND_ACTIVE_SQL);
		@SuppressWarnings("null")
		@NonNull
		ResultSet rs = ps.executeQuery();
		return rs;
	}

	public int insert(long id, long version, Kind kind, Timestamp start,
			@Nullable Timestamp finish, long subjectId1, long subjectId2)
			throws SQLException {
		logger.debug("ExperimentTDG.insert(id={}, version={})", id, version);
		PreparedStatement ps = DbRegistry.getDbConnection().prepareStatement(
				INSERT_SQL);
		ps.setLong(1, id);
		ps.setLong(2, version);
		ps.setString(3, kind.name());
		ps.setTimestamp(4, start);
		ps.setTimestamp(5, finish);
		ps.setLong(6, subjectId1);
		ps.setLong(7, subjectId2);
		logger.debug(ps);
		int c = ps.executeUpdate();
		ps.close();
		return c;
	}

	public int update(long id, long version, Kind kind, Timestamp start,
			@Nullable Timestamp finish, long subjectId1, long subjectId2)
			throws SQLException {
		logger.debug("ExperimentTDG.insert(id={}, version={})", id, version);
		PreparedStatement ps = DbRegistry.getDbConnection().prepareStatement(
				UPDATE_SQL);
		ps.setString(1, kind.name());
		ps.setTimestamp(2, start);
		ps.setTimestamp(3, finish);
		ps.setLong(4, subjectId1);
		ps.setLong(5, subjectId2);
		ps.setLong(6, id);
		ps.setLong(7, version);
		int c = ps.executeUpdate();
		logger.debug("{} = {}", c, ps);
		ps.close(); // Experiments are not further modified by dispatchers.
		return c;
	}

}
