package org.dsrg.ourSpace.wea.techSvc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class TdgSupertypeForLongId extends TdgSupertypeWithId<Long> {

	@Override
	protected void setId(PreparedStatement ps, int i, Long id) throws SQLException {
		ps.setLong(i, id);
	}

}
