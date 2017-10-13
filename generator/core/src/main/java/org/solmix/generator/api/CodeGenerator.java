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

package org.solmix.generator.api;

import static org.solmix.generator.util.Messages.getString;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.solmix.commons.util.ClassLoaderUtils;
import org.solmix.generator.codegen.RootClassInfo;
import org.solmix.generator.config.ConfigurationInfo;
import org.solmix.generator.config.DomainInfo;
import org.solmix.generator.config.InvalidConfigurationException;
import org.solmix.generator.internal.DefaultShellCallback;
import org.solmix.generator.internal.NullProgressCallback;
import org.solmix.generator.internal.ObjectFactory;
import org.solmix.generator.internal.XmlFileMergerJaxp;

/**
 * This class is the main interface to MyBatis generator. A typical execution of the tool involves these steps:
 * 
 * <ol>
 * <li>Create a ConfigurationInfo object. The ConfigurationInfo can be the result of a parsing the XML configuration
 * file, or it can be created solely in Java.</li>
 * <li>Create a CodeGenerator object</li>
 * <li>Call one of the generate() methods</li>
 * </ol>
 *
 * @author Jeff Butler
 * @see org.mybatis.generator.config.xml.ConfigurationInfoParser
 */
public class CodeGenerator
{

    /** The configuration. */
    private ConfigurationInfo configuration;

    /** The shell callback. */
    private ShellCallback shellCallback;

    /** The generated java files. */
    private List<GeneratedJavaFile> generatedJavaFiles;

    /** The generated xml files. */
    private List<GeneratedXmlFile> generatedXmlFiles;

    /** The warnings. */
    private List<String> warnings;

    /** The projects. */
    private Set<String> projects;

    /**
     * Constructs a CodeGenerator object.
     * 
     * @param configuration The configuration for this invocation
     * @param shellCallback an instance of a ShellCallback interface. You may specify <code>null</code> in which case
     *        the DefaultShellCallback will be used.
     * @param warnings Any warnings generated during execution will be added to this list. Warnings do not affect the
     *        running of the tool, but they may affect the results. A typical warning is an unsupported data type. In
     *        that case, the column will be ignored and generation will continue. You may specify <code>null</code> if
     *        you do not want warnings returned.
     * @throws InvalidConfigurationInfoException if the specified configuration is invalid
     */
    public CodeGenerator(ConfigurationInfo configuration, ShellCallback shellCallback, List<String> warnings) throws InvalidConfigurationException
    {
        super();
        if (configuration == null) {
            throw new IllegalArgumentException(getString("RuntimeError.2"));
        } else {
            this.configuration = configuration;
        }

        if (shellCallback == null) {
            this.shellCallback = new DefaultShellCallback(false);
        } else {
            this.shellCallback = shellCallback;
        }

        if (warnings == null) {
            this.warnings = new ArrayList<String>();
        } else {
            this.warnings = warnings;
        }
        generatedJavaFiles = new ArrayList<GeneratedJavaFile>();
        generatedXmlFiles = new ArrayList<GeneratedXmlFile>();
        projects = new HashSet<String>();

        this.configuration.validate();
    }

    /**
     * This is the main method for generating code. This method is long running, but progress can be provided and the
     * method can be canceled through the ProgressCallback interface. This version of the method runs all configured
     * contexts.
     *
     * @param callback an instance of the ProgressCallback interface, or <code>null</code> if you do not require
     *        progress information
     * @throws SQLException the SQL exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InterruptedException if the method is canceled through the ProgressCallback
     */
    public void generate(ProgressCallback callback) throws SQLException, IOException, InterruptedException {
        generate(callback, null, null, true);
    }

    /**
     * This is the main method for generating code. This method is long running, but progress can be provided and the
     * method can be canceled through the ProgressCallback interface.
     *
     * @param callback an instance of the ProgressCallback interface, or <code>null</code> if you do not require
     *        progress information
     * @param contextIds a set of Strings containing context ids to run. Only the contexts with an id specified in this
     *        list will be run. If the list is null or empty, than all contexts are run.
     * @throws SQLException the SQL exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InterruptedException if the method is canceled through the ProgressCallback
     */
    public void generate(ProgressCallback callback, Set<String> contextIds) throws SQLException, IOException, InterruptedException {
        generate(callback, contextIds, null, true);
    }

    /**
     * This is the main method for generating code. This method is long running, but progress can be provided and the
     * method can be cancelled through the ProgressCallback interface.
     *
     * @param callback an instance of the ProgressCallback interface, or <code>null</code> if you do not require
     *        progress information
     * @param contextIds a set of Strings containing context ids to run. Only the contexts with an id specified in this
     *        list will be run. If the list is null or empty, than all contexts are run.
     * @param fullyQualifiedTableNames a set of table names to generate. The elements of the set must be Strings that
     *        exactly match what's specified in the configuration. For example, if table name = "foo" and schema =
     *        "bar", then the fully qualified table name is "foo.bar". If the Set is null or empty, then all tables in
     *        the configuration will be used for code generation.
     * @throws SQLException the SQL exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InterruptedException if the method is canceled through the ProgressCallback
     */
    public void generate(ProgressCallback callback, Set<String> contextIds, Set<String> fullyQualifiedTableNames) throws SQLException, IOException,
        InterruptedException {
        generate(callback, contextIds, fullyQualifiedTableNames, true);
    }

    /**
     * This is the main method for generating code. This method is long running, but progress can be provided and the
     * method can be cancelled through the ProgressCallback interface.
     *
     * @param callback an instance of the ProgressCallback interface, or <code>null</code> if you do not require
     *        progress information
     * @param contextIds a set of Strings containing context ids to run. Only the contexts with an id specified in this
     *        list will be run. If the list is null or empty, than all contexts are run.
     * @param fullyQualifiedTableNames a set of table names to generate. The elements of the set must be Strings that
     *        exactly match what's specified in the configuration. For example, if table name = "foo" and schema =
     *        "bar", then the fully qualified table name is "foo.bar". If the Set is null or empty, then all tables in
     *        the configuration will be used for code generation.
     * @param writeFiles if true, then the generated files will be written to disk. If false, then the generator runs
     *        but nothing is written
     * @throws SQLException the SQL exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InterruptedException if the method is canceled through the ProgressCallback
     */
    public void generate(ProgressCallback callback, Set<String> contextIds, Set<String> fullyQualifiedTableNames, boolean writeFiles)
        throws SQLException, IOException, InterruptedException {

        if (callback == null) {
            callback = new NullProgressCallback();
        }

        generatedJavaFiles.clear();
        generatedXmlFiles.clear();
        ObjectFactory.reset();
        RootClassInfo.reset();

        // calculate the contexts to run
        List<DomainInfo> contextsToRun;
        if (contextIds == null || contextIds.size() == 0) {
            contextsToRun = configuration.getDomains();
        } else {
            contextsToRun = new ArrayList<DomainInfo>();
            for (DomainInfo context : configuration.getDomains()) {
                if (contextIds.contains(context.getId())) {
                    contextsToRun.add(context);
                }
            }
        }

        // setup custom classloader if required
        if (configuration.getClassPathEntries().size() > 0) {
            ClassLoader classLoader = ClassLoaderUtils.getCustomClassloader(configuration.getClassPathEntries());
            ObjectFactory.addExternalClassLoader(classLoader);
        }

        // now run the introspections...
        int totalSteps = 0;
        for (DomainInfo context : contextsToRun) {
            totalSteps += context.getIntrospectionSteps();
        }
        callback.introspectionStarted(totalSteps);
        //解析出要要生成的table
        for (DomainInfo context : contextsToRun) {
            context.introspectTables(callback, warnings, fullyQualifiedTableNames);
        }

        // now run the generates
        totalSteps = 0;
        for (DomainInfo context : contextsToRun) {
            totalSteps += context.getGenerationSteps();
        }
        callback.generationStarted(totalSteps);

        for (DomainInfo context : contextsToRun) {
            context.generateFiles(callback, generatedJavaFiles, generatedXmlFiles, warnings);
        }

        // now save the files
        if (writeFiles) {
            callback.saveStarted(generatedXmlFiles.size() + generatedJavaFiles.size());

            for (GeneratedXmlFile gxf : generatedXmlFiles) {
                projects.add(gxf.getTargetProject());
                writeGeneratedXmlFile(gxf, callback);
            }

            for (GeneratedJavaFile gjf : generatedJavaFiles) {
                projects.add(gjf.getTargetProject());
                writeGeneratedJavaFile(gjf, callback);
            }

            for (String project : projects) {
                shellCallback.refreshProject(project);
            }
        }

        callback.done();
    }

    private void writeGeneratedJavaFile(GeneratedJavaFile gjf, ProgressCallback callback) throws InterruptedException, IOException {
        File targetFile;
        String source;
        try {
            File directory = shellCallback.getDirectory(gjf.getTargetProject(), gjf.getTargetPackage());
            targetFile = new File(directory, gjf.getFileName());
            if (targetFile.exists()) {
                if (shellCallback.isMergeSupported()) {
                    source = shellCallback.mergeJavaFile(gjf.getFormattedContent(), targetFile, null,gjf.getFileEncoding());
                } else if (shellCallback.isOverwriteEnabled()) {
                    source = gjf.getFormattedContent();
                    warnings.add(getString("Warning.11", targetFile.getAbsolutePath()));
                } else {
                    source = gjf.getFormattedContent();
                    targetFile = getUniqueFileName(directory, gjf.getFileName());
                    warnings.add(getString("Warning.2", targetFile.getAbsolutePath()));
                }
            } else {
                source = gjf.getFormattedContent();
            }

            callback.checkCancel();
            callback.startTask(getString("Progress.15", targetFile.getName()));
            writeFile(targetFile, source, gjf.getFileEncoding());
        } catch (ShellException e) {
            warnings.add(e.getMessage());
        }
    }

    private void writeGeneratedXmlFile(GeneratedXmlFile gxf, ProgressCallback callback) throws InterruptedException, IOException {
        File targetFile;
        String source;
        try {
            File directory = shellCallback.getDirectory(gxf.getTargetProject(), gxf.getTargetPackage());
            targetFile = new File(directory, gxf.getFileName());
            if (targetFile.exists()) {
                if (gxf.isMergeable()) {
                    source = XmlFileMergerJaxp.getMergedSource(gxf, targetFile);
                } else if (shellCallback.isOverwriteEnabled()) {
                    source = gxf.getFormattedContent();
                    warnings.add(getString("Warning.11", targetFile.getAbsolutePath()));
                } else {
                    source = gxf.getFormattedContent();
                    targetFile = getUniqueFileName(directory, gxf.getFileName());
                    warnings.add(getString("Warning.2", targetFile.getAbsolutePath()));
                }
            } else {
                source = gxf.getFormattedContent();
            }

            callback.checkCancel();
            callback.startTask(getString("Progress.15", targetFile.getName()));
            writeFile(targetFile, source, "UTF-8");
        } catch (ShellException e) {
            warnings.add(e.getMessage());
        }
    }

    /**
     * Writes, or overwrites, the contents of the specified file.
     *
     * @param file the file
     * @param content the content
     * @param fileEncoding the file encoding
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void writeFile(File file, String content, String fileEncoding) throws IOException {
        FileOutputStream fos = new FileOutputStream(file, false);
        OutputStreamWriter osw;
        if (fileEncoding == null) {
            osw = new OutputStreamWriter(fos);
        } else {
            osw = new OutputStreamWriter(fos, fileEncoding);
        }

        BufferedWriter bw = new BufferedWriter(osw);
        bw.write(content);
        bw.close();
    }

    /**
     * Gets the unique file name.
     *
     * @param directory the directory
     * @param fileName the file name
     * @return the unique file name
     */
    private File getUniqueFileName(File directory, String fileName) {
        File answer = null;

        // try up to 1000 times to generate a unique file name
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < 1000; i++) {
            sb.setLength(0);
            sb.append(fileName);
            sb.append('.');
            sb.append(i);

            File testFile = new File(directory, sb.toString());
            if (!testFile.exists()) {
                answer = testFile;
                break;
            }
        }

        if (answer == null) {
            throw new RuntimeException(getString("RuntimeError.3", directory.getAbsolutePath()));
        }

        return answer;
    }

    /**
     * Returns the list of generated Java files after a call to one of the generate methods. This is useful if you
     * prefer to process the generated files yourself and do not want the generator to write them to disk.
     * 
     * @return the list of generated Java files
     */
    public List<GeneratedJavaFile> getGeneratedJavaFiles() {
        return generatedJavaFiles;
    }

    /**
     * Returns the list of generated XML files after a call to one of the generate methods. This is useful if you prefer
     * to process the generated files yourself and do not want the generator to write them to disk.
     * 
     * @return the list of generated XML files
     */
    public List<GeneratedXmlFile> getGeneratedXmlFiles() {
        return generatedXmlFiles;
    }
}
