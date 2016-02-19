package org.dsrg.ourSpace.wea.appPres.dispatcher;

import javax.servlet.ServletException;

import org.dsrg.ourSpace.wea.domLogic.Attr;
import org.dsrg.ourSpace.wea.domLogic.Page;
import org.dsrg.ourSpace.wea.domLogic.command.PageCmd;
import org.dsrg.soenea.domain.command.CommandException;

/**
 * Generically handles "goto page" requests.
 * 
 * @author Patrice Chalin
 * 
 */
public class PageDispatcher extends DispatcherSuperType {

	@Override
	public void execute() throws ServletException {
		if (!requestMethodIsPost())
			return; // Response already generated.

		try {
			Page targetPage = getTargetPage();
			setRequestAttribute(Attr.gotoPage, targetPage);
			new PageCmd(this.helper()).execute();
			commit();
		} catch (CommandException e) {
			throw new ServletException(e);
		}
		forward();
	}

	private Page getTargetPage() {
		String cmd = helper().getString(Attr.cmd.nm());
		Page targetPage;
		if (Page.NextPage.name().equals(cmd)) {
			targetPage = Page.NextPage;
		} else if (Page.PrevPage.name().equals(cmd)) {
			targetPage = Page.PrevPage;
		} else {
			targetPage = Page.Page;
		}
		return targetPage;
	}

}
