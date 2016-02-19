package org.dsrg.ourSpace.wea.appPres.dispatcher;

import javax.servlet.ServletException;

import org.dsrg.ourSpace.wea.domLogic.Attr;
import org.dsrg.ourSpace.wea.domLogic.command.AuthenticateCmd;
import org.dsrg.ourSpace.wea.domLogic.command.DomainCommandSupertype;
import org.dsrg.ourSpace.wea.domLogic.experiment.Experiment.Kind;
import org.dsrg.soenea.domain.command.CommandException;
import org.jmlspecs.annotation.NonNull;
import org.jmlspecs.annotation.Nullable;

public class AuthenticateDispatcher extends DispatcherSuperType {

	/**
	 * Respond to user attempt to login. Respond based on chosen role.
	 */
	@Override
	public void execute() throws ServletException {
		if (!requestMethodIsPost())
			return; // Response already generated.

		this.getAndSetAsAttr(Attr.userId);
		this.getAndSetAsAttr(Attr.password);
		this.getRoleAndSetAsAttr();
		this.getKindAndSetAsAttr();
		
		try {
			DomainCommandSupertype cmd = new AuthenticateCmd(helper());
			cmd.execute();
			commit();
		} catch (CommandException e) {
			throw new ServletException(e);
		}
		Object errMsg = this.helper().getRequestAttribute(Attr.errorMessage.nm());
		if(errMsg != null) {
			getRequest().getSession().invalidate();
		}
		this.forward();
	}

	private @Nullable Kind getKindAndSetAsAttr() throws ServletException {
		String kindAsString = this.getRequest().getParameter(Attr.kind.nm());
		Kind kind;
		try {
			@SuppressWarnings("null")
			@NonNull
			String kindAsUpperCaseString = kindAsString.toUpperCase();
			kind = Kind.valueOf(kindAsUpperCaseString);
		} catch (IllegalArgumentException e) {
			throw new ServletException("Invalid kind parameter: " + kindAsString, e);
		} catch (NullPointerException e) {
			// throw new ServletException("Invalid kind parameter: " + kindAsString, e);
			// Kind is optional. E.g., if an active experiment exists.
			kind = null;
		}
		helper().setRequestAttribute(Attr.kind.nm(), kind);
		return kind;
	}

}
