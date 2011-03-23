package com.artagon.xacml.v30.spi.repository;

import java.util.Iterator;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.artagon.xacml.v30.Policy;
import com.artagon.xacml.v30.PolicyIDReference;
import com.artagon.xacml.v30.PolicyResolutionException;
import com.artagon.xacml.v30.PolicySet;
import com.artagon.xacml.v30.PolicySetIDReference;
import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;

/**
 * A default implementation of {@link PolicyReferenceResolver}.
 * Maintains a cache of resolved policies by the reference.
 * 
 * @author Giedrius Trumpickas
 */
public class DefaultPolicyReferenceResolver 
	implements PolicyReferenceResolver, PolicyRepositoryListener
{
	private final static Logger log = LoggerFactory.getLogger(DefaultPolicyReferenceResolver.class);
	
	private ConcurrentMap<PolicyIDReference, Policy> policyIDRefCache;
	private ConcurrentMap<PolicySetIDReference, PolicySet> policySetIDRefCache;
	
	private PolicyRepository repository;
	private boolean enableRefCache;
	
	public DefaultPolicyReferenceResolver(
			PolicyRepository repository){
		this(repository, true, 1024);
	}
	
	public DefaultPolicyReferenceResolver(
			PolicyRepository policyRepository, 
			boolean enabledRefCache, int size)
	{
		Preconditions.checkNotNull(policyRepository);
		this.repository = policyRepository;
		Preconditions.checkState(repository != null);
		this.enableRefCache = enabledRefCache;
		this.policyIDRefCache = new MapMaker()
		.initialCapacity(size)
		.softKeys()
		.softValues()
		.makeMap();
		this.policySetIDRefCache = new MapMaker()
		.initialCapacity(size)
		.softKeys()
		.softValues()
		.makeMap();
		this.repository.addPolicyRepositoryListener(this);
	}
	
	/**
	 * A default implementation invokes 
	 * {@link #getPolicy(String, VersionMatch, VersionMatch, VersionMatch)
	 */
	@Override
	public Policy resolve(PolicyIDReference ref)
			throws PolicyResolutionException 
	{
		Policy p =  policyIDRefCache.get(ref);
		if(p != null){
			if(log.isDebugEnabled()){
				log.debug("Found Policy id=\"{}\" " +
						"version=\"{}\" for reference=\"{}\" in the cache", 
						new Object[]{p.getId(), p.getVersion(), ref});
			}
			return p;
		}
		p =  repository.getPolicy(
					ref.getId(), 
					ref.getVersionMatch(), 
					ref.getEarliestVersion(), 
					ref.getLatestVersion());
		if(p != null && 
				enableRefCache){
			policyIDRefCache.put(ref, p);
		}
		if(p != null && 
				log.isDebugEnabled()){
			log.debug("Resolved policy id=\"{}\" " +
					"version=\"{}\" for reference=\"{}\" from repository", 
					new Object[]{p.getId(), p.getVersion(), ref});
		}
		return p;
	}
	
	/**
	 * A default implementation invokes 
	 * {@link #getPolicySet(String, VersionMatch, VersionMatch, VersionMatch)
	 */
	@Override
	public PolicySet resolve(PolicySetIDReference ref)
			throws PolicyResolutionException 
	{
		PolicySet p = policySetIDRefCache.get(ref);
		if(p != null){
			if(log.isDebugEnabled()){
				log.debug("Found PolicySet id=\"{}\" " +
						"version=\"{}\" for reference=\"{}\" in the cache", 
						new Object[]{p.getId(), p.getVersion(), ref});
			}
			return p;
		}
		p =  repository.getPolicySet(
					ref.getId(), 
					ref.getVersionMatch(), 
					ref.getEarliestVersion(), 
					ref.getLatestVersion());
		if(p != null && 
				enableRefCache){
			policySetIDRefCache.put(ref, p);
		}
		if(p != null && log.isDebugEnabled()){
			log.debug("Resolved policy set id=\"{}\" " +
					"version=\"{}\" for reference=\"{}\" from repository", 
					new Object[]{p.getId(), p.getVersion(), ref});
		}
		return p;
	}
	
	protected final void clearRefCahce(){
		policyIDRefCache.clear();
		policySetIDRefCache.clear();
	}

	/**
	 * Removes a cached references pointing
	 * to the given policy
	 * 
	 * @param p a policy
	 */
	private void removeCachedReferences(Policy p)
	{
		Iterator<PolicyIDReference> it = policyIDRefCache.keySet().iterator();
		while(it.hasNext()){
			PolicyIDReference ref = it.next();
			if(ref.isReferenceTo(p)){
				if(log.isDebugEnabled()){
					log.debug("Removing=\"{}\" from cache", ref);
				}
				it.remove();
			}
		}
	}
	
	/**
	 * Removes a cached references pointing
	 * to the given policy set
	 * 
	 * @param p a policy set
	 */
	private void removeCachedReferences(PolicySet p){
		Iterator<PolicySetIDReference> it = policySetIDRefCache.keySet().iterator();
		while(it.hasNext()){
			PolicySetIDReference ref = it.next();
			if(ref.isReferenceTo(p)){
				if(log.isDebugEnabled()){
					log.debug("Removing=\"{}\" from cache", ref);
				}
				it.remove();
			}
		}
	}

	@Override
	public void policyAdded(Policy p) {
		removeCachedReferences(p);
	}

	@Override
	public void policyRemoved(Policy p) {
		removeCachedReferences(p);
	}

	@Override
	public void policySetAdded(PolicySet p) {
		removeCachedReferences(p);
	}

	@Override
	public void policySetRemoved(PolicySet p) {
		removeCachedReferences(p);		
	}
}
