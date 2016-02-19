package org.dsrg.ourSpace.wea.domLogic.pageState;

import org.dsrg.ourSpace.wea.domLogic.subject.ISubject;

public abstract class PageState {

	public abstract boolean askUserToWait();

	public abstract boolean allPageAttrAreInit(ISubject subject);
	
}
