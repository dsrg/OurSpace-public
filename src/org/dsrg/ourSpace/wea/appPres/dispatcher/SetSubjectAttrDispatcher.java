package org.dsrg.ourSpace.wea.appPres.dispatcher;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.dsrg.ourSpace.wea.domLogic.Attr;
import org.dsrg.ourSpace.wea.domLogic.command.SetSubjectAttrCmd;
import org.dsrg.soenea.domain.command.CommandException;

public class SetSubjectAttrDispatcher extends DispatcherSuperType {

	@Override
	public void execute() throws ServletException, IOException {
		if (!requestMethodIsPost())
			return; // Response already generated.

		try {
			if(getRequest().getParameter(Attr.role.nm()) != null) {
				this.getRoleAndSetAsAttr();
			}
			new SetSubjectAttrCmd(this.helper()).execute();
			commit();
		} catch (CommandException e) {
			// throw new ServletException(e);
			this.getResponse().sendError(HttpServletResponse.SC_EXPECTATION_FAILED, e.toString());
		}
		forward();
	}

}
