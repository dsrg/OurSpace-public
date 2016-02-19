package org.dsrg.ourSpace.wea.domLogic.experiment;

import java.util.Date;
import java.util.List;

import org.dsrg.ourSpace.wea.domLogic.Attr;
import org.dsrg.ourSpace.wea.domLogic.subject.ISubject;
import org.dsrg.soenea.domain.interf.IDomainObject;

public interface IExperiment extends IDomainObject<Long> {

	public static final int NUM_SUBJECTS = 2;

	public abstract Date getStart();

	public boolean isFinished();

	public abstract Date getFinish();

	/**
	 * @requires 0 <= i && i < NUM_SUBJECTS;
	 * @return getSubjects(i).
	 */
	public abstract ISubject getSubject(int i);

	/**
	 * @requires 0 <= i && i < NUM_SUBJECTS;
	 * @return the subject other than getSubject(i).
	 */
	public abstract ISubject getOtherSubject(int i);

	public abstract List<ISubject> getSubjects();

	public abstract void setStart(Date start);

	public abstract void setFinish(Date finish);

	/**
	 * This is a convenience method intended for use only during the time that
	 * the team insignia is being chosen by the subjects. (Once the team
	 * insignia has been agreed upon, it will be set as a
	 * {@code Attr.teamInsignia} attribute of each subject.)
	 * 
	 * @return <ul>
	 *         <li>The highest-scoring team insignia based on the subjects'
	 *         ordered {@code Attr.teamInsigniaList}s if they both exist.
	 *         <li>Otherwise return {@code Attr.teamInsignia.first()}
	 *         </ul>
	 */
	public abstract Attr getTeamInsignia();

	/**
	 * @return the highest-scoring {@code Attr.teamName} based on the subject's
	 *         ordered {@code Attr.teamNameList}s if they exist. Otherwise
	 *         return {@code Attr.teamName.first()}. Note that a subject's (or
	 *         his/her partner's) {@code Attr.suggestedTeamName} is returned
	 *         when {@code Attr.YourSuggestedTeamName} (or, respectively,
	 *         {@code Attr.PartnersSuggestedTeamName}) has the top rating.
	 */
	public abstract String getTeamName();

	/**
	 * @return The average each subject's {@code Attr.myExpectedEffortLevel}.
	 */
	public abstract float getTeamNorm();

}