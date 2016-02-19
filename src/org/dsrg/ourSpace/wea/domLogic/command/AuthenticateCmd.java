package org.dsrg.ourSpace.wea.domLogic.command;

import org.dsrg.ourSpace.wea.domLogic.Attr;
import org.dsrg.ourSpace.wea.domLogic.Page;
import org.dsrg.ourSpace.wea.domLogic.Role;
import org.dsrg.ourSpace.wea.domLogic.experiment.Experiment;
import org.dsrg.ourSpace.wea.domLogic.experiment.Experiment.Kind;
import org.dsrg.ourSpace.wea.domLogic.experiment.ExperimentFactory;
import org.dsrg.ourSpace.wea.domLogic.experiment.ExperimentInputMapper;
import org.dsrg.soenea.domain.MapperException;
import org.dsrg.soenea.domain.command.CommandException;
import org.dsrg.soenea.domain.helper.IHelper;

public class AuthenticateCmd extends DomainCommandSupertype {

	public AuthenticateCmd(IHelper helper) {
		super(helper);
	}

	@Override
	public void process() throws CommandException {
		String userId = checkForMandatoryReqAttr(String.class, Attr.userId);
		String password = checkForMandatoryReqAttr(String.class, Attr.password);
		Role role = checkForMandatoryReqAttr(Role.class, Attr.role);

		// Verify credentials
                // Note: No (real) authentication needed on intranet.
		if (!userId.equals("experimenter") || !password.equals("")) {
			setReqAttr(Attr.errorMessage, INVALID_LOGIN_MSG);
			setPage(Page.Login);
			logger.warn(INVALID_LOGIN_MSG);
			return;
		}
		Long experimentId = (Long) getSessionAttr(Attr.experimentId);
		logger.debug(name() + ": expId = {}", experimentId);
		Experiment exp = null;
		try {
			if (experimentId != null) {
				exp = ExperimentInputMapper.it().find(experimentId);
				if (exp != null && exp.isFinished()) {
					exp = null;
				}
				logger.debug(name() + ": find({}) exp = {}", experimentId, exp);
			}
			if (exp == null) {
				/*
				 * Use the currently active experiment if there is one;
				 * otherwise create a new experiment.
				 */
				exp = ExperimentInputMapper.it().findActive();
				logger.debug(name() + ": findActive exp = {}", exp);
				if (exp == null) {
					Kind kind = checkForMandatoryReqAttr(Kind.class, Attr.kind);
					exp = ExperimentFactory.createNew(kind);
					logger.debug(name() + ": new exp = {}", exp);
				}
				experimentId = exp.getId();
			}
		} catch (MapperException e) {
			throw new CommandException(e);
		}
		setSessionAttr(Attr.role, role);
		setSessionAttr(Attr.experimentId, experimentId);
		setReqAttr(Attr.gotoPage, Page.Page);
		setExpAndSubjectAndPageRelatedReqAttrAndRegisterDirtyAsNeeded(role, exp);
	}

}
