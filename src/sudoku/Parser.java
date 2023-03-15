package sudoku;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * A class to read in a textual representation of a Sudoku board.
 * 
 * The format is 9 lines with 9 characters, each character being a space or 1 - 9.
 */
public class Parser
{
	/**
	 * Added to input lines so that parsing doesn't fail if trailing spaces
	 * for the line are omitted.
	 */
	public static final String NINE_SPACES = "         ";

	/**
	 * Parser output is a set of CellValues, listing the x and y co-ordinates
	 * of the known values in the Sudoku board.
	 */
	public static class CellValue
	{
		public final int x;
		public final int y;
		public final int value;

		public CellValue(int x, int y, int value)
		{
			this.x = x;
			this.y = y;
			this.value = value;
		}
	}

	/**
	 * Reads 9 lines of 9 characters and returns a CellValue for all non-empty cells.
	 */
	public static Set<CellValue> parse(BufferedReader reader) throws IOException
	{
		Set<CellValue> cellValues = new HashSet<>();
		for (int y = 0; y < 9; ++y)
		{
			String line = reader.readLine() + NINE_SPACES;
			for (int x = 0; x < 9; ++x)
			{
				String valueText = line.substring(x, x + 1);
				if (!" ".equals(valueText))
				{
					int value = Integer.parseInt(valueText);
					cellValues.add(new CellValue(x, y, value));
				}
			}
		}
		return cellValues;
	}

	/**
	 * Reads the board in and sets the known cell values.
	 * Note that the board uses values 0 - 8, not 1 - 9.
	 */
	public static void parseInto(BufferedReader reader, Board board) throws IOException
	{
		Set<CellValue> cellValues = Parser.parse(reader);
		for (CellValue cellValue : cellValues)
		{
			board.setCell(cellValue.x, cellValue.y, cellValue.value - 1);
		}
	}
}
