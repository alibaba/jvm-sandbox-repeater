/*
 * Copyright (c) 2001-2008 Caucho Technology, Inc.  All rights reserved.
 *
 * The Apache Software License, Version 1.1
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Caucho Technology (http://www.caucho.com/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "Burlap", "Resin", and "Caucho" must not be used to
 *    endorse or promote products derived from this software without prior
 *    written permission. For written permission, please contact
 *    info@caucho.com.
 *
 * 5. Products derived from this software may not be called "Resin"
 *    nor may "Resin" appear in their names without prior written
 *    permission of Caucho Technology.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL CAUCHO TECHNOLOGY OR ITS CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * @author Scott Ferguson
 */

package com.caucho.hessian.io;

import com.caucho.hessian.HessianException;

import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The classloader-specific Factory for returning serialization
 */
public class ContextSerializerFactory
{
  private static final Logger log
    = Logger.getLogger(ContextSerializerFactory.class.getName());

  private static Deserializer OBJECT_DESERIALIZER
    = new BasicDeserializer(BasicDeserializer.OBJECT);

  private static final WeakHashMap<ClassLoader,SoftReference<ContextSerializerFactory>>
    _contextRefMap
    = new WeakHashMap<ClassLoader,SoftReference<ContextSerializerFactory>>();

  private static final ClassLoader _systemClassLoader;

  private static HashMap<String,Serializer> _staticSerializerMap;
  private static HashMap<String,Deserializer> _staticDeserializerMap;
  private static HashMap _staticClassNameMap;

  private ContextSerializerFactory _parent;
  private WeakReference<ClassLoader> _loaderRef;

  private final HashSet<String> _serializerFiles = new HashSet<String>();
  private final HashSet<String> _deserializerFiles = new HashSet<String>();

  private final HashMap<String,Serializer> _serializerClassMap
    = new HashMap<String,Serializer>();

  private final ConcurrentHashMap<String,Serializer> _customSerializerMap
    = new ConcurrentHashMap<String,Serializer>();

  private final HashMap<Class<?>,Serializer> _serializerInterfaceMap
    = new HashMap<Class<?>,Serializer>();

  private final HashMap<String,Deserializer> _deserializerClassMap
    = new HashMap<String,Deserializer>();

  private final HashMap<String,Deserializer> _deserializerClassNameMap
    = new HashMap<String,Deserializer>();

  private final ConcurrentHashMap<String,Deserializer> _customDeserializerMap
    = new ConcurrentHashMap<String,Deserializer>();

  private final HashMap<Class<?>,Deserializer> _deserializerInterfaceMap
    = new HashMap<Class<?>,Deserializer>();

  public ContextSerializerFactory(ContextSerializerFactory parent,
                                  ClassLoader loader)
  {
    if (loader == null)
      loader = _systemClassLoader;

    _loaderRef = new WeakReference<ClassLoader>(loader);

    init();
  }

  public static ContextSerializerFactory create()
  {
    return create(Thread.currentThread().getContextClassLoader());
  }

  public static ContextSerializerFactory create(ClassLoader loader)
  {
    synchronized (_contextRefMap) {
      SoftReference<ContextSerializerFactory> factoryRef
        = _contextRefMap.get(loader);

      ContextSerializerFactory factory = null;

      if (factoryRef != null)
        factory = factoryRef.get();

      if (factory == null) {
        ContextSerializerFactory parent = null;

        if (loader != null)
          parent = create(loader.getParent());

        factory = new ContextSerializerFactory(parent, loader);
        factoryRef = new SoftReference<ContextSerializerFactory>(factory);

        _contextRefMap.put(loader, factoryRef);
      }

      return factory;
    }
  }

  public ClassLoader getClassLoader()
  {
    WeakReference<ClassLoader> loaderRef = _loaderRef;
    
    if (loaderRef != null)
      return loaderRef.get();
    else
      return null;
  }

  /**
   * Returns the serializer for a given class.
   */
  public Serializer getSerializer(String className)
  {
    Serializer serializer = _serializerClassMap.get(className);

    if (serializer == AbstractSerializer.NULL)
      return null;
    else
      return serializer;
  }

  /**
   * Returns a custom serializer the class
   *
   * @param cl the class of the object that needs to be serialized.
   *
   * @return a serializer object for the serialization.
   */
  public Serializer getCustomSerializer(Class cl)
  {
    Serializer serializer = _customSerializerMap.get(cl.getName());

    if (serializer == AbstractSerializer.NULL)
      return null;
    else if (serializer != null)
      return serializer;

    try {
      Class serClass = Class.forName(cl.getName() + "HessianSerializer",
                                       false, cl.getClassLoader());

      Serializer ser = (Serializer) serClass.newInstance();

      _customSerializerMap.put(cl.getName(), ser);

      return ser;
    } catch (ClassNotFoundException e) {
      log.log(Level.ALL, e.toString(), e);
    } catch (Exception e) {
      throw new HessianException(e);
    }

    _customSerializerMap.put(cl.getName(), AbstractSerializer.NULL);

    return null;
  }

  /**
   * Returns the deserializer for a given class.
   */
  public Deserializer getDeserializer(String className)
  {
    Deserializer deserializer = _deserializerClassMap.get(className);
    
    if (deserializer != null && deserializer != AbstractDeserializer.NULL) {
      return deserializer;
    }
    
    deserializer = _deserializerInterfaceMap.get(className);
    
    if (deserializer != null && deserializer != AbstractDeserializer.NULL) {
      return deserializer;
    }
    
    return null;
  }

  /**
   * Returns a custom deserializer the class
   *
   * @param cl the class of the object that needs to be deserialized.
   *
   * @return a deserializer object for the deserialization.
   */
  public Deserializer getCustomDeserializer(Class cl)
  {
    Deserializer deserializer = _customDeserializerMap.get(cl.getName());

    if (deserializer == AbstractDeserializer.NULL)
      return null;
    else if (deserializer != null)
      return deserializer;

    try {
      Class serClass = Class.forName(cl.getName() + "HessianDeserializer",
                                       false, cl.getClassLoader());

      Deserializer ser = (Deserializer) serClass.newInstance();

      _customDeserializerMap.put(cl.getName(), ser);

      return ser;
    } catch (ClassNotFoundException e) {
      log.log(Level.ALL, e.toString(), e);
    } catch (Exception e) {
      throw new HessianException(e);
    }

    _customDeserializerMap.put(cl.getName(), AbstractDeserializer.NULL);

    return null;
  }

  /**
   * Initialize the factory
   */
  private void init()
  {
    if (_parent != null) {
      _serializerFiles.addAll(_parent._serializerFiles);
      _deserializerFiles.addAll(_parent._deserializerFiles);

      _serializerClassMap.putAll(_parent._serializerClassMap);
      _deserializerClassMap.putAll(_parent._deserializerClassMap);
    }

    if (_parent == null) {
      _serializerClassMap.putAll(_staticSerializerMap);
      _deserializerClassMap.putAll(_staticDeserializerMap);
      _deserializerClassNameMap.putAll(_staticClassNameMap);
    }

    HashMap<Class,Class> classMap;

    classMap = new HashMap<Class,Class>();
    // cause classloader isolation; if target app classloader loaded hessian the fellow code will detected failed
    // code place is —> if (! type.isAssignableFrom(serializerClass))

//    initSerializerFiles("META-INF/hessian/serializers",
//                        _serializerFiles,
//                        classMap,
//                        Serializer.class);

    for (Map.Entry<Class,Class> entry : classMap.entrySet()) {
      try {
        Serializer ser = (Serializer) entry.getValue().newInstance();

        if (entry.getKey().isInterface())
          _serializerInterfaceMap.put(entry.getKey(), ser);
        else
          _serializerClassMap.put(entry.getKey().getName(), ser);
      } catch (Exception e) {
        throw new HessianException(e);
      }
    }

    classMap = new HashMap<Class,Class>();
    // cause classloader isolation; if target app classloader loaded hessian the fellow code will detected failed
    // code place is —> if (! type.isAssignableFrom(serializerClass))

//    initSerializerFiles("META-INF/hessian/deserializers",
//                        _deserializerFiles,
//                        classMap,
//                        Deserializer.class);

    for (Map.Entry<Class,Class> entry : classMap.entrySet()) {
      try {
        Deserializer ser = (Deserializer) entry.getValue().newInstance();

        if (entry.getKey().isInterface())
          _deserializerInterfaceMap.put(entry.getKey(), ser);
        else {
          _deserializerClassMap.put(entry.getKey().getName(), ser);
        }
      } catch (Exception e) {
        throw new HessianException(e);
      }
    }
  }

  private void initSerializerFiles(String fileName,
                                   HashSet<String> fileList,
                                   HashMap<Class,Class> classMap,
                                   Class type)
  {
    try {
      ClassLoader classLoader = getClassLoader();
      
      // on systems with the security manager enabled, the system classloader
      // is null
      if (classLoader == null)
        return;

      Enumeration iter;
      
      iter = classLoader.getResources(fileName);
      while (iter.hasMoreElements()) {
        URL url = (URL) iter.nextElement();

        if (fileList.contains(url.toString()))
          continue;

        fileList.add(url.toString());

        InputStream is = null;
        try {
          is = url.openStream();

          Properties props = new Properties();
          props.load(is);

          for (Map.Entry entry : props.entrySet()) {
            String apiName = (String) entry.getKey();
            String serializerName = (String) entry.getValue();

            Class apiClass = null;
            Class serializerClass = null;

            try {
              apiClass = Class.forName(apiName, false, classLoader);
            } catch (ClassNotFoundException e) {
              log.fine(url + ": " + apiName + " is not available in this context: " + getClassLoader());
              continue;
            }

            try {
              serializerClass = Class.forName(serializerName, false, classLoader);
            } catch (ClassNotFoundException e) {
              log.fine(url + ": " + serializerName + " is not available in this context: " + getClassLoader());
              continue;
            }

            if (! type.isAssignableFrom(serializerClass))
              throw new HessianException(url + ": " + serializerClass.getName() + " is invalid because it does not implement " + type.getName());

            classMap.put(apiClass, serializerClass);
          }
        } finally {
          if (is != null)
            is.close();
        }
      }
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new HessianException(e);
    }
  }

  private static void addBasic(Class cl, String typeName, int type)
  {
    _staticSerializerMap.put(cl.getName(), new BasicSerializer(type));

    Deserializer deserializer = new BasicDeserializer(type);
    _staticDeserializerMap.put(cl.getName(), deserializer);
    _staticClassNameMap.put(typeName, deserializer);
  }

  static {
    _staticSerializerMap = new HashMap();
    _staticDeserializerMap = new HashMap();
    _staticClassNameMap = new HashMap();

    addBasic(void.class, "void", BasicSerializer.NULL);

    addBasic(Boolean.class, "boolean", BasicSerializer.BOOLEAN);
    addBasic(Byte.class, "byte", BasicSerializer.BYTE);
    addBasic(Short.class, "short", BasicSerializer.SHORT);
    addBasic(Integer.class, "int", BasicSerializer.INTEGER);
    addBasic(Long.class, "long", BasicSerializer.LONG);
    addBasic(Float.class, "float", BasicSerializer.FLOAT);
    addBasic(Double.class, "double", BasicSerializer.DOUBLE);
    addBasic(Character.class, "char", BasicSerializer.CHARACTER_OBJECT);
    addBasic(String.class, "string", BasicSerializer.STRING);
    addBasic(Object.class, "object", BasicSerializer.OBJECT);
    addBasic(Date.class, "date", BasicSerializer.DATE);

    addBasic(boolean.class, "boolean", BasicSerializer.BOOLEAN);
    addBasic(byte.class, "byte", BasicSerializer.BYTE);
    addBasic(short.class, "short", BasicSerializer.SHORT);
    addBasic(int.class, "int", BasicSerializer.INTEGER);
    addBasic(long.class, "long", BasicSerializer.LONG);
    addBasic(float.class, "float", BasicSerializer.FLOAT);
    addBasic(double.class, "double", BasicSerializer.DOUBLE);
    addBasic(char.class, "char", BasicSerializer.CHARACTER);

    addBasic(boolean[].class, "[boolean", BasicSerializer.BOOLEAN_ARRAY);
    addBasic(byte[].class, "[byte", BasicSerializer.BYTE_ARRAY);
    _staticSerializerMap.put(byte[].class.getName(), ByteArraySerializer.SER);
    addBasic(short[].class, "[short", BasicSerializer.SHORT_ARRAY);
    addBasic(int[].class, "[int", BasicSerializer.INTEGER_ARRAY);
    addBasic(long[].class, "[long", BasicSerializer.LONG_ARRAY);
    addBasic(float[].class, "[float", BasicSerializer.FLOAT_ARRAY);
    addBasic(double[].class, "[double", BasicSerializer.DOUBLE_ARRAY);
    addBasic(char[].class, "[char", BasicSerializer.CHARACTER_ARRAY);
    addBasic(String[].class, "[string", BasicSerializer.STRING_ARRAY);
    addBasic(Object[].class, "[object", BasicSerializer.OBJECT_ARRAY);

    Deserializer objectDeserializer = new JavaDeserializer(Object.class);
    _staticDeserializerMap.put("object", objectDeserializer);
    _staticClassNameMap.put("object", objectDeserializer);

    _staticSerializerMap.put(Class.class.getName(), new ClassSerializer());

    _staticDeserializerMap.put(Number.class.getName(), new BasicDeserializer(BasicSerializer.NUMBER));

    /*
    for (Class cl : new Class[] { BigDecimal.class, File.class, ObjectName.class }) {
      _staticSerializerMap.put(cl, StringValueSerializer.SER);
      _staticDeserializerMap.put(cl, new StringValueDeserializer(cl));
    }

    _staticSerializerMap.put(ObjectName.class, StringValueSerializer.SER);
    try {
      _staticDeserializerMap.put(ObjectName.class,
                           new StringValueDeserializer(ObjectName.class));
    } catch (Throwable e) {
    }
    */
    
    _staticSerializerMap.put(InetAddress.class.getName(),
                             InetAddressSerializer.create());

    _staticSerializerMap.put(java.sql.Date.class.getName(),
                             new SqlDateSerializer());
    _staticSerializerMap.put(java.sql.Time.class.getName(),
                             new SqlDateSerializer());
    _staticSerializerMap.put(java.sql.Timestamp.class.getName(),
                             new SqlDateSerializer());

    _staticDeserializerMap.put(java.sql.Date.class.getName(),
                               new SqlDateDeserializer(java.sql.Date.class));
    _staticDeserializerMap.put(java.sql.Time.class.getName(),
                               new SqlDateDeserializer(java.sql.Time.class));
    _staticDeserializerMap.put(java.sql.Timestamp.class.getName(),
                               new SqlDateDeserializer(java.sql.Timestamp.class));

    // hessian/3bb5
    _staticDeserializerMap.put(StackTraceElement.class.getName(),
                               new StackTraceElementDeserializer());

    ClassLoader systemClassLoader = null;
    try {
      systemClassLoader = ClassLoader.getSystemClassLoader();
    } catch (Exception e) {
    }

    _systemClassLoader = systemClassLoader;
  }
}

