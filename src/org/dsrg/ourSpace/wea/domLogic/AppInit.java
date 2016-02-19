package org.dsrg.ourSpace.wea.domLogic;

import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dsrg.ourSpace.wea.domLogic.experiment.Experiment;
import org.dsrg.ourSpace.wea.domLogic.experiment.ExperimentOutputMapper;
import org.dsrg.ourSpace.wea.domLogic.subject.Subject;
import org.dsrg.ourSpace.wea.domLogic.subject.SubjectOutputMapper;
import org.dsrg.soenea.service.MySQLConnectionFactory;
import org.dsrg.soenea.service.registry.Registry;
import org.dsrg.soenea.service.threadLocal.DbRegistry;
import org.dsrg.soenea.uow.MapperFactory;
import org.dsrg.soenea.uow.UoW;

public class AppInit {
	
	@SuppressWarnings("null")
	protected static Logger logger = LogManager.getLogger();

	private boolean initialized = false;

	private static class SingletonHolder {
		static final AppInit INSTANCE = new AppInit();
	}
	public static AppInit getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public synchronized void init() throws SQLException {
		if (!initialized) {
			prepareDbRegistry("");
			setupUoW();
			initialized = true;
		}
	}

	private void prepareDbRegistry(String db_id) throws SQLException {
		MySQLConnectionFactory f = new MySQLConnectionFactory(null, null, null,
				null);
		try {
			f.defaultInitialization(db_id);
		} catch (SQLException e) {
			logger.fatal("AppInit.prepareDbRegistry", e);
			e.printStackTrace();
		}
		DbRegistry.setConFactory(db_id, f);
		String tablePrefix;
		try {
			tablePrefix = Registry.getProperty(db_id + "mySqlTablePrefix");
		} catch (Exception e1) {
			e1.printStackTrace();
			tablePrefix = "";
		}
		if (tablePrefix == null) {
			tablePrefix = "";
		}
		DbRegistry.setTablePrefix(db_id, tablePrefix);
		// SoenEAConnection con = DbRegistry.getDbConnection();
		// con.setAutoCommit(false);
		// con.close();
	}

	private void setupUoW() {
		MapperFactory myDomain2MapperMapper = new MapperFactory();
		myDomain2MapperMapper.addMapping(Experiment.class,
				ExperimentOutputMapper.class);
		myDomain2MapperMapper.addMapping(Subject.class,
				SubjectOutputMapper.class);
		UoW.initMapperFactory(myDomain2MapperMapper);
	}

	public void createNewUowAndStartTransaction() throws SQLException {
		UoW.newCurrent();
		DbRegistry.getDbConnection().setAutoCommit(false);
		DbRegistry.getDbConnection().createStatement()
				.execute("START TRANSACTION;");
	}
}
