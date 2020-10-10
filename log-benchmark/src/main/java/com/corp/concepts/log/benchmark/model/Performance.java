package com.corp.concepts.log.benchmark.model;

import lombok.Data;

@Data
public class Performance {
	private long attempt;
	private int taskIndex;
	private long elapsedTime;
}
