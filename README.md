# sudoku-solver
Constraint propagation Sudoku solver by Jon Moore.

## Overview

This is an attempt to create a solver for Sudoku puzzles which solves
everything that can be solved 'without backtracking'.

'Without backtracking' seems like a straightforward idea when starting
out, but it turns out to be much more nebulous than I thought.  The
idea was to find a solver whose logic did not at any point hypothesise
the value of a cell, determine if that value leads to a contradiction,
and then remove that value from consideration.  Instead, the values of
cells are worked out by applying rules which eliminate values based on
other values in the grid.

This solution works quite well:

- There is no explicit search or backtracking, just constraint
  propagation.
- In trials, it solves at least 90% of the 'Evil' puzzles from
  sudoku.com.
- As far as I can tell, the puzzles it can't solve all require
  some form of backtracking search.

(Of course, it is entirely likely that there are non-backtracking
strategies that I simply haven't come up with that would deal with
some or all of the problem cases.  The `games` directory contains
a small selection of Sudoku boards that this solver can't complete.)

## Running the Solver

Run the `sudoku.Main.main` method.

Type / paste the board contents into the standard input.  The format
is just the digits 1-9 in a grid representing the board, with spaces
where there is no value and carriage returns at the end of lines.
See the files in the `games` folder for examples.

The output shows the values still considered possible for each cell.
If the board has been solved, there will be only one value listed
for each cell.

## Rules

There are only two rules implemented in this solver, which are applied
repeatedly until a solution is found (or a limit on the number of
iterations is reached).  Typically between 4 and 6 iterations seem
to be required for a board which can be solved - each iteration applying
each of the rules once.

### House Rule

'House' is (apparently) the Sudoku term for a set of 9 cells which must
contain the digits 1-9 - i.e. the horizontal lines, vertical lines and
9-cell squares.

The house rule is:

If it is possible to find a set of N cells in a house, and there are only
N distinct values which are possible in any of those N cells, then none of
the other 9-N cells in the house can contain any of those values.

(This is a generalisation of the well-known rule that if a house contains
two cells which can only contain the same two values, none of the other
cells can contain those values.)

### Pivot Rule

The pivot rule is:

If there are two houses, A and B, which share 3 cells, and the unshared
cells in house A cannot contain value V, then the unshared cells in house
B also cannot contain value V.

For example, take a horizontal house overlapping with a square house:

```
      A B C
D E F G H I J K L
      M N O
```

If (A, B, C, M, N, O) cannot contain the value V, then (G, H, I) must
contain value V, and therefore (D, E, F, J, K, L) cannot contain V.

## Implementation

The implementation keeps a bitmap for each cell in the board which
represents the values which are still possible for that cell.

Repeated application of the two rules above removes values from the
set of possibilities until either there is only one possible value for
every cell, or a limit on the number of iterations is reached, and we
conclude that this puzzle needs backtracking search, or cleverer
rules.






