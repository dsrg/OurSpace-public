package org.dsrg.ourSpace.wea.domLogic.command;

import org.dsrg.ourSpace.wea.domLogic.Attr;
import org.dsrg.ourSpace.wea.domLogic.Page;
import org.dsrg.ourSpace.wea.domLogic.Role;
import org.dsrg.ourSpace.wea.domLogic.subject.ISubject;
import org.dsrg.soenea.domain.command.CommandException;
import org.dsrg.soenea.domain.helper.IHelper;

/**
 * 
 */
public class SetSubjectAttrCmd extends AuthWithActiveExpCmdSupertype {

	public SetSubjectAttrCmd(IHelper helper) throws CommandException {
		super(helper);
	}

	@Override
	public void process() throws CommandException {
		Role roleInSession = role();
		Role role = roleInSession;
		if(role == Role.EXPERIMENTER) {
			// Get role from request.
			role = checkForMandatoryReqAttr(Role.class, Attr.role);
			if(!role.isSubject()) {
				String msg = this.getClass().getSimpleName() + ": ";
				msg += String.format(INVALID_ATTRIBUTE_VALUE_MSG, "request", Attr.role, role);
				msg += "; must be a subject role";
				logger.error(msg);
				throw new CommandException(msg);
			}
		}
		ISubject subject = experiment().getSubject(role.subjectIndex());
		
		final String chatText = helper().getString(Attr.chatText.nm());
		if(chatText != null) {
			subject.addChatLine(roleInSession, chatText);
			registerDirty(subject);
		}
		setSubjectAttrFromReqParam(subject);
		setPage(Page.Blank);
	}
}
