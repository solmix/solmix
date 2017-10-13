/**
 *    Copyright 2006-2017 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.solmix.generator.ant;

import static org.solmix.commons.util.StringUtils.stringHasValue;
import static org.solmix.generator.util.Messages.getString;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.PropertySet;
import org.solmix.commons.util.DataUtils;
import org.solmix.generator.api.CodeGenerator;
import org.solmix.generator.config.ConfigurationInfo;
import org.solmix.generator.config.ConfigurationParser;
import org.solmix.generator.config.InvalidConfigurationException;
import org.solmix.generator.config.XMLParserException;
import org.solmix.generator.internal.DefaultShellCallback;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerFactory;

/**
 * This is an Ant task that will run the generator. The following is a sample
 * Ant script that shows how to run the generator from Ant:
 * 
 * <pre>
 *  &lt;project default="genfiles" basedir="."&gt;
 *    &lt;property name="generated.source.dir" value="${basedir}" /&gt;
 *    &lt;target name="genfiles" description="Generate the files"&gt;
 *      &lt;taskdef name="mbgenerator"
 *               classname="org.solmix.generator.ant.GeneratorAntTask"
 *               classpath="mybatis-generator-core-x.x.x.jar" /&gt;
 *      &lt;mbgenerator overwrite="true" configfile="generatorConfig.xml" verbose="false" &gt;
 *        &lt;propertyset&gt;
 *          &lt;propertyref name="generated.source.dir"/&gt;
 *        &lt;/propertyset&gt;
 *      &lt;/mbgenerator&gt;
 *    &lt;/target&gt;
 *  &lt;/project&gt;
 * </pre>
 * 
 * <p>The task requires that the attribute "configFile" be set to an existing XML
 * configuration file.
 * 
 * <p>The task supports these optional attributes:
 * <ul>
 * <li>"overwrite" - if true, then existing Java files will be overwritten. if
 * false (default), then existing Java files will be untouched and the generator
 * will write new Java files with a unique name</li>
 * <li>"verbose" - if true, then the generator will log progress messages to the
 * Ant log. Default is false</li>
 * <li>"contextIds" - a comma delimited list of contaxtIds to use for this run</li>
 * <li>"fullyQualifiedTableNames" - a comma delimited list of fully qualified
 * table names to use for this run</li>
 * </ul>
 * 
 * 
 * @author Jeff Butler
 */
public class GeneratorAntTask extends Task {

    private String configfile;
    private boolean overwrite;
    private PropertySet propertyset;
    private boolean verbose;
    private String contextIds;
    private String fullyQualifiedTableNames;

    public GeneratorAntTask() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.tools.ant.Task#execute()
     */
    @Override
    public void execute() throws BuildException {
        if (!stringHasValue(configfile)) {
            throw new BuildException(getString("RuntimeError.0")); 
        }

        List<String> warnings = new ArrayList<String>();

        File configurationFile = new File(configfile);
        if (!configurationFile.exists()) {
            throw new BuildException(getString(
                    "RuntimeError.1", configfile)); 
        }

        Set<String> fullyqualifiedTables = new HashSet<String>();
        if (stringHasValue(fullyQualifiedTableNames)) {
            StringTokenizer st = new StringTokenizer(fullyQualifiedTableNames,
                    ","); 
            while (st.hasMoreTokens()) {
                String s = st.nextToken().trim();
                if (s.length() > 0) {
                    fullyqualifiedTables.add(s);
                }
            }
        }

        Set<String> contexts = new HashSet<String>();
        if (stringHasValue(contextIds)) {
            StringTokenizer st = new StringTokenizer(contextIds, ","); 
            while (st.hasMoreTokens()) {
                String s = st.nextToken().trim();
                if (s.length() > 0) {
                    contexts.add(s);
                }
            }
        }
        Container c = ContainerFactory.newInstance().createContainer();
        try {
            Properties p = propertyset == null ? null : propertyset
                    .getProperties();

            ConfigurationParser cp = new ConfigurationParser(c,DataUtils.toDataTypeMap(p), warnings);
            ConfigurationInfo config = cp.parseConfiguration(configurationFile);

            DefaultShellCallback callback = new DefaultShellCallback(overwrite);

            CodeGenerator codeGenerator = new CodeGenerator(config, callback, warnings);

            codeGenerator.generate(new AntProgressCallback(this, verbose), contexts,
                    fullyqualifiedTables);

        } catch (XMLParserException e) {
            for (String error : e.getErrors()) {
                log(error, Project.MSG_ERR);
            }

            throw new BuildException(e.getMessage());
        } catch (SQLException e) {
            throw new BuildException(e.getMessage());
        } catch (IOException e) {
            throw new BuildException(e.getMessage());
        } catch (InvalidConfigurationException e) {
            for (String error : e.getErrors()) {
                log(error, Project.MSG_ERR);
            }

            throw new BuildException(e.getMessage());
        } catch (InterruptedException e) {
            // ignore (will never happen with the DefaultShellCallback)
        } catch (Exception e) {
            log(e, Project.MSG_ERR);
            throw new BuildException(e.getMessage());
        }finally{
            if(c!=null){
                c.close();
            }
        }

        for (String error : warnings) {
            log(error, Project.MSG_WARN);
        }
    }

    /**
     * @return Returns the configfile.
     */
    public String getConfigfile() {
        return configfile;
    }

    /**
     * @param configfile
     *            The configfile to set.
     */
    public void setConfigfile(String configfile) {
        this.configfile = configfile;
    }

    /**
     * @return Returns the overwrite.
     */
    public boolean isOverwrite() {
        return overwrite;
    }

    /**
     * @param overwrite
     *            The overwrite to set.
     */
    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }

    public PropertySet createPropertyset() {
        if (propertyset == null) {
            propertyset = new PropertySet();
        }

        return propertyset;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public String getContextIds() {
        return contextIds;
    }

    public void setContextIds(String contextIds) {
        this.contextIds = contextIds;
    }

    public String getFullyQualifiedTableNames() {
        return fullyQualifiedTableNames;
    }

    public void setFullyQualifiedTableNames(String fullyQualifiedTableNames) {
        this.fullyQualifiedTableNames = fullyQualifiedTableNames;
    }
}
