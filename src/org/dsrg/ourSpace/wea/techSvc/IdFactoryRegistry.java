package org.dsrg.ourSpace.wea.techSvc;

import org.dsrg.soenea.service.SingleAppUniqueIdFactory;
import org.dsrg.soenea.service.UniqueIdFactory;
import org.jmlspecs.annotation.Nullable;

public class IdFactoryRegistry {

	private static @Nullable
	UniqueIdFactory idFactory = null;

	public static synchronized UniqueIdFactory getIdFactory() {
		UniqueIdFactory theOne = idFactory;
		if (theOne == null) {
			theOne = new SingleAppUniqueIdFactory();
			idFactory = theOne;
		}
		return theOne;
	}

	private IdFactoryRegistry() {
	}
}
