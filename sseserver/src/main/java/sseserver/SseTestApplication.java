package sseserver;

import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/*")
public class SseTestApplication extends ResourceConfig {
	public SseTestApplication() {
		register(new SseWriteManagerBinder());
		register(SseService.class);
	}
}

