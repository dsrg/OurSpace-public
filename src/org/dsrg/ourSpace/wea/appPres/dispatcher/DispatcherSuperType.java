package org.dsrg.ourSpace.wea.appPres.dispatcher;

import static org.dsrg.util.Nulls.nonNull;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dsrg.ourSpace.wea.domLogic.Attr;
import org.dsrg.ourSpace.wea.domLogic.Page;
import org.dsrg.ourSpace.wea.domLogic.Role;
import org.dsrg.soenea.application.servlet.dispatcher.Dispatcher;
import org.dsrg.soenea.domain.MapperException;
import org.dsrg.soenea.domain.mapper.LostUpdateException;
import org.dsrg.soenea.uow.UoW;
import org.jmlspecs.annotation.NonNull;

public abstract class DispatcherSuperType extends Dispatcher {

	@SuppressWarnings("null")
	protected static Logger logger = LogManager.getLogger();

	/**
	 * Forwards to "/WEB-INF/JSP/" + getFormat() + "/" + target + ".jsp".
	 */
	protected void forward(Page target) throws ServletException {
		forward2JSP(target.nm());
	}

	private void forward2JSP(String target) throws ServletException {
		String fullyQualifiedTarget = "/WEB-INF/JSP/" + getFormat() + "/"
				+ target + ".jsp";
		logger.info("forwarding to " + fullyQualifiedTarget);
		try {
			super.forward(fullyQualifiedTarget);
		} catch (IOException e) {
			throw new ServletException(e);
		}
	}

	@SuppressWarnings("null")
	protected String getFormat() {
		final String format = helper().getString(Attr.format.nm());
		return format == null ? "HTML" : format.toUpperCase();
	}

	protected Long getParamAndSetAsLongAttr(Attr a) throws ServletException {
		String name = a.nm();
		try {
			String paramAsString = this.getRequest().getParameter(name);
			if (paramAsString == null) {
				Object attribute = this.helper().getAttribute(name);
				if (attribute instanceof Long) {
					return (Long) attribute;
				} else {
					// Attribute should be a String.
					paramAsString = (String) attribute;
				}
			}
			long param = Long.parseLong(paramAsString);
			this.helper().setRequestAttribute(name, param);
			return param;
		} catch (NumberFormatException e) {
			throw new ServletException("invalid parameter name: " + name, e);
		}
	}

	/**
	 * Redirects to: <context-path><servlet-path>/p
	 * 
	 * @param p
	 *            the last part of the path to be redirected to.
	 * @throws IOException
	 */
	protected void relativeRedirect(String p) throws IOException {
		String path = this.getRequest().getContextPath()
				+ this.getRequest().getServletPath() + "/" + p;
		getResponse().sendRedirect(path);
	}

	/**
	 * Ensures that POST method was used to invoke this dispatcher. If POST was
	 * not used, send an error response.
	 * 
	 * @return true iff POST was not used as a method to invoke this dispatcher.
	 * @throws ServletException
	 */
	protected boolean requestMethodIsPost() throws ServletException {
		String requestMethod = this.getRequest().getMethod();
		if ("POST".equals(requestMethod))
			return true;
		try {
			getResponse().sendError(
					HttpServletResponse.SC_FORBIDDEN,
					"Only method POST can be used for this action, not "
							+ requestMethod);
		} catch (IOException e) {
			throw new ServletException(e);
		}
		getResponse().addHeader("Allow", "POST");
		return false;
	}

	protected String getAndSetAsAttr(Attr name) throws ServletException {
		String nameAsString = name.nm();
		String parameterValue = this.getRequest().getParameter(nameAsString);
		if (parameterValue == null) {
			throw new ServletException("non-null parameter is null: " + name);
		}
		this.getRequest().setAttribute(nameAsString, parameterValue);
		return parameterValue;
	}

	protected Role getRoleAndSetAsAttr() throws ServletException {
		String roleAsString = this.getRequest().getParameter(Attr.role.nm());
		Role role;
		try {
			@SuppressWarnings("null")
			@NonNull
			String roleAsUpperCaseString = roleAsString.toUpperCase();
			role = Role.valueOf(roleAsUpperCaseString);
		} catch (IllegalArgumentException e) {
			throw new ServletException("Invalid role parameter", e);
		} catch (NullPointerException e) {
			throw new ServletException("Invalid role parameter", e);
		}
		helper().setRequestAttribute(Attr.role.nm(), role);
		return role;
	}

	protected void setRequestAttribute(Attr a, Object value)
			throws ServletException {
		this.helper().setRequestAttribute(a.nm(), value);
	}

	protected void commit() throws ServletException {
		Exception ex = null;
		try {
			logger.trace("[{}] DispatcherSuperType.commit(): start {}", Thread
					.currentThread().getId(), this);
			UoW.getCurrent().commit();
			logger.trace("[{}] DispatcherSuperType.commit(): end {}", Thread
					.currentThread().getId(), this);
			return;
		} catch (InstantiationException e) {
			ex = e;
		} catch (IllegalAccessException e) {
			ex = e;
		} catch (MapperException e) {
			ex = e;
		} catch (SQLException e) {
			ex = e;
		}
		logger.error("[{}] DispatcherSuperType.commit(): exception {} {}",
				Thread.currentThread().getId(), ex, this);
		if (ex instanceof LostUpdateException) {
			logger.error(
					"[{}] DispatcherSuperType.commit(): setting HTTP response status to {}",
					Thread.currentThread().getId(),
					HttpServletResponse.SC_CONFLICT);
			getResponse().setStatus(HttpServletResponse.SC_CONFLICT);
		}
		throw new ServletException(ex);
	}

	protected Page getPage() {
		final Object result = this.helper().getRequestAttribute(Attr.page.nm());
		com.google.common.base.Preconditions.checkState(result != null);
		return (Page) result;
	}

	protected void forward() throws ServletException {
		this.forward(getPage());
	}

	protected String reqParamToString() {
		List<String> names = helper().getParameterNames();
		if (logger.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			for (String name : names) {
				String value = helper().getString(nonNull(name));
				sb.append(name + "='" + value + "', ");
			}
			return nonNull(sb.toString());
		}
		return "";
	}

}
