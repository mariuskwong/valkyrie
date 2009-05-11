package com.othersonline.kv.distributed.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.othersonline.kv.distributed.Configuration;
import com.othersonline.kv.distributed.ConnectionFactory;
import com.othersonline.kv.distributed.Context;
import com.othersonline.kv.distributed.DistributedKeyValueStore;
import com.othersonline.kv.distributed.Node;
import com.othersonline.kv.distributed.NodeStore;
import com.othersonline.kv.distributed.backends.TokyoTyrantConnectionFactory;
import com.othersonline.kv.distributed.impl.DefaultDistributedKeyValueStore;
import com.othersonline.kv.distributed.impl.DefaultNodeImpl;
import com.othersonline.kv.distributed.impl.KetamaHashAlgorithm;
import com.othersonline.kv.distributed.impl.KetamaNodeLocator;
import com.othersonline.kv.distributed.impl.NonPersistentThreadPoolOperationQueue;
import com.othersonline.kv.distributed.impl.PassthroughContextSerializer;

import junit.framework.TestCase;

public class DistributedKeyValueStoreTestCase extends TestCase {

	public void testSimpleDistributedKeyValueStore() throws Exception {
		Configuration config = new Configuration();
		config.setRequiredReads(1);
		config.setRequiredWrites(1);
		config.setReplicas(1);
		config.setWriteOperationTimeout(500l);
		config.setReadOperationTimeout(300l);
		ConnectionFactory cf = new TokyoTyrantConnectionFactory();
		NodeStore nodeStore = new DummyNodeStore(Arrays.asList(new Node[] {
				new DefaultNodeImpl(1, 1, "salt:1:1",
						"tcp://stanley:1978?socketTimeout=200&maxActive=20",
						Arrays.asList(new Integer[] { new Integer(200) })),
				new DefaultNodeImpl(2, 2, "salt:2:2",
						"tcp://stanley:1978?socketTimeout=200&maxActive=20",
						Arrays.asList(new Integer[] { new Integer(400) })),
				new DefaultNodeImpl(3, 3, "salt:3:3",
						"tcp://stanley:1978?socketTimeout=200&maxActive=20",
						Arrays.asList(new Integer[] { new Integer(600) }))

		}));

		DefaultDistributedKeyValueStore kv = new DefaultDistributedKeyValueStore();
		kv.setAsyncOperationQueue(new DummyOperationQueue(cf));
		kv.setConfiguration(config);
		kv.setConnectionFactory(cf);
		kv.setContextSerializer(new PassthroughContextSerializer());
		kv.setHashAlgorithm(new KetamaHashAlgorithm());
		kv.setNodeLocator(new KetamaNodeLocator(nodeStore));
		kv.setNodeStore(nodeStore);
		kv.setSyncOperationQueue(new NonPersistentThreadPoolOperationQueue(cf)
				.start());

		String key = "test.key";
		String value = "hello world 2";
		kv.set(key, value.getBytes());

		List<Context<byte[]>> values = kv.get(key);
		assertEquals(values.size(), 1);
		Context<byte[]> context = values.get(0);
		String s = new String(context.getValue());
		assertEquals(s, value);

		kv.delete(key);

		values = kv.get(key);
		assertEquals(values.size(), 1);
		context = values.get(0);
		assertNull(context.getValue());

		testScalability(nodeStore, kv);
	}

	private void testScalability(NodeStore nodeStore,
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
		nodeStore.addNode(new DefaultNodeImpl(2, 2, "salt:2:2",
				"tcp://stanley:1978", Arrays.asList(new Integer[] {})));
		for (String key : keys) {
			List<Context<byte[]>> contexts = store.get(key);
			assertNotNull(contexts);
			assertTrue(contexts.size() >= 1);
			for (Context<byte[]> context : contexts) {
				byte[] data = context.getValue();
				assertNotNull(data);
			}
		}
	}
}