package org.dsrg.ourSpace.wea.domLogic.experiment;

import static com.google.common.base.Preconditions.checkNotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.dsrg.ourSpace.wea.domLogic.DateFactory;
import org.dsrg.ourSpace.wea.domLogic.InputMapperSupertype;
import org.dsrg.ourSpace.wea.domLogic.experiment.Experiment.Kind;
import org.dsrg.ourSpace.wea.domLogic.subject.ISubject;
import org.dsrg.ourSpace.wea.domLogic.subject.SubjectInputMapper;
import org.dsrg.ourSpace.wea.techSvc.ExperimentTDG;
import org.dsrg.soenea.domain.MapperException;
import org.jmlspecs.annotation.NonNull;
import org.jmlspecs.annotation.Nullable;

public class ExperimentInputMapper extends
		InputMapperSupertype<Long, Experiment> {

	private ExperimentInputMapper() {
		super(Experiment.class);
	}

	private static class SingletonHolder {
		public static final ExperimentInputMapper INSTANCE = new ExperimentInputMapper();
	}

	public static ExperimentInputMapper it() {
		return SingletonHolder.INSTANCE;
	}

	public @Nullable
	Experiment findActive() throws MapperException {
		try {
			ResultSet rs = ExperimentTDG.it().findActive();
			if (!rs.next())
				return null;
			// Return the first element and ignore the rest for now.
			Experiment result = this.makeDO(rs);
			return result;
		} catch (SQLException e) {
			throw new MapperException(e);
		}
	}

	@Override
	protected Experiment makeDO(ResultSet rs) throws SQLException,
			MapperException {
		long id = rs.getLong("id");
		Date start = getJavaUtilDate(rs, "start");
		checkNotNull(start, "Experiment " + id
				+ ": 'start' column value is null");
		return ExperimentFactory.createClean(id, rs.getInt("version"),
				getKind(rs), start, getJavaUtilDate(rs, "finish"),
				getSubjects(rs));
	}

	@Override
	protected ResultSet tdgFind(Long id) throws SQLException {
		return ExperimentTDG.it().find(id);
	}

	@Override
	protected ResultSet tdgFindAll() throws SQLException {
		return ExperimentTDG.it().findAll();
	}

	private Kind getKind(ResultSet rs) throws SQLException {
		String kindAsString = rs.getString("kind");
		@SuppressWarnings("null")
		Kind kind = Kind.valueOf(kindAsString); // Might throw exceptions.
		return kind;
	}

	protected @Nullable
	Date getJavaUtilDate(ResultSet rs, String fieldName) throws SQLException {
		Timestamp timestamp = rs.getTimestamp(fieldName);
		return timestamp == null ? null : DateFactory.newDate(timestamp
				.getTime());
	}

	private static List<? extends ISubject> getSubjects(ResultSet rs)
			throws SQLException, MapperException {
		long subject1Id = rs.getLong("subject1");
		long subject2Id = rs.getLong("subject2");
		ISubject subject1 = SubjectInputMapper.it().find(subject1Id);
		ISubject subject2 = SubjectInputMapper.it().find(subject2Id);
		@SuppressWarnings("null")
		@NonNull
		List<? extends ISubject> subjects = Arrays.asList(subject1, subject2);
		return subjects;
	}

}
