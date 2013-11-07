/*
 * SOLMIX PROJECT
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
package org.solmix.security.rbac.realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DSResponse;
import org.solmix.api.exception.SlxException;
import org.solmix.fmk.context.SlxContext;
import org.solmix.fmk.criterion.Criteria;
import org.solmix.fmk.datasource.FetchOp;


/**
 * DataSource powered Shiro  realm.
 * @author solmix.f@gmail.com
 * @version $Id$  2013-8-18
 */

public class DataSourceRealm extends AuthorizingRealm
{

	public static final String REALM_NAME="DSRealm";
    /**
     * {@inheritDoc}
     * 
     * @see org.apache.shiro.realm.AuthorizingRealm#doGetAuthorizationInfo(org.apache.shiro.subject.PrincipalCollection)
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
    
        System.out.println("=======================");
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.shiro.realm.AuthenticatingRealm#doGetAuthenticationInfo(org.apache.shiro.authc.AuthenticationToken)
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
    	UsernamePasswordToken up=(UsernamePasswordToken)token;
    	String userName=up.getUsername();
    	char[] pwd=up.getPassword();
    	
    	if(userName==null){
    		throw new AuthenticationException("User name must be not null.");
    	}
    	if(pwd==null)
    		throw new AuthenticationException("Password name must be not null.");
    	try {
    		//get the password
    		SimpleAuthenticationInfo crypwd =SlxContext.doInSystemContext(new FetchOp<SimpleAuthenticationInfo>("SECURITY"){

				@Override
				public SimpleAuthenticationInfo fetch(DSRequest request) throws SlxException {
					DSResponse resp=request.execute();
					return resp.getContext().getData(SimpleAuthenticationInfo.class);
				}
				
			}.withCriteria(new Criteria("USERNAME", userName).add("PASSWORD", pwd).add("HOST", up.getHost()).add("REMEMBER_ME", up.isRememberMe()))
			 .withOpId("doGetAuthenticationInfo"));
    		
    		return crypwd;
    		
		} catch (SlxException e) {
			throw new AuthenticationException(e.getMessage());
		}
    }
    

}
