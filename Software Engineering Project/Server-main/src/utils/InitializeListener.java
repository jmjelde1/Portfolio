package utils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class InitializeListener implements ServletContextListener {
	
	@Override
	public final void contextInitialized(final ServletContextEvent sce) {
		System.out.println("Server startup detected.");
	}
	
	@Override
	public final void contextDestroyed(final ServletContextEvent sce) { 
		System.out.println("Server shutdown detected.");
	}

}
