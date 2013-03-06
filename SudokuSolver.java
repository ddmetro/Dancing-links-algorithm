import java.util.ArrayList;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.util.Scanner;
import java.io.IOException;
import java.io.FileNotFoundException;

public class SudokuSolver extends DLXSolver
{
  int size = 9;

	public SudokuSolver(int sudokuSquareLength)
	{
		super();
		size = sudokuSquareLength;
		columnCount = size*size*4;
		rowCount = size*size*size;
		matrix = new Node[rowCount+1][columnCount+1];

		initialize();
	}

	public void initialize()
	{
		initializeColumnHeads();
		//System.out.println("done with columnHeads");
		initializeRows();
		//System.out.println("done with rows");
		linkColumns();
		//System.out.println("done with linking Columns");
		initializeFilledCells();
	}

	public void initializeColumnHeads()
	{
		for(int col = 0; col < columnCount + 1; col++)
		{
			matrix[0][col] = new Node(""+0+"#"+col+"#"+0, 0, null);
		}

		for(int col = 1; col < columnCount; col++)
		{
			matrix[0][col].right = matrix[0][col+1];
		}
		matrix[0][columnCount].right = root;

		for(int col = 2; col < columnCount + 1; col++)
		{
			matrix[0][col].left = matrix[0][col-1];
		}
		matrix[0][1].left = root;

		root.right = matrix[0][1];
		root.left  = matrix[0][columnCount];
	}
	
	private void initializeRows()
	{
		int matrixRowIndex = 1;
		int row, col;
		Node n1, n2, n3, n4;

		ArrayList columns[] = new ArrayList[columnCount+1];
		for(int count = 0; count < columnCount + 1; count++)
		{
			columns[count] = new ArrayList();
		}

		for(int sudokuRowIndex = 1; sudokuRowIndex <= size; sudokuRowIndex++)
		{
			for(int sudokuColIndex = 1; sudokuColIndex <= size; sudokuColIndex++)
			{
				for(int sudokuCellValue = 1; sudokuCellValue <= size; sudokuCellValue++, matrixRowIndex++)
				{
					//System.out.println("***********************************");
					row = matrixRowIndex;

					col = size * (sudokuRowIndex - 1) + sudokuCellValue;
					n1 = new Node(""+sudokuRowIndex+"#"+sudokuColIndex+"#"+sudokuCellValue, -1, matrix[0][col]);
					matrix[row][col] = n1;
					//System.out.println("row:" + row+ ", col:" + col);
					columns[col].add(row);

					col = size * (sudokuColIndex - 1) + sudokuCellValue + (size*size);
					n2 = new Node(""+sudokuRowIndex+"#"+sudokuColIndex+"#"+sudokuCellValue, -1, matrix[0][col]);
					matrix[row][col] = n2;
					//System.out.println("row:" + row+ ", col:" + col);
					columns[col].add(row);

					col = size * (sudokuRowIndex - 1) + sudokuColIndex + (size*size*3);
					n4 = new Node(""+sudokuRowIndex+"#"+sudokuColIndex+"#"+sudokuCellValue, -1, matrix[0][col]);
					matrix[row][col] = n4;
					//System.out.println("row:" + row+ ", col:" + col);
					columns[col].add(row);

					int subSquareIndex = getSubSquareIndex(sudokuRowIndex, sudokuColIndex);
					col = (size * size * 2) + sudokuCellValue + (size * (subSquareIndex - 1));
					n3 = new Node(""+sudokuRowIndex+"#"+sudokuColIndex+"#"+sudokuCellValue, -1, matrix[0][col]);
					matrix[row][col] = n3;
					//System.out.println("row:" + row+ ", col:" + col);
					columns[col].add(row);
					//System.out.println("***********************************");

					n1.right = n2;
					n2.right = n3;
					n3.right = n4;
					n4.right = n1;

					n1.left = n4;
					n2.left = n1;
					n3.left = n2;
					n4.left = n3;
				}			
			}			
		}

		//printDLXMatrix(columns);
	}

	private int getSubSquareIndex(int sudokuRow, int sudokuCol)
	{
		int subSquareSize = (int)Math.sqrt(size);
		int subSquareIndex = 1;

		for(int row = 1; row <= size; row = row + subSquareSize)
		{
			for(int col = 1; col <= size; col = col + subSquareSize, subSquareIndex++)
			{
				if((row <= sudokuRow && sudokuRow < (row + subSquareSize)) && (col <= sudokuCol && sudokuCol < (col + subSquareSize)))
				{
					return subSquareIndex;
				}
			}
		}

		return -1;
	}

	private void linkColumns()
	{
		Node prev = null, curr = null;
		int size;
		for(int col = 1; col <= columnCount; col++)
		{
			prev = matrix[0][col];//columnHead
			size = 0;

			for(int row = 1; row <= rowCount; row++)
			{
				if(matrix[row][col] != null)
				{
					curr = matrix[row][col];
					prev.down = curr;
					curr.up = prev;
					prev = curr;
					size++;
				}
			}

			prev.down = matrix[0][col];
			matrix[0][col].up = prev;
			matrix[0][col].size = size;
		}
	}

	private void initializeFilledCells()
	{
		Scanner sc = null;
		try
		{
			sc = new Scanner(new File("sudoku_3"));			
		}
		catch(FileNotFoundException e)
		{
			System.out.println("exception occurred while reading from the file: " + e);
		}

		int row = 0;
		while(row < size)
		{
			String[] cells = sc.nextLine().split("\\|");
			for(int count = 0; count < cells.length; count++)
			{
				if(!cells[count].trim().equals(""))
				{
					int val = Integer.parseInt(cells[count].trim());
					processPrefilledCell(getCellColumn(row+1, count+1, val));
				}
			}
			row++;
		}		
	}

	private Node getCellColumn(int sudokuRowIndex, int sudokuColumnIndex, int sudokuCellValue)
	{
		int col = size * (sudokuRowIndex - 1) + sudokuColumnIndex + (size*size*3);
		int row = (sudokuRowIndex-1)*size*size + (sudokuColumnIndex-1)*size + sudokuCellValue;
		return matrix[row][col];
	}

	private void processPrefilledCell(Node n)
	{
		coverColumn(n.columnHead);
		solution.put(filledCellCount++, n);
		for(Node r = n.right; r != n; r = r.right)
		{
			System.out.println("r: " + r);
			coverColumn(r.columnHead);
		}
	}

	protected void printSolution(int k)
	{
		//System.out.println("cellCount: " + k);
		int[][] ans = new int[size][size];
		for(int i = 0; i < k; i++)
		{
			String s = solution.get(i).toString();
			String[] token = s.split("#");
			int row = Integer.parseInt(token[0]);
			int col = Integer.parseInt(token[1]);
			int val = Integer.parseInt(token[2]);
			ans[row-1][col-1] = val;
		}

		for(int r = 0; r < size; r++)
		{
			for(int c = 0; c < size; c++)
			{
				System.out.print(ans[r][c] + " ");
			}
			System.out.println();
		}
	}

	private void printDLXMatrix(ArrayList columns[])
	{
		try
		{
			FileWriter fw = new FileWriter(new java.io.File("cols"));
			BufferedWriter bw = new BufferedWriter(fw);

			for(int count = 1; count < columnCount + 1; count++)
			{
				bw.write("ColumnIndex: " + count + "\n");
				for(int i = 0; i < columns[count].size(); i++)
				{
					bw.write(columns[count].get(i) + "\n");
				}
			}

			bw.close();

			fw = new FileWriter(new java.io.File("cols-3"));
			bw = new BufferedWriter(fw);

			for(int count = 1; count <= 9; count++)
			{
				for(int i = 1; i <= 9; i++)
				{
					bw.write(getSubSquareIndex(count, i) + " ");
				}
				bw.write("\n");
			}

			bw.close();

		}
		catch(IOException e)
		{
			System.out.println("Exception occurred while printing matrix: " + e);
		}		
	}

}//end of class SudokuSolver
