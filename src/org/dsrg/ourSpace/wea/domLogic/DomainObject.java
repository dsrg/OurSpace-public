package org.dsrg.ourSpace.wea.domLogic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dsrg.soenea.domain.DomainObjectLongId;

public class DomainObject extends DomainObjectLongId {

	@SuppressWarnings("null")
	protected static Logger logger = LogManager.getLogger();

	public DomainObject(Long id) {
		super(id);
	}

	protected DomainObject(long id, long version) {
		super(id, version);
	}

}
