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

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Serializing a Java annotation
 */
public class AnnotationSerializer extends AbstractSerializer
{
  private static final Logger log
    = Logger.getLogger(AnnotationSerializer.class.getName());

  private static Object []NULL_ARGS = new Object[0];

  private Class _annType;
  private Method []_methods;
  private MethodSerializer []_methodSerializers;
  
  public AnnotationSerializer(Class annType)
  { 
    if (! Annotation.class.isAssignableFrom(annType)) {
      throw new IllegalStateException(annType.getName() + " is invalid because it is not a java.lang.annotation.Annotation");
    }
  }
  
  public void writeObject(Object obj, AbstractHessianOutput out)
    throws IOException
  {
    if (out.addRef(obj)) {
      return;
    }

    init(((Annotation) obj).annotationType());
    
    int ref = out.writeObjectBegin(_annType.getName());

    if (ref < -1) {
      writeObject10(obj, out);
    }
    else {
      if (ref == -1) {
        writeDefinition20(out);
        out.writeObjectBegin(_annType.getName());
      }

      writeInstance(obj, out);
    }
  }
  
  protected void writeObject10(Object obj, AbstractHessianOutput out)
    throws IOException
  {
    for (int i = 0; i < _methods.length; i++) {
      Method method = _methods[i];

      out.writeString(method.getName());

      _methodSerializers[i].serialize(out, obj, method);
    }
      
    out.writeMapEnd();
  }
  
  private void writeDefinition20(AbstractHessianOutput out)
    throws IOException
  {
    out.writeClassFieldLength(_methods.length);

    for (int i = 0; i < _methods.length; i++) {
      Method method = _methods[i];
      
      out.writeString(method.getName());
    }
  }
  
  public void writeInstance(Object obj, AbstractHessianOutput out)
    throws IOException
  {
    for (int i = 0; i < _methods.length; i++) {
      Method method = _methods[i];

      _methodSerializers[i].serialize(out, obj, method);
    }
  }


  private void init(Class cl)
  {
    synchronized (this) {
      if (_annType != null)
        return;

      _annType = cl;
      
      ArrayList methods = new ArrayList();
    
      for (Method method : _annType.getDeclaredMethods()) {
        if (method.getName().equals("hashCode")
            || method.getName().equals("toString")
            || method.getName().equals("annotationType")) {
          continue;
        }

        if (method.getParameterTypes().length != 0)
          continue;

        methods.add(method);

        method.setAccessible(true);
      }

      if (_annType == null)
        throw new IllegalStateException(cl.getName() + " is invalid because it does not have a valid annotationType()");

      _methods = new Method[methods.size()];
      methods.toArray(_methods);

      _methodSerializers = new MethodSerializer[_methods.length];

      for (int i = 0; i < _methods.length; i++) {
        _methodSerializers[i] = getMethodSerializer(_methods[i].getReturnType());
      }
    }
  }

  private Class getAnnotationType(Class cl)
  {
    if (cl == null)
      return null;
    
    if (Annotation.class.equals(cl.getSuperclass()))
      return cl;

    Class ifaces[] = cl.getInterfaces();

    if (ifaces != null) {
      for (Class iface : ifaces) {
        if (iface.equals(Annotation.class))
          return cl;

        Class annType = getAnnotationType(iface);

        if (annType != null)
          return annType;
      }
    }

    return getAnnotationType(cl.getSuperclass());
  }
  
  private static MethodSerializer getMethodSerializer(Class type)
  {
    if (int.class.equals(type)
        || byte.class.equals(type)
        || short.class.equals(type)
        || int.class.equals(type)) {
      return IntMethodSerializer.SER;
    }
    else if (long.class.equals(type)) {
      return LongMethodSerializer.SER;
    }
    else if (double.class.equals(type) ||
        float.class.equals(type)) {
      return DoubleMethodSerializer.SER;
    }
    else if (boolean.class.equals(type)) {
      return BooleanMethodSerializer.SER;
    }
    else if (String.class.equals(type)) {
      return StringMethodSerializer.SER;
    }
    else if (java.util.Date.class.equals(type)
             || java.sql.Date.class.equals(type)
             || java.sql.Timestamp.class.equals(type)
             || java.sql.Time.class.equals(type)) {
      return DateMethodSerializer.SER;
    }
    else
      return MethodSerializer.SER;
  }

  static HessianException error(Method method, Throwable cause)
  {
    String msg = (method.getDeclaringClass().getSimpleName()
                  + "." + method.getName() + "(): " + cause);

    throw new HessianMethodSerializationException(msg, cause);
  }

  static class MethodSerializer {
    static final MethodSerializer SER = new MethodSerializer();
    
    void serialize(AbstractHessianOutput out, Object obj, Method method)
      throws IOException
    {
      Object value = null;

      try {
        value = method.invoke(obj);
      } catch (InvocationTargetException e) {
        throw error(method, e.getCause());
      } catch (IllegalAccessException e) {
        log.log(Level.FINE, e.toString(), e);
      }

      try {
        out.writeObject(value);
      } catch (Exception e) {
        throw error(method, e);
      }
    }
  }

  static class BooleanMethodSerializer extends MethodSerializer {
    static final MethodSerializer SER = new BooleanMethodSerializer();
    
    void serialize(AbstractHessianOutput out, Object obj, Method method)
      throws IOException
    {
      boolean value = false;

      try {
        value = (Boolean) method.invoke(obj);
      } catch (InvocationTargetException e) {
        throw error(method, e.getCause());
      } catch (IllegalAccessException e) {
        log.log(Level.FINE, e.toString(), e);
      }

      out.writeBoolean(value);
    }
  }

  static class IntMethodSerializer extends MethodSerializer {
    static final MethodSerializer SER = new IntMethodSerializer();
    
    void serialize(AbstractHessianOutput out, Object obj, Method method)
      throws IOException
    {
      int value = 0;

      try {
        value = (Integer) method.invoke(obj);
      } catch (InvocationTargetException e) {
        throw error(method, e.getCause());
      } catch (IllegalAccessException e) {
        log.log(Level.FINE, e.toString(), e);
      }

      out.writeInt(value);
    }
  }

  static class LongMethodSerializer extends MethodSerializer {
    static final MethodSerializer SER = new LongMethodSerializer();
    
    void serialize(AbstractHessianOutput out, Object obj, Method method)
      throws IOException
    {
      long value = 0;

      try {
        value = (Long) method.invoke(obj);
      } catch (InvocationTargetException e) {
        throw error(method, e.getCause());
      } catch (IllegalAccessException e) {
        log.log(Level.FINE, e.toString(), e);
      }

      out.writeLong(value);
    }
  }

  static class DoubleMethodSerializer extends MethodSerializer {
    static final MethodSerializer SER = new DoubleMethodSerializer();
    
    void serialize(AbstractHessianOutput out, Object obj, Method method)
      throws IOException
    {
      double value = 0;

      try {
        value = (Double) method.invoke(obj);
      } catch (InvocationTargetException e) {
        throw error(method, e.getCause());
      } catch (IllegalAccessException e) {
        log.log(Level.FINE, e.toString(), e);
      }

      out.writeDouble(value);
    }
  }

  static class StringMethodSerializer extends MethodSerializer {
    static final MethodSerializer SER = new StringMethodSerializer();
    
    void serialize(AbstractHessianOutput out, Object obj, Method method)
      throws IOException
    {
      String value = null;

      try {
        value = (String) method.invoke(obj);
      } catch (InvocationTargetException e) {
        throw error(method, e.getCause());
      } catch (IllegalAccessException e) {
        log.log(Level.FINE, e.toString(), e);
      }

      out.writeString(value);
    }
  }

  static class DateMethodSerializer extends MethodSerializer {
    static final MethodSerializer SER = new DateMethodSerializer();

    void serialize(AbstractHessianOutput out, Object obj, Method method)
      throws IOException
    {
      java.util.Date value = null;

      try {
        value = (java.util.Date) method.invoke(obj);
      } catch (InvocationTargetException e) {
        throw error(method, e.getCause());
      } catch (IllegalAccessException e) {
        log.log(Level.FINE, e.toString(), e);
      }

      if (value == null)
        out.writeNull();
      else
        out.writeUTCDate(value.getTime());
    }
  }
}
