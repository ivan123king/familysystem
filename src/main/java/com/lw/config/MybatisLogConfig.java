package com.lw.config;

import org.apache.ibatis.logging.stdout.StdOutImpl;

/**
 * 匹配mybatis日志打印
 */
public class MybatisLogConfig extends StdOutImpl {

    public MybatisLogConfig(String clazz) {
        super(clazz);
    }

    @Override
    public boolean isTraceEnabled() {
        return false;
    }
}
