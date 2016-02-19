package org.dsrg.ourSpace.wea.domLogic.command;

import org.dsrg.ourSpace.wea.domLogic.Attr;
import org.dsrg.ourSpace.wea.domLogic.experiment.Experiment;
import org.dsrg.ourSpace.wea.domLogic.experiment.ExperimentInputMapper;
import org.dsrg.soenea.domain.MapperException;
import org.dsrg.soenea.domain.command.CommandException;
import org.dsrg.soenea.domain.helper.IHelper;

public class LoginCmd extends DomainCommandSupertype {

	public LoginCmd(IHelper helper) {
		super(helper);
	}

	@Override
	public void process() throws CommandException {
		try {
			Experiment exp = ExperimentInputMapper.it().findActive();
			if (exp == null)
				return;
			setReqAttr(Attr.experiment, exp);
			// Long experimentId = exp.getId();
			// setSessionAttr(Attr.experimentId, experimentId);
		} catch (MapperException e) {
			throw new CommandException(e);
		}

	}

}
