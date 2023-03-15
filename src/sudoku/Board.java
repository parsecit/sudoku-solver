package sudoku;

/**
 * Represents a Sudoku board, and implements the logical operations for solving it.
 * Uses values 0 - 8.   These need to be mapped to 1 - 9 for a standard Sudoku puzzle.
 */
public class Board
{
	/**
	 * A cell in the Sudoku board.
	 * Contains a bitmap representing the values which are possible for the cell.
	 * bits is therefore in the range 0 - 511 (9 bits).
	 */
	public static class Cell
	{
		public int bits;

		public Cell(int bits)
		{
			this.bits = bits;
		}
	}

	/**
	 * All the lowest 9 bits set to 1.
	 */
	public static int ALL_ONES = 511;

	/**
	 * The board is a 9x9 array of cells.
	 */
	public Cell[][] board = createBoard();

	/**
	 * Create a board with all values possible for all cells.
	 */
	public Cell[][] createBoard()
	{
		Cell[][] board = new Cell[9][9];
		for (int x = 0; x < 9; ++x)
		{
			for (int y = 0; y < 9; ++y)
			{
				board[x][y] = new Cell(ALL_ONES);
			}
		}
		return board;
	}

	/**
	 * Is there any cell which does not have a single possible value?
	 */
	public boolean isComplete()
	{
		for (Cell[] cells : board)
		{
			for (Cell cell : cells)
			{
				if (bitCount(cell.bits) > 1) return false;
			}
		}
		return true;
	}

	/**
	 * Return the bitmap for the given cell.
	 */
	public int getCellBits(int x, int y)
	{
		return board[x][y].bits;
	}

	/**
	 * Return the value (0-8) of the given cell.
	 * Returns null if the value is not yet known.
	 */
	public Integer getCellValue(int x, int y)
	{
		int bits = board[x][y].bits;
		if (bitCount(bits) == 1)
		{
			for (int i = 0; i < 9; ++i)
			{
				if ((bits & (1 << i)) > 0)
				{
					return i;
				}
			}
		}
		return null;
	}

	/**
	 * Set a fixed value for the given cell.
	 * Removes all the bits representing the other values.
	 */
	public void setCell(int x, int y, int value)
	{
		board[x][y].bits = 1 << value;
	}

	/**
	 * Run through up to maxIterations iterations of the solving process to
	 * try to solve the board.  Returns the number of iterations actually
	 * run.
	 */
	public int solve(int maxIterations)
	{
		int iterations = 0;
		for (iterations = 0; iterations < maxIterations && !isComplete(); ++iterations)
		{
			processIteration();
		}
		return iterations;
	}

	/**
	 * A 'house' is a section of the board which contains 1 - 9.
	 * These are the vertical lines, the horizontal lines and the 3x3 squares.
	 *
	 * The rule implemented here is:
	 *  - IF there is a combination of n cells within the house
	 *  - AND between all the cells, there are only n possible values
	 *  - THEN all the other (9-n) cells cannot contain any of those possible values.
	 *
	 * Combinations of 1 - 9 cells are exhaustively listed by using a bitmap which
	 * ranges from 0 - 511 (and should probably just be 1 - 510).
	 */
	public static void processHouse(Cell[] house)
	{
		// For every combination of bits, representing every possible
		// combination of cells in the house ...
		for (int mask = 0; mask < ALL_ONES; ++mask)
		{
			// Work out the union of all possible bits in the cells identified in the mask.
			int union = 0;
			for (int i = 0; i < 9; ++i)
			{
				if ((mask & (1 << i)) > 0) union |= house[i].bits;
			}
			// If the number of possible values equals the number of cells ...
			if (bitCount(union) == bitCount(mask))
			{
				int unionInverse = ALL_ONES ^ union;
				// Then no other cells in the house can contain any of the values
				for (int i = 0; i < 9; ++i)
				{
					if ((mask & (1 << i)) == 0) house[i].bits = house[i].bits & unionInverse;
				}
			}
		}
	}

	/**
	 * Generate all houses and pass them through processHouse().
	 * Processes vertical lines, horizontal lines and 3x3 squares.
	 */
	public void processAllHouses()
	{
		// Re-used to hold the cells in each house processed.
		Cell[] house = new Cell[9];
		// Vertical houses
		for (int x = 0; x < 9; ++x)
		{
			for (int y = 0; y < 9; ++y) house[y] = board[x][y];
			processHouse(house);
		}
		// Horizontal houses
		for (int y = 0; y < 9; ++y)
		{
			for (int x = 0; x < 9; ++x) house[x] = board[x][y];
			processHouse(house);
		}
		// Square houses
		for (int x = 0; x < 9; x += 3)
		{
			for (int y = 0; y < 9; y += 3)
			{
				for (int i = 0; i < 3; ++i)
				{
					for (int j = 0; j < 3; ++j)
					{
						house[(i * 3) + j] = board[x + i][y + j];
					}
				}
				processHouse(house);
			}
		}
	}

	/**
	 * Applies the 'pivot' rule to a single scenario.
	 * 
	 * The pivot rule is that for any arrangement of cells:
	 * 
	 *     a b c * * * d e f
	 *           u v w
	 *           x y z
	 *  
	 * if there is a value which cannot be the value of any of a, b, c, d, e, f
	 * then the same value cannot be the value of any of u, v, w, x, y, z.
	 * 
	 * And vice-versa.
	 * 
	 * This is because any value which cannot be in a - f MUST be in one of
	 * the 'pivot' cells shown as asterisks.
	 */
	public static void processPivot(Cell a, Cell b, Cell c, Cell d, Cell e, Cell f, Cell u, Cell v, Cell w, Cell x, Cell y, Cell z)
	{
		int union = 0 | a.bits | b.bits | c.bits | d.bits | e.bits | f.bits;
		u.bits &=  union;
		v.bits &=  union;
		w.bits &=  union;
		x.bits &=  union;
		y.bits &=  union;
		z.bits &=  union;
		union = 0 | u.bits | v.bits | w.bits | x.bits | y.bits | z.bits;
		a.bits &=  union;
		b.bits &=  union;
		c.bits &=  union;
		d.bits &=  union;
		e.bits &=  union;
		f.bits &=  union;
	}

	/**
	 * Lists all sets of cells forming a 'pivot' as described above, and
	 * runs each set through processPivot().
	 */
	public void processAllPivots()
	{
		for (int x = 0; x < 9; x += 3)
		{
			for (int y = 0; y < 9; y += 3)
			{
				for (int i = 0 ; i < 3; ++i)
				{
					int x1 = x + i;
					int x2 = x + ((i + 1) % 3);
					int x3 = x + ((i + 2) % 3);
					processPivot(
							board[x2][y], board[x2][y + 1], board[x2][y + 2], board[x3][y], board[x3][y + 1], board[x3][y + 2],
							board[x1][(y + 3) % 9], board[x1][(y + 4) % 9], board[x1][(y + 5) % 9], board[x1][(y + 6) % 9], board[x1][(y + 7) % 9], board[x1][(y + 8) % 9]);
				}
				for (int j = 0 ; j < 3; ++j)
				{
					int y1 = y + j;
					int y2 = y + ((j + 1) % 3);
					int y3 = y + ((j + 2) % 3);
					processPivot(
							board[x][y2], board[x + 1][y2], board[x + 2][y2], board[x][y3], board[x + 1][y3], board[x + 2][y3],
							board[(x + 3) %9][y1], board[(x + 4) %9][y1], board[(x + 5) %9][y1], board[(x + 6) %9][y1], board[(x + 7) %9][y1], board[(x + 8) %9][y1]);
				}
			}
		}
	}

	/**
	 * Runs a single solving iteration which applies both the 'houses' and 'pivots' rules.
	 */
	public void processIteration()
	{
		processAllHouses();
		processAllPivots();
	}

	/**
	 * Returns the number of set bits in the given integer.
	 */
	public static int bitCount(int bitmap)
	{
		int count = 0;
		for (int i = 0; i < 9; ++i) count += ((bitmap & (1 << i)) > 0) ? 1 : 0;
		return count;
	}
}
