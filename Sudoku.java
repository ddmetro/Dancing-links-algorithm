public class Sudoku 
{

    public static void main(String[] args) throws Exception
	{
		Sudoku solver = new Sudoku();
		int T = 1;
		for(int count = 0; count < T; count++)
		{
			solver.solve(count+1);			
		}

	}//end of main()

	public void solve(int testNumber)
	{
		int size = 9;
		SudokuSolver s = new SudokuSolver(size);
		s.solve();

	}//end of solve()

}//end of class Sudoku
