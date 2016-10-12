/*
 * Copyright 2015 The Solmix Project
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
package org.solmix.commons.pager;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
@SuppressWarnings({ "unchecked", "rawtypes" })
public class Pager {
    public static final String DEFAULT_PROCESSOR_CLASSNAME = DefaultPagerProcessor.class.getName();
    private static final Map<String,Pager> PAGER_PROCESSOR_MAP = Collections.synchronizedMap(new HashMap<String,Pager>());

    private PagerProcessor processor;
    private boolean skipNulls = false;
    private PagerEventHandler eventHandler;

    public Pager(PagerProcessor processor) {
        this.processor = processor;
        skipNulls = false;
        eventHandler = null;
        if (processor instanceof PagerProcessorExt) {
            skipNulls = ((PagerProcessorExt) processor).skipNulls();
            eventHandler = ((PagerProcessorExt) processor).getEventHandler();
        }
    }

    public static Pager getDefaultPager() {
        try {
            return getPager(DEFAULT_PROCESSOR_CLASSNAME);
        } catch (Exception e) {
            throw new RuntimeException("This should never happen", e);
        }
    }

    /**
     * Get a pager based on the PagerProcessor supplied.
     */
    public static Pager getPager(String className)
        throws InstantiationException, IllegalAccessException,
        ClassNotFoundException {
        Pager p = PAGER_PROCESSOR_MAP.get(className);

        if (p == null) {
            PagerProcessor processor = (PagerProcessor)
                                       Class.forName(className).newInstance();
            p = new Pager(processor);
            PAGER_PROCESSOR_MAP.put(className, p);
        }
        return p;
    }

    /**
     * Seek to the specified page number in the source collection and
     * return page size and number of elements in the List.
     * If page number or page size is -1, then everything in the
     * source collection will be returned.
     * 
     * @param source The source collection to seek through.
     * @param pagenum The page number to seek to. If there not
     *        enough pages in the collection, then an empty list will be
     *        returned.
     * @param pagesize The size of each page.
     * @return PageList containing results of seek.
     */
    public PageList seek(Collection source, int pagenum, int pagesize) {
        return seek(source, pagenum, pagesize, null);
    }

    /**
     * Seek to the specified pagenum in the source collection and return pagsize
     * numberof of elements in the List, as specified the PageControl object.
     * If pagenum or pagesize is -1, then everything in the
     * source collection will be returned.
     * 
     * @param source The source collection to seek through.
     * @param pc The PageControl object to use to control paging.
     * @return PageList containing results of seek.
     */
    public PageList seek(Collection source, PageControl pc) {
        if (pc == null)
            pc = PageControl.PAGE_ALL;

        return seek(source, pc.getPageNum(), pc.getPageSize(), null);
    }

    public PageList seek(Collection source, PageControl pc, Object procData) {
        if (pc == null)
            pc = PageControl.PAGE_ALL;
        return seek(source, pc.getPageNum(), pc.getPageSize(), procData);
    }

    /**
     * Seek to the specified pagenum in the source collection and
     * return pagsize numberof of elements in the List.
     * If pagenum or pagesize is -1, then everything in the
     * source collection will be returned.
     * 
     * @param source The source collection to seek through.
     * @param pagenum The page number to seek to. If there not
     *        enough pages in the collection, then an empty list will be
     *        returned.
     * @param pagesize The size of each page.
     * @param procData - any data object required by the _processor.
     * @return PageList containing results of seek.
     */
    public PageList seek(Collection source, int pagenum, int pagesize,
                         Object procData) {
        PageList dest = new PageList();
        dest.setTotalSize(seek(source, dest, pagenum, pagesize, procData));
        return dest;
    }

    /**
     * Seek to the specified pagenum in the source collection and place
     * pagesize number of elements into the dest collection.
     * If pagenum or pagesize is -1, then everything in the
     * source collection will be placed in the dest collection.
     * 
     * @param source The source collection to seek through.
     * @param dest The collection to place results into.
     * @param pagenum The page number to seek to. If there not
     *        enough pages in the collection, then an empty list will be
     *        returned.
     * @param pagesize The size of each page.
     */
    public void seek(Collection source, Collection dest, int pagenum,
                     int pagesize) {
        seek(source, dest, pagenum, pagesize, null);
    }

    /**
     * Seek to the specified page number in the source collection and place
     * page size number of elements into the dest collection.
     * If page number or page size is -1, then everything in the
     * source collection will be placed in the dest collection.
     * 
     * @param source The source collection to seek through.
     * @param dest The collection to place results into.
     * @param pagenum The page number to seek to. If there not
     *        enough pages in the collection, then an empty list will be
     *        returned.
     * @param pagesize The size of each page.
     * @param procData any object required to process the item.
     */
    public int seek(Collection source, Collection dest, int pagenum,
                    int pagesize, Object procData) {
        Iterator iter = source.iterator();
        int i, currentPage, size = source.size();

        if (pagesize == -1 || pagenum == -1) {
            pagenum = 0;
            pagesize = Integer.MAX_VALUE;
        }

        for (i = 0, currentPage = 0; iter.hasNext() && currentPage < pagenum; i++, currentPage += (i % pagesize == 0) ? 1
                                                                                                                     : 0) {
            iter.next();
        }

        if (eventHandler != null)
            eventHandler.init();

        if (skipNulls) {
            Object elt;
            while (iter.hasNext()) {
                if (processor instanceof PagerProcessorExt)
                    elt = ((PagerProcessorExt) processor)
                                                          .processElement(iter.next(), procData);
                else
                    elt = processor.processElement(iter.next());
                if (elt == null) {
                    size--;
                    continue;
                }

                // Need to keep accurate count, so gotta keep checking
                if (dest.size() < pagesize) {
                    dest.add(elt);
                } else if (procData == null) {
                    break;
                }
            }
        } else {
            while (iter.hasNext() && dest.size() < pagesize) {
                dest.add(processor.processElement(iter.next()));
            }
        }

        if (eventHandler != null)
            eventHandler.cleanup();

        return size;
    }

    /**
     * Seek to the specified pagenum in the source collection and place
     * pagesize number of elements into the dest collection. Unlike,
     * seek(), all items are passed to the Processor or ProcessorExt
     * regardless whether they are placed in dest collection. If pagenum
     * or pagesize is -1, then everything in the source collection will
     * be placed in the dest collection.
     * 
     * @param source The source collection to seek through.
     * @param pagenum The page number to seek to. If there not
     *        enough pages in the collection, then an empty list will be
     *        returned.
     * @param pagesize The size of each page.
     * @param procData any object required to process the item.
     */
    public PageList seekAll(Collection source, int pagenum, int pagesize,
                            Object procData) {
        PageList dest = new PageList();
        dest.setTotalSize(seekAll(source, dest, pagenum, pagesize, procData));
        return dest;
    }

    /**
     * Seek to the specified page number in the source collection and place
     * page size number of elements into the dest collection. Unlike,
     * seek(), all items are passed to the Processor or ProcessorExt
     * regardless whether they are placed in dest collection. If page number
     * or page size is -1, then everything in the source collection will
     * be placed in the dest collection.
     * 
     * @param source The source collection to seek through.
     * @param dest The collection to place results into.
     * @param pagenum The page number to seek to. If there not
     *        enough pages in the collection, then an empty list will be
     *        returned.
     * @param pagesize The size of each page.
     * @param procData any object required to process the item.
     */
   
	public int seekAll(Collection source, Collection dest, int pagenum,
                       int pagesize, Object procData) {
        Iterator iter = source.iterator();
        int i, currentPage, size = source.size();

        if (pagesize == -1 || pagenum == -1) {
            pagenum = 0;
            pagesize = Integer.MAX_VALUE;
        }

        for (i = 0, currentPage = 0; iter.hasNext() && currentPage < pagenum; currentPage += (i != 0 && i % pagesize == 0) ? 1
                                                                                                                          : 0) {
            Object ret = null;

            if (processor instanceof PagerProcessorExt) {
                ret = ((PagerProcessorExt) processor).processElement(iter.next(), procData);
            } else {
                ret = processor.processElement(iter.next());
            }

            if (ret != null) {
                i++;
            }
        }

        if (eventHandler != null)
            eventHandler.init();

        if (skipNulls) {
            Object elt;
            while (iter.hasNext()) {
                if (processor instanceof PagerProcessorExt)
                    elt = ((PagerProcessorExt) processor)
                                                          .processElement(iter.next(), procData);
                else
                    elt = processor.processElement(iter.next());

                if (elt == null) {
                    size--;
                    continue;
                }

                if (dest.size() < pagesize)
                    dest.add(elt);
            }
        } else {
            while (iter.hasNext()) {
                Object elt = processor.processElement(iter.next());
                if (dest.size() < pagesize)
                    dest.add(elt);
            }
        }

        if (eventHandler != null)
            eventHandler.cleanup();

        return size;
    }

    /**
     * Process all objects in the source page list and return the destination
     * page list with the same total size
     */
    public PageList<?> processAll(PageList<?> source) {
        PageList dest = new PageList();
        int size = source.getTotalSize();
        for (Iterator<?> it = source.iterator(); it.hasNext();) {
            Object elt = this.processor.processElement(it.next());
            if (elt == null) {
                size--;
                continue;
            }
            dest.add(elt);
        }

        dest.setTotalSize(size);
        return dest;
    }

    public Object processOne(Object one) {
        return processor.processElement(one);
    }
}
