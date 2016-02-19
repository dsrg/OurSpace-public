package org.dsrg.ourSpace.wea.domLogic.subject;

import java.util.List;
import java.util.Set;

import org.dsrg.ourSpace.wea.domLogic.Attr;
import org.dsrg.ourSpace.wea.domLogic.ChatLine;
import org.dsrg.ourSpace.wea.domLogic.Page;
import org.dsrg.ourSpace.wea.domLogic.Role;
import org.dsrg.soenea.domain.interf.IDomainObject;

public interface ISubject extends IDomainObject<Long> {

	public abstract List<Attr> getTeamAttrList(Attr a);
	
	public abstract List<ChatLine> getChatHistory();

	public abstract Page getCurrentPage();

	public abstract Set<String> getAttrs();

	public abstract String get(String attrName);

	public abstract String getAvatarName();

	public abstract boolean hasAttr(String attrName);

	public abstract void addChatLine(Role role, String text);

	public abstract void remove(String attrName);

	public abstract void set(String attrName, String attrValue);

	public abstract void setCurrentPage(Page page);

}