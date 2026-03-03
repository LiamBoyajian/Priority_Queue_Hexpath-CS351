package edu.uwm.cs351;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import edu.uwm.cs351.util.AbstractEntry;

/**
 * A specialized map class that maps terrains to integer costs.
 */
public class TerrainCostMap extends AbstractMap<Terrain, Integer> {
	//Data Structure (three fields). Our solution uses a static constant
	// field as well.

	private static Consumer<String> reporter = (s) -> System.out.println("Invariant error: " + s);

	private boolean report(String error) {
		reporter.accept(error);
		return false;
	}

	private Integer[] costs;
	private int size;
	private int version;
	private MyEntrySet currentEntrySet = null;
	private final int LENGTH = Terrain.values().length;

	private class TerrainCostEntry implements Map.Entry<Terrain, Integer> {

		private Terrain key;
		private final Integer value; // exclusively a pointer

		public TerrainCostEntry(Terrain key) {
			this.key = key;
			this.value = TerrainCostMap.this.costs[key.ordinal()];
		}

		@Override // implementation
		public Terrain getKey() {
			return key;
		}

		@Override // implementation
		public Integer getValue() {
			Integer result = TerrainCostMap.this.costs[key.ordinal()];
			return result;
		}

		@Override // implementation
		public Integer setValue(Integer value) {
			if (value == null)
				throw new NullPointerException("value is null");

			costs[key.ordinal()] = value;

			return this.value;
		}

		@Override // implementation
		public boolean equals(Object obj) {
			if (!(obj instanceof Entry<?, ?>) || obj == null)
				return false;
			if (obj == this)
				return true;
			Entry<?, ?> given = (Entry<?, ?>) obj;
			if (given.getKey() == null)
				return false;
			if (given.getValue() == null)
				return false;

			return given.getKey().equals(this.getKey()) && given.getValue().equals(this.getValue());

		}

		@Override // implementation
		public int hashCode() {
			return Objects.hashCode(key) ^ Objects.hashCode(value);
		}

		@Override // implementation
		public String toString() {
			return "TerrainCostEntry [key=" + Objects.hashCode(key) + ", value=" + value + "]";
		}

	}

	@Override
	public Set<Entry<Terrain, Integer>> entrySet() {
		assert wellFormed() : "before entrySet";
		if (currentEntrySet != null)
			return currentEntrySet;

		currentEntrySet = new MyEntrySet();
		return currentEntrySet;
	}

	public TerrainCostMap() {
		costs = new Integer[LENGTH];
		size = 0;
		version = 0;
		assert wellFormed() : "after constructor";
	}

	public TerrainCostMap(String fromString) {
		TerrainCostMap temp = TerrainCostMap.fromString(fromString);

		costs = temp.costs;
		size = temp.size;
		version = 0;
		assert wellFormed() : "after constructor";
	}

	@Override
	public int size() {
		assert wellFormed() : "before size";
		return size;
	}

	private boolean wellFormed() {
		// What could go wrong? (Don't worry about negative costs -- they're OK)
		if (costs == null)
			return report("costs == null");
		if (size > LENGTH)
			return report("size overflow");
		if (size < 0)
			return report("size underflow");
		if (costs.length > LENGTH)
			return report("costs.length overflow");
		if (costs.length < LENGTH)
			return report("costs.length underflow");
		int count = 0;
		for (Integer n : costs) {
			if (n == null)
				continue;
			++count;
		}
		if (count != size)
			return report("size incorrect: " + count + ", " + size);

		return true;
	}

	@Override
	public Integer put(Terrain key, Integer value) {
		assert wellFormed() : "before put";

		if (value == null)
			throw new NullPointerException("value is null");

		Integer result = costs[key.ordinal()];
		costs[key.ordinal()] = value;

		if (result == null) {
			++size;
			++version;
		}
		assert wellFormed() : "after put";
		return result;
	}

	@Override
	public Integer get(Object key) {
		if (!(key instanceof Terrain))
			return null;
		return costs[((Terrain) key).ordinal()];
	}

	public static TerrainCostMap fromString(String from) {
		TerrainCostMap result = new TerrainCostMap();
		String temp = from.substring(1, from.length() - 1);
		for (String self : temp.split(", ")) {
			if (self.length() == 0)
				continue;
			String[] current = self.split("=");

			Terrain key = Terrain.valueOf(current[0]);

			result.costs[key.ordinal()] = Integer.parseInt(current[1]);

			++result.size;
		}

		return result;
	}

	@Override
	public Integer remove(Object key) {
		assert wellFormed() : "before remove";
		if (!(key instanceof Terrain))
			return null;
		Integer result = costs[((Terrain) key).ordinal()];
		costs[((Terrain) key).ordinal()] = null;

		if (result != null)
			--size;
		++version;
		assert wellFormed() : "after remove";
		return result;
	}

	private TerrainCostMap(boolean ignored) {
	} // used by spy

	// Body of the class, including the entry set

	public TerrainSet asTerrainSet() {
		assert wellFormed() : "before asTerrainSet";
		Terrain[] temp = new Terrain[size];
		int last = 0;
		for (int i = 0; i < costs.length; i++) {
			if (costs[i] == null)
				continue;

			temp[last] = Terrain.values()[i];
			++last;
		}

		return new TerrainSet(temp);
	}

	private class MyEntrySet extends AbstractSet<Entry<Terrain, Integer>> {

		@Override // required
		public int size() {
			return TerrainCostMap.this.size;
		}

		@Override // required
		public Iterator<Entry<Terrain, Integer>> iterator() {
			return new MyEntrySetIterator();
		}

	}

	private class MyEntrySetIterator implements Iterator<Entry<Terrain, Integer>>// implements what ?
	{
		// Data structure (three fields only)
		private int remaining;
		private int index;
		private int colVersion;

		MyEntrySetIterator() {
			colVersion = version;
			index = -1;
			remaining = size;
		}

		private boolean versionMismatch() {
			return colVersion != version;
		}

		private boolean wellFormed() {

			if (!TerrainCostMap.this.wellFormed())
				return false;

			if (version != colVersion)
				return true;

			if (index < -1)
				return report("index underflow");

			if (index >= TerrainCostMap.this.costs.length)
				return report("index overflow");

			if (remaining > size)
				return report("remaining greater than size");

			if (TerrainCostMap.this.costs.length - index <= 0 && remaining > 0)
				return report("incorrect remaining value");

			int countOfReal = 0;

			for (int i = 0; i < TerrainCostMap.this.costs.length && i <= index; i++) {
				if (TerrainCostMap.this.costs[i] != null)
					++countOfReal;
			}

			if (size - countOfReal != remaining)
				return report("remaining should be at least one smaller than size");

			return true;
		}

		@Override // required
		public boolean hasNext() {
			assert wellFormed() : "before hasNext";
			if (versionMismatch())
				throw new ConcurrentModificationException("version mismatch");

			return remaining > 0;
		}

		@Override // required
		public Entry<Terrain, Integer> next() {
			assert wellFormed() : "before next";
			if (!hasNext())
				throw new NoSuchElementException("no next element");

			if (versionMismatch())
				throw new ConcurrentModificationException("version mismatch");

			do {
				++index;
			} while (TerrainCostMap.this.costs[index] == null);

			--remaining;

			assert wellFormed() : "after next";
			return new TerrainCostEntry(Terrain.values()[index]);
		}

		@Override
		public void remove() {
			assert wellFormed() : "before remove";
			if (versionMismatch())
				throw new ConcurrentModificationException("version mismatch");

			if (index < 0 || TerrainCostMap.this.costs[index] == null)
				throw new IllegalStateException("no value to remove");

			TerrainCostMap.this.remove(Terrain.values()[index]);

			++colVersion;

			assert wellFormed() : "after remove";
		}

		MyEntrySetIterator(boolean ignored) {
		} // for spy

	}

	/**
	 * Used for testing the invariant. Do not change this code.
	 */
	public static class Spy {
		/**
		 * Return the sink for invariant error messages
		 * 
		 * @return current reporter
		 */
		public Consumer<String> getReporter() {
			return reporter;
		}

		/**
		 * Change the sink for invariant error messages.
		 * 
		 * @param r where to send invariant error messages.
		 */
		public void setReporter(Consumer<String> r) {
			reporter = r;
		}

		/**
		 * Create a debugging instance of the main class with a particular data
		 * structure.
		 * 
		 * @param a array of Integer costs
		 * @param s purported size
		 * @param v version
		 * @return a new instance with the given data structure
		 */
		public TerrainCostMap newInstance(Integer[] a, int s, int v) {
			TerrainCostMap result = new TerrainCostMap(false);
			result.costs = a;
			result.size = s;
			result.version = v;
			return result;
		}

		/**
		 * Return a debugging instance of the iterator with the given data structure.
		 * 
		 * @param m outer object, must not be null
		 * @param r remaining
		 * @param i index
		 * @param v copy of the version
		 * @return debugging instance of the iterator with this data structure
		 */
		public Iterator<Map.Entry<Terrain, Integer>> newIterator(TerrainCostMap m, int r, int i, int v) {
			MyEntrySetIterator result = m.new MyEntrySetIterator(false);
			result.remaining = r;
			result.index = i;
			result.colVersion = v;
			return result;
		}

		/**
		 * Return whether debugging instance meets the requirements on the invariant.
		 * 
		 * @param m instance of to use, must not be null
		 * @return whether it passes the check
		 */
		public boolean wellFormed(TerrainCostMap m) {
			return m.wellFormed();
		}

		/**
		 * Return whether the iterator meets its invariant.
		 * 
		 * @param it iterator to check, must not be null
		 * @return whether it thinks the invariant is ok.
		 */
		public boolean wellFormed(Iterator<Map.Entry<Terrain, Integer>> it) {
			return ((MyEntrySetIterator) it).wellFormed();
		}
	}

}
