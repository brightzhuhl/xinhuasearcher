package coderz.demo.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import coderz.demo.Constant;

public class ScheduleExcutor implements ExecutorService{
	
	private static Log logger = LogFactory.getLog(ScheduleExcutor.class);
	
	private int taskSize = PropertiesUtil.getIntValue(Constant.SCHEDULE_EXECUTOR_TASKSIZE);
	
	private int poolSize = PropertiesUtil.getIntValue(Constant.SCHEDULE_EXECUTOR_POOLSIZE);
	
	private long initDelay = PropertiesUtil.getLongValue(Constant.SCHEDULE_EXECUTOR_INIT_DELAY),
					period = PropertiesUtil.getLongValue(Constant.SCHEDULE_EXECUTOR_PERIOD);
	
	private Queue<Runnable> tasks = new LinkedList<>();
	
	private boolean isStart = false;
	
	private ScheduledExecutorService service;
	private ExecutorService processService;
	
	public ScheduleExcutor(int poolSize) {
		processService = Executors.newFixedThreadPool(poolSize);
		service = Executors.newScheduledThreadPool(1);
		this.poolSize = poolSize;
	}
	
	public static void main(String[] args) {
		
		ExecutorService service = new ScheduleExcutor(10);
		
		while(true){
			service.execute(()->{
				try {
					Thread.sleep(500);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}
	}
	
	
	
	@Override
	public void execute(Runnable task) {
		synchronized(tasks){
			if(!isStart){
				start();
				isStart = true;
			}
			while(tasks.size()>=taskSize){
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



	@Override
	public void shutdown() {
		service.shutdown();
	}



	@Override
	public List<Runnable> shutdownNow() {
		return service.shutdownNow();
	}



	@Override
	public boolean isShutdown() {
		return service.isShutdown();
	}



	@Override
	public boolean isTerminated() {
		return service.isTerminated();
	}



	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		return service.awaitTermination(timeout, unit);
	}



	@Override
	public <T> Future<T> submit(Callable<T> task) {
		return service.submit(task);
	}



	@Override
	public <T> Future<T> submit(Runnable task, T result) {
		return service.submit(task, result);
	}



	@Override
	public Future<?> submit(Runnable task) {
		return service.submit(task);
	}



	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
		return service.invokeAll(tasks);
	}



	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
			throws InterruptedException {
		return service.invokeAll(tasks,timeout,unit);
	}



	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
		return service.invokeAny(tasks);
	}



	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		return service.invokeAny(tasks, timeout, unit);
	}
	
	
	
	private Runnable getTask(){
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
	
	
	
	private void start() {
		if(isStart){
			return;
		}
		service.scheduleAtFixedRate(()->{
			processService.execute(()->{
				try {
					getTask().run();
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			});
		},initDelay, period, TimeUnit.MILLISECONDS);
	}
	
	
	
	public int getTaskSize() {
		return taskSize;
	}
	
	
	
	public void setTaskSize(int taskSize) {
		this.taskSize = taskSize;
	}
	
	
	
	public int getPoolSize() {
		return poolSize;
	}
	
	
	
	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}
	
	
	
	public Queue<Runnable> getTasks() {
		return tasks;
	}
	
	
	
	public void setTasks(Queue<Runnable> tasks) {
		this.tasks = tasks;
	}



	public long getInitDelay() {
		return initDelay;
	}



	public void setInitDelay(long initDelay) {
		this.initDelay = initDelay;
	}



	public long getPeriod() {
		return period;
	}



	public void setPeriod(long period) {
		this.period = period;
	}
}
