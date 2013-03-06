public class Node
{
    	Node left;
	Node right;
	Node up;
	Node down;
	Node columnHead;
	String name;
	int size;

	public Node(String name, int size, Node columnHead)
	{
		this.name = name;
		this.size = size;
		this.columnHead = columnHead;
	}

	public String toString()
	{
		return name;
	}
}
