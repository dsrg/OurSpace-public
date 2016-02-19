package org.dsrg.ourSpace.wea.domLogic.command;

import static org.dsrg.util.Nulls.nonNull;

import org.dsrg.ourSpace.wea.domLogic.Attr;
import org.dsrg.ourSpace.wea.domLogic.Page;
import org.dsrg.soenea.domain.command.CommandException;
import org.dsrg.soenea.domain.helper.IHelper;

/**
 * A supertype for domain commands that require an authenticated user whose
 * associated experiment id refers to an active experiment. If the id is of a
 * finished experiment, then set the logical page to <code>Page.Login</code> and
 * return.
 * 
 * @author Patrice Chalin
 * 
 */
public abstract class AuthWithActiveExpCmdSupertype extends
		AuthenticatedDomCmdSupertype {

	public AuthWithActiveExpCmdSupertype(IHelper helper)
			throws CommandException {
		super(helper);
	}

	/**
	 * If <code>experiment()</code> is finished, then set the logical page to
	 * <code>Page.Login</code> and return. Otherwise, proceed with command
	 * execution as usual.
	 */
	@Override
	public void execute() throws CommandException {
		if (experiment().isFinished()) {
			setSessionAttr(Attr.experimentId, null);
			String msg = String.format(EXPERIMENT_IS_ALREADY_FINISHED_MSG,
					experiment().getId());
			logger.warn(msg);
			setReqAttr(Attr.warningMessage, nonNull(msg));
			setPage(Page.Login);
			return;
		}
		super.execute();
	}

}
