package org.dsrg.ourSpace.wea.domLogic.command;

import static com.google.common.base.Preconditions.checkState;
import static org.dsrg.util.Nulls.nonNull;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dsrg.ourSpace.wea.domLogic.Attr;
import org.dsrg.ourSpace.wea.domLogic.MessageConst;
import org.dsrg.ourSpace.wea.domLogic.Page;
import org.dsrg.ourSpace.wea.domLogic.Role;
import org.dsrg.ourSpace.wea.domLogic.experiment.Experiment;
import org.dsrg.ourSpace.wea.domLogic.experiment.ExperimentInputMapper;
import org.dsrg.ourSpace.wea.domLogic.experiment.PageSequencer;
import org.dsrg.ourSpace.wea.domLogic.subject.ISubject;
import org.dsrg.soenea.domain.MapperException;
import org.dsrg.soenea.domain.command.CommandError;
import org.dsrg.soenea.domain.command.CommandException;
import org.dsrg.soenea.domain.helper.IHelper;
import org.dsrg.soenea.domain.interf.IDomainObject;
import org.dsrg.soenea.uow.UoW;
import org.jmlspecs.annotation.NonNull;
import org.jmlspecs.annotation.Nullable;

public abstract class DomainCommandSupertype extends
		org.dsrg.soenea.domain.command.DomainCommand implements MessageConst {

	@SuppressWarnings("null")
	protected static Logger logger = LogManager.getLogger();

	public DomainCommandSupertype(IHelper helper) {
		super(helper);
	}

	/** Do nothing by default. */
	@Override
	public void setUp() throws CommandException {
		logger.info(this.getClass().getSimpleName() + " triggered");
	}

	/** Do nothing by default. */
	@Override
	public void tearDown() throws CommandError {
	}

	/**
	 * Does a call to checkForMandatorySessionAttr to get an experiment id. If
	 * successful, an attempt is made to fetch the experiment. If no such
	 * experiment is found, then an exception is thrown with message
	 * INVALID_EXPERIMENT_ID_MSG.
	 * 
	 * Not yet: If the experiment is finish then an exception is thrown with
	 * message EXPERIMENT_IS_ALREADY_FINISHED_MSG.
	 * 
	 * @throws CommandException
	 */
	protected Experiment checkForMandatoryExp() throws CommandException {
		Long experimentId = checkForMandatorySessionAttr(Long.class,
				Attr.experimentId);
		Experiment exp;
		try {
			exp = ExperimentInputMapper.it().find(experimentId);
			if (exp == null) {
				String msg = String.format(INVALID_EXPERIMENT_ID_MSG,
						experimentId);
				throw setErrorAttrAndCreateCmdException(nonNull(msg));
			}
			// checkExpIsNotFinished(exp);
		} catch (MapperException e) {
			throw new CommandException(e);
		}
		return exp;
	}

	protected void checkExpIsNotFinished(Experiment exp)
			throws CommandException {
		if (exp.isFinished()) {
			String msg = String.format(EXPERIMENT_IS_ALREADY_FINISHED_MSG,
					exp.getId());
			throw setErrorAttrAndCreateCmdException(nonNull(msg));
		}
	}

	protected <T> T checkForMandatoryReqAttr(Class<T> c, Attr a)
			throws CommandException //
	{
		Object value = getReqAttr(a);
		return checkForMandatoryAttr(c, "request", a, value);
	}

	protected <T> T checkForMandatorySessionAttr(Class<T> c, Attr a)
			throws CommandException //
	{
		Object value = getSessionAttr(a);
		return checkForMandatoryAttr(c, "session", a, value);
	}

	protected <T> T checkForMandatoryAttr(Class<T> c, String scopeName, Attr a,
			@Nullable Object value) throws CommandException //
	{
		if (value != null && c.isInstance(value)) {
			@SuppressWarnings("unchecked")
			T result = (T) value;
			return result;
		}
		String msg;
		if (value == null) {
			msg = String.format(MANDATORY_ATTRIBUTE_MISSING_MSG, scopeName, a);
		} else {
			msg = String.format(EXPECTED_ATTRIBUTE_TYPE_MSG, scopeName, a, c,
					value.getClass());
		}
		@SuppressWarnings("null")
		@NonNull
		String nnMsg = msg;
		throw setErrorAttrAndCreateCmdException(nnMsg);
	}

	protected CommandException setErrorAttrAndCreateCmdException(String msg) {
		setReqAttr(Attr.errorMessage, msg);
		setReqAttr(Attr.page, Page.Error);
		return new CommandException(msg);
	}

	protected @Nullable
	Object getReqAttr(Attr a) {
		return this.helper().getRequestAttribute(a.nm());
	}

	protected @Nullable
	Object getSessionAttr(Attr a) {
		return this.helper().getSessionAttribute(a.nm());
	}

	/** Sets the specified request attribute to the given value. */
	protected void setReqAttr(Attr a, Object value) {
		this.helper().setRequestAttribute(a.nm(), value);
	}

	/** Sets the specified request attribute to the given value. */
	protected void setSessionAttr(Attr a, @Nullable Object value) {
		this.helper().setSessionAttribute(a.nm(), value);
	}

	/** Sets the Attr.page request attribute to the given page. */
	protected void setPage(Page page) {
		setReqAttr(Attr.page, page);
	}

	/** Sets the Attr.page request attribute to the given page. */
	protected void setSubpages(Page... pages) {
		List<Page> value = Arrays.asList(pages);
		setReqAttr(Attr.subpages, nonNull(value));
	}

	/** @return the simple class name of this class. */
	protected String name() {
		return nonNull(this.getClass().getSimpleName());
	}

	protected void registerDirty(IDomainObject<Long> o) throws CommandException {
		try {
			UoW.getCurrent().registerDirty(o);
		} catch (MapperException e) {
			final String msg = name() + ": could not rgisterDirty " + o;
			throw new CommandException(msg, e);
		}

	}

	/**
	 * Set's the given subjects attributes based on the subject attributes
	 * present as parameter requests. An attribute is considered present when it
	 * is non-null and non-empty after trimming off whitespace.
	 * 
	 * @param subject
	 * @throws CommandException
	 */
	protected void setSubjectAttrFromReqParam(ISubject subject)
			throws CommandException {
		List<String> names = helper().getParameterNames();
		if (logger.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			for (String name : names) {
				String value = helper().getString(nonNull(name));
				sb.append(name + "='" + value + "', ");
			}
			logger.trace(name() + ": subject(id={}) param = [{}]", subject.getId(),
					sb.toString());
		}

		for (String _paramName : names) {
			String paramName = nonNull(_paramName);
			if (!Attr.isSubjecFormAttr(paramName))
				continue;
			String paramValue = helper().getString(paramName);
			paramValue = paramValue == null ? "" : paramValue.trim();
			if (paramValue.isEmpty()) {
				subject.remove(paramName);
			} else {
				subject.set(paramName, paramValue);
			}
			registerDirty(subject);
		}
	}

	protected void setExpAndSubjectAndPageRelatedReqAttrAndRegisterDirtyAsNeeded(
			final Role role, final Experiment exp) throws CommandException //
	{
		Page page;
		PageSequencer pageSequencer = exp.getPageSequencer();
		List<Page> subpages;
		final int i = role == Role.EXPERIMENTER ? 0 : role.subjectIndex();
		final ISubject subject = exp.getSubject(i);
		final ISubject otherSubject = exp.getOtherSubject(i);
		final Page currentSubpage = subject.getCurrentPage();
		final Page othersPage = otherSubject.getCurrentPage();
		final Page targetPageDirection = checkForMandatoryReqAttr(Page.class,
				Attr.gotoPage);
		logger.trace(
				name()
						+ ": {} current page = {}, other's page = {}, page direction = {}",
				role, currentSubpage, othersPage, targetPageDirection);
		checkState(currentSubpage.isSubjectSubpage());
		Page newSubpage = currentSubpage;
		Page newOthersPage = othersPage;
		Page newCommonPage = Page.Blank;

		if (role == Role.EXPERIMENTER) {
			page = Page.Experimenter;
			// Avoid DB update conflicts.
			// // Are the subjects waiting on the experimenter?
			// if (subject.hasAttr(Attr.subjectIsReadyAndWaiting.nm())
			// && otherSubject.hasAttr(Attr.subjectIsReadyAndWaiting.nm())
			// && currentSubpage == othersPage
			// // Also require plank durations
			// && subject.hasAttr(Attr.plankDurationAverage.nm())
			// && otherSubject.hasAttr(Attr.plankDurationAverage.nm())) {
			// newSubpage = newSubpage.next();
			// newOthersPage = newOthersPage.next();
			// // Update subject attr
			// subject.remove(Attr.subjectIsReadyAndWaiting.nm());
			// subject.setCurrentPage(newSubpage);
			// registerDirty(subject);
			// // Update other subject attr
			// otherSubject.remove(Attr.subjectIsReadyAndWaiting.nm());
			// otherSubject.setCurrentPage(newOthersPage);
			// registerDirty(otherSubject);
			// } /*
			// * else stay on same page
			// */
			if (newSubpage.hasCommonPart())
				newCommonPage = newSubpage;
			subpages = Arrays.asList(newSubpage, newCommonPage, newOthersPage);
		} else {
			page = Page.Subject;

			setSubjectAttrFromReqParam(subject);

			if (currentSubpage == Page.NotLoggedIn) {
				newSubpage = Page.Start;
			} else if (targetPageDirection == Page.PrevPage) {
				if (currentSubpage != Page.Start)
					newSubpage = currentSubpage.prev();
			} else if (currentSubpage == Page.End) {
				// Stay on this page because it is the last page.
			} else if (!currentSubpage.allPageAttrAreInit(subject)) {
				if (targetPageDirection == Page.NextPage) {
					// Stay on this page: user needs to provide more info.
					String msg = FILL_OUT_REQUIRED_FIELDS;
					helper().setRequestAttribute(Attr.alertMessage.nm(), msg);
					logger.debug(msg);
				}
			} else if (!pageSequencer.next(currentSubpage).subjectsNeedToBeOnSamePage()) {
				if (targetPageDirection == Page.NextPage) {
					newSubpage = pageSequencer.next(currentSubpage); // currentSubpage.next();
				} /*
				 * else Stay on this page because that is what is requested.
				 */
			} else

			// Beyond this point:
			// currentSubpage != Page.End
			// currentSubpage.allPageAttrAreInit(subject)
			// pageSequencer.next(currentSubpage).subjectsNeedToBeOnSamePage()

			if (othersPage.ordinal() < currentSubpage.ordinal()) {
				// Stay on this page: wait for other to catch up.
				if (targetPageDirection == Page.NextPage) {
					subject.set(Attr.subjectIsReadyAndWaiting.nm(), "true");
					registerDirty(subject);
				}
			} else if (othersPage.ordinal() > currentSubpage.ordinal()) {
				// Should never happen in the production system.
				if (targetPageDirection == Page.NextPage) {
					newSubpage = pageSequencer.next(currentSubpage); // currentSubpage.next();
				}
			} else

			// Beyond this point, in addition:
			// othersPage == currentSubpage

			if (!subject.hasAttr(Attr.subjectIsReadyAndWaiting.nm())) {
				if (targetPageDirection == Page.NextPage) {
					subject.set(Attr.subjectIsReadyAndWaiting.nm(), "true");
					registerDirty(subject);
					logger.debug("subject {} subjectIsReadyAndWaiting := true",
							subject.getId());
				}
			} else if (otherSubject.hasAttr(Attr.subjectIsReadyAndWaiting.nm())) {
				// Both subjects are waiting.
				if (role == Role.SUBJECT1) {
					// Allow them both to proceed even if the
					// targetPageDirection == Page.Page.
					newSubpage = pageSequencer.next(currentSubpage); // currentSubpage.next();
					newOthersPage = newSubpage;
				}
			} else {
				// subject: subjectIsReadyAndWaiting
				// otherSubject: !subjectIsReadyAndWaiting
				// so stay on this page.
				logger.debug("subject {} is waiting for other subject",
						subject.getId());
			}

			// switch (targetPageDirection) {
			// case NextPage:
			// if (!currentSubpage.allPageAttrAreInit(subject)) {
			// String msg = FILL_OUT_REQUIRED_FIELDS;
			// helper().setRequestAttribute(Attr.alertMessage.nm(),
			// msg);
			// } else if (currentSubpage == Page.MutualIntroductions
			// && othersPage.ordinal() < Page.MutualIntroductions
			// .ordinal()) {
			// String msg = PLEASE_WAIT_FOR_PARTNER;
			// helper().setRequestAttribute(Attr.alertMessage.nm(),
			// msg);
			// } else if (currentSubpage != Page.End) {
			// newSubpage = currentSubpage.next();
			// }
			// break;
			// case PrevPage:
			// if (currentSubpage != Page.Start) {
			// newSubpage = currentSubpage.prev();
			// }
			// break;
			// default:
			// /* stay on current page */
			// }
			// }

			final boolean allPageAttrAreInit = newSubpage
					.allPageAttrAreInit(subject);
			logger.trace(
					"{} {}: current page = {}, new page = {}, allPageAttrAreInit = {}",
					name(), role, currentSubpage, newSubpage,
					allPageAttrAreInit);

			if (!allPageAttrAreInit) {
				helper().setRequestAttribute(Attr.nextShouldBeDisabled.nm(),
						"true");
			}

			if (newSubpage != currentSubpage) {
				subject.setCurrentPage(newSubpage);
				subject.remove(Attr.subjectIsReadyAndWaiting.nm());
				registerDirty(subject);
				if (currentSubpage == Page.TeamInsignia) {
					final Attr teamInsignia = exp.getTeamInsignia();
					subject.set(Attr.teamInsignia.nm(), teamInsignia.nm());
				} else if (currentSubpage == Page.TeamName) {
					final String teamName = exp.getTeamName();
					subject.set(Attr.teamName.nm(), teamName);
				}
			}
			if (newOthersPage != othersPage) {
				otherSubject.setCurrentPage(newOthersPage);
				otherSubject.remove(Attr.subjectIsReadyAndWaiting.nm());
				registerDirty(otherSubject);
				if (othersPage == Page.TeamInsignia) {
					final Attr teamInsignia = exp.getTeamInsignia();
					otherSubject.set(Attr.teamInsignia.nm(), teamInsignia.nm());
				} else if (othersPage == Page.TeamName) {
					final String teamName = exp.getTeamName();
					otherSubject.set(Attr.teamName.nm(), teamName);
				}

			}

			if (newSubpage.hasCommonPart()) {
				// if (othersPage.ordinal() <
				// Page.MutualIntroductions.ordinal()) {
				// othersPage = Page.WaitForPartner;
				// }
				subpages = Arrays.asList(newSubpage, newSubpage, newOthersPage);
				logger.debug("subpages {}", subpages);
			} else {
				subpages = Arrays.asList(newSubpage);
			}
		}
		setReqAttr(Attr.showTeamInsignia, nonNull(subpages.get(0)
				.showTeamInsignia()));
		setReqAttr(Attr.showTeamName, nonNull(subpages.get(0).showTeamName()));
		setReqAttr(Attr.experiment, exp);
		setPage(page);
		setReqAttr(Attr.subpages, nonNull(subpages));
		setReqAttr(Attr.pageTitle, (subpages.size() == 1 ? subpages.get(0)
				: subpages.get(1)).title());
		setReqAttr(Attr.subject, subject);
		setReqAttr(Attr.otherSubject, otherSubject);
	}
}
