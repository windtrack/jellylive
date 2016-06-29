package com.oo58.jelly.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 封装的线程池
 *
 */
public class ThreadPoolWrap {
	private static final int DEFAULT_COREPOOLSIZE = 2; //线程池维护线程的最少数量
	private static final long DEFAULT_KEEP_ALIVE_TIME = 30L;//线程池维护线程所允许的空闲时间
	private static final int DEFAULT_MAXIMUM_POOLSIZE = 30; //线程池维护线程的最大数量
	private static ThreadPoolWrap instance;
	private BlockingQueue<Runnable> bq;
	private ThreadPoolExecutor executor ;
	private static final int QUEUE_SIZE = 50;

	private ThreadPoolWrap() {
		executor = null;
		bq = new ArrayBlockingQueue(QUEUE_SIZE);
		executor = new ThreadPoolExecutor(DEFAULT_COREPOOLSIZE, DEFAULT_MAXIMUM_POOLSIZE, DEFAULT_KEEP_ALIVE_TIME, TimeUnit.SECONDS, bq);
	}

	public static ThreadPoolWrap getThreadPool() {
		if (instance == null)
			instance = new ThreadPoolWrap();
		return instance;
	}

	/**
	 * 添加一个任务
	 * @param paramRunnable
     */
	public void executeTask(Runnable paramRunnable) {
		try {
			this.executor.execute(paramRunnable);
		}catch (Exception e) {

		}
	}

	/**
	 * 是否有任务在进行
	 * @return
     */
	public boolean isThreadPoolActive() {
		boolean flag = true;
		if (executor.getActiveCount() < 1)
			flag = false;
		return flag;
	}

	/**
	 * 移除任务
	 * @param paramRunnable
     */
	public void removeTask(Runnable paramRunnable) {
		this.executor.remove(paramRunnable);
	}

	/**
	 * 销毁
	 */
	public void shutdown() {
		this.executor.shutdown();
		instance = null;
	}
}
