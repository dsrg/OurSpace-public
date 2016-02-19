package org.dsrg.ourSpace.wea.domLogic.command;

import org.dsrg.ourSpace.wea.domLogic.Attr;
import org.dsrg.ourSpace.wea.domLogic.Role;
import org.dsrg.ourSpace.wea.domLogic.experiment.Experiment;
import org.dsrg.soenea.domain.command.CommandException;
import org.dsrg.soenea.domain.helper.IHelper;

/**
 * Supertype for domain commands whose execution only make sense if the user is
 * authenticated and the session has all mandatory session attributes, namely:
 * <ul>
 * <li>role of the user
 * <li>experiment id of a valid experiment; note that the experiment with this
 * id is loaded and made available.
 * </ul>
 * 
 * @author Patrice Chalin
 * 
 */
public abstract class AuthenticatedDomCmdSupertype extends
		DomainCommandSupertype {

	private final Role role;
	private final Experiment experiment;

	/**
	 * Will attempt to retrieve the role and experiment.
	 * 
	 * @param helper
	 * @throws CommandException
	 *             if role or experiment id were absent from session, or if
	 *             corresponding experiment could not be retrieved.
	 */
	public AuthenticatedDomCmdSupertype(IHelper helper) throws CommandException {
		super(helper);
		role = checkForMandatorySessionAttr(Role.class, Attr.role);
		experiment = checkForMandatoryExp();
		logger.debug(name() + ": exp = {}", experiment);
	}

	@Override
	public void process() throws CommandException {
		// TODO Auto-generated method stub

	}

	/**
	 * @return experiment corresponding to experiment id retrieved from session.
	 */
	public Experiment experiment() {
		return experiment;
	}

	/**
	 * @return role stored in the session.
	 */
	public Role role() {
		return role;
	}

}
