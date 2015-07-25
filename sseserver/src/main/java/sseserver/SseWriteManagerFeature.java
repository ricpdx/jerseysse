package sseserver;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

public class SseWriteManagerFeature implements Feature {
	@Override
	public boolean configure(FeatureContext context) {
		context.register(new SseWriteManagerBinder());
		return true;
	}
}
