package com.rubiconproject.oss.kv.distributed.test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.rubiconproject.oss.kv.backends.ConnectionFactory;
import com.rubiconproject.oss.kv.backends.UriConnectionFactory;
import com.rubiconproject.oss.kv.distributed.Configuration;
import com.rubiconproject.oss.kv.distributed.Context;
import com.rubiconproject.oss.kv.distributed.DistributedKeyValueStore;
import com.rubiconproject.oss.kv.distributed.Node;
import com.rubiconproject.oss.kv.distributed.NodeStore;
import com.rubiconproject.oss.kv.distributed.OperationQueue;
import com.rubiconproject.oss.kv.distributed.hashing.MD5HashAlgorithm;
import com.rubiconproject.oss.kv.distributed.impl.DefaultDistributedKeyValueStore;
import com.rubiconproject.oss.kv.distributed.impl.DefaultNodeImpl;
import com.rubiconproject.oss.kv.distributed.impl.DynamoNodeLocator;
import com.rubiconproject.oss.kv.distributed.impl.NodeRankContextFilter;
import com.rubiconproject.oss.kv.distributed.impl.NonPersistentThreadPoolOperationQueue;
import com.rubiconproject.oss.kv.distributed.impl.PassthroughContextSerializer;

import junit.framework.TestCase;

public class DistributedKeyValueStoreTestCase extends TestCase {

	Configuration config = new Configuration();

	public void setUp() {
		config.setRequiredReads(2);
		config.setRequiredWrites(2);
		config.setWriteReplicas(3);
		config.setReadReplicas(3);
		config.setWriteOperationTimeout(500l);
		config.setReadOperationTimeout(300l);
	}

	public void testSimpleDistributedKeyValueStore() throws Exception {
		ConnectionFactory cf = new UriConnectionFactory();
		List<Node> nodeList = new LinkedList<Node>();
		nodeList.add(new DefaultNodeImpl(1, 1, "salt:1:1",
				"hash://localhost?id=1"));
		nodeList.add(new DefaultNodeImpl(2, 2, "salt:2:2",
				"hash://localhost?id=2"));
		nodeList.add(new DefaultNodeImpl(3, 3, "salt:3:3",
				"hash://localhost?id=3"));
		NodeStore nodeStore = new DummyNodeStore(nodeList);

		DynamoNodeLocator locator = new DynamoNodeLocator();
		locator.setActiveNodes(nodeStore.getActiveNodes());
		nodeStore.addChangeListener(locator);

		DefaultDistributedKeyValueStore kv = new DefaultDistributedKeyValueStore();
		OperationQueue asyncOpqueue = new NonPersistentThreadPoolOperationQueue(
				null, cf);
		asyncOpqueue.start();
		OperationQueue syncOpqueue = new NonPersistentThreadPoolOperationQueue(
				null, cf);
		syncOpqueue.start();
		kv.setAsyncOperationQueue(asyncOpqueue);
		kv.setConfiguration(config);
		kv.setContextSerializer(new PassthroughContextSerializer());
		kv.setContextFilter(new NodeRankContextFilter<byte[]>(config));
		kv.setHashAlgorithm(new MD5HashAlgorithm());
		kv.setNodeLocator(locator);
		kv.setSyncOperationQueue(syncOpqueue);
		kv.start();

		testBasicOperations(kv);
		testIncrementalScalability(nodeStore, kv);
	}

	private void testBasicOperations(DistributedKeyValueStore store)
			throws Exception {
		String key = "test.key";
		String value = "hello world 2";

		// fetch a null value
		List<Context<byte[]>> values = store.getContexts(key, true, true, 400,
				2000);
		assertNotNull(values);
		assertTrue(values.size() >= config.getRequiredReads());
		for (Context<byte[]> context : values) {
			assertEquals(context.getKey(), key);
			assertNull(context.getValue());
		}
		Context<byte[]> context = store.get(key);
		assertNotNull(context);
		assertEquals(context.getKey(), key);
		assertNull(context.getValue());

		// set a value and fetch
		store.set(key, value.getBytes());
		values = store.getContexts(key, true, true, 400, 2000);
		assertTrue(values.size() >= 2);
		context = values.get(0);
		String s = new String(context.getValue());
		assertEquals(s, value);

		context = store.get(key);
		s = new String(context.getValue());
		assertEquals(s, value);

		store.delete(key);

		values = store.getContexts(key, true, true, 400, 2000);
		assertTrue(values.size() >= 2);
		context = values.get(0);
		assertNull(context.getValue());
	}

	private void testIncrementalScalability(NodeStore nodeStore,
			DistributedKeyValueStore store) throws Exception {
		int numKeys = 1000;
		Random random = new Random();
		List<String> keys = new ArrayList<String>(numKeys);
		for (int i = 0; i < numKeys; ++i) {
			String key = String.format("/blobs/users/%1$d/%2$d/%3$d", random
					.nextInt(100), random.nextInt(10000), random
					.nextInt(Integer.MAX_VALUE));
			store.set(key, "Hello world".getBytes());
			keys.add(key);
		}

		// now add a new node and attempt to retrieve values
		nodeStore.addNode(new DefaultNodeImpl(4, 4, "salt:4:4",
				"hash://localhost?id=4"));
		for (String key : keys) {
			List<Context<byte[]>> contexts = store.getContexts(key, true, true,
					400, 2000);
			assertNotNull(contexts);
			assertTrue(contexts.size() >= 1);
			// at least one should have non-null data
			boolean foundData = false;
			for (Context<byte[]> context : contexts) {
				assertNotNull(context);
				String k = context.getKey();
				byte[] data = context.getValue();
				assertNotNull(k);
				if (data != null)
					foundData = true;
			}
			assertTrue(foundData);
		}

		// now see if the node rank context filter works
		for (String key : keys) {
			Context<byte[]> context = store.get(key);
			assertNotNull(context);
			assertNotNull(context.getKey());
			assertNotNull(context.getValue());
		}
	}
}
