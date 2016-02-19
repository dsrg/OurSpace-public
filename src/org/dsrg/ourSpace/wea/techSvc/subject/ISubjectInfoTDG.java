package org.dsrg.ourSpace.wea.techSvc.subject;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ISubjectInfoTDG {

	public abstract String getTableName();

	public abstract ResultSet find(Long id) throws SQLException;

	public abstract void insert(long id, String attrName, String attrValue)
			throws SQLException;

	public abstract void delete(long id) throws SQLException;

}