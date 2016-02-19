package org.dsrg.ourSpace.wea.domLogic.command;

import org.dsrg.soenea.domain.command.CommandException;
import org.dsrg.soenea.domain.helper.IHelper;

public class DoNothingCmd extends DomainCommandSupertype {

	public DoNothingCmd(IHelper helper) {
		super(helper);
	}

	@Override
	public void process() throws CommandException {
	}

}
