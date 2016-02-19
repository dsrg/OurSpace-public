package org.dsrg.ourSpace.wea.domLogic.experiment;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.dsrg.ourSpace.wea.domLogic.DateFactory;
import org.dsrg.ourSpace.wea.domLogic.experiment.Experiment.Kind;
import org.dsrg.ourSpace.wea.domLogic.subject.ISubject;
import org.dsrg.ourSpace.wea.domLogic.subject.SubjectFactory;
import org.dsrg.ourSpace.wea.techSvc.ExperimentTDG;
import org.dsrg.soenea.domain.DomainObject;
import org.dsrg.soenea.domain.MapperException;
import org.dsrg.soenea.uow.UoW;
import org.jmlspecs.annotation.NonNull;
import org.jmlspecs.annotation.Nullable;

public class ExperimentFactory {

	public static Experiment createClean(long id, long version, Kind kind, Date start,
			@Nullable Date finish, List<? extends ISubject> subjects) {
		Experiment result = new Experiment(id, version, kind, start, finish, subjects);
		UoW.getCurrent().registerClean(result);
		return result;
	}

	public static Experiment createNew() throws MapperException {
		return createNew(Kind.HCM);
	}
	
	public static Experiment createNew(Kind kind) throws MapperException {
		Date start = DateFactory.newDate();
		Date finish = null;
		@SuppressWarnings("null")
		@NonNull
		List<? extends ISubject> subjects = Arrays.asList(
				SubjectFactory.createNew(), SubjectFactory.createNew());
		return createNew(kind, start, finish, subjects);
	}

	private static Experiment createNew(Kind kind, Date start,
			@Nullable Date finish, List<? extends ISubject> subjects)
			throws MapperException {
		Experiment result = new Experiment(getNextId(),
				DomainObject.INITIAL_VERSION, kind, start, finish, subjects);
		UoW.getCurrent().registerNew(result);
		return result;
	}

	private static long getNextId() throws MapperException {
		try {
			return ExperimentTDG.it().getNextId();
		} catch (SQLException e) {
			throw new MapperException(e);
		}
	}

}
