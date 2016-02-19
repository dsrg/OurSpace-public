package org.dsrg.ourSpace.wea.domLogic;

import static org.dsrg.util.Nulls.nonNull;

import java.util.EnumSet;
import java.util.List;
import java.util.NoSuchElementException;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;

/**
 * Enumeration of application attributes and parameters whose names are used as
 * keys to access values in helpers for any of the three contexts:
 * <ul>
 * <li>request,
 * <li>session,
 * <li>application
 * </ul>
 * 
 * @author Patrice Chalin
 */
public enum Attr {
	// Request parameters and attributes
	// httpStatus,
	// Application scope
	gameMgr,
	//
	chatText, chatHistory, errorMessage, experiment, experimentId, format, //
	otherSubject, page, password, role, kind, subject, subpages, userId, warningMessage,
	// Attributes from forms
	_startSubjectFormAttr,
	// Subject form attributes
	firstName, gender, avatarImage, teamInsignia, teamName, yearInSchool, //
	major, careerPlans, favoriteTvShows, somethingAboutYou, iReadPartnersInfo, //
	iStruggledWith, //
	howCanPartnerImprove, iReadPartnersSuggestion, //
	teamInsigniaList, teamNameList, // Comma (',') separated list.
	okWithTeamInsignia, okWithTeamName, suggestedTeamName, //
	myExpectedEffortLevel, okWithExpectedTeamEffort, //
	whoPerformedBetter, self, partner, both,
	// Experimenter form attribute
	plankDurationAverage, // not really a form attr, but used as if it were.
	plankDurationAverage1, plankDurationAverage2,
	//
	_endSubjectFormAttr,
	// -----
	// Values of attributes from forms
	//
	// {@link Attr#gender}
	Male, Female,
	// {@link Attr#yearInSchool}
	Freshman, Sophomore, Junior, Senior, Graduate, NotInSchool,
	// {@link Attr#teamInsignia}
	Apple, Cat, ChessPiece, Dog, FitGuy, Guitar, Horse, Plank, PowerCat, SmileyFace, Tree, Trophy,
	// {@link Attr#teamName}
	TheATeam("The \"A\" Team"), Wildcats, TeamAmerica("Team America"), ThePlanksters(
			"The Planksters"), YourSuggestedTeamName, PartnersSuggestedTeamName,
	// -----
	// Other
	//
	cmd, gotoPage, pageTitle, alertMessage,
	// Page status fields
	nextShouldBeDisabled, // "true" or not set
	subjectIsReadyAndWaiting, // "true" or not set.
	//
	showTeamInsignia, showTeamName;

	private String title;

	private Attr() {
		this("");
	}

	private Attr(String title) {
		this.title = title;
	}

	public String title() {
		return title.isEmpty() ? nonNull(name()) : title;
	}

	public boolean isSubjecFormAttr() {
		return _startSubjectFormAttr.ordinal() < this.ordinal()
				&& this.ordinal() < _endSubjectFormAttr.ordinal();
	}

	public static boolean isSubjecFormAttr(String s) {
		try {
			Attr a = valueOf(s);
			return a.isSubjecFormAttr();
		} catch (IllegalArgumentException e) {
			// fall through
		} catch (NullPointerException e) {
			// fall through
		}
		return false;
	}

	/**
	 * Convenience method just to get nullity checker to stop complaining that
	 * name() might return null.
	 * 
	 * @return this.name()
	 */
	public String nm() {
		return nonNull(name());
	}

	/**
	 * @return If this names an attribute whose values are a subrange of this
	 *         class, then return the first value in the subrange. Otherwise
	 *         throw an exception.
	 * @throws IllegalArgumentException.
	 */
	public Attr first() throws IllegalArgumentException {
		switch (this) {
		case teamInsignia:
			return Apple;
		case teamName:
			return TheATeam;
		default:
			throw new IllegalArgumentException(name());
		}
	}

	/**
	 * @return If this names an attribute whose values are a subrange of this
	 *         class, then return the first value in the subrange. Otherwise
	 *         throw an exception.
	 * @throws IllegalArgumentException.
	 */
	public Attr last() throws IllegalArgumentException {
		switch (this) {
		case teamInsignia:
			return Trophy;
		case teamName:
			return PartnersSuggestedTeamName;
		default:
			throw new IllegalArgumentException(name());
		}
	}

	/**
	 * @return If this names an attribute whose values are a subrange of this
	 *         class, then return true iff <code>a</code> is contained in this
	 *         subrange.
	 * @throws IllegalArgumentException.
	 */
	public boolean contains(Attr a) throws IllegalArgumentException {
		Attr first = this.first();
		Attr last = this.last();
		return first.ordinal() <= a.ordinal() && a.ordinal() <= last.ordinal();
	}

	public static Optional<Attr> valueOpt(String s) {
		try {
			return nonNull(Optional.of(Attr.valueOf(s)));
		} catch (IllegalArgumentException e) {
			return nonNull(Optional.<Attr> absent());
		}
	}

	public Attr next() throws NoSuchElementException {
		final int i = this.ordinal() + 1;
		if (i >= values().length)
			throw new NoSuchElementException(this.toString());
		Attr result = values()[i];
		return nonNull(result);
	}

	public List<Attr> subrange() throws IllegalArgumentException {
		return nonNull(FluentIterable.from(
				EnumSet.range(this.first(), this.last())).toList());
	}
}
