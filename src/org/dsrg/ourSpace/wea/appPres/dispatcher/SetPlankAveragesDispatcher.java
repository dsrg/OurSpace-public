package org.dsrg.ourSpace.wea.appPres.dispatcher;

import java.io.IOException;

import javax.servlet.ServletException;

import org.dsrg.ourSpace.wea.domLogic.Attr;
import org.dsrg.ourSpace.wea.domLogic.command.SetPlankAvgAttrCmd;
import org.dsrg.soenea.domain.command.CommandException;

public class SetPlankAveragesDispatcher extends DispatcherSuperType {

	@Override
	public void execute() throws ServletException, IOException {
		if (!requestMethodIsPost())
			return; // Response already generated.

		this.getAndSetAsAttr(Attr.plankDurationAverage1);
		this.getAndSetAsAttr(Attr.plankDurationAverage2);

		try {
			new SetPlankAvgAttrCmd(this.helper()).execute();
			commit();
		} catch (CommandException e) {
			// throw new ServletException(e);
			// this.getResponse().sendError(HttpServletResponse.SC_EXPECTATION_FAILED, e.toString());
		}
		forward();
	}

}
