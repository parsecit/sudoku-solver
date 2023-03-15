package sudoku;

import java.io.PrintWriter;

/**
 * Generates readable representations of the Sudoku board.
 */
public class Viewer
{
	/**
	 * Outputs the board with all known values.
	 */
	public static void view(Board board, PrintWriter writer)
	{
		for (int y = 0; y < 9; ++y)
		{
			if (y % 3 == 0) writer.write("-------------------------------\n");
			for (int x = 0; x < 9; ++x)
			{
				if (x % 3 == 0) writer.write("|");
				Integer value = board.getCellValue(x, y);
				writer.write(" " + (value != null ? value + 1 : " ") + " ");
			}
			writer.write("|\n");
		}
		writer.write("-------------------------------\n");
	}

	/**
	 * Outputs the board showing all values which are still possible for each cell.
	 */
	public static void inspect(Board board, PrintWriter writer)
	{
		for (int y = 0; y < 9; ++y)
		{
			if (y % 3 == 0) writer.write("-------------------------------------------------------------------------------------------------------------------------\n");
			for (int x = 0; x < 9; ++x)
			{
				if (x % 3 == 0) writer.write("|");
				int bits = board.getCellBits(x, y);
				String output = "{";
				for (int i = 0; i < 9; ++i) output += ((bits & (1 << i)) > 0) ? "" + (i + 1) : " ";
				output += "}";
				writer.write(" " + output + " ");
			}
			writer.write("|\n");
		}
		writer.write("-------------------------------------------------------------------------------------------------------------------------\n");
	}
}
