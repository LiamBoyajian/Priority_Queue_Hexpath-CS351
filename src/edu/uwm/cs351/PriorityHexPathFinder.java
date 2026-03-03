package edu.uwm.cs351;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * Render a file of hex tiles on the screen, and then
 * perform a priority search on it. <br/>
 * Usage: &lt;filename&gt; &lt;start&gt; &lt;end&gt; {&lt;terrain&gt;=&lt;cost&gt;, ...}<br/>
 * The "start" and "end" are three element hex coordinates, e.g., <code>&lt2,1,1&gt;</code>.
 * The terrain cost map shows the costs to traverse a half hexagon e.g., <code>{LAND=2 DESERT=10}</code>.
 */
public class PriorityHexPathFinder extends JFrame {
	/**
	 * Eclipse wants this
	 */
	private static final long serialVersionUID = 1L;

	public static void main(final String[] args) {
		final HexBoard board = new HexBoard();
		if (args.length != 4) {
			System.out.println("Set Run>Run Configurations>Arguments>Program Arguments to have four arguments:");
			System.out.println("\ta filename (file of hextiles)");
			System.out.println("\ta starting hex coordinate");
			System.out.println("\tan ending hex coordinate");
			System.out.println("\tthe terrain cost map");
			System.exit(1);
		}
		PriorityHexWorklist worklist;
		HexCoordinate start, end;
		TerrainCostMap map;
		try {
			readSeq(board, new BufferedReader(new FileReader(args[0])));
			start = HexCoordinate.fromString(args[1]);
			end = HexCoordinate.fromString(args[2]);
			map = TerrainCostMap.fromString(args[3]);
		} catch (IOException|FormatException|IllegalArgumentException e) {
			System.out.println(e.getMessage());
			System.exit(1);
			return;
		}
		worklist = new PriorityHexWorklist(map);
		final Search search = new Search(map.asTerrainSet(), worklist);
		final HexPath path = search.find(start, end, board);
		if (path == null) {
			System.out.println("No path found.");
		} else {
			System.out.println("Path cost: " + worklist.getCost(board.get(end)));
		}
		final String title = "Priority search from " + start + " to " + end;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				PriorityHexPathFinder x = new PriorityHexPathFinder(title,board,search,path);
				x.setSize(500, 300);
				x.setVisible(true);
				x.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			}
		});
	}
	
	private static void readSeq(HexBoard b, BufferedReader r) throws IOException {
		String input;
		while ((input = r.readLine()) != null) {
			if (input.startsWith("#")) continue;
			try {
				HexTile tile = HexTile.fromString(input);
				b.add(new HexBoard.HexPiece(tile.getTerrain(), tile.getLocation()));
			} catch (FormatException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	@SuppressWarnings("serial")
	public PriorityHexPathFinder(final String title, final HexBoard b, final Search search, final HexPath path) {
		super(title);
		this.setContentPane(new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				for (HexBoard.HexPiece p : b) {
					p.asTile().draw(g);
				}
				search.markVisited(g);
				if (path != null) {
					g.setColor(Color.MAGENTA);
					((Graphics2D)g).setStroke(new BasicStroke(3));
					HexPathOperations.draw(path, g, HexTile.WIDTH);
				}
			}
		});
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
}
