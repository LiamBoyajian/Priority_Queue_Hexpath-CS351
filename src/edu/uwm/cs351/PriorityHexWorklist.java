package edu.uwm.cs351;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;

import edu.uwm.cs351.HexBoard.HexPiece;
import edu.uwm.cs351.util.Worklist;

/**
 * A special case worklist for hex pieces. We use a terrain cost map to
 * determine which piece should be next explored: the one at lowest cost from
 * the start. The cost counts each half-hexagon of terrain using the cost map.
 * So when going from one hex piece to an adjacent one, we add the cost of each
 * one's terrain. This implementation assumes that other than the starting
 * piece, each piece is added because it was adjacent to the hex piece most
 * recently returned from {@link #next}.
 * <p>
 * Nothing in this class accesses the neighbors or the location of the pieces.
 * They don't need to be on a board.
 */
public class PriorityHexWorklist implements Worklist<HexPiece> {

	// Data Structure

	// - use the library PriorityQueue class
	PriorityQueue<HexPiece> queue;
	// - use a HashMap to keep track of costs to reach each hex piece from the start
	HashMap<HexPiece,Integer> costPerPiece = new HashMap<HexPiece, Integer>();
	
	TerrainCostMap costs;
	PriceComparator comparator;
	// - No wellFormed expected
	HexPiece lastNext = null;

	public class PriceComparator implements Comparator<HexPiece> {

		@Override // required
		public int compare(HexPiece o1, HexPiece o2) {
			return costPerPiece.get(o1) - costPerPiece.get(o2);
		}

	}

	public PriorityHexWorklist(TerrainCostMap m) {
		//
		comparator = new PriceComparator();
		queue = new PriorityQueue<HexPiece>(comparator);
		// The priority queue should use the map to order pieces
		costs = m;
	}
	
	// Keep track of he current hex piece (piece last returned by next).
	// If this is null, it means the worklist is being initialized.
	// Otherwise, it represents the most recently returned hex piece.
	// Every time a piece is added, store the cost to reach it in the map,
	// before adding it to the priority queue.

	/**
	 * Return the cost of the cheapest path from the starting piece to this given
	 * piece, as currently computed. This method is used for testing.
	 * 
	 * @param p end point being asked about, must not be null
	 * @return null if the piece has not yet been added to this worklist
	 */
	public Integer getCost(HexPiece p) {
		if(costPerPiece.containsKey(p)) return costPerPiece.get(p);
		
		//if(p == lastNext) return 0;
		
		if (lastNext == null)
			return null;		
		
		while (lastNext != p && queue.size() > 0) {
			for (HexPiece n : lastNext.neighbors) {
				if(n == null || costPerPiece.containsKey(n)) continue;
				queue.add(n);
			}
			next();
		}
		if(lastNext == p) return costPerPiece.get(p);
		return null;
	}

	@Override
	public boolean hasNext() {
		return queue.size() > 0;
	}

	@Override
	public HexPiece next() {

		if (!hasNext())
			throw new NoSuchElementException("No more elements");
		lastNext = queue.poll();
		return lastNext;
	}

	@Override
	public void add(HexPiece element) {
		if (queue.contains(element))
			throw new IllegalArgumentException("duplicate element");
		if(lastNext != null) {
			costPerPiece.put(element, costPerPiece.get(lastNext)+costs.get(lastNext.getTerrain()) + costs.get(element.getTerrain()));
		}else {
			costPerPiece.put(element,0);
		}
			
		queue.add(element);
	}
}
