package org.dsrg.ourSpace.wea.domLogic.experiment;

import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dsrg.ourSpace.wea.domLogic.subject.ISubject;
import org.dsrg.ourSpace.wea.techSvc.ExperimentTDG;
import org.dsrg.soenea.domain.MapperException;
import org.dsrg.soenea.domain.mapper.GenericOutputMapper;
import org.dsrg.soenea.domain.mapper.LostUpdateException;
import org.dsrg.soenea.uow.UoW;

public class ExperimentOutputMapper extends
		GenericOutputMapper<Long, Experiment> {

	@SuppressWarnings("null")
	protected static Logger logger = LogManager.getLogger();

	@Override
	public void insert(Experiment experiment) throws MapperException {
		logger.debug("ExperimentOutputMapper.insert(Experiment={})", experiment);
		try {
			Timestamp finishSqlDate = experiment.isFinished() ? new Timestamp(
					experiment.getFinish().getTime()) : null;
			ExperimentTDG.it().insert(experiment.getId(),
					experiment.getVersion(),
					experiment.getKind(),
					new Timestamp(experiment.getStart().getTime()),
					finishSqlDate, experiment.getSubjects().get(0).getId(),
					experiment.getSubjects().get(1).getId());
		} catch (SQLException e) {
			throw new MapperException(e);
		}
	}

	@Override
	public void update(Experiment experiment) throws MapperException {
		logger.debug("ExperimentOutputMapper.update(Experiment={})", experiment);
		try {
			Timestamp finishSqlDate = experiment.isFinished() ? new Timestamp(
					experiment.getFinish().getTime()) : null;
			int c = ExperimentTDG.it().update(experiment.getId(),
					experiment.getVersion(),
					experiment.getKind(),
					new Timestamp(experiment.getStart().getTime()),
					finishSqlDate, experiment.getSubjects().get(0).getId(),
					experiment.getSubjects().get(1).getId());
			if (c == 0) {
				/*
				 * In the presence of optimistic off-line locking, an update
				 * operation must always affect one row of the table (since we
				 * are updating the version).
				 */
				throw new LostUpdateException("Subject: id " + experiment.getId()
						+ ", version " + experiment.getVersion());
			}
		} catch (SQLException e) {
			throw new MapperException(e);
		}
		experiment.setVersion(experiment.getVersion() + 1);
	}

	@Override
	public void delete(Experiment experiment) throws MapperException {
		logger.debug("ExperimentOutputMapper.delete(Experiment={})", experiment);
		try {
			ExperimentTDG.it().delete(experiment.getId(),
					experiment.getVersion());
			// if (count == 0)
			// throw new LostUpdateException(
			// "Delete target was not in database: " + experiment);
		} catch (SQLException e) {
			throw new MapperException(e);
		}
	}

	@Override
	public void cascadeDelete(Experiment d) throws MapperException {
		for (ISubject s : d.getSubjects()) {
			assert s != null;
			UoW.getCurrent().registerRemoved(s);
		}
	}
}
