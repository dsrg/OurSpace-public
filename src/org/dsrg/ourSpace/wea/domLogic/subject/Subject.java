package org.dsrg.ourSpace.wea.domLogic.subject;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.dsrg.util.Nulls.nonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dsrg.ourSpace.wea.domLogic.Attr;
import org.dsrg.ourSpace.wea.domLogic.ChatLine;
import org.dsrg.ourSpace.wea.domLogic.DomainObject;
import org.dsrg.ourSpace.wea.domLogic.Page;
import org.dsrg.ourSpace.wea.domLogic.Role;
import org.jmlspecs.annotation.NonNull;
import org.jmlspecs.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

public class Subject extends DomainObject implements ISubject {

	public static final String FEMALE_AVATAR_NAME = "Partner"; // 2015.03.22 was: "Stacey";
	public static final String MALE_AVATAR_NAME = "Partner"; // 2015.03.22 was: "Rick";

	public static final String TEAM_ATTR_LIST_SEPARATOR = ",";

	private static final String CHAT_HISTORY_SEPARATOR_REGEX = "[\t\n]";
	private static final char CHAT_HISTORY_FIELD_SEPARATOR = CHAT_HISTORY_SEPARATOR_REGEX
			.charAt(1);
	private static final char CHAT_HISTORY_LINE_SEPARATOR = CHAT_HISTORY_SEPARATOR_REGEX
			.charAt(2);

	public static final Subject NULL_SUBJECT = new Subject(NULL_ID,
			INITIAL_VERSION);

	public int MAX_NUM_CHAT_HISTORY_LINES = 5;

	private final HashMap<String, String> attr;

	public Subject(long id, long version) {
		super(id, version);
		this.attr = new HashMap<String, String>();
	}

	/**
	 * @param attrName
	 * @return immutable map of this's attribute map.
	 */
	public Map<String, String> getAttr() {
		ImmutableMap<String, String> result = ImmutableMap
				.<String, String> builder().putAll(this.attr).build();
		return nonNull(result);
	}

	@Override
	public boolean hasAttr(String attrName) {
		return this.attr.containsKey(attrName);
	}

	/** @requires #hasAttr(attrName). */
	@Override
	public String get(String attrName) {
		String result = this.attr.get(attrName);
		checkNotNull(result);
		return result;
	}

	@Override
	public void set(String attrName, String attrValue) {
		checkState(this.getId() != NULL_ID, "NULL_SUBJECT is immutable");
		this.attr.put(attrName, attrValue);
	}

	@Override
	public void remove(String attrName) {
		checkState(this.getId() != NULL_ID, "NULL_SUBJECT is immutable");
		this.attr.remove(attrName);
	}

	@Override
	public Page getCurrentPage() {
		Page result = Page.NotLoggedIn;
		if (!hasAttr(Attr.page.nm()))
			return result;
		String pageName = get(Attr.page.nm());
		try {
			result = Page.valueOf(pageName);
		} catch (IllegalArgumentException e) {
			return result;
		}
		return result;
	}

	@Override
	public void setCurrentPage(Page page) {
		set(Attr.page.nm(), page.nm());
	}

	@Override
	public Set<String> getAttrs() {
		return new HashSet<String>(this.attr.keySet());
	}

	@Override
	public List<ChatLine> getChatHistory() {
		List<ChatLine> result = new ArrayList<ChatLine>();
		if (!hasAttr(Attr.chatHistory.nm()))
			return result;
		String[] chatHistoryParts = get(Attr.chatHistory.nm()).split(
				CHAT_HISTORY_SEPARATOR_REGEX);
		checkState(chatHistoryParts.length % 2 == 0,
				"chat history parts should have an even length but it is: %s",
				Arrays.toString(chatHistoryParts));
		for (int i = 0; i < chatHistoryParts.length; i++) {
			Role role = Role.valueOf(nonNull(chatHistoryParts[i]));
			i++;
			String text = nonNull(chatHistoryParts[i]);
			result.add(new ChatLine(role, text));
		}
		return result;
	}

	@Override
	public List<Attr> getTeamAttrList(Attr a) {
		return getTeamAttrList(a,
				a == Attr.teamInsignia ? Attr.teamInsigniaList
						: Attr.teamNameList);
	}

	private List<Attr> getTeamAttrList(Attr a, Attr aList) {
		List<Attr> result;
		if (hasAttr(aList.nm())) {
			String listAsString = get(aList.nm());
			String[] attrNames = nonNull(listAsString
					.split(TEAM_ATTR_LIST_SEPARATOR));
			result = toAttrList(attrNames);
		} else {
			result = a.subrange();
		}
		return result;
	}

	private List<Attr> toAttrList(String[] attrNames) {
		List<Attr> result;
		result = new ArrayList<Attr>();
		for (String attrName : attrNames) {
			final Optional<Attr> opt = Attr.valueOpt(nonNull(attrName));
			if (!opt.isPresent()) {
				logger.error("Subject.toAttrList(): invalid attribute name {}",
						attrName);
				continue;
			}
			final Attr a = opt.get();
			result.add(a);
		}
		return result;
	}

	@Override
	public void addChatLine(Role role, String text) {
		String chatHistory = "";
		if (hasAttr(Attr.chatHistory.nm())) {
			chatHistory = get(Attr.chatHistory.nm())
					+ CHAT_HISTORY_LINE_SEPARATOR;
		}
		chatHistory += role.name() + CHAT_HISTORY_FIELD_SEPARATOR + text;
		chatHistory = trimChatHistoryIfNecessary(chatHistory);
		set(Attr.chatHistory.nm(), chatHistory);
	}

	@Override
	public String getAvatarName() {
		String genderAsString = hasAttr(Attr.gender.nm()) ? get(Attr.gender
				.nm()) : null;
		return Attr.Male.name().equals(genderAsString) ? MALE_AVATAR_NAME
				: FEMALE_AVATAR_NAME;
	}

	private String trimChatHistoryIfNecessary(String chatHistory) {
		String[] lines = chatHistory.split(String
				.valueOf(CHAT_HISTORY_LINE_SEPARATOR));
		if (lines.length < MAX_NUM_CHAT_HISTORY_LINES)
			return chatHistory;

		lines = Arrays.copyOfRange(lines, lines.length
				- MAX_NUM_CHAT_HISTORY_LINES, lines.length);
		int i = 0;
		String result = lines[i];
		for (i++; i < lines.length; i++) {
			result = result + CHAT_HISTORY_LINE_SEPARATOR + lines[i];
		}
		return nonNull(result);
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Subject))
			return false;
		Subject s = (Subject) o;
		return super.equals(o) && this.attr.equals(s.attr);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + this.attr.hashCode();
		return result;
	}

	@Override
	public String toString() {
		ToStringHelper o = Objects.toStringHelper(this).add("id", this.getId())
				.add("version", this.getVersion());
		List<String> attrList = new ArrayList<String>();
		attrList.addAll(this.getAttrs());
		Collections.sort(attrList);
		for (String attrName : attrList) {
			if (attrName == null)
				continue; // Skip invalid entry. TODO: log it.
			o.add(attrName, this.attr.get(attrName));
		}
		@SuppressWarnings("null")
		@NonNull
		String result = o.toString();
		return result;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new UnsupportedOperationException();
	}
}
