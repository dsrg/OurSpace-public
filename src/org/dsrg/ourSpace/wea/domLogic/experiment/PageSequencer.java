package org.dsrg.ourSpace.wea.domLogic.experiment;

import org.dsrg.ourSpace.wea.domLogic.Page;
import org.dsrg.ourSpace.wea.domLogic.experiment.Experiment.Kind;

public class PageSequencer {

	private final Kind kind;

	public PageSequencer(Kind kind) {
		this.kind = kind;
	}

	public Page next(Page page) {
		if (kind == Kind.HCM)
			return page.next();

		// Kind is LCM:
		switch (page) {
		case NotLoggedIn:
		case Start:
		case TaskDescription:
			return page.next();

		case BasicInformation:
			return Page.MutualSupport;

		case MutualSupport:
			return Page.End;

		default:
			throw new IllegalStateException("Illegal page '" + page
					+ "' for kind '" + kind + "'");
		}
	}
}
