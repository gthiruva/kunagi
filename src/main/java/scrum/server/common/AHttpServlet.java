package scrum.server.common;

import ilarkesto.base.PermissionDeniedException;
import ilarkesto.core.logging.Log;
import ilarkesto.persistence.AEntity;
import ilarkesto.webapp.Servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scrum.server.ScrumWebApplication;
import scrum.server.WebSession;
import scrum.server.project.Project;

public abstract class AHttpServlet extends HttpServlet {

	private static final Log LOG = Log.get(AHttpServlet.class);

	protected abstract void onRequest(HttpServletRequest req, HttpServletResponse resp, WebSession session)
			throws IOException;

	protected void onInit(ServletConfig config) {}

	@Override
	protected final void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Servlet.preventCaching(resp);
		try {
			onRequest(req, resp, (WebSession) ScrumWebApplication.get().getWebSession(req));
		} catch (Throwable ex) {
			LOG.fatal("GET failed:", getClass().getName(), "->", ex);
		}
	}

	@Override
	protected final void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Servlet.preventCaching(resp);
		try {
			onRequest(req, resp, (WebSession) ScrumWebApplication.get().getWebSession(req));
		} catch (Throwable ex) {
			LOG.fatal("POST failed:", getClass().getName(), "->", ex);
		}
	}

	@Override
	public final void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			onInit(config);
		} catch (Throwable ex) {
			LOG.fatal("Init failed:", getClass().getName(), "->", ex);
		}
	}

	@Override
	public final void init() throws ServletException {
		super.init();
	}

	// --- helper ---

	public static <E extends AEntity> E getEntityByParameter(HttpServletRequest req, Class<E> type) {
		return getEntityByParameter(req, "entityId", type);
	}

	public static <E extends AEntity> E getEntityByParameter(HttpServletRequest req, String parameterName, Class<E> type) {
		String id = req.getParameter(parameterName);
		if (id == null) throw new RuntimeException(parameterName + "==null");
		return (E) ScrumWebApplication.get().getDaoService().getById(id);
	}

	public static Project getProject(WebSession session, HttpServletRequest req) {
		Project project = getEntityByParameter(req, "projectId", Project.class);
		if (!project.isVisibleFor(session.getUser())) throw new PermissionDeniedException();
		return project;
	}

}
