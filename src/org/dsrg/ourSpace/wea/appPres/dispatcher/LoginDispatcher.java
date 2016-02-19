package org.dsrg.ourSpace.wea.appPres.dispatcher;

import javax.servlet.ServletException;

import org.dsrg.ourSpace.wea.domLogic.Attr;
import org.dsrg.ourSpace.wea.domLogic.Page;
import org.dsrg.ourSpace.wea.domLogic.command.DomainCommandSupertype;
import org.dsrg.ourSpace.wea.domLogic.command.LoginCmd;
import org.dsrg.soenea.domain.command.CommandException;

public class LoginDispatcher extends DispatcherSuperType {

	@Override
	public void execute() throws ServletException {
		try {
			String nullableParamValue = this.getRequest().getParameter(
					Attr.kind.nm());
			this.helper().setRequestAttribute(Attr.kind.nm(),
					nullableParamValue);
			DomainCommandSupertype cmd = new LoginCmd(helper());
			cmd.execute();
			// LoginCmd should not be making any changes that would
			// need to be persisted, but just in case.
			commit();
		} catch (CommandException e) {
			throw new ServletException(e);
		}
		this.forward(Page.Login);
	}
}
