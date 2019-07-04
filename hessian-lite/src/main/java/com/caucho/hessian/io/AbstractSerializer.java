/*
 * Copyright (c) 2001-2004 Caucho Technology, Inc.  All rights reserved.
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
import java.util.logging.Logger;

/**
 * Serializing an object. 
 */
abstract public class AbstractSerializer implements Serializer {
  public static final NullSerializer NULL = new NullSerializer();
  
  protected static final Logger log
    = Logger.getLogger(AbstractSerializer.class.getName());
  
  @Override
  public void writeObject(Object obj, AbstractHessianOutput out)
    throws IOException
  {
    if (out.addRef(obj)) {
      return;
    }
    
    try {
      Object replace = writeReplace(obj);
      
      if (replace != null) {
        // out.removeRef(obj);

        out.writeObject(replace);

        out.replaceRef(replace, obj);

        return;
      }
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      // log.log(Level.FINE, e.toString(), e);
      throw new HessianException(e);
    }

    Class<?> cl = getClass(obj);

    int ref = out.writeObjectBegin(cl.getName());

    if (ref < -1) {
      writeObject10(obj, out);
    }
    else {
      if (ref == -1) {
        writeDefinition20(cl, out);

        out.writeObjectBegin(cl.getName());
      }

      writeInstance(obj, out);
    }
  }

  protected Object writeReplace(Object obj)
  {
    return null;
  }

  protected Class<?> getClass(Object obj)
  {
    return obj.getClass();
  }

  protected void writeObject10(Object obj,
                            AbstractHessianOutput out)
    throws IOException
  {
    throw new UnsupportedOperationException(getClass().getName());
  }

  protected void writeDefinition20(Class<?> cl,
                                   AbstractHessianOutput out)
    throws IOException
  {
    throw new UnsupportedOperationException(getClass().getName());
  }

  protected void writeInstance(Object obj,
                            AbstractHessianOutput out)
    throws IOException
  {
    throw new UnsupportedOperationException(getClass().getName());
  }

  /**
   * The NullSerializer exists as a marker for the factory classes so
   * they save a null result.
   */
  static final class NullSerializer extends AbstractSerializer {
    public void writeObject(Object obj, AbstractHessianOutput out)
      throws IOException
    {
      throw new IllegalStateException(getClass().getName());
    }
  }
}
