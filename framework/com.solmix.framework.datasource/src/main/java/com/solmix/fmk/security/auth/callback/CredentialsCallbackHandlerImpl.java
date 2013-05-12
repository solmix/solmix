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

package com.solmix.fmk.security.auth.callback;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.RealmCallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solmix.api.security.User;
import com.solmix.api.security.auth.callback.CredentialsCallbackHandler;

/**
 * 
 * @author Administrator
 * @version 110035 2012-10-12
 */

public class CredentialsCallbackHandlerImpl implements CredentialsCallbackHandler
{

    protected static Logger log = LoggerFactory.getLogger(CredentialsCallbackHandlerImpl.class);

    private String name;

    private char[] pswd;

    protected User user;

    private String realm;

    public CredentialsCallbackHandlerImpl(String name, char[] pswd)
    {
        this.name = name;
        this.pswd = pswd;
    }

    public CredentialsCallbackHandlerImpl(String name, char[] pswd, String realm)
    {
        this(name, pswd);
        this.realm = realm;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.security.auth.callback.CallbackHandler#handle(javax.security.auth.callback.Callback[])
     */
    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (Callback callback : callbacks) {
            if (callback instanceof NameCallback) {
                ((NameCallback) callback).setName(name);
            } else if (callback instanceof PasswordCallback) {
                ((PasswordCallback) callback).setPassword(pswd);
            } else if (callback instanceof RealmCallback) {
                ((RealmCallback) callback).setText(realm);
            } else if (callback instanceof UserCallback) {
                user = ((UserCallback) callback).getUser();
            } else if (callback instanceof TextOutputCallback) {
                TextOutputCallback outputCallback = (TextOutputCallback) callback;
                switch (outputCallback.getMessageType()) {
                    case TextOutputCallback.INFORMATION:
                        log.info(outputCallback.getMessage());
                        break;
                    case TextOutputCallback.ERROR:
                        log.error(outputCallback.getMessage());
                        break;
                    case TextOutputCallback.WARNING:
                        log.warn(outputCallback.getMessage());
                        break;
                    default:
                        if (log.isDebugEnabled()) {
                            log.debug("Unsupported message type : {}", Integer.toString(outputCallback.getMessageType()));
                            log.debug("Message : {}", outputCallback.getMessage());
                        }
                }
            }
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.security.auth.callback.CredentialsCallbackHandler#getUser()
     */
    @Override
    public User getUser() {
        return user;
    }

}
