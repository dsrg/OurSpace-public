package org.dsrg.ourSpace.wea.techSvc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.dsrg.soenea.service.UniqueIdFactory;
import org.dsrg.soenea.service.threadLocal.DbRegistry;
import org.jmlspecs.annotation.NonNull;

/**
 * TDG supertype.
 * 
 * @author Patrice Chalin, Copyright (c) 2013.
 */
public abstract class TdgSupertypeWithId<IdField> extends TdgSupertype {

	private final String FIND_SQL = "SELECT * from " + getTableName()
			+ " WHERE id=?";
	public final String DELETE_SQL = "DELETE FROM " + getTableName()
			+ " WHERE id=? AND version=?;";

	public long getNextId() throws SQLException {
		return UniqueIdFactory.getMaxId(getTableBaseName(), "id");
	}

	protected abstract void setId(PreparedStatement stmt, int i, IdField id)
			throws SQLException;

	public ResultSet find(IdField id) throws SQLException {
		Connection con = DbRegistry.getDbConnection();
		@SuppressWarnings("null")
		@NonNull
		PreparedStatement ps = con.prepareStatement(FIND_SQL);
		this.setId(ps, 1, id);
		@SuppressWarnings("null")
		@NonNull
		ResultSet rs = ps.executeQuery();
		return rs;
	}

	public int delete(long id, long version) throws SQLException {
		PreparedStatement ps = DbRegistry.getDbConnection().prepareStatement(
				DELETE_SQL);
		ps.setLong(1, id);
		ps.setLong(2, version);
		int count = ps.executeUpdate();
		ps.close();
		return count;
	}

}
