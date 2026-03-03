import java.util.Map;

import edu.uwm.cs351.Terrain;
import edu.uwm.cs351.TerrainCostMap;
import edu.uwm.cs351.TerrainSet;

public class TestTerrainCostMap extends AbstractTestMap<Terrain,Integer> {
	TerrainCostMap self;
	
	@Override
	protected Map<Terrain, Integer> create() {
		return self = new TerrainCostMap();
	}

	@Override
	protected void initMapElements() {
		k = Terrain.values();
		l = Terrain.values();
		v = new Integer[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		w = new Integer[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		permitNulls = false;
		sorted = true;  // element order
		preserveOrder = false; // put order
		failFast = true;
		hasRemove = true;
	}

	
	/// test490: testing illegal operations
	
	public void test490() {
		assertException(NullPointerException.class, () -> m.put(null, 34));
	}
	
	public void test491() {
		assertException(NullPointerException.class, () -> m.put(Terrain.CITY, null));
	}
	
	public void test492() {
		m.put(Terrain.FOREST, 40);
		assertException(NullPointerException.class, () -> m.put(Terrain.FOREST, null));
		assertEquals(1, m.size());
	}
	
	public void test493() {
		m.put(Terrain.DESERT, -5);
		Map.Entry<Terrain, Integer> e = m.entrySet().iterator().next();
		assertException(NullPointerException.class, () -> e.setValue(null));
		assertEquals(1, m.size());
	}
	
	
	/// test5xx specialized tests of TerraiNCostMap
	
	public void test500() {
		assertEquals(Ts(112620019), m.toString()); // how do maps print?
	}
	
	public void test501() {
		m.put(Terrain.CITY, 6);
		assertEquals(Ts(1864721475), m.toString()); // no spaces
	}
	
	public void test502() {
		m.put(Terrain.MOUNTAIN, 0);
		m.put(Terrain.WATER, -4);
		// Hints: (1) WATER comes first, (2) There is a single space character
		assertEquals(Ts(2127798302), m.toString());
	}
	
	public void test503() {
		m.put(Terrain.LAND, 1001);
		m.put(Terrain.FOREST, 5);
		m.put(Terrain.DESERT, -1);
		assertEquals("{LAND=1001, FOREST=5, DESERT=-1}", m.toString());
	}
	
	
	public void test510() {
		m = TerrainCostMap.fromString("{}");
		assertTrue(m.isEmpty());
	}
	
	public void test511() {
		m = TerrainCostMap.fromString("{INACCESSIBLE=9}");
		assertEquals(1, m.size());
		assertEquals(Integer.valueOf(9), m.get(Terrain.INACCESSIBLE));
	}
	
	public void test512() {
		m = TerrainCostMap.fromString("{DESERT=0, FOREST=-5}");
		assertEquals(2, m.size());
		assertEquals(Integer.valueOf(0), m.get(Terrain.DESERT));
		assertEquals(Integer.valueOf(-5), m.get(Terrain.FOREST));
	}
	
	public void test513() {
		m = TerrainCostMap.fromString("{MOUNTAIN=12, WATER=53211, LAND=666}");
		assertEquals(3, m.size());
		assertEquals(Integer.valueOf(12), m.get(Terrain.MOUNTAIN));
		assertEquals(Integer.valueOf(53211), m.get(Terrain.WATER));
		assertEquals(Integer.valueOf(666), m.get(Terrain.LAND));	
	}
	
	public void test517() {
		m = TerrainCostMap.fromString("{INACCESSIBLE=0, WATER=1, LAND=2, FOREST=3, MOUNTAIN=4, CITY=5, DESERT=6}");
		assertEquals(7, m.size());
		assertEquals(Integer.valueOf(0), m.get(Terrain.INACCESSIBLE));
		assertEquals(Integer.valueOf(1), m.get(Terrain.WATER));
		assertEquals(Integer.valueOf(2), m.get(Terrain.LAND));
		assertEquals(Integer.valueOf(3), m.get(Terrain.FOREST));
		assertEquals(Integer.valueOf(4), m.get(Terrain.MOUNTAIN));
		assertEquals(Integer.valueOf(5), m.get(Terrain.CITY));
		assertEquals(Integer.valueOf(6), m.get(Terrain.DESERT));	
	}
	
	
	public void test520() {
		TerrainSet s = self.asTerrainSet();
		assertTrue(s.isEmpty());
	}
	
	public void test521() {
		m.put(Terrain.CITY, 34);
		assertEquals(new TerrainSet(Terrain.CITY), self.asTerrainSet());
	}
	
	public void test522() {
		m.put(Terrain.FOREST, 45);
		m.put(Terrain.LAND, 0);
		m.put(Terrain.DESERT, 500);
		assertEquals(new TerrainSet(Terrain.FOREST, Terrain.LAND, Terrain.DESERT), self.asTerrainSet());
	}
	
	public void test523() {
		m.put(Terrain.MOUNTAIN, 13);
		m.put(Terrain.WATER, 4);
		m.put(Terrain.INACCESSIBLE, 67);
		m.put(Terrain.CITY, 0);
		m.remove(Terrain.CITY);
		assertEquals(new TerrainSet(Terrain.MOUNTAIN, Terrain.WATER, Terrain.INACCESSIBLE), self.asTerrainSet());		
	}
	
}
