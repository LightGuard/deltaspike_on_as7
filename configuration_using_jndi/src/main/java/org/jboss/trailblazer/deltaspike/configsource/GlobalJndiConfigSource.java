/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the &quot;License&quot;);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.trailblazer.deltaspike.configsource;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.deltaspike.core.spi.config.ConfigSource;
import org.apache.deltaspike.core.spi.config.ConfigSourceProvider;

/**
 * Configuration source for DeltaSpike to look up in the java:global/deltaspike JNDI context.
 *
 * @author <a href="https://community.jboss.org/people/lightguard">Jason Porter</a>
 */
public class GlobalJndiConfigSource implements ConfigSource, ConfigSourceProvider {

    private final String GLOBAL_ENV_BASE_NAME = "java:global/deltaspike";
    private final Logger logger = Logger.getLogger(GlobalJndiConfigSource.class.getName());
    private final InitialContext initialContext;

    public GlobalJndiConfigSource() {
        try {
            initialContext = new InitialContext();
        } catch (NamingException e) {
            logger.fine("Error constructing an InitialContext");
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getOrdinal() {
        return 350;
    }

    @Override
    public String getPropertyValue(String key) {
        try {
            String jndiKey;
            if ("java:global".startsWith(key)) {
                jndiKey = key;
            } else {
                jndiKey = GLOBAL_ENV_BASE_NAME + "/" + key;
            }

            Object result = initialContext.lookup(jndiKey);

            if (result instanceof String) {
                return (String) result;
            } else {
                logger.warning("Result from JNDI was not a string!");
                return result.toString();
            }
        } catch (NamingException e) {
            // swallow, we're just looking to see if it's there.
        }
        return null;
    }

    @Override
    public String getConfigName() {
        return GLOBAL_ENV_BASE_NAME;
    }

    @Override
    public List<ConfigSource> getConfigSources() {
        return Arrays.asList((ConfigSource) this);
    }
}
