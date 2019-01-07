package Tree;


/*
 * TreeTest.java
 * JUnit based test
 *
 * Created on June 16, 2006, 9:32 PM
 */
 

import junit.framework.*;
import java.util.Stack;
 
public class TreeTest extends TestCase {
  
  final static int THREADS = 50;
  final static int TRIES = 100;      
  static boolean[] test = new boolean[THREADS * TRIES];
  Tree instance = new Tree(100);
  Thread[] thread = new Thread[THREADS];
  
  public TreeTest(String testName) {
    super(testName);
  }
 
  public static Test suite() {
    TestSuite suite = new TestSuite(TreeTest.class);
    
    return (Test) suite;
  }
 
  public void testGetAndIncrement() throws Exception {
    System.out.printf("Parallel, %d threads, %d tries\n", THREADS, TRIES);
    
    long startTime=System.currentTimeMillis();   //获取开始时间
    for (int i = 0; i < THREADS; i++) {
      thread[i] = new MyThread();
    }
    for (int i = 0; i < THREADS; i ++) {
      thread[i].start();
    }
    for (int i = 0; i < THREADS; i ++) {
      thread[i].join();
    }
    check(test);
    
    long endTime=System.currentTimeMillis(); //获取结束时间  
    
    
    double elapsedTime = ((double) (endTime - startTime)) / 1000.0;
    double throughput = (TRIES*THREADS / elapsedTime);
    System.out.printf(" 吞吐量: %f, 运行时间: %f \n",throughput,elapsedTime);
  }
 
  
   class MyThread extends Thread {
    public void run() {
      for (int j = 0; j < TRIES; j++) {
	  int i = instance.getAndIncrement();
	  if (test[i]) {
	    System.out.printf("ERROR duplicate value %d\n", i);
	  } else {
		  System.out.println(ThreadId.get() + " result is " + i);
	    test[i] = true;
	  }
	}
    }
  }
   
   void check(boolean[] test) throws Exception {
     for (int i = 0; i < test.length; i++) {
       if (!test[i]) {
         System.out.println("missing value at " + i);
         throw new Exception();
       }
     }
   }
  
}
