package com.corp.concepts.log.benchmark;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

import com.corp.concepts.log.benchmark.model.Performance;
import com.corp.concepts.log.benchmark.utils.DBUtil;
import com.corp.concepts.log.benchmark.utils.JdbcLogTask;

/**
 * A benchmarking tool for Log4J
 * {@link org.apache.logging.log4j.core.appender.db.jdbc.JdbcAppender}
 * 
 * @author Selcuk SERT
 *
 */
public class JdbcLogBenchmark {
	private static final List<Future<Performance>> executionList = new ArrayList<>();
	private static long totalAttempts = 0;
	private static long totalTimeElapsed = 0;
	private static double maxLPS = 0.0;
	private static boolean isAsync;

	public static void main(String[] args) {
		String noOfThreadsStr = System.getProperty("NO_OF_THREADS");
		String maxAttemptsStr = System.getProperty("MAX_ATTEMPTS");

		String dbName = System.getProperty("MYSQL_DB_NAME");
		String dbUser = System.getProperty("MYSQL_DB_USER");
		String dbPass = System.getProperty("MYSQL_DB_PASS");

		isAsync = Boolean.valueOf(System.getProperty("ASYNC_LOGGER_ENABLED", "true"));

		if (StringUtils.isAnyBlank(noOfThreadsStr, maxAttemptsStr, dbName, dbUser, dbPass)) {
			throw new IllegalArgumentException("Need system properties to be set: MYSQL_HOST(optional), "
					+ "MYSQL_PORT(optional), " + "ASYNC_LOGGER_ENABLED(optional), " + "MYSQL_DB_NAME, "
					+ "MYSQL_DB_USER, " + "MYSQL_DB_PASS, " + "NO_OF_THREADS, " + "MAX_ATTEMPTS");
		}

		Integer noOfThreads, maxAttempts;
		try {
			noOfThreads = Integer.valueOf(noOfThreadsStr);
			maxAttempts = Integer.valueOf(maxAttemptsStr);
		} catch (NumberFormatException nfe) {
			LogManager.getRootLogger().error("Error: ", nfe);
			return;
		}

		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(noOfThreads);

		DBUtil.emptyTable();

		int idx = noOfThreads;
		while (idx-- > 0) {
			Future<Performance> future = executor.submit(new JdbcLogTask(idx, (maxAttempts / noOfThreads), isAsync));
			executionList.add(future);
		}

		long startTime = Calendar.getInstance().getTimeInMillis();
		for (Future<Performance> task : executionList) {
			try {
				aggregate(task.get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		long endTime = Calendar.getInstance().getTimeInMillis();
		totalTimeElapsed = TimeUnit.MILLISECONDS.toSeconds(endTime - startTime);

		if (isAsync) {
			while (DBUtil.getRecordCount() != maxAttempts) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		executor.shutdown();

		results();
	}

	private static void aggregate(Performance performance) {
		double lps = 0;
		if (performance.getElapsedTime() != 0) {
			lps = (double) performance.getAttempt() / performance.getElapsedTime();
		}
		System.out.println(String.format("[%d] \t | C: %d \t | T (secs): %d | LPS: %.2f", performance.getTaskIndex(),
				performance.getAttempt(), performance.getElapsedTime(), lps));

		maxLPS = lps > maxLPS ? lps : maxLPS;

		totalAttempts += performance.getAttempt();
	}

	private static void results() {
		String result = String.format(
				"[%s-%s] > | Attempts: %d\t| Elapsed (secs): %d\t| Max LPS: %.2f\t| Inserted records in DB: %d",
				"Result", (isAsync ? "Asynch Logger" : "Synch Logger"), totalAttempts, totalTimeElapsed, maxLPS,
				DBUtil.getRecordCount());

		System.out.println("\n" + StringUtils.repeat("-", 2 * result.length()));
		System.out.println(result);
		System.out.println(StringUtils.repeat("-", 2 * result.length()));
	}
}
