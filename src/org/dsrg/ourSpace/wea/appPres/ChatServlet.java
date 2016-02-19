package org.dsrg.ourSpace.wea.appPres;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.dsrg.util.Nulls.nonNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dsrg.ourSpace.wea.domLogic.Attr;
import org.dsrg.ourSpace.wea.domLogic.GameMgr;
import org.dsrg.ourSpace.wea.domLogic.Role;
import org.jmlspecs.annotation.Nullable;

@SuppressWarnings("deprecation")
@WebServlet("/ChatServlet")
public class ChatServlet extends WebSocketServlet {

	private static final long serialVersionUID = -3778699021377219054L;

	@SuppressWarnings("null")
	protected static Logger logger = LogManager.getLogger();

	private final AtomicInteger connectionIds = new AtomicInteger(0);
	private final Set<ChatMessageInbound> connections = new CopyOnWriteArraySet<ChatMessageInbound>();

	@Override
	public void init(@Nullable ServletConfig config) throws ServletException {
		super.init(config);
	}

	@Override
	protected void doGet(@Nullable HttpServletRequest request,
			@Nullable HttpServletResponse resp) throws ServletException,
			IOException {
		checkNotNull(request);
		checkNotNull(resp);

		// Check for mandatory parameters.

		String roleAsString = request.getParameter(Attr.role.nm());
		if (roleAsString == null) {
			String msg = String.format(
					"%s: mandatory role parameter is missing (i.e. is null)",
					getServletName(), roleAsString);
			logger.error(msg);
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
			return;
		}
		try {
			Role role = Role.valueOf(roleAsString);
			request.setAttribute(Attr.role.nm(), role);
		} catch (IllegalArgumentException e) {
			String msg = String.format("%s: unrecognized role parameter '%s'",
					getServletName(), roleAsString);
			logger.error(msg);
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
			return;
		}
		super.doGet(request, resp);
	}

	@Override
	protected StreamInbound createWebSocketInbound(
			@Nullable String subProtocol, @Nullable HttpServletRequest request) {
		checkNotNull(request);

		Role role = (Role) request.getAttribute(Attr.role.nm());
		checkNotNull(role);
		ServletContext ctx = getServletContext();
		GameMgr gameMgr = (GameMgr) ctx.getAttribute(Attr.gameMgr.nm());
		checkNotNull(gameMgr);
		return new ChatMessageInbound(gameMgr, role,
				connectionIds.incrementAndGet());
	}

	private final class ChatMessageInbound extends MessageInbound {

		@SuppressWarnings("unused")
		private final Role role;
		private final String nickname;
		private GameMgr gameMgr;

		private ChatMessageInbound(GameMgr gameMgr, Role role, int id) {
			this.role = role;
			this.nickname = role.name() + id;
			this.gameMgr = gameMgr;
		}

		@Override
		protected void onOpen(@Nullable WsOutbound outbound) {
			connections.add(this);
			String message = String.format("* %s %s", nickname, "has joined.");
			logger.info("ws {} onOpen: {}", nickname, message);
			// broadcast(nonNull(message));
		}

		@Override
		protected void onClose(int status) {
			connections.remove(this);
			String message = String.format("* %s %s", nickname,
					"has disconnected.");
			logger.info("ws {} onClose: {}", nickname, message);
			// broadcast(nonNull(message));
		}

		@Override
		protected void onBinaryMessage(@Nullable ByteBuffer message)
				throws IOException {
			throw new UnsupportedOperationException(
					"Binary message not supported.");
		}

		@Override
		protected void onTextMessage(@Nullable CharBuffer message)
				throws IOException {
			if (message == null)
				return;
			logger.info("ws {} onMessage: {}", nickname, message);
			int key = Integer.parseInt(message.toString());
			key = key == 'A' ? 37 : key == 'D' ? 39 : key == 'W' ? 38
					: key == 'S' ? 40 : key;
			GameMgr.Coord c;
			switch (key) {
			case 37:
				c = gameMgr.playerChangeX(-1);
				break;
			case 38:
				c = gameMgr.playerChangeY(-1);
				break;
			case 39:
				c = gameMgr.playerChangeX(+1);
				break;
			case 40:
				c = gameMgr.playerChangeY(+1);
				break;
			default:
				// SNO
				c = new GameMgr.Coord(1, 1);
				// fall through
			}
			// Never trust the client
			// String filteredMessage = HTMLFilter.filter(message.toString());
			// String.format("%s: %s", nickname,
			// HTMLFilter.filter(message.toString()));
			// broadcast(nonNull(filteredMessage));
			String outMsg = c.x + " " + c.y;
			broadcast(nonNull(outMsg));
		}

		private void broadcast(String message) {
			logger.info("broadcast: {}", message);
			for (ChatMessageInbound connection : connections) {
				// if (connection == this)
				// continue;
				try {
					CharBuffer buffer = CharBuffer.wrap(message);
					connection.getWsOutbound().writeTextMessage(buffer);
				} catch (IOException ignore) {
					// Ignore
				}
			}
		}
	}

}
