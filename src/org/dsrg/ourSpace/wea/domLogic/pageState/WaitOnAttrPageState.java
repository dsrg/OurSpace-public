package org.dsrg.ourSpace.wea.domLogic.pageState;

import java.util.Collections;
import java.util.List;

import org.dsrg.ourSpace.wea.domLogic.Attr;
import org.dsrg.ourSpace.wea.domLogic.subject.ISubject;

public class WaitOnAttrPageState extends DefaultPageState {

	private List<Attr> attrs;

	// FIXME: temporary
	public WaitOnAttrPageState() {
		this.attrs = Collections.emptyList();
	}
	
	public WaitOnAttrPageState(List<Attr> attrs) {
		this.attrs = attrs;
	}
	
	@Override
	public boolean allPageAttrAreInit(ISubject subject) {
		for (Attr attr : attrs) {
			if(!subject.hasAttr(attr.nm())
					|| subject.get(attr.nm()).isEmpty())
				return false;
		}
		return true;
	}

}
