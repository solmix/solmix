package org.solmix.runtime.helper;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class loader that can be used to create proxies in cases where
 * the the client classes are not visible to the loader of the
 * service class.    
 */
public class ProxyClassLoader extends ClassLoader {
    private final Class<?> classes[];
    private final Set<ClassLoader> loaders = new HashSet<ClassLoader>();
    private boolean checkSystem;

    public ProxyClassLoader(ClassLoader parent) {
        super(parent);
        classes = null;
    }

    public ProxyClassLoader(ClassLoader parent, Class<?>[] cls) {
        super(parent);
        classes = cls;
    }

    public void addLoader(ClassLoader loader) {
        if (loader == null) {
            checkSystem = true;
        } else {
            loaders.add(loader);
        }
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        if (classes != null) {
            for (Class<?> c : classes) {
                if (name.equals(c.getName())) {
                    return c;
                }
            }
        }
        for (ClassLoader loader : loaders) {
            try {
                return loader.loadClass(name);
            } catch (ClassNotFoundException cnfe) {
                // Try next
            } catch (NoClassDefFoundError cnfe) {
                // Try next
            }
        }
        if (checkSystem) {
            try {
                return getSystemClassLoader().loadClass(name);
            } catch (ClassNotFoundException cnfe) {
                // Try next
            } catch (NoClassDefFoundError cnfe) {
                // Try next
            }
        }
        throw new ClassNotFoundException(name);
    }
    
    @Override
    public URL findResource(String name) {
        for (ClassLoader loader : loaders) {
            URL url = loader.getResource(name);
            if (url != null) {
                return url;
            }
        }
        return null;
    }
}
