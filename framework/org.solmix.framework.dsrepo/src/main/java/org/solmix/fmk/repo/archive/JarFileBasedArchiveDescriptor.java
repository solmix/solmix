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
package org.solmix.fmk.repo.archive;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.repo.NamedInputStream;
import org.solmix.api.repo.archive.ArchiveDescriptorFactory;
import org.solmix.api.repo.archive.ArchiveEntry;
import org.solmix.api.repo.archive.ArchiveEntryHandler;
import org.solmix.api.repo.archive.VisitContext;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-11-23
 */

public class JarFileBasedArchiveDescriptor  extends AbstractArchiveDescriptor
{
    private static final Logger LOG = LoggerFactory.getLogger(ExplodedArchiveDescriptor.class.getName());

    /**
     * @param archiveDescriptorFactory
     * @param archiveUrl
     * @param entryBasePrefix
     */
    protected JarFileBasedArchiveDescriptor(ArchiveDescriptorFactory archiveDescriptorFactory, URL archiveUrl, String entryBasePrefix)
    {
        super(archiveDescriptorFactory, archiveUrl, entryBasePrefix);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.repo.archive.ArchiveDescriptor#visitArchive(org.solmix.api.repo.archive.VisitContext)
     */
    @Override
    public void visitArchive(VisitContext context) {
        final JarFile jarFile = resolveJarFileReference();
        if ( jarFile == null ) {
              return;
        }

        final Enumeration<? extends ZipEntry> zipEntries = jarFile.entries();
        while ( zipEntries.hasMoreElements() ) {
              final ZipEntry zipEntry = zipEntries.nextElement();
              final String entryName = extractName( zipEntry );

              if ( getEntryBasePrefix() != null && ! entryName.startsWith( getEntryBasePrefix() ) ) {
                    continue;
              }
              if ( zipEntry.isDirectory() ) {
                    continue;
              }

              if ( entryName.equals( getEntryBasePrefix() ) ) {
                    // exact match, might be a nested jar entry (ie from jar:file:..../foo.ear!/bar.jar)
                    //
                    // This algorithm assumes that the zipped file is only the URL root (including entry), not
                    // just any random entry
                    try {
                          final InputStream is = new BufferedInputStream( jarFile.getInputStream( zipEntry ) );
                          try {
                                final JarInputStream jarInputStream = new JarInputStream( is );
                                ZipEntry subZipEntry = jarInputStream.getNextEntry();
                                while ( subZipEntry != null ) {
                                      if ( ! subZipEntry.isDirectory() ) {

                                            final String name = extractName( subZipEntry );
                                            final String relativeName = extractRelativeName( subZipEntry );
                                            final NamedInputStream namedInputStream
                                                        = buildByteBasedInputStream( name, jarInputStream );

                                            final ArchiveEntry entry = new ArchiveEntry() {
                                                  @Override
                                                  public String getName() {
                                                        return name;
                                                  }

                                                  @Override
                                                  public String getNameWithinArchive() {
                                                        return relativeName;
                                                  }

                                                @Override
                                                public NamedInputStream getNamedInputStream() {
                                                    return namedInputStream;
                                                }
                                            };

                                            final ArchiveEntryHandler entryHandler = context.obtainArchiveEntryHandler( entry );
                                            entryHandler.handleEntry( entry, context );
                                      }

                                      subZipEntry = jarInputStream.getNextEntry();
                                }
                          }
                          finally {
                                is.close();
                          }
                    }
                    catch (Exception e) {
                          throw new java.lang.IllegalArgumentException( "Error accessing JarFile entry [" + zipEntry.getName() + "]", e );
                    }
              }
              else {
                    final String name = extractName( zipEntry );
                    final String relativeName = extractRelativeName( zipEntry );
                    final NamedInputStream namedInputStream;
                    try {
                        namedInputStream = buildByteBasedInputStream( name, jarFile.getInputStream( zipEntry ) );
                    }
                    catch (IOException e) {
                          throw  new java.lang.IllegalArgumentException(
                                      String.format(
                                                  "Unable to access stream from jar file [%s] for entry [%s]",
                                                  jarFile.getName(),
                                                  zipEntry.getName()
                                      )
                          );
                    }

                    final ArchiveEntry entry = new ArchiveEntry() {
                          @Override
                          public String getName() {
                                return name;
                          }

                          @Override
                          public String getNameWithinArchive() {
                                return relativeName;
                          }


                        @Override
                        public NamedInputStream getNamedInputStream() {
                            return namedInputStream;
                        }
                    };

                    final ArchiveEntryHandler entryHandler = context.obtainArchiveEntryHandler( entry );
                    entryHandler.handleEntry( entry, context );
              }
        }
  }

  private JarFile resolveJarFileReference() {
        try {
              final String filePart = getArchiveUrl().getFile();
              if ( filePart != null && filePart.indexOf( ' ' ) != -1 ) {
                    // unescaped (from the container), keep as is
                    return new JarFile( getArchiveUrl().getFile() );
              }
              else {
                    return new JarFile( getArchiveUrl().toURI().getSchemeSpecificPart() );
              }
        }
        catch (IOException e) {
              LOG.error( "unable to find file"+getArchiveUrl(), e );
        }
        catch (URISyntaxException e) {
            LOG.warn( "malformed url warning "+getArchiveUrl(), e );
        }
        return null;
  }

}
