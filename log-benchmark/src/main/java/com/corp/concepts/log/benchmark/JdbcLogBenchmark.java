package com.corp.concepts.log.benchmark;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

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

	public static void main(String[] args) {
		String noOfThreadsStr = System.getProperty("NO_OF_THREADS");
		String maxAttemptsStr = System.getProperty("MAX_ATTEMPTS");

		String dbName = System.getProperty("MYSQL_DB_NAME");
		String dbUser = System.getProperty("MYSQL_DB_USER");
		String dbPass = System.getProperty("MYSQL_DB_PASS");

		if (StringUtils.isAnyBlank(noOfThreadsStr, maxAttemptsStr, dbName, dbUser, dbPass)) {
			throw new IllegalArgumentException(
					"Need system properties to be set: MYSQL_HOST(optional), MYSQL_PORT(optional), "
							+ "MYSQL_DB_NAME, MYSQL_DB_USER, MYSQL_DB_PASS, NO_OF_THREADS, MAX_ATTEMPTS");
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
			Future<Performance> future = executor.submit(new JdbcLogTask(idx, maxAttempts / noOfThreads));
			executionList.add(future);
		}
		executor.shutdown();

		for (Future<Performance> task : executionList) {
			try {
				aggregate(task.get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}

		results();
	}

	private static void aggregate(Performance performance) {
		System.out.println(String.format("[%d] \t | C: %d \t | T (secs): %d", performance.getTaskIndex(),
				performance.getAttempt(), performance.getElapsedTime()));
		totalAttempts += performance.getAttempt();
		totalTimeElapsed += performance.getElapsedTime();
	}

	private static void results() {
		String result = String.format("[%s] > | Attempts: %d\t| Elapsed (secs): %d\t| DB Records Inserted: %d",
				"Result", totalAttempts, totalTimeElapsed, DBUtil.getRecordCount());

		System.out.println("\n" + StringUtils.repeat("-", 2 * result.length()));
		System.out.println(result);
		System.out.println(StringUtils.repeat("-", 2 * result.length()));
	}
}
