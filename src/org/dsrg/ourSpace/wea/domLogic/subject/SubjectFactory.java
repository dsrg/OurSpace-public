package org.dsrg.ourSpace.wea.domLogic.subject;

import java.sql.SQLException;

import org.dsrg.ourSpace.wea.techSvc.subject.SubjectTDG;
import org.dsrg.soenea.domain.DomainObject;
import org.dsrg.soenea.domain.MapperException;
import org.dsrg.soenea.uow.UoW;

public class SubjectFactory {

	public static Subject createClean(long id, long version) {
		Subject result = new Subject(id, version);
		UoW.getCurrent().registerClean(result);
		return result;
	}

	public static Subject createNew()
			throws MapperException // MissingMappingException, SQLException
			{
		Subject result = new Subject(getNextId(), DomainObject.INITIAL_VERSION);
		UoW.getCurrent().registerNew(result);
		return result;
	}

	private static long getNextId() throws MapperException {
		try {
			return SubjectTDG.it().getNextId();
		} catch (SQLException e) {
			throw new MapperException(e);
		}
	}

}
