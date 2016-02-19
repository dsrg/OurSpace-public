package org.dsrg.ourSpace.wea.domLogic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.dsrg.soenea.domain.DomainObject;
import org.dsrg.soenea.domain.MapperException;
import org.dsrg.soenea.domain.ObjectRemovedException;
import org.dsrg.soenea.domain.mapper.DomainObjectNotFoundException;
import org.dsrg.soenea.domain.mapper.IdentityMap;
import org.dsrg.soenea.domain.mapper.LostUpdateException;
import org.jmlspecs.annotation.Nullable;

public abstract class InputMapperSupertype<IdField, DO extends DomainObject<IdField>> {

	private Class<DO> DO_class;

	protected InputMapperSupertype(Class<DO> clazz) {
		this.DO_class = clazz;
	}

	public @Nullable
	DO find(IdField id) throws MapperException {
		DO result = identityMapGet(id);
		if (result != null)
			return result;
		try {
			ResultSet rs = this.tdgFind(id);
			if (rs.next())
				return makeDO(rs);
		} catch (SQLException e) {
			throw new MapperException(e);
		}
		return result;
	}

	public @Nullable
	DO find(IdField id, long version) throws MapperException {
		DO result = find(id);
		if (result != null && result.getVersion() != version) {
			String warning = this.DO_class.getSimpleName() + " '" + result.getId()
					+ "' data is out-of-date: expected version " + version
					+ " but database record is at version "
					+ result.getVersion();
			throw new LostUpdateException(warning);
		}
		return result;
	}

	public List<DO> findAll() throws MapperException {
		List<DO> result = new ArrayList<DO>();
		try {
			ResultSet rs = tdgFindAll();
			while (rs.next()) {
				try {
					DO subject = getOrMakeDO(rs);
					result.add(subject);
				} catch (ObjectRemovedException e) {
					// Since object has been removed,
					// do not add it to list.
					// Hence, nothing more to do.
				}
			}
		} catch (SQLException e) {
			throw new MapperException(e);
		}
		return result;
	}

	protected DO getOrMakeDO(ResultSet rs) throws MapperException,
			ObjectRemovedException {
		try {
			IdField id = getId(rs);
			DO result = identityMapGet(id);
			return result == null ? makeDO(rs) : result;
		} catch (SQLException e) {
			throw new MapperException(e);
		}
	}

	protected IdField getId(ResultSet rs) throws SQLException, MapperException {
		Object o = rs.getObject("id");
		if (o == null)
			throw new MapperException("id field is null");
		@SuppressWarnings("unchecked")
		IdField id = (IdField) o;
		return id;
	}

	protected abstract DO makeDO(ResultSet rs) throws SQLException, MapperException;

	protected @Nullable
	DO identityMapGet(IdField id) throws ObjectRemovedException {
		try {
			return IdentityMap.get(id, DO_class);
		} catch (DomainObjectNotFoundException e) {
			return null;
		}
	}

	protected abstract ResultSet tdgFind(IdField id) throws SQLException;

	protected abstract ResultSet tdgFindAll() throws SQLException;

}
