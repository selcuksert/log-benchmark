# log-benchmark
A benchmarking tool for Log4J [`JdbcAppender`](https://github.com/apache/logging-log4j2/blob/master/log4j-jdbc/src/main/java/org/apache/logging/log4j/jdbc/appender/JdbcAppender.java)

## Log Database
MYSQL is used in this one, and such a table need to be created:
```SQL
CREATE TABLE log(
    thread VARCHAR(100),
    entry_date TIMESTAMP,
    logger VARCHAR(100),
    log_level VARCHAR(100),
    message TEXT,
    exception TEXT
);
```
