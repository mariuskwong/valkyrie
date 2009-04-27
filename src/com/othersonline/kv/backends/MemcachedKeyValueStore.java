package com.othersonline.kv.backends;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.BinaryConnectionFactory;
import net.spy.memcached.ConnectionFactory;
import net.spy.memcached.DefaultConnectionFactory;
import net.spy.memcached.HashAlgorithm;
import net.spy.memcached.KetamaConnectionFactory;
import net.spy.memcached.KetamaNodeLocator;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.MemcachedClientIF;
import net.spy.memcached.MemcachedNode;
import net.spy.memcached.NodeLocator;
import net.spy.memcached.OperationFactory;
import net.spy.memcached.protocol.binary.BinaryMemcachedNodeImpl;
import net.spy.memcached.protocol.binary.BinaryOperationFactory;

import com.othersonline.kv.KeyValueStore;
import com.othersonline.kv.KeyValueStoreException;
import com.othersonline.kv.BaseManagedKeyValueStore;
import com.othersonline.kv.mgmt.MemcachedImplMXBean;
import com.othersonline.kv.transcoder.Transcoder;
import com.othersonline.kv.transcoder.spy.SpyMemcachedByteArrayTranscoder;

/**
 * Proxy to the spy memcached client. Comments are copied from javadoc for that
 * client.
 * 
 * @author sam
 * 
 */
public class MemcachedKeyValueStore extends BaseManagedKeyValueStore implements
		KeyValueStore {
	public static final String IDENTIFIER = "memcached";

	private SpyMemcachedByteArrayTranscoder spyByteTranscoder = new SpyMemcachedByteArrayTranscoder();

	private MemcachedClientIF mcc;

	private boolean useBinaryProtocol = false;

	private boolean useKetama = true;

	private long getOperationTimeout = 1000l;

	private long setOperationTimeout = 1000l;

	private List<InetSocketAddress> hosts;

	public MemcachedKeyValueStore() {
	}

	public MemcachedKeyValueStore(String hosts) {
		setHosts(hosts);
	}

	public MemcachedKeyValueStore(List<InetSocketAddress> hosts) {
		this.hosts = hosts;
	}

	public void setHosts(String hosts) {
		this.hosts = AddrUtil.getAddresses(hosts);
	}

	public void setUseBinaryProtocol(boolean useBinaryProtocol) {
		this.useBinaryProtocol = useBinaryProtocol;
	}

	public void setUseKetama(boolean useKetama) {
		this.useKetama = useKetama;
	}

	public void setGetOperationTimeout(long millis) {
		this.getOperationTimeout = millis;
	}

	public void setSetOperationTimeout(long millis) {
		this.setOperationTimeout = millis;
	}

	public void start() throws IOException {
		ConnectionFactory cf = null;
		if (useBinaryProtocol && useKetama)
			cf = new KetamaBinaryConnectionFactory();
		else if (useBinaryProtocol)
			cf = new BinaryConnectionFactory();
		else if (useKetama)
			cf = new KetamaConnectionFactory();
		else
			cf = new DefaultConnectionFactory();
		mcc = new MemcachedClient(cf, hosts);
		super.start();
	}

	public void stop() {
		mcc.shutdown();
		mcc = null;
		super.stop();
	}

	public String getIdentifier() {
		return IDENTIFIER;
	}

	public boolean exists(String key) throws KeyValueStoreException,
			IOException {
		assertReadable();
		MemcachedClientIF mcc = getMemcachedClient();
		try {
			boolean value = (mcc.get(key) != null);
			return value;
		} finally {
			releaseMemcachedClient(mcc);
		}
	}

	public Object get(String key) throws KeyValueStoreException, IOException {
		assertReadable();
		MemcachedClientIF mcc = getMemcachedClient();
		try {
			Future<Object> future = mcc.asyncGet(key);
			Object value = future.get(getOperationTimeout,
					TimeUnit.MILLISECONDS);
			return value;
		} catch (InterruptedException e) {
			throw new KeyValueStoreException(e);
		} catch (ExecutionException e) {
			throw new KeyValueStoreException(e);
		} catch (TimeoutException e) {
			throw new KeyValueStoreException(e);
		} finally {
			releaseMemcachedClient(mcc);
		}
	}

	public Object get(String key, Transcoder transcoder)
			throws KeyValueStoreException, IOException, ClassNotFoundException {
		assertReadable();
		MemcachedClientIF mcc = getMemcachedClient();
		try {
			Future<byte[]> future = mcc.asyncGet(key, spyByteTranscoder);
			byte[] bytes = future.get(getOperationTimeout,
					TimeUnit.MILLISECONDS);
			if (bytes == null)
				return null;
			else {
				Object obj = transcoder.decode(bytes);
				return obj;
			}
		} catch (InterruptedException e) {
			throw new KeyValueStoreException(e);
		} catch (ExecutionException e) {
			throw new KeyValueStoreException(e);
		} catch (TimeoutException e) {
			throw new KeyValueStoreException(e);
		} finally {
			releaseMemcachedClient(mcc);
		}
	}

	public Map<String, Object> getBulk(String... keys)
			throws KeyValueStoreException, IOException, ClassNotFoundException {
		assertReadable();
		MemcachedClientIF mcc = getMemcachedClient();
		try {
			Future<Map<String, Object>> future = mcc.asyncGetBulk(keys);
			Map<String, Object> results = future.get(getOperationTimeout,
					TimeUnit.MILLISECONDS);
			return results;
		} catch (InterruptedException e) {
			throw new KeyValueStoreException(e);
		} catch (ExecutionException e) {
			throw new KeyValueStoreException(e);
		} catch (TimeoutException e) {
			throw new KeyValueStoreException(e);
		} finally {
			releaseMemcachedClient(mcc);
		}
	}

	public Map<String, Object> getBulk(final List<String> keys)
			throws KeyValueStoreException, IOException, ClassNotFoundException {
		assertReadable();
		MemcachedClientIF mcc = getMemcachedClient();
		try {
			Future<Map<String, Object>> future = mcc.asyncGetBulk(keys);
			Map<String, Object> results = future.get(getOperationTimeout,
					TimeUnit.MILLISECONDS);
			return results;
		} catch (InterruptedException e) {
			throw new KeyValueStoreException(e);
		} catch (ExecutionException e) {
			throw new KeyValueStoreException(e);
		} catch (TimeoutException e) {
			throw new KeyValueStoreException(e);
		} finally {
			releaseMemcachedClient(mcc);
		}
	}

	public Map<String, Object> getBulk(final List<String> keys,
			Transcoder transcoder) throws KeyValueStoreException, IOException,
			ClassNotFoundException {
		assertReadable();
		MemcachedClientIF mcc = getMemcachedClient();
		try {
			Future<Map<String, Object>> future = mcc.asyncGetBulk(keys);
			Map<String, Object> results = future.get(getOperationTimeout,
					TimeUnit.MILLISECONDS);
			Map<String, Object> retval = new HashMap<String, Object>(results
					.size());
			for (Map.Entry<String, Object> entry : results.entrySet()) {
				byte[] bytes = (byte[]) entry.getValue();
				Object obj = transcoder.decode(bytes);
				retval.put(entry.getKey(), obj);
			}
			return results;
		} catch (InterruptedException e) {
			throw new KeyValueStoreException(e);
		} catch (ExecutionException e) {
			throw new KeyValueStoreException(e);
		} catch (TimeoutException e) {
			throw new KeyValueStoreException(e);
		} finally {
			releaseMemcachedClient(mcc);
		}
	}

	public void set(String key, Serializable value)
			throws KeyValueStoreException, IOException {
		set(key, value, 0);
	}

	public void set(String key, Serializable value, Transcoder transcoder)
			throws KeyValueStoreException, IOException {
		set(key, value, transcoder, 0);
	}

	public void set(String key, Serializable value, int exp)
			throws KeyValueStoreException, IOException {
		assertWriteable();
		MemcachedClientIF mcc = getMemcachedClient();
		try {
			Future<Boolean> future = mcc.set(key, exp, value);
			future.get(setOperationTimeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new KeyValueStoreException(e);
		} catch (ExecutionException e) {
			throw new KeyValueStoreException(e);
		} catch (TimeoutException e) {
			throw new KeyValueStoreException(e);
		} finally {
			releaseMemcachedClient(mcc);
		}
	}

	public void set(String key, Serializable value, Transcoder transcoder,
			int exp) throws KeyValueStoreException, IOException {
		assertWriteable();
		MemcachedClientIF mcc = getMemcachedClient();
		try {
			byte[] bytes = transcoder.encode(value);
			Future<Boolean> future = mcc
					.set(key, exp, bytes, spyByteTranscoder);
			future.get(setOperationTimeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new KeyValueStoreException(e);
		} catch (ExecutionException e) {
			throw new KeyValueStoreException(e);
		} catch (TimeoutException e) {
			throw new KeyValueStoreException(e);
		} finally {
			releaseMemcachedClient(mcc);
		}
	}

	public void delete(String key) throws KeyValueStoreException, IOException {
		assertWriteable();
		MemcachedClientIF mcc = getMemcachedClient();
		try {
			Future<Boolean> future = mcc.delete(key);
			future.get(setOperationTimeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new KeyValueStoreException(e);
		} catch (ExecutionException e) {
			throw new KeyValueStoreException(e);
		} catch (TimeoutException e) {
			throw new KeyValueStoreException(e);
		} finally {
			releaseMemcachedClient(mcc);
		}
	}

	/**
	 * Increment the given counter, returning the new value.
	 * 
	 * @param key
	 *            the key
	 * @param by
	 *            the amount to increment
	 * @param def
	 *            the default value (if the counter does not exist)
	 * @return the new value, or -1 if we were unable to increment or add
	 * @throws KeyValueStoreException
	 */
	public long incr(String key, int by, long def)
			throws KeyValueStoreException {
		MemcachedClientIF mcc = getMemcachedClient();
		try {
			return mcc.incr(key, by, def);
		} finally {
			releaseMemcachedClient(mcc);
		}
	}

	/**
	 * Increment the given counter, returning the new value.
	 * 
	 * @param key
	 *            the key
	 * @param by
	 *            the amount to increment
	 * @param def
	 *            the default value (if the counter does not exist)
	 * @param exp
	 *            the expiration of this object
	 * @return the new value, or -1 if we were unable to increment or add
	 * @throws KeyValueStoreException
	 */
	public long incr(String key, int by, long def, int exp)
			throws KeyValueStoreException {
		MemcachedClientIF mcc = getMemcachedClient();
		try {
			return mcc.incr(key, by, def, exp);
		} finally {
			releaseMemcachedClient(mcc);
		}
	}

	/**
	 * Decrement the given counter, returning the new value.
	 * 
	 * @param key
	 *            the key
	 * @param by
	 *            the amount to decrement
	 * @param def
	 *            the default value (if the counter does not exist)
	 * @return the new value, or -1 if we were unable to decrement or add
	 * @throws KeyValueStoreException
	 */
	public long decr(String key, int by, long def)
			throws KeyValueStoreException {
		MemcachedClientIF mcc = getMemcachedClient();
		try {
			return mcc.decr(key, by, def);
		} finally {
			releaseMemcachedClient(mcc);
		}
	}

	/**
	 * Decrement the given counter, returning the new value.
	 * 
	 * @param key
	 *            the key
	 * @param by
	 *            the amount to decrement
	 * @param def
	 *            the default value (if the counter does not exist)
	 * @param exp
	 *            the expiration of this object
	 * @return the new value, or -1 if we were unable to decrement or add
	 * @throws KeyValueStoreException
	 */
	public long decr(String key, int by, long def, int exp)
			throws KeyValueStoreException {
		MemcachedClientIF mcc = getMemcachedClient();
		try {
			return mcc.incr(key, by, def, exp);
		} finally {
			releaseMemcachedClient(mcc);
		}
	}

	/**
	 * Get all of the stats from all of the connections.
	 * 
	 * @return Map of all stats from all hosts
	 * @throws KeyValueStoreException
	 */
	public Map<SocketAddress, Map<String, String>> getStats()
			throws KeyValueStoreException {
		MemcachedClientIF mcc = getMemcachedClient();
		try {
			return mcc.getStats();
		} finally {
			releaseMemcachedClient(mcc);
		}
	}

	/**
	 * Get a set of stats from all connections.
	 * 
	 * @param arg
	 *            which stats to get
	 * @return map of matching stats from all hosts
	 * @throws KeyValueStoreException
	 */
	public Map<SocketAddress, Map<String, String>> getStats(String arg)
			throws KeyValueStoreException {
		MemcachedClientIF mcc = getMemcachedClient();
		try {
			return mcc.getStats(arg);
		} finally {
			releaseMemcachedClient(mcc);
		}
	}

	/**
	 * Get the addresses of unavailable servers.
	 * 
	 * This is based on a snapshot in time so shouldn't be considered completely
	 * accurate, but is a useful for getting a feel for what's working and
	 * what's not working.
	 * 
	 * @return collection of currently unavailable servers
	 * @throws KeyValueStoreException
	 */
	public Collection<SocketAddress> getUnavailableServers()
			throws KeyValueStoreException {
		MemcachedClientIF mcc = getMemcachedClient();
		try {
			return mcc.getUnavailableServers();
		} finally {
			releaseMemcachedClient(mcc);
		}
	}

	/**
	 * Get the versions of all of the connected memcacheds.
	 * 
	 * @return map of server version on all hosts
	 * @throws KeyValueStoreException
	 */
	public Map<SocketAddress, String> getVersions()
			throws KeyValueStoreException {
		MemcachedClientIF mcc = getMemcachedClient();
		try {
			return mcc.getVersions();
		} finally {
			releaseMemcachedClient(mcc);
		}
	}

	public Object getMXBean() {
		return new MemcachedImplMXBean(this);
	}

	private MemcachedClientIF getMemcachedClient()
			throws KeyValueStoreException {
		try {
			return mcc;
		} catch (Exception e) {
			throw new KeyValueStoreException(e);
		}
	}

	private void releaseMemcachedClient(MemcachedClientIF client) {
	}

	/**
	 * A binary wire protocol that uses ketama hashing and ketama node locator.
	 * 
	 * @author sam
	 * 
	 */
	private static class KetamaBinaryConnectionFactory extends
			DefaultConnectionFactory {

		public KetamaBinaryConnectionFactory(int qLen, int bufSize) {
			super(qLen, bufSize, HashAlgorithm.KETAMA_HASH);
		}

		public KetamaBinaryConnectionFactory() {
			this(DEFAULT_OP_QUEUE_LEN, DEFAULT_READ_BUFFER_SIZE);
		}

		public NodeLocator createLocator(List<MemcachedNode> nodes) {
			return new KetamaNodeLocator(nodes, getHashAlg());
		}

		public MemcachedNode createMemcachedNode(SocketAddress sa,
				SocketChannel c, int bufSize) {
			return new BinaryMemcachedNodeImpl(sa, c, bufSize,
					createReadOperationQueue(), createWriteOperationQueue(),
					createOperationQueue());
		}

		public OperationFactory getOperationFactory() {
			return new BinaryOperationFactory();
		}
	}
}