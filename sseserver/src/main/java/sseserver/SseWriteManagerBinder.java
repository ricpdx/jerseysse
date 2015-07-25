package sseserver;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

public class SseWriteManagerBinder extends AbstractBinder {
	@Override
	protected void configure() {
		bindFactory(SseWriteManagerFactory.class)
				.to(SseWriteManager.class)
				.in(Singleton.class);
	}
}
