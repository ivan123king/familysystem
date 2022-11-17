package com.lw.config;

import com.lw.config.mybatis.BoundSqlSqlSource;
import com.lw.config.mybatis.page.Page;
import com.lw.config.mybatis.page.PageRequest;
import com.mysql.cj.x.protobuf.MysqlxPrepare;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.util.StringUtils;

import javax.xml.bind.PropertyException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * mybatis 分页拦截器
 * 自定义分页
 */
@Intercepts({@Signature(
        type = Executor.class,
        method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}
)})
@Slf4j
public class MybatisPageInterceptor implements Interceptor {

    private static String dialect = "";
    private static String pageSqlId = "";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        if (mappedStatement.getId().matches(pageSqlId)) {
            //获取参数
            Object param = invocation.getArgs()[1];
            BoundSql boundSql = mappedStatement.getBoundSql(param);

            if (log.isDebugEnabled()) {
                log.debug("[sql_id]=" + mappedStatement.getId());
                log.debug("[sql]=" + boundSql.getSql());
            }

            //获取查询SQL
            String sql = boundSql.getSql();

            //获取数据库连接
            Configuration config = mappedStatement.getConfiguration();
            Connection connection = config.getEnvironment().getDataSource().getConnection();

            //查询总数据
            String countSql = "select count(*) from (" + sql + ")  tmp_count";
            PreparedStatement countStmt = connection.prepareStatement(countSql);
            BoundSql countBS = new BoundSql(mappedStatement.getConfiguration(), countSql, boundSql.getParameterMappings(), boundSql.getParameterObject());
            this.setParameters(countStmt, mappedStatement, countBS, boundSql.getParameterObject());
            ResultSet rs = countStmt.executeQuery();
            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }

            rs.close();
            countStmt.close();

            PageRequest pageRequest = this.getPageRequest(param);

            String pageSql = generatePageSql(sql, pageRequest);
            executeSql(invocation,pageSql);

            //重新执行新的sql
            Object result = invocation.proceed();
            connection.close();

            //处理新的结构
            Page<?> page = new Page((List)result, pageRequest, count);
            List<Page> returnResultList = new ArrayList<>();
            returnResultList.add(page);
            return returnResultList;
        }
        return invocation.proceed();
    }

    private void executeSql(Invocation invocation, String sql){
        final Object[] args = invocation.getArgs();
        MappedStatement statement = (MappedStatement) args[0];
        BoundSql boundSql = statement.getBoundSql(args[1]);
        MappedStatement newStatement = createNewMappedStatement(statement, new BoundSqlSqlSource(boundSql));
        MetaObject msObject = MetaObject.forObject(newStatement, new DefaultObjectFactory(), new DefaultObjectWrapperFactory(), new DefaultReflectorFactory());
        msObject.setValue("sqlSource.boundSql.sql", sql);
        args[0] = newStatement;
    }
    /**
     * 获取新的MappedStatement
     * @param ms
     * @param newSqlSource
     * @return
     */
    private MappedStatement createNewMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder =
                new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
            StringBuilder keyProperties = new StringBuilder();
            for (String keyProperty : ms.getKeyProperties()) {
                keyProperties.append(keyProperty).append(",");
            }
            keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
            builder.keyProperty(keyProperties.toString());
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());
        return builder.build();
    }

    /**
     * 从参数列表返回PageRequest
     */
    private PageRequest getPageRequest(Object paramMap) {
        if (paramMap == null) {
            return null;
        } else if (PageRequest.class.isAssignableFrom(paramMap.getClass())) {
            return (PageRequest) paramMap;
        } else {
            if (paramMap instanceof MapperMethod.ParamMap) {
                MapperMethod.ParamMap map = (MapperMethod.ParamMap) paramMap;
                Iterator iterator = map.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    Object obj = entry.getValue();
                    if (obj != null && PageRequest.class.isAssignableFrom(obj.getClass())) {
                        return (PageRequest) obj;
                    }
                }
            }
            return null;
        }
    }

    private Object getWhereParameter(Object obj) {
        if (obj instanceof MapperMethod.ParamMap) {
            MapperMethod.ParamMap paramMap = (MapperMethod.ParamMap) obj;
            if (paramMap.size() == 4) {
                Iterator iterator = paramMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry var4 = (Map.Entry) iterator.next();
                    Object var5 = var4.getValue();
                    if (PageRequest.class.isAssignableFrom(var5.getClass())) {
                        return paramMap.get("param1");
                    }
                }
            }
        }
        return obj;
    }

    private void setParameters(PreparedStatement ps, MappedStatement mappedStatement, BoundSql boundSql, Object parameterObject) throws SQLException {
        ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
        List parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings != null) {
            Configuration configuration = mappedStatement.getConfiguration();
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            MetaObject metaObject = parameterObject == null ? null : configuration.newMetaObject(parameterObject);

            for (int i = 0; i < parameterMappings.size(); ++i) {
                ParameterMapping parameterMapping = (ParameterMapping) parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    String propertyName = parameterMapping.getProperty();
                    PropertyTokenizer prop = new PropertyTokenizer(propertyName);
                    Object value = null;
                    if (parameterObject == null) {
                        value = null;
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (propertyName.startsWith("__frch_") && boundSql.hasAdditionalParameter(prop.getName())) {
                        value = boundSql.getAdditionalParameter(prop.getName());
                        if (value != null) {
                            value = configuration.newMetaObject(value).getValue(propertyName.substring(prop.getName().length()));
                        }
                    } else {
                        value = metaObject == null ? null : metaObject.getValue(propertyName);
                    }

                    TypeHandler typeHandler = parameterMapping.getTypeHandler();
                    if (typeHandler == null) {
                        throw new ExecutorException("There was no TypeHandler found for parameter " + propertyName + " of statement " + mappedStatement.getId());
                    }

                    typeHandler.setParameter(ps, i + 1, value, parameterMapping.getJdbcType());
                }
            }
        }

    }

    /**
     * 重新生成分页SQL
     *
     * @param sql
     * @param pageRequest
     * @return
     */
    private String generatePageSql(String sql, PageRequest pageRequest) {
        if (pageRequest != null && !StringUtils.isEmpty(dialect)) {
            StringBuffer pageSql = new StringBuffer();
            if ("mysql".equals(dialect)) {
                pageSql.append(sql);
                pageSql.append(" limit " + pageRequest.getOffset() + "," + pageRequest.getSize());
            } else if ("oracle".equals(dialect)) {
                pageSql.append("select * from (select tmp_tb.*,ROWNUM row_id from (");
                pageSql.append(sql);
                pageSql.append(" ) tmp_tb ");
                pageSql.append(") where row_id>");
                pageSql.append(pageRequest.getOffset());
                pageSql.append(" and row_id<= ");
                pageSql.append(pageRequest.getOffset() + pageRequest.getSize());
            } else if ("pg".equals(dialect)) {
                pageSql.append(sql);
                int offsetNum = 0;
                if (pageRequest != null && pageRequest.getOffset() != 0) {
                    offsetNum = pageRequest.getOffset() - 1;
                }

                pageSql.append(" limit " + pageRequest.getSize() + " offset " + offsetNum * pageRequest.getSize());
            }

            log.debug(pageSql.toString());
            return pageSql.toString();
        } else {
            return sql;
        }
    }

    public Object plugin(Object arg0) {
        return Plugin.wrap(arg0, this);
    }

    /**
     * 加载mybatis-config.xml中配置
     *
     * @param p
     */
    public void setProperties(Properties p) {
        dialect = p.getProperty("dialect");
        if (StringUtils.isEmpty(dialect)) {
            try {
                throw new PropertyException("dialect property is not found!");
            } catch (PropertyException var4) {
                var4.printStackTrace();
            }
        }

        pageSqlId = p.getProperty("pageSqlId");
        if (StringUtils.isEmpty(pageSqlId)) {
            try {
                throw new PropertyException("pageSqlId property is not found!");
            } catch (PropertyException var3) {
                var3.printStackTrace();
            }
        }

    }
}
