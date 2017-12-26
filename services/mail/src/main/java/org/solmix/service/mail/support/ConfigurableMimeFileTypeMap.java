package org.solmix.service.mail.support;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;

import org.solmix.runtime.resource.InputStreamResource;
import org.solmix.runtime.resource.support.ClassLoaderResource;


public class ConfigurableMimeFileTypeMap extends FileTypeMap
{

    private InputStreamResource mappingLocation = new ClassLoaderResource("org/solmix/service/mail/support/mime.types", ConfigurableMimeFileTypeMap.class.getClassLoader());

    /**
     * Used to configure additional mappings.
     */
    private String[] mappings;

    /**
     * The delegate FileTypeMap, compiled from the mappings in the mapping file
     * and the entries in the <code>mappings</code> property.
     */
    private FileTypeMap fileTypeMap;


    /**
     * Specify the <code>Resource</code> from which mappings are loaded.
     * <p>Needs to follow the <code>mime.types<code> file format, as specified
     * by the Java Activation Framework, containing lines such as:<br>
     * <code>text/html  html htm HTML HTM</code>
     */
    public void setMappingLocation(InputStreamResource mappingLocation) {
          this.mappingLocation = mappingLocation;
    }

    /**
     * Specify additional MIME type mappings as lines that follow the
     * <code>mime.types<code> file format, as specified by the
     * Java Activation Framework, for example:<br>
     * <code>text/html  html htm HTML HTM</code>
     */
    public void setMappings(String[] mappings) {
          this.mappings = mappings;
    }


    /**
     * Creates the final merged mapping set.
     */
    public void afterPropertiesSet() {
          getFileTypeMap();
    }

    /**
     * Return the delegate FileTypeMap, compiled from the mappings in the mapping file
     * and the entries in the <code>mappings</code> property.
     * @see #setMappingLocation
     * @see #setMappings
     * @see #createFileTypeMap
     */
    protected final FileTypeMap getFileTypeMap() {
          if (this.fileTypeMap == null) {
                try {
                      this.fileTypeMap = createFileTypeMap(this.mappingLocation, this.mappings);
                }
                catch (IOException ex) {
                      throw new IllegalStateException(
                                  "Could not load specified MIME type mapping file: " + this.mappingLocation, ex);
                }
          }
          return this.fileTypeMap;
    }

    /**
     * Compile a {@link FileTypeMap} from the mappings in the given mapping file
     * and the given mapping entries.
     * <p>The default implementation creates an Activation Framework {@link MimetypesFileTypeMap},
     * passing in an InputStream from the mapping resource (if any) and registering
     * the mapping lines programmatically.
     * @param mappingLocation a <code>mime.types</code> mapping resource (can be <code>null</code>)
     * @param mappings MIME type mapping lines (can be <code>null</code>)
     * @return the compiled FileTypeMap
     * @throws IOException if resource access failed
     * @see javax.activation.MimetypesFileTypeMap#MimetypesFileTypeMap(java.io.InputStream)
     * @see javax.activation.MimetypesFileTypeMap#addMimeTypes(String)
     */
    protected FileTypeMap createFileTypeMap(InputStreamResource mappingLocation, String[] mappings) throws IOException {
          MimetypesFileTypeMap fileTypeMap = null;
          if (mappingLocation != null) {
                InputStream is = mappingLocation.getInputStream();
                try {
                      fileTypeMap = new MimetypesFileTypeMap(is);
                }
                finally {
                      is.close();
                }
          }
          else {
                fileTypeMap = new MimetypesFileTypeMap();
          }
          if (mappings != null) {
                for (String mapping : mappings) {
                      fileTypeMap.addMimeTypes(mapping);
                }
          }
          return fileTypeMap;
    }


    /**
     * Delegates to the underlying FileTypeMap.
     * @see #getFileTypeMap()
     */
    @Override
    public String getContentType(File file) {
          return getFileTypeMap().getContentType(file);
    }

    /**
     * Delegates to the underlying FileTypeMap.
     * @see #getFileTypeMap()
     */
    @Override
    public String getContentType(String fileName) {
          return getFileTypeMap().getContentType(fileName);
    }

}
