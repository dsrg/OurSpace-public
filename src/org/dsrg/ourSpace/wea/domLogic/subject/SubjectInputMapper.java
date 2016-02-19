package org.dsrg.ourSpace.wea.domLogic.subject;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.dsrg.ourSpace.wea.domLogic.InputMapperSupertype;
import org.dsrg.ourSpace.wea.techSvc.subject.SubjectTDG;

/**
 * Subject input mapper implemented as a singleton (using Bill Pugh's idiom).
 * 
 * @author Patrice Chalin, Copyright (c) 2013.
 */
public class SubjectInputMapper extends InputMapperSupertype<Long, Subject> {

	private SubjectInputMapper() {
		super(Subject.class);
	}

	private static class SingletonHolder {
		public static final SubjectInputMapper INSTANCE = new SubjectInputMapper();
	}

	public static SubjectInputMapper it() {
		return SingletonHolder.INSTANCE;
	}

	@Override
	protected Subject makeDO(ResultSet rs) throws SQLException {
		Subject result = SubjectFactory.createClean(rs.getLong("id"),
				rs.getInt("version"));
		return addAttributes(result);
	}

	private Subject addAttributes(Subject subject) throws SQLException {
		ResultSet rs = SubjectTDG.it().dependent().find(subject.getId());
		while (rs.next()) {
			String attrName = rs.getString("attrName");
			if (attrName == null)
				continue; // Skip invalid entry. TODO: log it.
			String attrValue = rs.getString("attrValue");
			if (attrValue == null)
				continue; // Skip invalid entry. TODO: log it.
			subject.set(attrName, attrValue);
		}
		return subject;
	}

	@Override
	protected ResultSet tdgFind(Long id) throws SQLException {
		return SubjectTDG.it().find(id);
	}

	@Override
	protected ResultSet tdgFindAll() throws SQLException {
		return SubjectTDG.it().findAll();
	}

}
