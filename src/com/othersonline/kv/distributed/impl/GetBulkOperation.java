package com.othersonline.kv.distributed.impl;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Callable;

import com.othersonline.kv.distributed.AbstractOperation;
import com.othersonline.kv.distributed.Operation;
import com.othersonline.kv.distributed.OperationResult;
import com.othersonline.kv.distributed.OperationStatus;
import com.othersonline.kv.transcoder.Transcoder;

public class GetBulkOperation<V> extends AbstractOperation<V> implements
		Operation<V>, Callable<OperationResult<V>> {
	private static final long serialVersionUID = -3908847991063100534L;

	protected String[] keys;

	public GetBulkOperation(Transcoder transcoder, String... keys) {
		super(transcoder, null);
		this.keys = keys;
	}

	public String getName() {
		return "getBulk";
	}

	public GetBulkOperation<V> copy() {
		return new GetBulkOperation<V>(this.transcoder, this.key);
	}

	public OperationResult<V> call() throws Exception {
		try {
			long start = System.currentTimeMillis();
			Map<String, V> results = (Map<String, V>) ((transcoder == null) ? store
					.getBulk(keys)
					: store.getBulk(Arrays.asList(keys), transcoder));
			OperationResult<V> result = new GetBulkOperationResult<V>(this,
					results, OperationStatus.Success, System
							.currentTimeMillis()
							- start, null);
			return result;
		} finally {
		}
	}

}
