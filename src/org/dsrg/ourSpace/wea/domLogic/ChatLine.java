package org.dsrg.ourSpace.wea.domLogic;

import static org.dsrg.util.Nulls.nonNull;

import org.jmlspecs.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

public class ChatLine {

	private final Role role; // that originated the text.
	private final String text;

	public ChatLine(Role role, String text) {
		this.role = role;
		this.text = text;
	}

	public Role getRole() {
		return role;
	}

	public String getText() {
		return text;
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (o == this)
			return true;
		if (!(o instanceof ChatLine))
			return false;
		ChatLine chatLine = (ChatLine) o;
		return this.role.equals(chatLine.role)
				&& this.text.equals(chatLine.text);
	}

	@Override
	public int hashCode() {
		int result = this.role.hashCode();
		result = 31 * result + this.text.hashCode();
		return result;
	}

	@Override
	public String toString() {
		ToStringHelper o = Objects.toStringHelper(this).add("role", this.role)
				.add("text", this.text);
		return nonNull(o.toString());
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new UnsupportedOperationException();
	}

}
