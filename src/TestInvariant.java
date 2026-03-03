import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import edu.uwm.cs351.Terrain;
import edu.uwm.cs351.TerrainCostMap;
import junit.framework.TestCase;

public class TestInvariant extends TestCase {
	protected TerrainCostMap.Spy spy = new TerrainCostMap.Spy();
	protected int reports;
	protected TerrainCostMap self;
	protected Iterator<Map.Entry<Terrain, Integer>> it;
	
	protected void assertReporting(boolean expected, Supplier<Boolean> test) {
		reports = 0;
		Consumer<String> savedReporter = spy.getReporter();
		try {
			spy.setReporter((String message) -> {
				++reports;
				if (message == null || message.trim().isEmpty()) {
					assertFalse("Uninformative report is not acceptable", true);
				}
				if (expected) {
					assertFalse("Reported error incorrectly: " + message, true);
				}
			});
			assertEquals(expected, test.get().booleanValue());
			if (!expected) {
				assertEquals("Expected exactly one invariant error to be reported", 1, reports);
			}
			spy.setReporter(null);
		} finally {
			spy.setReporter(savedReporter);
		}
	}

	protected void assertWellFormed(boolean expected, TerrainCostMap m) {
		assertReporting(expected, () -> spy.wellFormed(m));
	}

	protected void assertWellFormed(boolean expected, Iterator<Map.Entry<Terrain,Integer>> it) {
		assertReporting(expected, () -> spy.wellFormed(it));
	}

	
	public void testA0() {
		self = spy.newInstance(null, 0, 0);
		assertWellFormed(false, self);
	}
	
	public void testA1() {
		self = spy.newInstance(new Integer[1], 0, 0);
		assertWellFormed(false, self);
	}
	
	public void testA2() {
		self = spy.newInstance(new Integer[2], 0, 0);
		assertWellFormed(false, self);
	}
	
	public void testA3() {
		self = spy.newInstance(new Integer[4], 0, 0);
		assertWellFormed(false, self);
	}
	
	public void testA4() {
		self = spy.newInstance(new Integer[8], 0, 0);
		assertWellFormed(false, self);
	}
	
	public void testA5() {
		self = spy.newInstance(new Integer[6], 0, 0);
		assertWellFormed(false, self);
	}
	
	public void testA6() {
		self = spy.newInstance(new Integer[7], 0, 0);
		assertWellFormed(true, self);
	}
	
	public void testA7() {
		self = spy.newInstance(new Integer[7], 0, 1);
		assertWellFormed(true, self);
	}
	
	public void testA8() {
		self = spy.newInstance(new Integer[11], 0, 0);
		assertWellFormed(false, self);
	}
	
	
	public void testB0() {
		self = spy.newInstance(new Integer[7], 1, 1);
		assertWellFormed(false, self);
	}
	
	public void testB1() {
		Integer[] a = new Integer[7];
		a[3] = 0;
		self = spy.newInstance(a, 1, 1);
		assertWellFormed(true, self);
	}
	
	public void testB2() {
		Integer[] a = new Integer[7];
		a[5] = -1;
		self = spy.newInstance(a, 1, 1);
		assertWellFormed(true, self);
	}
	
	public void testB3() {
		Integer[] a = new Integer[7];
		a[0] = 100;
		self = spy.newInstance(a, 1, 1);
		assertWellFormed(true, self);
	}
	
	public void testB4() {
		Integer[] a = new Integer[7];
		a[3] = 0;
		self = spy.newInstance(a, 2, 1);
		assertWellFormed(false, self);
	}
	
	public void testB5() {
		Integer[] a = new Integer[7];
		a[6] = 0;
		self = spy.newInstance(a, 1, 42);
		assertWellFormed(true, self);
	}

	
	public void testC0() {
		Integer[] a = new Integer[7];
		a[6] = 0;
		a[0] = 0;
		self = spy.newInstance(a, 1, 42);
		assertWellFormed(false, self);
	}
	
	public void testC1() {
		Integer[] a = new Integer[7];
		a[6] = 0;
		a[0] = 0;
		self = spy.newInstance(a, 2, 42);
		assertWellFormed(true, self);
	}
	
	public void testC2() {
		Integer[] a = new Integer[7];
		a[1] = -1;
		a[2] = 3;
		self = spy.newInstance(a, 2, 42);
		assertWellFormed(true, self);
	}
	
	public void testC3() {
		Integer[] a = new Integer[7];
		a[3] = Integer.MAX_VALUE;
		a[6] = Integer.MIN_VALUE;
		self = spy.newInstance(a, 2, 42);
		assertWellFormed(true, self);
	}
	
	public void testC4() {
		Integer[] a = new Integer[7];
		a[1] = -1;
		a[2] = 3;
		self = spy.newInstance(a, 3, 42);
		assertWellFormed(false, self);
	}
	
	public void testC5() {
		Integer[] a = new Integer[7];
		a[4] = 0;
		a[5] = -10;
		a[6] = 7;
		self = spy.newInstance(a, 3, 42);
		assertWellFormed(true, self);
	}
	
	public void testC6() {
		Integer[] a = new Integer[7];
		a[4] = 0;
		a[5] = -10;
		a[6] = 7;
		self = spy.newInstance(a, 4, 42);
		assertWellFormed(false, self);
	}
	
	public void testC7() {
		Integer[] a = new Integer[7];
		a[1] = 0;
		a[3] = -10;
		a[5] = 7;
		a[6] = 8;
		self = spy.newInstance(a, 4, 42);
		assertWellFormed(true, self);
	}
	
	public void testC8() {
		Integer[] a = new Integer[7];
		a[0] = 0;
		a[1] = 1;
		a[3] = 6;
		a[4] = -2;
		a[5] = 17;
		a[6] = 8;
		self = spy.newInstance(a, 5, 42);
		assertWellFormed(false, self);
	}
	
	public void testC9() {
		Integer[] a = new Integer[7];
		a[0] = 0;
		a[1] = 1;
		a[2] = 1000;
		a[3] = 6;
		a[4] = -2;
		a[5] = 17;
		a[6] = 8;
		self = spy.newInstance(a, 7, 42);
		assertWellFormed(true, self);
	}
	
	public void testI0() {
		Integer[] a = new Integer[7];
		self = spy.newInstance(a, 0, 67);
		it = spy.newIterator(self, 0, -1, 67);
		assertWellFormed(true, it);
	}
	
	public void testI1() {
		Integer[] a = new Integer[7];
		self = spy.newInstance(a, 1, 67);
		it = spy.newIterator(self, 0, -1, 67);
		assertWellFormed(false, it);
	}
	
	public void testI2() {
		Integer[] a = new Integer[7];
		self = spy.newInstance(a, 1, 67);
		it = spy.newIterator(self, 0, -1, 42);
		assertWellFormed(false, it);
	}
	
	public void testI3() {
		Integer[] a = new Integer[7];
		self = spy.newInstance(a, 0, 67);
		it = spy.newIterator(self, 1, -1, 42);
		assertWellFormed(true, it);
	}
	
	public void testI4() {
		Integer[] a = new Integer[7];
		self = spy.newInstance(a, 0, 67);
		it = spy.newIterator(self, 0, 4, 67);
		assertWellFormed(true, it);
	}
	
	public void testI5() {
		Integer[] a = new Integer[7];
		self = spy.newInstance(a, 0, 67);
		it = spy.newIterator(self, 0, 7, 67);
		assertWellFormed(false, it);
	}
	
	public void testI6() {
		Integer[] a = new Integer[7];
		self = spy.newInstance(a, 0, 67);
		it = spy.newIterator(self, 0, 6, 67);
		assertWellFormed(true, it);
	}
	
	public void testI7() {
		Integer[] a = new Integer[7];
		self = spy.newInstance(a, 0, 67);
		it = spy.newIterator(self, 0, -4, 67);//index underflow
		assertWellFormed(false, it);
	}
	
	public void testI8() {
		Integer[] a = new Integer[7];
		self = spy.newInstance(a, 0, 67);
		it = spy.newIterator(self, 0, 0, 67);
		assertWellFormed(true, it);
	}
	
	public void testI9() {
		Integer[] a = new Integer[7];
		self = spy.newInstance(a, 0, 67);
		it = spy.newIterator(self, 1, 0, 67);
		assertWellFormed(false, it);
	}
	
	
	public void testJ0() {
		Integer[] a = new Integer[7];
		a[2] = 11;
		self = spy.newInstance(a, 0, 67);
		it = spy.newIterator(self, 0, 0, 42);//version mismatch
		assertWellFormed(false, it);
	}
	
	public void testJ1() {
		Integer[] a = new Integer[7];
		a[2] = 11;
		self = spy.newInstance(a, 1, 67);
		it = spy.newIterator(self, 1, -1, 67);
		assertWellFormed(true, it);
	}
	
	public void testJ2() {
		Integer[] a = new Integer[7];
		a[2] = 11;
		self = spy.newInstance(a, 1, 67);
		it = spy.newIterator(self, 1, 0, 67);
		assertWellFormed(true, it);
	}
	
	public void testJ3() {
		Integer[] a = new Integer[7];
		a[2] = 11;
		self = spy.newInstance(a, 1, 67);
		it = spy.newIterator(self, 1, 1, 67);
		assertWellFormed(true, it);
	}
	
	public void testJ4() {
		Integer[] a = new Integer[7];
		a[2] = 11;
		self = spy.newInstance(a, 1, 67);
		it = spy.newIterator(self, 1, 2, 67);
		assertWellFormed(false, it);
	}
	
	public void testJ5() {
		Integer[] a = new Integer[7];
		a[2] = 11;
		self = spy.newInstance(a, 1, 67);
		it = spy.newIterator(self, 0, 2, 67);
		assertWellFormed(true, it);
	}
	
	public void testJ6() {
		Integer[] a = new Integer[7];
		a[2] = 11;
		self = spy.newInstance(a, 1, 67);
		it = spy.newIterator(self, 0, 1, 67);
		assertWellFormed(false, it);
	}
	
	public void testJ7() {
		Integer[] a = new Integer[7];
		a[2] = 11;
		self = spy.newInstance(a, 1, 67);
		it = spy.newIterator(self, 0, 3, 67);
		assertWellFormed(true, it);
	}

	public void testJ8() {
		Integer[] a = new Integer[7];
		a[2] = 11;
		self = spy.newInstance(a, 1, 67);
		it = spy.newIterator(self, 0, 1, 100);
		assertWellFormed(true, it);
	}

	public void testJ9() {
		Integer[] a = new Integer[7];
		a[2] = 11;
		self = spy.newInstance(a, 1, 67);
		it = spy.newIterator(self, 0, 6, 67);
		assertWellFormed(true, it);
	}

	
	public void testK0() {
		Integer[] a = new Integer[7];
		a[1] = -3;
		a[5] = 100;
		self = spy.newInstance(a, 2, 119);
		it = spy.newIterator(self, 0, 6, 119);
		assertWellFormed(true, it);
	}
	
	public void testK1() {
		Integer[] a = new Integer[7];
		a[1] = -3;
		a[5] = 100;
		self = spy.newInstance(a, 2, 119);
		it = spy.newIterator(self, 0, 5, 119);
		assertWellFormed(true, it);
	}
	
	public void testK2() {
		Integer[] a = new Integer[7];
		a[1] = -3;
		a[5] = 100;
		self = spy.newInstance(a, 2, 119);
		it = spy.newIterator(self, 0, 4, 119);
		assertWellFormed(false, it);
	}
	
	public void testK3() {
		Integer[] a = new Integer[7];
		a[1] = -3;
		a[5] = 100;
		self = spy.newInstance(a, 2, 119);
		it = spy.newIterator(self, 1, 5, 119);
		assertWellFormed(false, it);
	}
	
	public void testK4() {
		Integer[] a = new Integer[7];
		a[1] = -3;
		a[5] = 100;
		self = spy.newInstance(a, 2, 119);
		it = spy.newIterator(self, 1, 4, 119);
		assertWellFormed(true, it);
	}
	
	public void testK5() {
		Integer[] a = new Integer[7];
		a[1] = -3;
		a[5] = 100;
		self = spy.newInstance(a, 2, 119);
		it = spy.newIterator(self, 1, 3, 119);
		assertWellFormed(true, it);
	}
	
	public void testK6() {
		Integer[] a = new Integer[7];
		a[1] = -3;
		a[5] = 100;
		self = spy.newInstance(a, 2, 119);
		it = spy.newIterator(self, 1, 2, 119);
		assertWellFormed(true, it);
	}
	
	public void testK7() {
		Integer[] a = new Integer[7];
		a[1] = -3;
		a[5] = 100;
		self = spy.newInstance(a, 2, 119);
		it = spy.newIterator(self, 1, 1, 119);
		assertWellFormed(true, it);
	}
	
	public void testK8() {
		Integer[] a = new Integer[7];
		a[1] = -3;
		a[5] = 100;
		self = spy.newInstance(a, 2, 119);
		it = spy.newIterator(self, 1, 0, 119);
		assertWellFormed(false, it);
	}
	
	public void testK9() {
		Integer[] a = new Integer[7];
		a[1] = -3;
		a[5] = 100;
		self = spy.newInstance(a, 2, 119);
		it = spy.newIterator(self, 2, 0, 119);
		assertWellFormed(true, it);
	}
	
	
	public void testL0() {
		Integer[] a = new Integer[7];
		a[0] = 0;
		a[1] = 1;
		a[3] = 3;
		a[5] = 5;
		a[6] = 6;
		self = spy.newInstance(a, 5, 119);
		it = spy.newIterator(self, 5, -1, 119);
		assertWellFormed(true, it);
		it = spy.newIterator(self, 4, -1, 119);
		assertWellFormed(false, it);
		it = spy.newIterator(self, 6, -1, 119);
		assertWellFormed(false, it);
	}
	
	public void testL1() {
		Integer[] a = new Integer[7];
		a[0] = 0;
		a[1] = 1;
		a[3] = 3;
		a[5] = 5;
		a[6] = 6;
		self = spy.newInstance(a, 5, 119);
		it = spy.newIterator(self, 5, 0, 119);
		assertWellFormed(false, it);
		it = spy.newIterator(self, 4, 0, 119);
		assertWellFormed(true, it);
		it = spy.newIterator(self, 3, 0, 119);
		assertWellFormed(false, it);
	}
	
	public void testL2() {
		Integer[] a = new Integer[7];
		a[0] = 0;
		a[1] = 1;
		a[3] = 3;
		a[5] = 5;
		a[6] = 6;
		self = spy.newInstance(a, 5, 119);
		it = spy.newIterator(self, 3, 1, 119);
		assertWellFormed(true, it);
		it = spy.newIterator(self, 4, 1, 119);
		assertWellFormed(false, it);
		it = spy.newIterator(self, 2, 1, 119);
		assertWellFormed(false, it);
	}
	
	public void testL3() {
		Integer[] a = new Integer[7];
		a[0] = 0;
		a[1] = 1;
		a[3] = 3;
		a[5] = 5;
		a[6] = 6;
		self = spy.newInstance(a, 5, 119);
		it = spy.newIterator(self, 2, 2, 119);
		assertWellFormed(false, it);
		it = spy.newIterator(self, 3, 2, 119);
		assertWellFormed(true, it);
		it = spy.newIterator(self, 4, 2, 119);
		assertWellFormed(false, it);
	}
	
	public void testL4() {
		Integer[] a = new Integer[7];
		a[0] = 0;
		a[1] = 1;
		a[3] = 3;
		a[5] = 5;
		a[6] = 6;
		self = spy.newInstance(a, 5, 119);
		it = spy.newIterator(self, 3, 3, 119);
		assertWellFormed(false, it);
		it = spy.newIterator(self, 2, 3, 119);
		assertWellFormed(true, it);
		it = spy.newIterator(self, 1, 3, 119);
		assertWellFormed(false, it);
	}
	
	public void testL5() {
		Integer[] a = new Integer[7];
		a[0] = 0;
		a[1] = 1;
		a[3] = 3;
		a[5] = 5;
		a[6] = 6;
		self = spy.newInstance(a, 5, 119);
		it = spy.newIterator(self, 1, 4, 119);
		assertWellFormed(false, it);
		it = spy.newIterator(self, 2, 4, 119);
		assertWellFormed(true, it);
		it = spy.newIterator(self, 3, 4, 119);
		assertWellFormed(false, it);
	}
	
	public void testL6() {
		Integer[] a = new Integer[7];
		a[0] = 0;
		a[1] = 1;
		a[3] = 3;
		a[5] = 5;
		a[6] = 6;
		self = spy.newInstance(a, 5, 119);
		it = spy.newIterator(self, 2, 5, 119);
		assertWellFormed(false, it);
		it = spy.newIterator(self, 1, 5, 119);
		assertWellFormed(true, it);
		it = spy.newIterator(self, 0, 5, 119);
		assertWellFormed(false, it);
	}
	
	public void testL7() {
		Integer[] a = new Integer[7];
		a[0] = 0;
		a[1] = 1;
		a[3] = 3;
		a[5] = 5;
		a[6] = 6;
		self = spy.newInstance(a, 5, 119);
		it = spy.newIterator(self, 1, 6, 119);
		assertWellFormed(false, it);
		it = spy.newIterator(self, 0, 6, 119);
		assertWellFormed(true, it);
		it = spy.newIterator(self, -1, 6, 119);
		assertWellFormed(false, it);
	}
	
	public void testL8() {
		Integer[] a = new Integer[7];
		a[0] = 0;
		a[1] = 1;
		a[3] = 3;
		a[5] = 5;
		a[6] = 6;
		self = spy.newInstance(a, 5, 119);
		it = spy.newIterator(self, 0, 7, 119);
		assertWellFormed(false, it);
		it = spy.newIterator(self, 0, 6, 101);
		assertWellFormed(true, it);
		it = spy.newIterator(self, 0, 5, 101);
		assertWellFormed(true, it);
	}
}
