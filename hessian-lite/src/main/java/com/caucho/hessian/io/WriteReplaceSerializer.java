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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Serializing an object for known object types.
 */
public class WriteReplaceSerializer extends AbstractSerializer
{
  private static final Logger log
    = Logger.getLogger(WriteReplaceSerializer.class.getName());

  private Object _writeReplaceFactory;
  private Method _writeReplace;
  private Serializer _baseSerializer;
  
  public WriteReplaceSerializer(Class<?> cl,
                                ClassLoader loader,
                                Serializer baseSerializer)
  {
    introspectWriteReplace(cl, loader);
    
    _baseSerializer = baseSerializer;
  }

  private void introspectWriteReplace(Class<?> cl, ClassLoader loader)
  {
    try {
      String className = cl.getName() + "HessianSerializer";

      Class<?> serializerClass = Class.forName(className, false, loader);

      Object serializerObject = serializerClass.newInstance();

      Method writeReplace = getWriteReplace(serializerClass, cl);

      if (writeReplace != null) {
        _writeReplaceFactory = serializerObject;
        _writeReplace = writeReplace;
      }
    } catch (ClassNotFoundException e) {
    } catch (Exception e) {
      log.log(Level.FINER, e.toString(), e);
    }
      
    _writeReplace = getWriteReplace(cl);
    if (_writeReplace != null)
      _writeReplace.setAccessible(true);
  }

  /**
   * Returns the writeReplace method
   */
  protected static Method getWriteReplace(Class<?> cl, Class<?> param)
  {
    for (; cl != null; cl = cl.getSuperclass()) {
      for (Method method : cl.getDeclaredMethods()) {
        if (method.getName().equals("writeReplace")
            && method.getParameterTypes().length == 1
            && param.equals(method.getParameterTypes()[0]))
          return method;
      }
    }

    return null;
  }

  /**
   * Returns the writeReplace method
   */
  protected static Method getWriteReplace(Class<?> cl)
  {
    for (; cl != null; cl = cl.getSuperclass()) {
      Method []methods = cl.getDeclaredMethods();
      
      for (int i = 0; i < methods.length; i++) {
        Method method = methods[i];

        if (method.getName().equals("writeReplace") &&
            method.getParameterTypes().length == 0)
          return method;
      }
    }

    return null;
  }

  @Override
  public void writeObject(Object obj, AbstractHessianOutput out)
    throws IOException
  {
    int ref = out.getRef(obj);
    
    if (ref >= 0) {
      out.writeRef(ref);
      
      return;
    }
    
    try {
      Object repl;

      repl = writeReplace(obj);

      if (obj == repl) {
        if (log.isLoggable(Level.FINE)) { 
          log.fine(this + ": Hessian writeReplace error.  The writeReplace method (" + _writeReplace + ") must not return the same object: " + obj);
        }
        
        _baseSerializer.writeObject(obj, out);

        return;
      }

      out.writeObject(repl);

      out.replaceRef(repl, obj);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  @Override
  protected Object writeReplace(Object obj)
  {
    try {
      if (_writeReplaceFactory != null)
        return _writeReplace.invoke(_writeReplaceFactory, obj);
      else
        return _writeReplace.invoke(obj);
    } catch (RuntimeException e) {
      throw e;
    } catch (InvocationTargetException e) {
      throw new RuntimeException(e.getCause());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
