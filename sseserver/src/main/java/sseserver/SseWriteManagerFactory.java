package sseserver;

import org.glassfish.hk2.api.Factory;

public class SseWriteManagerFactory implements Factory<SseWriteManager> {
	@Override
	public SseWriteManager provide() {
		return new SseWriteManager();
	}

	@Override
	public void dispose(SseWriteManager instance) {
		instance.shutdown();
	}
}
