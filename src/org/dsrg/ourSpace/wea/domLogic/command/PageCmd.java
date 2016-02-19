package org.dsrg.ourSpace.wea.domLogic.command;

import org.dsrg.soenea.domain.command.CommandException;
import org.dsrg.soenea.domain.helper.IHelper;

/**
 * This command will direct the subject to the next page provided all form
 * information has been received and is valid.
 */
public class PageCmd extends AuthWithActiveExpCmdSupertype {

	public PageCmd(IHelper helper) throws CommandException {
		super(helper);
	}

	@Override
	public void process() throws CommandException {
		setExpAndSubjectAndPageRelatedReqAttrAndRegisterDirtyAsNeeded(role(),
				experiment());
	}

}
