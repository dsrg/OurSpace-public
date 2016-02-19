package org.dsrg.ourSpace.wea.domLogic.subject;

import java.sql.SQLException;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dsrg.ourSpace.wea.techSvc.subject.SubjectTDG;
import org.dsrg.soenea.domain.MapperException;
import org.dsrg.soenea.domain.mapper.GenericOutputMapper;
import org.dsrg.soenea.domain.mapper.LostUpdateException;

public class SubjectOutputMapper extends GenericOutputMapper<Long, Subject> {

	@SuppressWarnings("null")
	private static Logger logger = LogManager
			.getFormatterLogger(SubjectOutputMapper.class);

	@Override
	public void insert(Subject subject) throws MapperException {
		logger.debug("SubjectOutputMapper.insert(subject=%s)", subject);
		Set<String> attrs = subject.getAttrs();
		try {
			long id = subject.getId();
			SubjectTDG.it().insert(id, subject.getVersion());
			// Dependent mapping: delete and (re-)insert attributes.
			SubjectTDG.it().dependent().delete(id);
			for (String attrName : attrs) {
				if (attrName == null)
					continue; // FIXME: log it!
				String value = subject.get(attrName);
				SubjectTDG.it().dependent().insert(id, attrName, value);
			}
		} catch (SQLException e) {
			throw new MapperException(e);
		}
	}

	@Override
	public void update(Subject subject) throws MapperException {
		if (logger.isDebugEnabled()) {
			String msg = String.format(
					"[%d] SubjectOutputMapper.update(s) start: s = %s", Thread
							.currentThread().getId(), subject);
			logger.debug(msg);
		}
		Set<String> attrs = subject.getAttrs();
		try {
			long id = subject.getId();
			int c = SubjectTDG.it().update(id, subject.getVersion());
			if (c == 0) {
				/*
				 * In the presence of optimistic off-line locking, an update
				 * operation must always affect one row of the table (since we
				 * are updating the version).
				 */
				throw new LostUpdateException("Subject: id " + subject.getId()
						+ ", version " + subject.getVersion());
			}
			// Dependent mapping: delete and re-insert attributes.
			SubjectTDG.it().dependent().delete(id);
			for (String attrName : attrs) {
				if (attrName == null)
					continue; // FIXME: log it!
				String value = subject.get(attrName);
				SubjectTDG.it().dependent().insert(id, attrName, value);
			}
		} catch (SQLException e) {
			throw new MapperException(e);
		}
		subject.setVersion(subject.getVersion() + 1);
		if (logger.isDebugEnabled()) {
			String msg = String.format(
					"[%d] SubjectOutputMapper.update(s) end: s = %s", Thread
							.currentThread().getId(), subject);
			logger.debug(msg);
		}
	}

	@Override
	public void delete(Subject subject) throws MapperException {
		try {
			SubjectTDG.it().delete(subject.getId(), subject.getVersion());
			SubjectTDG.it().dependent().delete(subject.getId());
		} catch (SQLException e) {
			throw new MapperException(e);
		}
		// if (count == 0) { throw new LostUpdateException("..."); }
	}

}
