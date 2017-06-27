package coderz.demo;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ExecutorsTest {
	private static int size = 10;
	private static Queue<Runnable> tasks = new LinkedList<>();
	public static void main(String[] args) {
		ScheduledExecutorService service = Executors.newScheduledThreadPool(10);
		service.scheduleAtFixedRate(()->{
			service.execute(()->{
				try {
					getTask().run();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			
		},500, 300, TimeUnit.MILLISECONDS);
		while(true){
			addTask(()->{
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println(Thread.currentThread().getName());
			});
		}
	}
	public static Runnable getTask(){
		synchronized (tasks) {
			while(tasks.isEmpty()){
				try {
					tasks.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			tasks.notifyAll();
			return tasks.poll();
		}
	}
	public static void addTask(Runnable task){
		synchronized(tasks){
			while(tasks.size()>size){
				try {
					tasks.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			tasks.add(task);
			tasks.notify();
		}
	}
}
