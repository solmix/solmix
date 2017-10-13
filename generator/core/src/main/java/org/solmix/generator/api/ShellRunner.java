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

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.solmix.generator.config.ConfigurationInfo;
import org.solmix.generator.config.ConfigurationParser;
import org.solmix.generator.config.InvalidConfigurationException;
import org.solmix.generator.config.XMLParserException;
import org.solmix.generator.internal.DefaultShellCallback;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerFactory;

/**
 * This class allows the code generator to be run from the command line.
 * 
 * @author Jeff Butler
 */
public class ShellRunner {
    private static final String CONFIG_FILE = "-configfile"; 
    private static final String OVERWRITE = "-overwrite"; 
    private static final String CONTEXT_IDS = "-contextids"; 
    private static final String TABLES = "-tables"; 
    private static final String VERBOSE = "-verbose"; 
    private static final String FORCE_JAVA_LOGGING = "-forceJavaLogging"; 
    private static final String HELP_1 = "-?"; 
    private static final String HELP_2 = "-h"; 

    public static void main(String[] args) {
        if (args.length == 0) {
            usage();
            System.exit(0);
            return; // only to satisfy compiler, never returns
        }

        Map<String, String> arguments = parseCommandLine(args);

        if (arguments.containsKey(HELP_1)) {
            usage();
            System.exit(0);
            return; // only to satisfy compiler, never returns
        }

        if (!arguments.containsKey(CONFIG_FILE)) {
            writeLine(getString("RuntimeError.0")); 
            return;
        }

        List<String> warnings = new ArrayList<String>();

        String configfile = arguments.get(CONFIG_FILE);
        File configurationFile = new File(configfile);
        if (!configurationFile.exists()) {
            writeLine(getString("RuntimeError.1", configfile)); 
            return;
        }

        Set<String> fullyqualifiedTables = new HashSet<String>();
        if (arguments.containsKey(TABLES)) {
            StringTokenizer st = new StringTokenizer(arguments.get(TABLES), ","); 
            while (st.hasMoreTokens()) {
                String s = st.nextToken().trim();
                if (s.length() > 0) {
                    fullyqualifiedTables.add(s);
                }
            }
        }

        Set<String> contexts = new HashSet<String>();
        if (arguments.containsKey(CONTEXT_IDS)) {
            StringTokenizer st = new StringTokenizer(
                    arguments.get(CONTEXT_IDS), ","); 
            while (st.hasMoreTokens()) {
                String s = st.nextToken().trim();
                if (s.length() > 0) {
                    contexts.add(s);
                }
            }
        }
        Container c=null;
        try {
            c = ContainerFactory.newInstance().createContainer();
            ConfigurationParser cp = new ConfigurationParser(c,warnings);
            ConfigurationInfo config = cp.parseConfiguration(configurationFile);

            DefaultShellCallback shellCallback = new DefaultShellCallback(
                    arguments.containsKey(OVERWRITE));

            CodeGenerator codeGenerator = new CodeGenerator(config, shellCallback, warnings);

            ProgressCallback progressCallback = arguments.containsKey(VERBOSE) ? new VerboseProgressCallback()
                    : null;

            codeGenerator.generate(progressCallback, contexts, fullyqualifiedTables);

        } catch (XMLParserException e) {
            writeLine(getString("Progress.3")); 
            writeLine();
            for (String error : e.getErrors()) {
                writeLine(error);
            }

            return;
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (InvalidConfigurationException e) {
            writeLine(getString("Progress.16")); 
            for (String error : e.getErrors()) {
                writeLine(error);
            }
            return;
        } catch (InterruptedException e) {
            // ignore (will never happen with the DefaultShellCallback)
        }

        for (String warning : warnings) {
            writeLine(warning);
        }

        if (warnings.size() == 0) {
            writeLine(getString("Progress.4")); 
        } else {
            writeLine();
            writeLine(getString("Progress.5")); 
        }
    }

    private static void usage() {
        String lines = getString("Usage.Lines"); 
        int intLines = Integer.parseInt(lines);
        for (int i = 0; i < intLines; i++) {
            String key = "Usage." + i; 
            writeLine(getString(key));
        }
    }

    private static void writeLine(String message) {
        System.out.println(message);
    }

    private static void writeLine() {
        System.out.println();
    }

    private static Map<String, String> parseCommandLine(String[] args) {
        List<String> errors = new ArrayList<String>();
        Map<String, String> arguments = new HashMap<String, String>();

        for (int i = 0; i < args.length; i++) {
            if (CONFIG_FILE.equalsIgnoreCase(args[i])) {
                if ((i + 1) < args.length) {
                    arguments.put(CONFIG_FILE, args[i + 1]);
                } else {
                    errors.add(getString(
                            "RuntimeError.19", CONFIG_FILE)); 
                }
                i++;
            } else if (OVERWRITE.equalsIgnoreCase(args[i])) {
                arguments.put(OVERWRITE, "Y"); 
            } else if (VERBOSE.equalsIgnoreCase(args[i])) {
                arguments.put(VERBOSE, "Y"); 
            } else if (HELP_1.equalsIgnoreCase(args[i])) {
                arguments.put(HELP_1, "Y"); 
            } else if (HELP_2.equalsIgnoreCase(args[i])) {
                // put HELP_1 in the map here too - so we only
                // have to check for one entry in the mainline
                arguments.put(HELP_1, "Y"); 
            } else if (FORCE_JAVA_LOGGING.equalsIgnoreCase(args[i])) {
            } else if (CONTEXT_IDS.equalsIgnoreCase(args[i])) {
                if ((i + 1) < args.length) {
                    arguments.put(CONTEXT_IDS, args[i + 1]);
                } else {
                    errors.add(getString(
                            "RuntimeError.19", CONTEXT_IDS)); 
                }
                i++;
            } else if (TABLES.equalsIgnoreCase(args[i])) {
                if ((i + 1) < args.length) {
                    arguments.put(TABLES, args[i + 1]);
                } else {
                    errors.add(getString("RuntimeError.19", TABLES)); 
                }
                i++;
            } else {
                errors.add(getString("RuntimeError.20", args[i])); 
            }
        }

        if (!errors.isEmpty()) {
            for (String error : errors) {
                writeLine(error);
            }

            System.exit(-1);
        }

        return arguments;
    }
}
