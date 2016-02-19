package org.dsrg.ourSpace.wea.domLogic;

import static org.dsrg.util.Nulls.nonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dsrg.ourSpace.wea.domLogic.pageState.DefaultPageState;
import org.dsrg.ourSpace.wea.domLogic.pageState.PageState;
import org.dsrg.ourSpace.wea.domLogic.pageState.WaitOnAttrPageState;
import org.dsrg.ourSpace.wea.domLogic.subject.ISubject;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

/**
 * Enumeration of logical application pages and commands.
 * 
 * @author Patrice Chalin
 */
public enum Page {
	Login,
	// Experimenter page
	Experimenter,
	// Subject pages
	Subject, // main page, rest are page parts
	NotLoggedIn, // 0
	Start, // 1
	TaskDescription, // 2
	BasicInformation(nonNull(ImmutableList.of(Attr.firstName, Attr.gender))), // 3
	Avatar(nonNull(ImmutableList.of(Attr.avatarImage))), // (20), // 4
	// 2015.03.22 removing next page.
	//MutualIntroductions(nonNull(ImmutableList.of(Attr.yearInSchool, Attr.major,
	//		Attr.careerPlans, Attr.favoriteTvShows, Attr.somethingAboutYou,
	//		Attr.iReadPartnersInfo))),
	// (60), 5
	MutualSupport(nonNull(ImmutableList.of(Attr.iStruggledWith,
			Attr.howCanPartnerImprove, Attr.iReadPartnersSuggestion))),
	// (60), 6
	TeamInsignia(nonNull(ImmutableList.of(Attr.okWithTeamInsignia))),
	// (45), 7
	TeamName(nonNull(ImmutableList.of(Attr.okWithTeamName))),
	// (45), 8
	Teamwork, // (90), // 9
	// Special note: transition to team norm will only be enabled if
	// plankDurationAverage values have been provided by the experimenter.
	TeamNorm(nonNull(ImmutableList.of(Attr.myExpectedEffortLevel, Attr.okWithExpectedTeamEffort,
			Attr.plankDurationAverage))), // (45), 10
	// 2015.03.22 removing next page.
	// IndividualPositions(nonNull(ImmutableList.of(Attr.whoPerformedBetter))), // (20), 11
	End, // (0); // 12
	// Other pages
	Blank, Error,
	// ================================
	// App/Pres Commands
	Authenticate, CancelOrEndExperiment, NextPage, PrevPage, Page, // current
																	// page.
	SetPlankAverages;

	private final PageState pageState;

	private Page() {
		this.pageState = new DefaultPageState();
	}

	private Page(List<Attr> attrs) {
		this.pageState = new WaitOnAttrPageState(attrs);
	}

	public boolean hasNext() {
		return this.ordinal() + 1 < values().length;
	}

	public boolean hasPrev() {
		return 0 <= this.ordinal() - 1;
	}

	public boolean subjectsNeedToBeOnSamePage() {
		return firstPageWithCommonPart().ordinal() <= this.ordinal()
				&& this.ordinal() <= End.ordinal();
	}

	public Page next() {
		final int i = this.ordinal() + 1;
		if (i >= values().length)
			throw new NoSuchElementException(this.toString());
		Page result = values()[i];
		return nonNull(result);
	}

	public Page prev() {
		final int i = this.ordinal() - 1;
		if (i < 0)
			throw new NoSuchElementException(this.toString());
		Page result = values()[i];
		return nonNull(result);
	}

	public boolean isSubjectSubpage() {
		return NotLoggedIn.ordinal() <= this.ordinal()
				&& this.ordinal() <= Blank.ordinal();
	}

	public static Page firstPageWithCommonPart() {
		// 2015.03.22 would have been MutualIntroductions
		return MutualSupport;
	}
	
	public static Page lastPageWithCommonPart() {
		// 2015.03.22 would have been IndividualPositions
		return TeamNorm;
	}
	
	public boolean hasCommonPart() {
		return firstPageWithCommonPart().ordinal() <= this.ordinal()
				&& this.ordinal() <= lastPageWithCommonPart().ordinal()
				// FIXME: temporary
				&& this != End;
	}

	public boolean showTeamInsignia() {
		return TeamName.ordinal() <= this.ordinal()
				&& this.ordinal() < End.ordinal();
	}

	public boolean showTeamName() {
		return TeamName.ordinal() < this.ordinal()
				&& this.ordinal() < End.ordinal();
	}

	public boolean askUserToWait() {
		return this.pageState.askUserToWait();
	}

	public boolean allPageAttrAreInit(ISubject subject) {
		return this.pageState.allPageAttrAreInit(subject);
	}

	/**
	 * @return this page's name as a title (placing spaces between words); e.g.
	 *         AbcPageXyz would be returned as title "Abc Page Xyz".
	 */
	public String title() {
		if(this == Experimenter)
			return this + " Console";
		Pattern p = Pattern.compile("[A-Z][a-z]+");
		Matcher m = p.matcher(name());
		List<String> list = new ArrayList<String>(4);
		while (m.find()) {
			list.add(m.group());
		}
		return nonNull(Joiner.on(" ").join(list));
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

	public int duration() {
		Integer result = durationMap.get(this);
		return result == null ? 0 : result;
	}

	private static Map<Page, Integer> durationMap = new HashMap<Page, Integer>();

	// It is likely that durations will be subject to
	// tuning; and should be changeable without recompiling.
	static {
		durationMap.put(TaskDescription, 25);
		durationMap.put(BasicInformation, 20);
		durationMap.put(Avatar, 20);
		// 2015.03.22 disable
		// durationMap.put(MutualIntroductions, 60);
		durationMap.put(MutualSupport, 60);
		durationMap.put(TeamInsignia, 45);
		durationMap.put(TeamName, 45);
		durationMap.put(Teamwork, 90);
		// 2015.03.22 disable
		// durationMap.put(IndividualPositions, 20);
	}

}
