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

package com.caucho.burlap.server;

import com.caucho.burlap.io.BurlapInput;
import com.caucho.burlap.io.BurlapOutput;
import com.caucho.services.server.AbstractSkeleton;
import com.caucho.services.server.ServiceContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Proxy class for Burlap services.
 */
public class BurlapSkeleton extends AbstractSkeleton {
  private static final Logger log
    = Logger.getLogger(BurlapSkeleton.class.getName());
  
  private Object _service;
  
  /**
   * Create a new burlap skeleton.
   *
   * @param service the underlying service object.
   * @param apiClass the API interface
   */
  public BurlapSkeleton(Object service, Class apiClass)
  {
    super(apiClass);

    _service = service;
  }
  
  /**
   * Create a new burlap skeleton.
   *
   * @param service the underlying service object.
   * @param apiClass the API interface
   */
  public BurlapSkeleton(Class apiClass)
  {
    super(apiClass);
  }

  /**
   * Invoke the object with the request from the input stream.
   *
   * @param in the Burlap input stream
   * @param out the Burlap output stream
   */
  public void invoke(BurlapInput in, BurlapOutput out)
    throws Exception
  {
    invoke(_service, in, out);
  }

  /**
   * Invoke the object with the request from the input stream.
   *
   * @param in the Burlap input stream
   * @param out the Burlap output stream
   */
  public void invoke(Object service, BurlapInput in, BurlapOutput out)
    throws Exception
  {
    in.readCall();

    ServiceContext context = ServiceContext.getContext();
    
    String header;
    while ((header = in.readHeader()) != null) {
      Object value = in.readObject();

      context.addHeader(header, value);
    }

    String methodName = in.readMethod();
    Method method = getMethod(methodName);

    if (log.isLoggable(Level.FINE))
      log.fine(this + " invoking " + methodName + " (" + method + ")");

    if (method != null) {
    }
    else if ("_burlap_getAttribute".equals(in.getMethod())) {
      String attrName = in.readString();
      in.completeCall();

      String value = null;

      if ("java.api.class".equals(attrName))
        value = getAPIClassName();
      else if ("java.home.class".equals(attrName))
        value = getHomeClassName();
      else if ("java.object.class".equals(attrName))
        value = getObjectClassName();

      out.startReply();

      out.writeObject(value);

      out.completeReply();
      return;
    }
    else if (method == null) {
      out.startReply();
      out.writeFault("NoSuchMethodException",
                     "The service has no method named: " + in.getMethod(),
                     null);
      out.completeReply();
      return;
    }

    Class []args = method.getParameterTypes();
    Object []values = new Object[args.length];

    for (int i = 0; i < args.length; i++)
      values[i] = in.readObject(args[i]);

    in.completeCall();

    Object result = null;
    
    try {
      result = method.invoke(service, values);
    } catch (Throwable e) {
      log.log(Level.FINE,
              service + "." + method.getName() + "() failed with exception:\n"
              + e.toString(),
              e);
      
      if (e instanceof InvocationTargetException
          && e.getCause() instanceof Exception)
        e = ((InvocationTargetException) e).getTargetException();
      out.startReply();
      out.writeFault("ServiceException", e.getMessage(), e);
      out.completeReply();
      return;
    }

    out.startReply();

    out.writeObject(result);
    
    out.completeReply();
  }
}
