package org.solmix.generator.mvn;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES,
requiresDependencyResolution = ResolutionScope.TEST)
public class GeneratorMojo extends AbstractMojo
{

    private ThreadLocal<ClassLoader> savedClassloader = new ThreadLocal<ClassLoader>();

    /**
     * Maven Project.
     *
     */
    @Parameter(property = "project", required = true, readonly = true)
    private MavenProject project;
    
    
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        // TODO Auto-generated method stub
        
    }

}
