package org.dsrg.ourSpace.wea.domLogic.command;

import org.dsrg.soenea.domain.command.CommandException;
import org.dsrg.soenea.domain.helper.IHelper;

public class ThrowExceptionCmd extends DomainCommandSupertype {

	public ThrowExceptionCmd(IHelper helper) {
		super(helper);
	}

	@Override
	public void process() throws CommandException {
		throw new CommandException(this.getClass().getSimpleName()
				+ " should not be invoked");
	}
}
