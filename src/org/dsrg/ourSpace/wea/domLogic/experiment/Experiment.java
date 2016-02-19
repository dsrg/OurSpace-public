package org.dsrg.ourSpace.wea.domLogic.experiment;

import static com.google.common.base.Preconditions.checkArgument;
import static org.dsrg.util.Nulls.nonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dsrg.ourSpace.wea.domLogic.Attr;
import org.dsrg.ourSpace.wea.domLogic.DateFactory;
import org.dsrg.ourSpace.wea.domLogic.DomainObject;
import org.dsrg.ourSpace.wea.domLogic.subject.ISubject;
import org.dsrg.ourSpace.wea.domLogic.subject.Subject;
import org.jmlspecs.annotation.NonNull;
import org.jmlspecs.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

public class Experiment extends DomainObject implements IExperiment {

	public static final List<Subject> NULL_SUBJECTS = nonNull(Collections
			.unmodifiableList(Arrays.asList(Subject.NULL_SUBJECT,
					Subject.NULL_SUBJECT)));
	public static final Experiment NULL_EXPERIMENT = new Experiment(NULL_ID,
			INITIAL_VERSION, Kind.HCM, DateFactory.newDate(0), null, NULL_SUBJECTS);

	public enum Kind {
		HCM, // High cohesion condition
		LCM; // Low cohesion condition
	}
	
	private Kind kind; // module kind: one of HCM or LCM.
	private Date start;
	private @Nullable
	Date finish;
	private final List<ISubject> subjects;
	private final PageSequencer pageSequencer;

	protected Experiment(long id, long version, Kind kind, Date start,
			@Nullable Date finish, List<? extends ISubject> subjects) {
		super(id, version);
		checkArgument(subjects.size() == NUM_SUBJECTS);

		this.kind = kind;
		this.start = start;
		this.finish = finish;
		this.subjects = new ArrayList<ISubject>(2);
		this.subjects.addAll(subjects);
		this.pageSequencer = new PageSequencer(kind);
	}

	public Kind getKind() {
		return kind;
	}

	@Override
	public Date getStart() {
		return start;
	}

	@Override
	public Date getFinish() {
		Date result = this.finish;
		return result == null ? this.start : result;
	}

	@Override
	public ISubject getSubject(int i) {
		checkArgument(0 <= i && i < NUM_SUBJECTS);
		return nonNull(getSubjects().get(i));
	}

	@Override
	public ISubject getOtherSubject(int i) {
		checkArgument(0 <= i && i < NUM_SUBJECTS);
		return nonNull(getSubjects().get((i + 1) % NUM_SUBJECTS));
	}

	@Override
	public List<ISubject> getSubjects() {
		return subjects;
	}

	/**
	 * @see {@link IExperiment#getTeamInsignia()}.
	 */
	@Override
	public Attr getTeamInsignia() {
		return getTeamChoice(Attr.teamInsignia);
	}

	/**
	 * @see {@link IExperiment#getTeamName()}.
	 */
	@Override
	public String getTeamName() {
		final Attr a = getTeamChoice(Attr.teamName);
		return a == Attr.YourSuggestedTeamName ? subjects.get(0).get(
				Attr.suggestedTeamName.nm())
				: a == Attr.PartnersSuggestedTeamName ? subjects.get(1).get(
						Attr.suggestedTeamName.nm()) : a.title();
	}

	protected Attr getTeamChoice(final Attr teamAttr)
			throws IllegalArgumentException {
		final Attr teamListAttr = teamAttr == Attr.teamInsignia ? Attr.teamInsigniaList
				: Attr.teamNameList;

		final Attr defaultResult = teamAttr.first();
		for (ISubject subject : subjects)
			if (!subject.hasAttr(teamListAttr.nm()))
				return defaultResult;

		final Map<Attr, Integer> weights = new HashMap<Attr, Integer>();
		final int numTI = teamAttr.subrange().size();

		final List<Attr> list1 = subjects.get(0).getTeamAttrList(teamAttr);
		logger.debug("getTeamChoice: list1 {}", list1);
		adjustTeamChoiceWeights(weights, numTI, list1);
		List<Attr> list2 = subjects.get(1).getTeamAttrList(teamAttr);
		logger.debug("getTeamChoice: list2 {}", list2);
		// Normalize list2 to subject 0's p.o.v.
		if (teamAttr == Attr.teamName) {
			list2 = normalizeTeamNameList(list2);
			logger.debug("getTeamChoice: list2 normalized {}", list2);
		}
		adjustTeamChoiceWeights(weights, numTI, list2);
		List<Entry<Attr, Integer>> entryList = new ArrayList<Entry<Attr, Integer>>(
				weights.entrySet());
		if (entryList.size() == 0)
			return defaultResult;
		if (entryList.size() > 1) {
			Collections.sort(entryList, new Comparator<Entry<Attr, Integer>>() {
				@Override
				public int compare(Entry<Attr, Integer> o1,
						Entry<Attr, Integer> o2) {
					int c = o2.getValue() - o1.getValue();
					return c != 0 ? c : o1.getKey().ordinal()
							- o2.getKey().ordinal();
				}
			});
		}
		// Sorted in reverse order on weight and in order on insignia/name,
		// hence max is first elt.
		logger.debug("getTeamChoice: {}", entryList);
		return nonNull(entryList.get(0).getKey());
	}

	private List<Attr> normalizeTeamNameList(List<Attr> list) {
		List<Attr> result = new ArrayList<Attr>(list.size());
		for (Attr a : list) {
			result.add(a == Attr.YourSuggestedTeamName ? Attr.PartnersSuggestedTeamName
					: a == Attr.PartnersSuggestedTeamName ? Attr.YourSuggestedTeamName
							: a);
		}
		return result;
	}

	protected void adjustTeamChoiceWeights(final Map<Attr, Integer> weights,
			final int numTI, final List<Attr> list) {
		final int multiplier = 10;
		int i = numTI;
		for (Attr attr : list) {
			int v = Objects.firstNonNull(weights.get(attr), 0);
			final int weight = i * multiplier;
			weights.put(attr, (int) (v == 0 ? weight : (v + weight) / 1.2));
			i--;
		}
	}

	@Override
	public float getTeamNorm() {
		float result = 0;
		int count = 0;
		for (ISubject subject : this.getSubjects()) {
			if (!subject.hasAttr(Attr.myExpectedEffortLevel.nm()))
				continue;
			String xs = subject.get(Attr.myExpectedEffortLevel.nm());
			try {
				result += Integer.parseInt(xs);
				count++;
			} catch (NumberFormatException e) {
				logger.error(
						"{}.getTeamNorm: illegal value for subject(id={})'s myExpectedEffortLevel: {} ",
						this.getClass().getSimpleName(), subject.getId(), xs);
				// Log error, but otherwise proceed as if value was 0.
			}
		}
		return count == 0 ? 0 : result / count;
	}

	public PageSequencer getPageSequencer() {
		return this.pageSequencer;
	}
	
	public void setKind(Kind kind) {
		this.kind = kind;
	}

	@Override
	public void setStart(Date start) {
		this.start = start;
	}

	@Override
	public void setFinish(Date finish) {
		this.finish = finish;
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Experiment))
			return false;
		Experiment exp = (Experiment) o;
		return super.equals(o) //
				&& this.kind.equals(exp.kind) //
				&& this.start.equals(exp.start) //
				&& java.util.Objects.equals(this.finish, exp.finish) //
				&& this.subjects.equals(exp.subjects);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + this.kind.hashCode();
		result = 31 * result + java.util.Objects.hash(this.start, this.finish);
		result = 31 * result + this.subjects.hashCode();
		return result;
	}

	@Override
	public String toString() {
		ToStringHelper o = Objects.toStringHelper(this).add("id", this.getId())
				.add("version", this.getVersion())
				.add("kind", this.kind)
				.add("start", this.start)
				.add("finish", this.finish).add("subjects", this.subjects);
		@SuppressWarnings("null")
		@NonNull
		String result = o.toString();
		return result;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isFinished() {
		return this.finish != null;
	}

}
