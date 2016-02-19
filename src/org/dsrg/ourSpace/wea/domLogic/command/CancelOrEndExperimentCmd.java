package org.dsrg.ourSpace.wea.domLogic.command;

import static org.dsrg.util.Nulls.nonNull;

import org.dsrg.ourSpace.wea.domLogic.Attr;
import org.dsrg.ourSpace.wea.domLogic.DateFactory;
import org.dsrg.ourSpace.wea.domLogic.Page;
import org.dsrg.ourSpace.wea.domLogic.Role;
import org.dsrg.ourSpace.wea.domLogic.experiment.Experiment;
import org.dsrg.ourSpace.wea.domLogic.subject.ISubject;
import org.dsrg.soenea.domain.MapperException;
import org.dsrg.soenea.domain.command.CommandException;
import org.dsrg.soenea.domain.helper.IHelper;
import org.dsrg.soenea.uow.UoW;

public class CancelOrEndExperimentCmd extends DomainCommandSupertype {

	public CancelOrEndExperimentCmd(IHelper helper) {
		super(helper);
	}

	@Override
	public void process() throws CommandException {
		helper().setApplicationAttribute(Attr.gameMgr.nm(), null);
		Role role = checkForMandatorySessionAttr(Role.class, Attr.role);
		if (role != Role.EXPERIMENTER) {
			setSessionAttr(Attr.role, null); // logout.
			String msg = String.format(ROLE_HAS_INSUFFICIENT_PRIVILEDGES_MSG,
					role, this.getClass().getSimpleName());
			throw setErrorAttrAndCreateCmdException(nonNull(msg));
		}
		try {
			Experiment exp = checkForMandatoryExp();
			logger.debug(name() + ": exp = {}", exp);
			final ISubject subject = exp.getSubject(0);
			final ISubject otherSubject = exp.getOtherSubject(0);

			if (exp.isFinished()) {
				String msg = String.format(EXPERIMENT_IS_ALREADY_FINISHED_MSG,
						exp.getId());
				logger.warn(msg);
				setReqAttr(Attr.warningMessage, nonNull(msg));
			} else if (subject.getCurrentPage() == Page.End
					&& otherSubject.getCurrentPage() == Page.End) {
				// Mark experiment as finished.
				exp.setFinish(DateFactory.newDate());
				UoW.getCurrent().registerDirty(exp);
			} else {
				// Actually cancel experiment.
				UoW.getCurrent().registerRemoved(exp);
			}
		} catch (MapperException e) {
			throw new CommandException(e);
		} finally {
			setSessionAttr(Attr.experimentId, null);
		}
		setPage(Page.Login);
	}

}
