package org.ie.sm;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.atmosphere.cpr.AtmosphereServlet;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class WebConfig implements WebApplicationInitializer {

	private static final String DISPATCHER_SERVLET_MAPPING = "/dispatcher/*";
	private static final String CHAT_MAPPING = "/chat/*";
	
	public void onStartup(ServletContext servletContext)
			throws ServletException {
		AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
		
		rootContext.register(RootConfiguration.class);
		
		servletContext.addListener(new ContextLoaderListener(rootContext));

		// Create the dispatcher servlet's Spring application context
		AnnotationConfigWebApplicationContext dispatcherContext = new AnnotationConfigWebApplicationContext();
		dispatcherContext.setServletContext(servletContext);
		dispatcherContext.setParent(rootContext);
		dispatcherContext.register(WebMvcConfiguration.class);

		// Register and map the dispatcher servlet
		ServletRegistration.Dynamic dispatcher = servletContext.addServlet(
				"dispatcher", new DispatcherServlet(dispatcherContext));
		dispatcher.setLoadOnStartup(0);
		dispatcher.addMapping(DISPATCHER_SERVLET_MAPPING);

		ServletRegistration.Dynamic atmosphereServlet = servletContext.addServlet(
				"AtmosphereServlet", AtmosphereServlet.class.getName());
		atmosphereServlet.setLoadOnStartup(0);
		atmosphereServlet.addMapping(CHAT_MAPPING);
		atmosphereServlet.setAsyncSupported(true);
		atmosphereServlet.setInitParameter("org.atmosphere.cpr.packages", "org.ie.sm");
		atmosphereServlet.setInitParameter("org.atmosphere.interceptor.HeartbeatInterceptor.clientHeartbeatFrequencyInSeconds", "10");
	}

}
