package sudoku;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;

/**
 * Reads a Sudoku board from standard input, solves it, and prints the solution.
 */
public class Main
{
	public static int MAX_ITERATIONS = 1000;

	public static void main(String[] args) throws Exception
	{
		// Create the board.
		Board board = new Board();
		// Read the board contents in from standard input.
		try (Reader reader = new InputStreamReader(System.in))
		{
			try (BufferedReader buffer = new BufferedReader(reader))
			{
				Parser.parseInto(buffer, board);
			}
		}
		try (PrintWriter writer = new PrintWriter(System.out))
		{
			// Print out the board as read in.
			Viewer.view(board, writer);
			// Solve the board.
			int iterations = board.solve(MAX_ITERATIONS);
			// Print the solved board out.
			Viewer.view(board, writer);
			// Did it work?
			writer.println((board.isComplete() ? "COMPLETE " : "NOT COMPLETE") + "After " + iterations + " iterations.");
			// Print out the internal state of the board (all values still possible for each cell).
			Viewer.inspect(board, writer);
		}
	}
}
