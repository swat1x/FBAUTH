package me.swat1x.fbauth.database;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.swat1x.fbauth.database.builder.TableBuilder;
import org.apache.commons.dbutils.QueryRunner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class Database {
    private final ExecutorService executor;
    private final HikariDataSource dataSource;

    public Database(String ip, int port, String database, String user, String password) {
        ThreadFactory threadFactory = (new ThreadFactoryBuilder()).setNameFormat("mysql-worker-%d").build();
        this.executor = Executors.newFixedThreadPool(4, threadFactory);
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + ip + ":" + port + "/" + database + "?useUnicode=yes&useSSL=false&characterEncoding=UTF-8&allowMultiQueries=true&autoReconnect=true&jdbcCompliantTruncation=false");
        config.setUsername(user);
        config.setPassword(password);
        config.setConnectionTimeout(30000L);
        config.setIdleTimeout(600000L);
        config.setMaxLifetime(1800000L);
        config.addDataSourceProperty("cachePrepStmts", true);
        config.addDataSourceProperty("prepStmtCacheSize", 250);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        this.dataSource = new HikariDataSource(config);
    }

    public ReThrowableQueryRunner sync() {
        return new ReThrowableQueryRunner(this.dataSource);
    }

    public CompletableAsyncQueryRunner async() {
        return new CompletableAsyncQueryRunner(this.executor, new QueryRunner(this.dataSource));
    }

    public void shutdown() {
        try {
            this.executor.shutdown();
            if (!this.executor.awaitTermination(800L, TimeUnit.MILLISECONDS)) {
                this.executor.shutdownNow();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static TableBuilder tableBuilder(String tableName){
        return new TableBuilder(tableName);
    }
}

