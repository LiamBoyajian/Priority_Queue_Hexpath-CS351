import java.util.NoSuchElementException;
import java.util.Random;

import edu.uwm.cs.junit.LockedTestCase;
import edu.uwm.cs351.HexBoard;
import edu.uwm.cs351.HexCoordinate;
import edu.uwm.cs351.PriorityHexWorklist;
import edu.uwm.cs351.Terrain;
import edu.uwm.cs351.TerrainCostMap;

public class TestPriorityHexWorklist extends LockedTestCase {
	protected <T> void assertException(Class<?> excClass, Runnable f) {
		try {
			f.run();
			assertFalse("Should have thrown an exception, not returned",true);
		} catch (RuntimeException ex) {
			if (!excClass.isInstance(ex)) {
				ex.printStackTrace();
				assertFalse("Wrong kind of exception thrown: "+ ex.getClass().getSimpleName(),true);
			}
		}		
	}

	Random random;
	TerrainCostMap map1, map2;
	PriorityHexWorklist self1;
	PriorityHexWorklist self2;
	
	HexBoard.HexPiece p0, p1, p2, p3, p4, p5, p6, p7, p8, p9;
	
	@Override // implementation
	protected void setUp() {
		random = new Random();
		map1 = new TerrainCostMap();
		map1.put(Terrain.CITY, 4);
		map1.put(Terrain.FOREST, 15);
		map1.put(Terrain.INACCESSIBLE, 50);
		map1.put(Terrain.MOUNTAIN, 125);
		map2 = new TerrainCostMap();
		map2.put(Terrain.LAND, 2);
		map2.put(Terrain.WATER, 3);
		map2.put(Terrain.DESERT, 5);
		map2.put(Terrain.INACCESSIBLE, 7);
		self1 = new PriorityHexWorklist(map1);
		self2 = new PriorityHexWorklist(map2);
	}

	protected HexBoard.HexPiece p(Terrain t) {
		return new HexBoard.HexPiece(t, new HexCoordinate(random.nextInt(10),random.nextInt(10)));
	}
	
	
	public void test00() {
		assertFalse(self1.hasNext());
	}
	
	public void test01() {
		assertException(NoSuchElementException.class, () -> self1.next());
	}
	
	public void test02() {
		assertNull(self1.getCost(p(Terrain.CITY)));
	}
	
	public void test03() {
		assertNull(self2.getCost(p(Terrain.CITY)));
	}
	
	
	public void test10() {
		p0 = p(Terrain.CITY);
		self1.add(p0);
		assertTrue(self1.hasNext());
	}
	
	public void test11() {
		p0 = p(Terrain.CITY);
		self1.add(p0);
		assertSame(p0, self1.next());
	}
	
	public void test12() {
		p0 = p(Terrain.CITY);
		self1.add(p0);
		self1.next();
		assertFalse(self1.hasNext());
	}
	
	public void test13() {
		p0 = p(Terrain.CITY);
		self1.add(p0);
		assertEquals(Integer.valueOf(0), self1.getCost(p0));
	}
	
	public void test14() {
		p0 = p(Terrain.CITY);
		self1.add(p0);
		self1.next();
		assertEquals(Integer.valueOf(0), self1.getCost(p0));
	}
	
	public void test15() {
		p0 = p(Terrain.CITY);
		self1.add(p0);
		assertNull(self1.getCost(p(Terrain.CITY)));
	}
	
	public void test16() {
		p0 = p(Terrain.CITY);
		self1.add(p0);
		assertException(IllegalArgumentException.class, () -> self1.add(p0));
	}
	
	public void test17() {
		p1 = p(Terrain.INACCESSIBLE);
		self1.add(p1);
		assertEquals(Integer.valueOf(0), self1.getCost(p1));
	}
	
	
	public void test20() {
		p2 = p(Terrain.FOREST);
		p3 = p(Terrain.MOUNTAIN);
		self1.add(p2);
		assertSame(p2, self1.next());
		self1.add(p3);
		assertTrue(self1.hasNext());
	}
	
	public void test21() {
		p2 = p(Terrain.FOREST);
		p3 = p(Terrain.MOUNTAIN);
		self1.add(p2);
		assertSame(p2, self1.next());
		self1.add(p3);
		assertSame(p3, self1.next());
	}
	
	public void test22() {
		p2 = p(Terrain.FOREST);
		p3 = p(Terrain.MOUNTAIN);
		self1.add(p2);
		assertSame(p2, self1.next());
		self1.add(p3);
		assertEquals(Integer.valueOf(140), self1.getCost(p3));
	}
	
	public void test23() {
		p2 = p(Terrain.FOREST);
		p3 = p(Terrain.MOUNTAIN);
		self1.add(p2);
		assertSame(p2, self1.next());
		self1.add(p3);
		assertEquals(Integer.valueOf(0), self1.getCost(p2));
	}
	
	public void test24() {
		p2 = p(Terrain.FOREST);
		p3 = p(Terrain.MOUNTAIN);
		self1.add(p2);
		assertSame(p2, self1.next());
		self1.add(p3);
		assertNull(self1.getCost(p(Terrain.MOUNTAIN)));
	}
	
	public void test25() {
		p4 = p(Terrain.LAND);
		p5 = p(Terrain.WATER);
		self2.add(p4);
		assertSame(p4, self2.next());
		self2.add(p5);
		assertTrue(self2.hasNext());
	}
	
	public void test26() {
		p4 = p(Terrain.LAND);
		p5 = p(Terrain.WATER);
		self2.add(p4);
		assertSame(p4, self2.next());
		self2.add(p5);
		assertSame(p5, self2.next());
	}
	
	public void test27() {
		p4 = p(Terrain.LAND);
		p5 = p(Terrain.WATER);
		self2.add(p4);
		assertSame(p4, self2.next());
		self2.add(p5);
		assertSame(p5, self2.next());
		assertFalse(self2.hasNext());
	}
	
	public void test28() {
		p4 = p(Terrain.LAND);
		p5 = p(Terrain.WATER);
		self2.add(p4);
		assertSame(p4, self2.next());
		self2.add(p5);
		assertSame(p5, self2.next());
		assertEquals(Integer.valueOf(5), self2.getCost(p5));
	}
	
	public void test29() {
		p4 = p(Terrain.LAND);
		p7 = p(Terrain.LAND);
		self2.add(p4);
		assertSame(p4, self2.next());
		self2.add(p7);
		assertSame(p7, self2.next());
		assertEquals(Integer.valueOf(4), self2.getCost(p7));
	}
	
	
	public void test30() {
		p0 = p(Terrain.CITY);
		p2 = p(Terrain.FOREST);
		p3 = p(Terrain.MOUNTAIN);
		self1.add(p3);
		assertSame(p3, self1.next());
		self1.add(p2);
		self1.add(p0);
		assertSame(p0, self1.next());
		assertSame(p2, self1.next());
		assertFalse(self1.hasNext());
	}
	
	public void test31() {
		p0 = p(Terrain.CITY);
		p2 = p(Terrain.FOREST);
		p3 = p(Terrain.MOUNTAIN);
		self1.add(p3);
		assertSame(p3, self1.next());
		self1.add(p2);
		self1.add(p0);
		assertSame(p0, self1.next());
		assertSame(p2, self1.next());
		assertEquals(Integer.valueOf(129), self1.getCost(p0));
		assertEquals(Integer.valueOf(140), self1.getCost(p2));
		assertEquals(Integer.valueOf(0), self1.getCost(p3));
	}

	public void test32() {
		p0 = p(Terrain.CITY);
		p2 = p(Terrain.FOREST);
		p3 = p(Terrain.MOUNTAIN);
		self1.add(p3);
		assertSame(p3, self1.next());
		self1.add(p2);
		assertSame(p2, self1.next());
		self1.add(p0);
		assertSame(p0, self1.next());
		assertFalse(self1.hasNext());
	}
	
	public void test33() {
		p0 = p(Terrain.CITY);
		p2 = p(Terrain.FOREST);
		p3 = p(Terrain.MOUNTAIN);
		self1.add(p3);
		assertSame(p3, self1.next());
		self1.add(p2);
		assertSame(p2, self1.next());
		self1.add(p0);
		assertSame(p0, self1.next());
		assertEquals(Integer.valueOf(159), self1.getCost(p0));
		assertEquals(Integer.valueOf(140), self1.getCost(p2));
		assertEquals(Integer.valueOf(0), self1.getCost(p3));
	}
	
	public void test40() {
		map1 = TerrainCostMap.fromString("{CITY=2, LAND=3, WATER=5, DESERT=7, FOREST=11}");
		self1 = new PriorityHexWorklist(map1);
		p0 = p(Terrain.CITY);
		p1 = p(Terrain.LAND);
		p2 = p(Terrain.WATER);
		p3 = p(Terrain.DESERT);
		p4 = p(Terrain.FOREST);
		p5 = p(Terrain.MOUNTAIN);
		p6 = p(Terrain.INACCESSIBLE);
		HexBoard.HexPiece[] p = new HexBoard.HexPiece[] {p0, p1, p2, p3, p4, p5, p6};
		self1.add(p0);
		assertEquals(Ts(221077456), ""+self1.getCost(p0)); // make sure to read Section 2.1
		assertSame(p[Ti(1234911157)], self1.next());
		self1.add(p1);
		self1.add(p2);
		assertEquals(Ts(1368606312), ""+self1.getCost(p1));
		assertEquals(Ts(275411624), ""+self1.getCost(p2));
		assertSame(p[Ti(586273343)], self1.next());
		self1.add(p3);
		assertEquals(Ts(2036702505), ""+self1.getCost(p3));
		assertEquals(p[Ti(292161164)], self1.next());
		self1.add(p4);
		assertEquals(Ts(54041665), ""+self1.getCost(p4));
		assertEquals(Ts(1871399920), ""+self1.getCost(p5));
		assertEquals(Ts(108351428), ""+self1.getCost(p6));
	}
}
