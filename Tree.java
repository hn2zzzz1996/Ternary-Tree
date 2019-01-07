package Tree;

import java.util.Stack;

import Tree.Node.CStatus;
 
public class Tree 
{
	Node[] leaf;
	
	public Tree(int size)
	{
		Node[] nodes = new Node[(size - 1)/2];
	    nodes[0] = new Node();
	    for (int i = 1; i < nodes.length; i++) {
	      nodes[i] = new Node(nodes[(i-1)/3]);//三叉完全树的性质，i节点的父节点为[(i-1)/3]
	    }
	    leaf = new Node[(size)/3];//叶子节点的数目
	    for (int i = 0; i < leaf.length; i++) {
	      leaf[i] = nodes[nodes.length - i - 1];
	    }
	} 
	
	public int getAndIncrement() 
	{
		//System.out.println("Try to increment by " + ThreadId.get());
		CStatus myStatus = CStatus.IDLE;
		Stack stack = new Stack();
		Node myLeaf = leaf[ThreadId.get()/3];
		Node node = myLeaf;
		int prior = 0;
		try
		{
		while((myStatus = node.precombine()) == CStatus.IDLE)
		{
			node = node.parent;
		}
		Node stop = node;
		
		node = myLeaf;
		int combined = 1;
		while(node != stop)
		{
			combined = node.combine(combined);
			stack.push(node);
			node = node.parent;
		}
		
		prior = node.op(combined, myStatus);
		
		while(!stack.empty())
		{
			node = (Node) stack.pop();
			node.distribute(prior);
		}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		//System.out.println("increment by " + ThreadId.get());
		return prior;
	}
	
	
	public static void main(String[] args) {
		final Tree combiningTree = new Tree(100);
		for(int i = 0; i < 50; i++) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					
					int prior = -1;
					try {
						for(int j = 0; j < 100; j++) {
							prior = combiningTree.getAndIncrement();
							System.out.println(ThreadId.get()+" the result is: "+ prior);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//System.out.println(ThreadId.get()+" the result is: "+ prior);
				}
			}).start();
		}
	}
	
}