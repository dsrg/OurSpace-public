package org.dsrg.ourSpace.wea.domLogic;

import static com.google.common.base.Preconditions.checkState;

public enum Role {
	EXPERIMENTER, SUBJECT1, SUBJECT2;

	public boolean isSubject() {
		return this == SUBJECT1 || this == SUBJECT2;
	}

	/** @requires isSubject(). */
	public int subjectIndex() {
		checkState(this != EXPERIMENTER);
		return this == SUBJECT1 ? 0 : 1;
	}
}
