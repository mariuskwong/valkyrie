package com.othersonline.kv.distributed.impl;

import com.othersonline.kv.distributed.Node;
import com.othersonline.kv.distributed.Operation;
import com.othersonline.kv.distributed.OperationResult;

public class DefaultOperationResult<V> implements OperationResult<V> {
	private Operation<V> operation;

	private Node node;

	private V value;

	public DefaultOperationResult(Operation<V> operation, Node node, V value) {
		this.operation = operation;
		this.node = node;
		this.value = value;
	}

	public Node getNode() {
		return node;
	}

	public V getValue() {
		return value;
	}

	public Operation<V> getOperation() {
		return operation;
	}

}