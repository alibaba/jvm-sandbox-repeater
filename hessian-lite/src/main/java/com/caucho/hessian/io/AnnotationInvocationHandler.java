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

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Proxy for a java annotation for known object types.
 */
public class AnnotationInvocationHandler implements InvocationHandler {
  private Class _annType;
  private HashMap<String,Object> _valueMap;

  public AnnotationInvocationHandler(Class annType,
                                     HashMap<String,Object> valueMap)
  {
    _annType = annType;
    _valueMap = valueMap;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object []args)
    throws Throwable
  {
    String name = method.getName();

    boolean zeroArgs = args == null || args.length == 0;
    
    if (name.equals("annotationType") && zeroArgs)
      return _annType;
    else if (name.equals("toString") && zeroArgs)
      return toString();
    else if (name.equals("hashCode") && zeroArgs)
      return doHashCode();
    else if (name.equals("equals") && ! zeroArgs && args.length == 1)
      return doEquals(args[0]);
    else if (! zeroArgs)
      return null;


    return _valueMap.get(method.getName());
  }
  
  public int doHashCode()
  {
    return 13;
  }
  
  public boolean doEquals(Object value)
  {
    if (! (value instanceof Annotation))
      return false;
    
    Annotation ann = (Annotation) value;
    
    if (! _annType.equals(ann.annotationType()))
      return false;
    
    return true;
  }

  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    
    sb.append("@");
    sb.append(_annType.getName());
    sb.append("[");

    boolean isFirst = true;
    for (Map.Entry entry : _valueMap.entrySet()) {
      if (! isFirst)
        sb.append(", ");
      isFirst = false;

      sb.append(entry.getKey());
      sb.append("=");

      if (entry.getValue() instanceof String)
        sb.append('"').append(entry.getValue()).append('"');
      else
        sb.append(entry.getValue());
    }
    sb.append("]");

    return sb.toString();
  }
}
