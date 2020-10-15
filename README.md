# log-benchmark
A benchmarking tool for Log4J [`JdbcAppender`](https://github.com/apache/logging-log4j2/blob/master/log4j-jdbc/src/main/java/org/apache/logging/log4j/jdbc/appender/JdbcAppender.java)

## Program Parameters
Need to pass/have following VM parameters/system properties:
|Name|Default Value|Description|
|----|-------------|-----------|
|MYSQL_HOST|localhost|MySQL Server Host|
|MYSQL_HOST|3306|MySQL Server Port|
|MYSQL_DB_NAME|-|DB name that hosts the log table|
|MYSQL_DB_USER|-|Username of DB user that has the privilege to alter the log table|
|MYSQL_DB_PASS|-|DB password of the `MYSQL_DB_USER`|
|ASYNC_LOGGER_ENABLED|true|Flag to decided whether Log4J [Asynchronous Logger](https://logging.apache.org/log4j/2.x/manual/async.html) is to be used|
|NO_OF_THREADS|-|Number of threads that are tasked to send log events in parallel|
|MAX_ATTEMPTS|-|Maximum number of logging attempts for all of logger threads|

## Log Database
MYSQL is used in this one and [HikariCP](https://github.com/brettwooldridge/HikariCP) JDBC connection pooling framework is utilized under the hood. 

### Log Table
Such a table need to be created:
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
