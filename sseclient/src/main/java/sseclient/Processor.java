package sseclient;

import org.glassfish.jersey.media.sse.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public class Processor {
	private final Logger logger = LoggerFactory.getLogger(Main.class);
	private String serverName;
	private String clientId;

	public void run(String serverName, String clientId, int duration) {
		this.serverName = serverName;
		this.clientId = clientId;
		logger.info("running with serverName={}, clientId={}.", serverName, clientId);
		if (duration == 0)
			listenEventInput();
		else
			listenEventSource(duration);
	}

	private void listenEventInput() {
		logger.info("listenEventInput: enter.");
		try {
			Client client = ClientBuilder.newBuilder().register(SseFeature.class).build();
			WebTarget target = client.target(String.format("http://%s:8080/sse/connect/", serverName)).path("{clientId}").resolveTemplate("clientId", clientId);
			EventInput eventInput = target.request().get(EventInput.class);
			while (!eventInput.isClosed()) {
				final InboundEvent inboundEvent = eventInput.read();
				if (inboundEvent == null) {
					// connection has been closed
					logger.info("listenEventInput: inboundEvent == null.");
					break;
				}
				logger.info("listenEventInput: inboundEvent.getName={}, inboundEvent.readData={}.", inboundEvent.getName(), inboundEvent.readData(String.class));
			}
		} catch (Exception ex) {
			logger.error("listenEventInput", ex);
		}

		logger.info("listenEventInput: exit.");
	}

	private void listenEventSource(int duration) {
		logger.info("listenEventSource: enter.");
		try {
			Client client = ClientBuilder.newBuilder().register(SseFeature.class).build();
			WebTarget target = client.target(String.format("http://%s:8080/sse/connect/", serverName)).path("{clientId}").resolveTemplate("clientId", clientId);
			EventSource eventSource = EventSource.target(target).build();
			EventListener listener = new EventListener() {
				@Override
				public void onEvent(InboundEvent inboundEvent) {
					logger.info("listenEventSource: inboundEvent.getName={}, inboundEvent.readData={}.", inboundEvent.getName(), inboundEvent.readData(String.class));
				}
			};
			eventSource.register(listener);
			eventSource.open();

			Thread.sleep(duration * 1000);
			//...
			eventSource.close();
		} catch (Exception ex) {
			logger.error("listenEventSource", ex);
		}

		logger.info("listenEventSource: exit.");
	}
}