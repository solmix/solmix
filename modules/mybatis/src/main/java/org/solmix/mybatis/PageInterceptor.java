/*
 * Copyright 2013 The Solmix Project
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.gnu.org/licenses/ 
 * or see the FSF site: http://www.fsf.org. 
 */

package org.solmix.mybatis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.MappedStatement.Builder;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.solmix.sql.SQLDriver;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年6月15日
 */
@Intercepts({ @Signature(type = Executor.class, method = "query", args = {
    MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class }) })
public class PageInterceptor implements Interceptor
{

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.ibatis.plugin.Interceptor#intercept(org.apache.ibatis.plugin.Invocation)
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement statement = (MappedStatement) args[0];
        Object parameter = args[1];
        BoundSql boundSql = statement.getBoundSql(parameter);

        Object parameterObject = boundSql.getParameterObject();
        if (parameterObject instanceof MybatisParameter) {
            MybatisParameter p = (MybatisParameter) parameterObject;
            if (p.isCanPage()) {
                String originalSql = boundSql.getSql().trim();
                SQLDriver sqlDriver = p.getSqlDriver();
               int total= p.getRequest().getContext().getTotalRow();
               if(total<=0){
                String countSql = sqlDriver.getRowCountQueryString(originalSql);
                Connection connection = statement.getConfiguration().getEnvironment().getDataSource().getConnection();
                PreparedStatement countStmt = connection.prepareStatement(countSql);
                BoundSql countBS = copyFromBoundSql(statement, boundSql,
                    countSql, p.getCriteria());
                DefaultParameterHandler parameterHandler = new DefaultParameterHandler(
                    statement, p.getCriteria(), countBS);
                parameterHandler.setParameters(countStmt);
                ResultSet rs = countStmt.executeQuery();
                if (rs.next()) {
                    total = rs.getInt(1);
                }
                rs.close();
                countStmt.close();
                connection.close();
               }
                p.getResponse().setTotalRows(total);

                int end = p.getRequest().getContext().getEndRow();
                int start = p.getRequest().getContext().getStartRow();
                int batch = p.getRequest().getContext().getBatchSize();
                if (end != -1 && end - start > batch) {
                    batch = end - start;
                    p.getRequest().getContext().setBatchSize(batch);
                }
                String limitQuery = sqlDriver.limitQuery(originalSql, start,
                    batch, null);
                BoundSql newBoundSql = copyFromBoundSql(statement, boundSql,
                    limitQuery.toString(), p.getCriteria());
                MappedStatement newMs = copyFromMappedStatement(statement,
                    new BoundSqlSqlSource(newBoundSql));
                invocation.getArgs()[0] = newMs;
                invocation.getArgs()[1] = p.getCriteria();
            }
        }

        return invocation.proceed();
    }

    public class BoundSqlSqlSource implements SqlSource
    {

        BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql)
        {
            this.boundSql = boundSql;
        }

        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }

    private BoundSql copyFromBoundSql(MappedStatement ms, BoundSql boundSql,
        String sql, Object parameterObject) {
        BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), sql,
            boundSql.getParameterMappings(), parameterObject);
        for (ParameterMapping mapping : boundSql.getParameterMappings()) {
            String prop = mapping.getProperty();
            if (boundSql.hasAdditionalParameter(prop)) {
                newBoundSql.setAdditionalParameter(prop,
                    boundSql.getAdditionalParameter(prop));
            }
        }
        return newBoundSql;
    }

    private MappedStatement copyFromMappedStatement(MappedStatement ms,
        SqlSource newSqlSource) {
        Builder builder = new Builder(ms.getConfiguration(), ms.getId(),
            newSqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        String[] keys = ms.getKeyProperties();
        if (keys != null && keys.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (String key : keys) {
                sb.append(key);
                sb.append(",");
            }
            String keyProperty = sb.deleteCharAt(sb.length()).toString();
            builder.keyProperty(keyProperty);
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
     * {@inheritDoc}
     * 
     * @see org.apache.ibatis.plugin.Interceptor#plugin(java.lang.Object)
     */
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.ibatis.plugin.Interceptor#setProperties(java.util.Properties)
     */
    @Override
    public void setProperties(Properties properties) {
        // TODO Auto-generated method stub

    }

}
