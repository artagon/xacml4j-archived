package org.xacml4j.v30.spi.pdp;

import java.util.concurrent.atomic.AtomicLong;

import org.xacml4j.v30.RequestContext;
import org.xacml4j.v30.Result;


public class NoCachePolicyDecisionCache implements PolicyDecisionCache
{
	private AtomicLong cacheMiss = new AtomicLong(0);
	
	@Override
	public long getCacheHitCount() {
		return 0;
	}
	
	@Override
	public long getCacheMissCount() {
		return cacheMiss.get();
	}

	@Override
	public void resetCount() {
		cacheMiss.set(0);
	}

	@Override
	public Result getDecision(RequestContext req) {
		cacheMiss.incrementAndGet();
		return null;
	}

	@Override
	public void putDecision(RequestContext req, Result res, int ttl) {
	}
}