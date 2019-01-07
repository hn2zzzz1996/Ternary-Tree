package Tree;

public class Node 
{
	  enum CStatus{IDLE, FIRST, SECOND, THIRD, RESULT, ROOT};
	  boolean locked = false;
	  CStatus status;
	  int firstValue;
	  int secondValue;
	  int thirdValue;
	  int secondResult, thirdResult;
	  int result;
	  Node parent;
	  int drained = 0, res = 0;
	  
	  
	  public Node()
	  {
		  status = CStatus.ROOT;
		  parent = null;
		  locked = false;
		  drained = 0; res = 0;
	  }
	  
	  public Node(Node parent)
	  {
		  this.parent = parent;
		  status = CStatus.IDLE;
		  locked = false;
		  drained = 0; res = 0;
	  }
	  
	  synchronized CStatus precombine() throws InterruptedException, UnexpectedStatusException
	  {
		  while(drained == 2 || drained == 3 || locked) wait();
		  
		  switch(status)
		  {
		  //Return IDLE or FIRST is of no significance
		  case IDLE:
			  status = CStatus.FIRST;
			  return CStatus.IDLE; 
		  case FIRST:
			  System.out.println("I'm the second: " + ThreadId.get());
			  drained = 1;
			  status = CStatus.SECOND;
			  return CStatus.SECOND;
		  case SECOND:
			  System.out.println("I'm the third: " + ThreadId.get());
			  drained = 2;
			  status = CStatus.THIRD;
			  return CStatus.THIRD;
		  case ROOT:
			  return CStatus.ROOT;
		  default:
			throw new UnexpectedStatusException();	  
		  }
	  }
	  
	  synchronized int combine(int combined) throws InterruptedException, UnexpectedStatusException
	  {
		  while(drained > 0 && drained != 3) wait();
		  locked = true;
		  firstValue = combined;
		  
		  switch(status)
		  {
		  case FIRST:
			  return firstValue;
		  case SECOND:
			  return firstValue + secondValue;
		  case THIRD:
			  return firstValue + secondValue + thirdValue;
		  default:
			  throw new UnexpectedStatusException();
		  }
	  }
	  
	  synchronized int op(int combined, CStatus myStatus) throws InterruptedException, UnexpectedStatusException
	  {
		  //System.out.println(ThreadId.get() + " want to op!");
		  switch(status)
		  {
		  case ROOT:
			  //System.out.println(ThreadId.get() + " op the root! modify to " + result + " " + combined);
			  int oldValue = result;
			  result += combined;
			  return oldValue;
		  case SECOND:
			  secondValue = combined;
			  drained = 3;
			  //System.out.println(ThreadId.get() + " the second go to here!");
			  notifyAll();
			  while(status != CStatus.RESULT) wait();
			  locked = false;
			  drained = 0;
			  notifyAll();
			  status = CStatus.IDLE;
			  return secondResult;
		  case THIRD:
			  switch(myStatus)
			  {
			  case SECOND:
				  //System.out.println(ThreadId.get() + " is the second");
				  secondValue = combined;
				  break;
			  case THIRD:
				  //System.out.println(ThreadId.get() + " is the third");
				  thirdValue = combined;
				  break;
			  default:
				break;					  
			  }
			  if(--drained == 0)
			  {
				  //System.out.println(ThreadId.get() + " notifyAll()");
				  notifyAll();
			  }
			  while(status != CStatus.RESULT) {
				  System.out.println(ThreadId.get() + " was waiting");
				  wait();
			  }
			  System.out.println(ThreadId.get() + " is awake()");
			  if(++res == 2) {
				  status = CStatus.IDLE;
				  locked = false;
				  res = 0;
				  notifyAll();
			  }
			  switch(myStatus) {
			  case SECOND:
				  return secondResult;
			  case THIRD:
				  return thirdResult;
			  }
		  default:
			  throw new UnexpectedStatusException();
		  }
	  }
	  
	  synchronized void distribute(int prior) throws UnexpectedStatusException
	  {
		  switch(status)
		  {
		  case FIRST:
			  status = CStatus.IDLE;
			  locked = false;
			  break;
		  case SECOND:
			  secondResult = prior + firstValue;
			  status = CStatus.RESULT;
			  break;
		  case THIRD:
			  thirdResult = prior + firstValue + secondValue;
			  secondResult = prior + firstValue;
			  status = CStatus.RESULT;
			  break;
		  default:
			throw new UnexpectedStatusException();	  
		  }
		  notifyAll();
	  }
}
 
