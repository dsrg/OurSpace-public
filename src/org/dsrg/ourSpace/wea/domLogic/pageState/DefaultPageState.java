package org.dsrg.ourSpace.wea.domLogic.pageState;

import org.dsrg.ourSpace.wea.domLogic.subject.ISubject;

public class DefaultPageState extends PageState {

	@Override
	public boolean askUserToWait() {
		return false;
	}

	@Override
	public boolean allPageAttrAreInit(ISubject subject) {
		return true;
	}

}
