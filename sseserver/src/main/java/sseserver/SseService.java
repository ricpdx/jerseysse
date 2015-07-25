package sseserver;

import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.SseFeature;

import javax.inject.Inject;
import javax.ws.rs.*;

@Path("/sse")
public class SseService {

	@Inject
	private SseWriteManager sseWriteManager;

	@GET
	@Produces(SseFeature.SERVER_SENT_EVENTS)
	@Path("/connect/{id}")
	public EventOutput connect(@PathParam("id") String id) throws InterruptedException {
		EventOutput eventOutput = new EventOutput();
		sseWriteManager.addSseConnection(id, eventOutput);
		return eventOutput;
	}
}
