package sseserver;

import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SseWriteManager {

	private final ConcurrentHashMap<String, EventOutput> connectionMap = new ConcurrentHashMap<>();

	private final ScheduledExecutorService messageExecutorService;

	private final Logger logger = LoggerFactory.getLogger(SseWriteManager.class);

	public SseWriteManager() {
		messageExecutorService = Executors.newScheduledThreadPool(1);
		messageExecutorService.scheduleWithFixedDelay(new messageProcessor(), 0, 5, TimeUnit.SECONDS);
	}

	public void addSseConnection(String id, EventOutput eventOutput) {
		logger.info("adding connection for id={}.", id);
		connectionMap.put(id, eventOutput);
	}

	private class messageProcessor implements Runnable {
		@Override
		public void run() {
			try {
				Iterator<Map.Entry<String, EventOutput>> iterator = connectionMap.entrySet().iterator();
				while (iterator.hasNext()) {
					boolean remove = false;
					Map.Entry<String, EventOutput> entry = iterator.next();
					if (entry.getValue() != null) {
						if (entry.getValue().isClosed()) {
							remove = true;
						} else {
							try {
								logger.info("writing to id={}.", entry.getKey());
								entry.getValue().write(new OutboundEvent.Builder().name("custom-message").data(String.class, "EOM").build());
							} catch (Exception ex) {
								logger.info(String.format("write failed to id=%s.", entry.getKey()), ex);
								remove = true;
							}
						}
					}
					if (remove) {
						// we are removing the eventOutput. close it is if it not already closed.
						if (entry.getValue() != null && !entry.getValue().isClosed()) {
							try {
								entry.getValue().close();
							} catch (Exception ex) {
								// do nothing.
							}
						}
						iterator.remove();
					}
				}
			} catch (Exception ex) {
				logger.error("messageProcessor.run threw exception.", ex);
			}
		}
	}

	public void shutdown() {
		if (messageExecutorService != null && !messageExecutorService.isShutdown()) {
			logger.info("SseWriteManager.shutdown: calling messageExecutorService.shutdown.");
			messageExecutorService.shutdown();
		} else {
			logger.info("SseWriteManager.shutdown: messageExecutorService == null || messageExecutorService.isShutdown().");
		}

	}
}
