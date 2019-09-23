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

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.caucho.burlap.io.BurlapRemoteObject;

/**
 * Factory for returning serialization methods.
 */
public class SerializerFactory extends AbstractSerializerFactory
{
  private static final Logger log
    = Logger.getLogger(SerializerFactory.class.getName());

  private static final Deserializer OBJECT_DESERIALIZER
    = new BasicDeserializer(BasicDeserializer.OBJECT);

  private static final ClassLoader _systemClassLoader;

  private static final HashMap _staticTypeMap;

  private static final
    WeakHashMap<ClassLoader,SoftReference<SerializerFactory>>
    _defaultFactoryRefMap
    = new WeakHashMap<ClassLoader,SoftReference<SerializerFactory>>();

  private ContextSerializerFactory _contextFactory;
  private WeakReference<ClassLoader> _loaderRef;

  protected Serializer _defaultSerializer;

  // Additional factories
  protected ArrayList _factories = new ArrayList();

  protected CollectionSerializer _collectionSerializer;
  protected MapSerializer _mapSerializer;

  private Deserializer _hashMapDeserializer;
  private Deserializer _arrayListDeserializer;
  private ConcurrentHashMap _cachedSerializerMap;
  private ConcurrentHashMap _cachedDeserializerMap;
  private HashMap _cachedTypeDeserializerMap;

  private boolean _isAllowNonSerializable;
  private boolean _isEnableUnsafeSerializer
    = (UnsafeSerializer.isEnabled()
        && UnsafeDeserializer.isEnabled());
  
  private ClassFactory _classFactory;

  public SerializerFactory()
  {
    this(Thread.currentThread().getContextClassLoader());
  }

  public SerializerFactory(ClassLoader loader)
  {
    _loaderRef = new WeakReference<ClassLoader>(loader);

    _contextFactory = ContextSerializerFactory.create(loader);
  }

  public static SerializerFactory createDefault()
  {
    ClassLoader loader = Thread.currentThread().getContextClassLoader();

    synchronized (_defaultFactoryRefMap) {
      SoftReference<SerializerFactory> factoryRef
        = _defaultFactoryRefMap.get(loader);

      SerializerFactory factory = null;

      if (factoryRef != null)
        factory = factoryRef.get();

      if (factory == null) {
        factory = new SerializerFactory();

        factoryRef = new SoftReference<SerializerFactory>(factory);

        _defaultFactoryRefMap.put(loader, factoryRef);
      }

      return factory;
    }
  }

  public ClassLoader getClassLoader()
  {
    return _loaderRef.get();
  }

  /**
   * Set true if the collection serializer should send the java type.
   */
  public void setSendCollectionType(boolean isSendType)
  {
    if (_collectionSerializer == null)
      _collectionSerializer = new CollectionSerializer();

    _collectionSerializer.setSendJavaType(isSendType);

    if (_mapSerializer == null)
      _mapSerializer = new MapSerializer();

    _mapSerializer.setSendJavaType(isSendType);
  }

  /**
   * Adds a factory.
   */
  public void addFactory(AbstractSerializerFactory factory)
  {
    _factories.add(factory);
  }

  /**
   * If true, non-serializable objects are allowed.
   */
  public void setAllowNonSerializable(boolean allow)
  {
    _isAllowNonSerializable = allow;
  }

  /**
   * If true, non-serializable objects are allowed.
   */
  public boolean isAllowNonSerializable()
  {
    return _isAllowNonSerializable;
  }

  /**
   * Returns the serializer for a class.
   *
   * @param cl the class of the object that needs to be serialized.
   *
   * @return a serializer object for the serialization.
   */
  public Serializer getObjectSerializer(Class<?> cl)
    throws HessianProtocolException
  {
    Serializer serializer = getSerializer(cl);

    if (serializer instanceof ObjectSerializer)
      return ((ObjectSerializer) serializer).getObjectSerializer();
    else
      return serializer;
  }
  
  public Class<?> loadSerializedClass(String className)
    throws ClassNotFoundException
  {
    return getClassFactory().load(className);
  }
  
  public ClassFactory getClassFactory()
  {
    synchronized (this) {
      if (_classFactory == null) {
        _classFactory = new ClassFactory(getClassLoader());
      }

      return _classFactory;
    }
  }

  /**
   * Returns the serializer for a class.
   *
   * @param cl the class of the object that needs to be serialized.
   *
   * @return a serializer object for the serialization.
   */
  public Serializer getSerializer(Class cl)
    throws HessianProtocolException
  {
    Serializer serializer;

    if (_cachedSerializerMap != null) {
      serializer = (Serializer) _cachedSerializerMap.get(cl);

      if (serializer != null) {
        return serializer;
      }
    }

    serializer = loadSerializer(cl);

    if (_cachedSerializerMap == null)
      _cachedSerializerMap = new ConcurrentHashMap(8);

    _cachedSerializerMap.put(cl, serializer);

    return serializer;
  }

  protected Serializer loadSerializer(Class<?> cl)
    throws HessianProtocolException
  {
    Serializer serializer = null;

    for (int i = 0;
         _factories != null && i < _factories.size();
         i++) {
      AbstractSerializerFactory factory;

      factory = (AbstractSerializerFactory) _factories.get(i);

      serializer = factory.getSerializer(cl);

      if (serializer != null)
        return serializer;
    }

    serializer = _contextFactory.getSerializer(cl.getName());

    if (serializer != null)
      return serializer;

    ClassLoader loader = cl.getClassLoader();

    if (loader == null)
      loader = _systemClassLoader;

    ContextSerializerFactory factory = null;

    factory = ContextSerializerFactory.create(loader);

    serializer = factory.getCustomSerializer(cl);

    if (serializer != null) {
      return serializer;
    }
    
    if (HessianRemoteObject.class.isAssignableFrom(cl)) {
      return new RemoteSerializer();
    }
    else if (BurlapRemoteObject.class.isAssignableFrom(cl)) {
      return new RemoteSerializer();
    }
    else if (InetAddress.class.isAssignableFrom(cl)) {
      return InetAddressSerializer.create();
    }
    else if (JavaSerializer.getWriteReplace(cl) != null) {
      Serializer baseSerializer = getDefaultSerializer(cl);
      
      return new WriteReplaceSerializer(cl, getClassLoader(), baseSerializer);
    }
    else if (Map.class.isAssignableFrom(cl)) {
      if (_mapSerializer == null)
        _mapSerializer = new MapSerializer();

      return _mapSerializer;
    }
    else if (Collection.class.isAssignableFrom(cl)) {
      if (_collectionSerializer == null) {
        _collectionSerializer = new CollectionSerializer();
      }

      return _collectionSerializer;
    }

    else if (cl.isArray()) {
      return new ArraySerializer();
    }

    else if (Throwable.class.isAssignableFrom(cl))
      return new ThrowableSerializer(cl, getClassLoader());

    else if (InputStream.class.isAssignableFrom(cl))
      return new InputStreamSerializer();

    else if (Iterator.class.isAssignableFrom(cl))
      return IteratorSerializer.create();

    else if (Calendar.class.isAssignableFrom(cl))
      return CalendarSerializer.SER;
    
    else if (Enumeration.class.isAssignableFrom(cl))
      return EnumerationSerializer.create();

    else if (Enum.class.isAssignableFrom(cl))
      return new EnumSerializer(cl);

    else if (Annotation.class.isAssignableFrom(cl))
      return new AnnotationSerializer(cl);

    return getDefaultSerializer(cl);
  }

  /**
   * Returns the default serializer for a class that isn't matched
   * directly.  Application can override this method to produce
   * bean-style serialization instead of field serialization.
   *
   * @param cl the class of the object that needs to be serialized.
   *
   * @return a serializer object for the serialization.
   */
  protected Serializer getDefaultSerializer(Class cl)
  {
    if (_defaultSerializer != null)
      return _defaultSerializer;

    if (! Serializable.class.isAssignableFrom(cl)
        && ! _isAllowNonSerializable) {
      throw new IllegalStateException("Serialized class " + cl.getName() + " must implement java.io.Serializable");
    }
    
    if (_isEnableUnsafeSerializer
        && JavaSerializer.getWriteReplace(cl) == null) {
      return UnsafeSerializer.create(cl);
    }
    else
      return JavaSerializer.create(cl);
  }

  /**
   * Returns the deserializer for a class.
   *
   * @param cl the class of the object that needs to be deserialized.
   *
   * @return a deserializer object for the serialization.
   */
  public Deserializer getDeserializer(Class cl)
    throws HessianProtocolException
  {
    Deserializer deserializer;

    if (_cachedDeserializerMap != null) {
      deserializer = (Deserializer) _cachedDeserializerMap.get(cl);

      if (deserializer != null)
        return deserializer;
    }

    deserializer = loadDeserializer(cl);

    if (_cachedDeserializerMap == null)
      _cachedDeserializerMap = new ConcurrentHashMap(8);

    _cachedDeserializerMap.put(cl, deserializer);

    return deserializer;
  }

  protected Deserializer loadDeserializer(Class cl)
    throws HessianProtocolException
  {
    Deserializer deserializer = null;

    for (int i = 0;
         deserializer == null && _factories != null && i < _factories.size();
         i++) {
      AbstractSerializerFactory factory;
      factory = (AbstractSerializerFactory) _factories.get(i);

      deserializer = factory.getDeserializer(cl);
    }

    if (deserializer != null)
      return deserializer;

    // XXX: need test
    deserializer = _contextFactory.getDeserializer(cl.getName());

    if (deserializer != null)
      return deserializer;

    ContextSerializerFactory factory = null;

    if (cl.getClassLoader() != null)
      factory = ContextSerializerFactory.create(cl.getClassLoader());
    else
      factory = ContextSerializerFactory.create(_systemClassLoader);

    deserializer = factory.getDeserializer(cl.getName());
    
    if (deserializer != null)
      return deserializer;
    
    deserializer = factory.getCustomDeserializer(cl);

    if (deserializer != null)
      return deserializer;

    if (Collection.class.isAssignableFrom(cl))
      deserializer = new CollectionDeserializer(cl);

    else if (Map.class.isAssignableFrom(cl)) {
      deserializer = new MapDeserializer(cl);
    }
    else if (Iterator.class.isAssignableFrom(cl)) {
      deserializer = IteratorDeserializer.create();
    }
    else if (Annotation.class.isAssignableFrom(cl)) {
      deserializer = new AnnotationDeserializer(cl);
    }
    else if (cl.isInterface()) {
      deserializer = new ObjectDeserializer(cl);
    }
    else if (cl.isArray()) {
      deserializer = new ArrayDeserializer(cl.getComponentType());
    }
    else if (Enumeration.class.isAssignableFrom(cl)) {
      deserializer = EnumerationDeserializer.create();
    }
    else if (Enum.class.isAssignableFrom(cl))
      deserializer = new EnumDeserializer(cl);

    else if (Class.class.equals(cl))
      deserializer = new ClassDeserializer(getClassLoader());

    else
      deserializer = getDefaultDeserializer(cl);
    
    return deserializer;
  }

  /**
   * Returns a custom serializer the class
   *
   * @param cl the class of the object that needs to be serialized.
   *
   * @return a serializer object for the serialization.
   */
  protected Deserializer getCustomDeserializer(Class cl)
  {
    try {
      Class serClass = Class.forName(cl.getName() + "HessianDeserializer",
                                       false, cl.getClassLoader());

      Deserializer ser = (Deserializer) serClass.newInstance();

      return ser;
    } catch (ClassNotFoundException e) {
      log.log(Level.FINEST, e.toString(), e);

      return null;
    } catch (Exception e) {
      log.log(Level.FINE, e.toString(), e);

      return null;
    }
  }

  /**
   * Returns the default serializer for a class that isn't matched
   * directly.  Application can override this method to produce
   * bean-style serialization instead of field serialization.
   *
   * @param cl the class of the object that needs to be serialized.
   *
   * @return a serializer object for the serialization.
   */
  protected Deserializer getDefaultDeserializer(Class cl)
  {
    if (InputStream.class.equals(cl))
      return InputStreamDeserializer.DESER;
    
    if (_isEnableUnsafeSerializer) {
      return new UnsafeDeserializer(cl);
    }
    else
      return new JavaDeserializer(cl);
  }

  /**
   * Reads the object as a list.
   */
  public Object readList(AbstractHessianInput in, int length, String type)
    throws HessianProtocolException, IOException
  {
    Deserializer deserializer = getDeserializer(type);

    if (deserializer != null)
      return deserializer.readList(in, length);
    else
      return new CollectionDeserializer(ArrayList.class).readList(in, length);
  }

  /**
   * Reads the object as a map.
   */
  public Object readMap(AbstractHessianInput in, String type)
    throws HessianProtocolException, IOException
  {
    Deserializer deserializer = getDeserializer(type);

    if (deserializer != null)
      return deserializer.readMap(in);
    else if (_hashMapDeserializer != null)
      return _hashMapDeserializer.readMap(in);
    else {
      _hashMapDeserializer = new MapDeserializer(HashMap.class);

      return _hashMapDeserializer.readMap(in);
    }
  }

  /**
   * Reads the object as a map.
   */
  public Object readObject(AbstractHessianInput in,
                           String type,
                           String []fieldNames)
    throws HessianProtocolException, IOException
  {
    Deserializer deserializer = getDeserializer(type);

    if (deserializer != null)
      return deserializer.readObject(in, fieldNames);
    else if (_hashMapDeserializer != null)
      return _hashMapDeserializer.readObject(in, fieldNames);
    else {
      _hashMapDeserializer = new MapDeserializer(HashMap.class);

      return _hashMapDeserializer.readObject(in, fieldNames);
    }
  }

  /**
   * Reads the object as a map.
   */
  public Deserializer getObjectDeserializer(String type, Class cl)
    throws HessianProtocolException
  {
    Deserializer reader = getObjectDeserializer(type);

    if (cl == null
        || cl.equals(reader.getType())
        || cl.isAssignableFrom(reader.getType())
        || reader.isReadResolve()
        || HessianHandle.class.isAssignableFrom(reader.getType())) {
      return reader;
    }

    if (log.isLoggable(Level.FINE)) {
      log.fine("hessian: expected deserializer '" + cl.getName() + "' at '" + type + "' ("
               + reader.getType().getName() + ")");
    }

    return getDeserializer(cl);
  }

  /**
   * Reads the object as a map.
   */
  public Deserializer getObjectDeserializer(String type)
    throws HessianProtocolException
  {
    Deserializer deserializer = getDeserializer(type);

    if (deserializer != null)
      return deserializer;
    else if (_hashMapDeserializer != null)
      return _hashMapDeserializer;
    else {
      _hashMapDeserializer = new MapDeserializer(HashMap.class);

      return _hashMapDeserializer;
    }
  }

  /**
   * Reads the object as a map.
   */
  public Deserializer getListDeserializer(String type, Class cl)
    throws HessianProtocolException
  {
    Deserializer reader = getListDeserializer(type);

    if (cl == null
        || cl.equals(reader.getType())
        || cl.isAssignableFrom(reader.getType())) {
      return reader;
    }

    if (log.isLoggable(Level.FINE)) {
      log.fine("hessian: expected '" + cl.getName() + "' at '" + type + "' ("
               + reader.getType().getName() + ")");
    }

    return getDeserializer(cl);
  }

  /**
   * Reads the object as a map.
   */
  public Deserializer getListDeserializer(String type)
    throws HessianProtocolException
  {
    Deserializer deserializer = getDeserializer(type);

    if (deserializer != null)
      return deserializer;
    else if (_arrayListDeserializer != null)
      return _arrayListDeserializer;
    else {
      _arrayListDeserializer = new CollectionDeserializer(ArrayList.class);

      return _arrayListDeserializer;
    }
  }

  /**
   * Returns a deserializer based on a string type.
   */
  public Deserializer getDeserializer(String type)
    throws HessianProtocolException
  {
    if (type == null || type.equals(""))
      return null;

    Deserializer deserializer;

    if (_cachedTypeDeserializerMap != null) {
      synchronized (_cachedTypeDeserializerMap) {
        deserializer = (Deserializer) _cachedTypeDeserializerMap.get(type);
      }

      if (deserializer != null)
        return deserializer;
    }


    deserializer = (Deserializer) _staticTypeMap.get(type);
    if (deserializer != null)
      return deserializer;

    if (type.startsWith("[")) {
      Deserializer subDeserializer = getDeserializer(type.substring(1));

      if (subDeserializer != null)
        deserializer = new ArrayDeserializer(subDeserializer.getType());
      else
        deserializer = new ArrayDeserializer(Object.class);
    }
    else {
      try {
        //Class cl = Class.forName(type, false, getClassLoader());
        // intercept hessian type here;
        Class cl;
        if (type.startsWith("com.caucho.hessian")) {
          // using sandbox module classloader
          cl = this.getClass().getClassLoader().loadClass(type);
        } else {
          cl = loadSerializedClass(type);
        }
        
        deserializer = getDeserializer(cl);
      } catch (Exception e) {
        log.warning("Hessian/Burlap: '" + type + "' is an unknown class in " + getClassLoader() + ":\n" + e);

        log.log(Level.FINER, e.toString(), e);
      }
    }

    if (deserializer != null) {
      if (_cachedTypeDeserializerMap == null)
        _cachedTypeDeserializerMap = new HashMap(8);

      synchronized (_cachedTypeDeserializerMap) {
        _cachedTypeDeserializerMap.put(type, deserializer);
      }
    }

    return deserializer;
  }

  private static void addBasic(Class<?> cl, String typeName, int type)
  {
    Deserializer deserializer = new BasicDeserializer(type);
    
    _staticTypeMap.put(typeName, deserializer);
  }

  static {
    _staticTypeMap = new HashMap();

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
    addBasic(StringBuilder.class, "string", BasicSerializer.STRING_BUILDER);
    addBasic(Object.class, "object", BasicSerializer.OBJECT);
    addBasic(java.util.Date.class, "date", BasicSerializer.DATE);

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
    addBasic(short[].class, "[short", BasicSerializer.SHORT_ARRAY);
    addBasic(int[].class, "[int", BasicSerializer.INTEGER_ARRAY);
    addBasic(long[].class, "[long", BasicSerializer.LONG_ARRAY);
    addBasic(float[].class, "[float", BasicSerializer.FLOAT_ARRAY);
    addBasic(double[].class, "[double", BasicSerializer.DOUBLE_ARRAY);
    addBasic(char[].class, "[char", BasicSerializer.CHARACTER_ARRAY);
    addBasic(String[].class, "[string", BasicSerializer.STRING_ARRAY);
    addBasic(Object[].class, "[object", BasicSerializer.OBJECT_ARRAY);

    Deserializer objectDeserializer = new JavaDeserializer(Object.class);
    _staticTypeMap.put("object", objectDeserializer);
    _staticTypeMap.put(HessianRemote.class.getName(),
                       RemoteDeserializer.DESER);


    ClassLoader systemClassLoader = null;
    try {
      systemClassLoader = ClassLoader.getSystemClassLoader();
    } catch (Exception e) {
    }

    _systemClassLoader = systemClassLoader;
  }
}
