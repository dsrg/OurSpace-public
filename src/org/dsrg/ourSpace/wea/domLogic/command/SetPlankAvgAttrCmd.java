package org.dsrg.ourSpace.wea.domLogic.command;

import static org.dsrg.util.Nulls.nonNull;

import org.dsrg.ourSpace.wea.domLogic.Attr;
import org.dsrg.ourSpace.wea.domLogic.Page;
import org.dsrg.ourSpace.wea.domLogic.Role;
import org.dsrg.ourSpace.wea.domLogic.experiment.IExperiment;
import org.dsrg.ourSpace.wea.domLogic.subject.ISubject;
import org.dsrg.soenea.domain.command.CommandException;
import org.dsrg.soenea.domain.helper.IHelper;

/**
 * Experimenter command.
 */
public class SetPlankAvgAttrCmd extends AuthWithActiveExpCmdSupertype {

	public SetPlankAvgAttrCmd(IHelper helper) throws CommandException {
		super(helper);
	}

	@Override
	public void process() throws CommandException {
		Role role = role();
		if (role != Role.EXPERIMENTER) {
			setSessionAttr(Attr.role, null); // logout.
			String msg = String.format(ROLE_HAS_INSUFFICIENT_PRIVILEDGES_MSG,
					role, this.getClass().getSimpleName());
			throw setErrorAttrAndCreateCmdException(nonNull(msg));
		}
		// float[] = checkForMandatoryReqAttr(Float.class, Attr.plankDurationAverage1);
		for (int i = 0; i < IExperiment.NUM_SUBJECTS; i++) {
			final Attr attr = i == 0 ? Attr.plankDurationAverage1
					: Attr.plankDurationAverage2;
			final String value = checkForMandatoryReqAttr(String.class, attr);
			ISubject subject = experiment().getSubject(i);
			// helper().setRequestAttribute(Attr.plankDurationAverage.nm(), value);
			// setSubjectAttrFromReqParam(subject);
			subject.set(Attr.plankDurationAverage.nm(), nonNull(value));
			registerDirty(subject);
		}
		setReqAttr(Attr.gotoPage, Page.Page);
		setExpAndSubjectAndPageRelatedReqAttrAndRegisterDirtyAsNeeded(role, experiment());
	}
}
