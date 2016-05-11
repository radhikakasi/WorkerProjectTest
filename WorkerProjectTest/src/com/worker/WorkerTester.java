package com.worker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class WorkerTester {

	  public static void main(String[] args) throws Exception {
	    final OrderProcessor processor = new OrderProcessor();
	    
	    // Submit orders in parallel from 4 threads
	    final ExecutorService orderSubmissionThreadPool = Executors.newFixedThreadPool(4);
	    
	    //Create order tasks and submit to above thread pool
	    final AtomicInteger orderNumber = new AtomicInteger(1);
	    Collection<Callable<Integer>> tasks = new ArrayList<Callable<Integer>>();
	    int totalOrders = 10;
	    for (int i = 0; i < totalOrders; i++) {
	      tasks.add(new Callable<Integer>() {
	        @Override
	        public Integer call() throws Exception {
	          final Integer orderNumberInt = orderNumber.getAndIncrement();
	          processor.addOrder(new Order(orderNumberInt, OrderStatus.NEW));
	          return orderNumberInt;
	        }
	      });
	    }
	    orderSubmissionThreadPool.invokeAll(tasks);

	    // Wait till all finish.
	    // This can be converted to wait notify pattern rather than sleep
	    while (processor.getFinishedOrderCount() < 10) {
	      Thread.sleep(100);
	    }

	    System.out.println("Total submitted: " + processor.getTotalOrderCount());
	    System.out.println("Total finished: " + processor.getFinishedOrderCount());

	    orderSubmissionThreadPool.shutdownNow();
	    processor.stop();

	    // Make sure we finished all the orders
	    assert(processor.getTotalOrderCount() == processor.getFinishedOrderCount());
	  }

	}
