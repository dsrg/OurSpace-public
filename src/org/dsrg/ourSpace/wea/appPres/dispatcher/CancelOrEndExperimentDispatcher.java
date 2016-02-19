package org.dsrg.ourSpace.wea.appPres.dispatcher;

import javax.servlet.ServletException;

import org.dsrg.ourSpace.wea.domLogic.command.CancelOrEndExperimentCmd;
import org.dsrg.soenea.domain.command.CommandException;

public class CancelOrEndExperimentDispatcher extends DispatcherSuperType {

	@Override
	public void execute() throws ServletException {
		if (!requestMethodIsPost())
			return; // Response already generated.

		try {
			new CancelOrEndExperimentCmd(this.helper()).execute();
			commit();
		} catch (CommandException e) {
			throw new ServletException(e);
		}
		getRequest().getSession().invalidate();
		forward();
	}

}
