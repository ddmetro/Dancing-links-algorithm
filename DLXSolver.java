import java.util.HashMap;

public abstract class DLXSolver
{
        Node root = null;
	HashMap<Integer, Node> solution = null;
	Node[][] matrix;
	int rowCount, columnCount;
	boolean isFinished;
	int filledCellCount;
	
	public DLXSolver()
	{
		root = new Node("Root", -1, null);
		isFinished = false;
		filledCellCount = 0;
		solution = new HashMap<Integer, Node>();
	}

	public abstract void initialize();

	public void solve()
	{
		search(filledCellCount);
	}

	protected void search(int k)
	{
		//System.out.println("k: " + k);
		if(root.right == root)
		{
			printSolution(k);
			isFinished = true;
			return;
		}

		Node c = chooseColumn();
		coverColumn(c);

		for(Node r = c.down; r != c && !isFinished; r = r.down)
		{
			solution.put(k, r);

			for(Node j = r.right; j != r; j = j.right)
			{
				coverColumn(j.columnHead);
			}

			search(k+1);
			
			r = solution.get(k);
			c = r.columnHead;

			for(Node j = r.left; j != r; j = j.left)
			{
				uncoverColumn(j.columnHead);
			}
		}
		
		uncoverColumn(c);
	}

	private Node chooseColumn()
	{
		int minSize = Integer.MAX_VALUE;
		Node minColumn = null;

		for(Node c = root.right; c != root; c = c.right)
		{
			if(c.size < minSize)
			{
				minColumn = c;
				minSize = c.size;	
			}
		}
		//System.out.println("chosen column: " + minColumn);

		return minColumn;
	}

	protected void coverColumn(Node c)
	{
		c.left.right = c.right;
		c.right.left = c.left;

		for(Node i = c.down; i != c; i = i.down)
		{
			for(Node j = i.right; j != i; j = j.right)
			{
				j.down.up = j.up;
				j.up.down = j.down;
				j.columnHead.size = j.columnHead.size - 1;
			}
		}
	}

	protected void uncoverColumn(Node c)
	{
		for(Node i = c.up; i != c; i = i.up)
		{
			for(Node j = i.left; j != i; j = j.left)
			{
				j.down.up = j;
				j.up.down = j;
				j.columnHead.size = j.columnHead.size + 1;
			}
		}

		c.right.left = c;
		c.left.right = c;
	}

	protected void printSolution(int k)
	{
		for(int i = 0; i < k; i++)
			System.out.println(solution.get(i).columnHead);
	}
}
