package com.lw.config.mybatis;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;

/**
 * 新的SqlSource需要实现
 */
public class BoundSqlSqlSource implements SqlSource {
    private BoundSql boundSql;
    public BoundSqlSqlSource(BoundSql boundSql) {
        this.boundSql = boundSql;
    }
    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return boundSql;
    }
}
