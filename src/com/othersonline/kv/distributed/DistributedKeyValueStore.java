package com.othersonline.kv.distributed;

import java.util.List;

import com.othersonline.kv.KeyValueStoreException;

public interface DistributedKeyValueStore {
	public List<Context<byte[]>> get(String key) throws KeyValueStoreException;

	public void set(String key, byte[] bytes) throws KeyValueStoreException;

	public void set(String key, Context<byte[]> context)
			throws KeyValueStoreException;

	public void delete(String key) throws KeyValueStoreException;
}