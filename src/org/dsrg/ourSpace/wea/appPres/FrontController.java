package org.dsrg.ourSpace.wea.appPres;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dsrg.ourSpace.wea.appPres.dispatcher.DispatcherSuperType;
import org.dsrg.ourSpace.wea.domLogic.AppInit;
import org.dsrg.ourSpace.wea.domLogic.Page;
import org.dsrg.soenea.application.servlet.DispatcherServlet;
import org.dsrg.soenea.service.registry.MissingResourceException;
import org.dsrg.soenea.service.registry.Registry;
import org.dsrg.soenea.service.threadLocal.DbRegistry;
import org.jmlspecs.annotation.Nullable;

/**
 * Servlet implementation class FrontController
 */
@WebServlet("/app")
public class FrontController extends DispatcherServlet {

	/**
	 * Command request attribute name.
	 */
	private static final String COMMAND = "cmd";

	private static final long serialVersionUID = -3485656938402686371L;

	@SuppressWarnings("null")
	protected static Logger logger = LogManager.getLogger();

	@Override
	public void init(@Nullable ServletConfig config) throws ServletException {
		super.init(config); // ensure the application context gets initialized.
		try {
			AppInit.getInstance().init();
		} catch (SQLException e) {
			throw new ServletException(e);
		}
	}

	@Override
	protected void preProcessRequest(HttpServletRequest request,
			HttpServletResponse response) //
	{
		super.preProcessRequest(request, response);
		// A bug in SoenEA prevents us from setting errorJSP in init().
		this.errorJSP = "/WEB-INF/JSP/HTML/Error.jsp";
		try {
			AppInit.getInstance().createNewUowAndStartTransaction();
		} catch (SQLException e) {
			// FIXME: SoenEA should permit us to throw a ServletException:
			// throw new ServletException(e);
			logger.error(e);
		}
		try {
			String developerMode = Registry.getProperty("developerMode");
			request.setAttribute("developerMode", developerMode);
		} catch (MissingResourceException e) {
			// Nothing to do.
		}
	}

	@Override
	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		super.processRequest(request, response);
	}

	@Override
	protected void postProcessRequest(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			boolean success = tryToCommit() || tryToRollback();
			if (!success)
				logger.fatal("Both commit and rollback have failed.");
		} finally {
			closeDbConnectionIfNeeded();
		}
	}

	private boolean tryToCommit() {
		boolean success = false;
		try {
			DbRegistry.getDbConnection().createStatement().execute("COMMIT;");
			success = true;
		} catch (SQLException e) {
			logger.error(e);
		}
		return success;
	}

	private boolean tryToRollback() {
		boolean success = false;
		try {
			DbRegistry.getDbConnection().createStatement().execute("ROLLBACK;");
			success = true;
		} catch (SQLException e) {
			logger.error(e);
		}
		return success;
	}

	private void closeDbConnectionIfNeeded() {
		try {
			DbRegistry.closeDbConnectionIfNeeded();
		} catch (SQLException e) {
			logger.error(e);
		}
	}

	@Override
	protected String getDispatcherName(HttpServletRequest request)
			throws ServletException {
		String command = (String) request.getAttribute(COMMAND);
		String dispatcherName = // super.getDispatcherName(request);
		command != null ? command : request.getParameter(COMMAND);
		HttpSession session = request.getSession();
		if (dispatcherName == null || dispatcherName.isEmpty() || session.isNew())
			dispatcherName = Page.Login.nm();
		String packageName = DispatcherSuperType.class.getPackage().getName();
		String fullyQualifiedDispatcherName = packageName + "."
				+ dispatcherName + "Dispatcher";
		logger.info("fullyQualifiedDispatcherName = " + fullyQualifiedDispatcherName);
		return fullyQualifiedDispatcherName;
	}

}
