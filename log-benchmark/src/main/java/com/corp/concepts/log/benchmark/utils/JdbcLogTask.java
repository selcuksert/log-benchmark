package com.corp.concepts.log.benchmark.utils;

import java.util.Calendar;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.corp.concepts.log.benchmark.model.Performance;

public class JdbcLogTask implements Callable<Performance> {

	private static Logger log;

	AtomicLong counter;
	int index;
	int maxAttempt;

	public JdbcLogTask(int index, int maxAttempt, boolean isAsynch) {
		counter = new AtomicLong(0);
		this.index = index;
		this.maxAttempt = maxAttempt;
		if (isAsynch) {
			log = LogManager.getLogger("AsynchLogger");
		} else {
			log = LogManager.getLogger("SynchLogger");
		}
	}

	@Override
	public Performance call() throws Exception {
		long startTime = Calendar.getInstance().getTimeInMillis();
		while (counter.get() < maxAttempt) {
			long count = counter.incrementAndGet();
			if (count % 100 == 0) {
				log.error("Error: ", new Exception(String.format("Test Exception: %d", count)));
			} else {
				log.debug(String.format("Counter: %d", count));
			}
		}
		long endTime = Calendar.getInstance().getTimeInMillis();

		Performance performance = new Performance();

		performance.setAttempt(counter.get());
		performance.setElapsedTime(TimeUnit.MILLISECONDS.toSeconds(endTime - startTime));
		performance.setTaskIndex(index);

		return performance;
	}

}
