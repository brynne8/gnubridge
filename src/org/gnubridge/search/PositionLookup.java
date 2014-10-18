package org.gnubridge.search;

import java.util.Map;
import java.util.WeakHashMap;

public class PositionLookup {

	Map<Long, byte[]> positions;
	private long lastKey;
	private byte[] lastNode;

	public PositionLookup() {
		try {
			positions = new WeakHashMap<Long, byte[]>(20000000, 0.5f);
		} catch (OutOfMemoryError e) {
			positions = new WeakHashMap<Long, byte[]>();
		}
	}

	public byte[] getNode(long uniqueKey) {
		if (uniqueKey == lastKey) {
			return lastNode;
		}
		byte[] result = positions.get(uniqueKey);
		lastKey = uniqueKey;
		lastNode = result;
		return result;
	}

	public void putNode(long uniqueKey, byte[] value) {
		positions.put(uniqueKey, value);
	}

}
