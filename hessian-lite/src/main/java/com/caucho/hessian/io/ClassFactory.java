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
 * 4. The names "Hessian", "Resin", and "Caucho" must not be used to
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Loads a class from the classloader.
 */
public class ClassFactory
{
  private static ArrayList<Allow> _staticAllowList;
  
  private ClassLoader _loader;
  private boolean _isWhitelist;
  
  private ArrayList<Allow> _allowList;
  
  ClassFactory(ClassLoader loader)
  {
    _loader = loader;
  }
  
  public Class<?> load(String className)
    throws ClassNotFoundException
  {
    if (isAllow(className)) {
      return Class.forName(className, false, _loader);
    }
    else {
      return HashMap.class;
    }
  }
  
  private boolean isAllow(String className)
  {
    ArrayList<Allow> allowList = _allowList;
    
    if (allowList == null) {
      return true;
    }
    
    int size = allowList.size();
    for (int i = 0; i < size; i++) {
      Allow allow = allowList.get(i);
      
      Boolean isAllow = allow.allow(className);
      
      if (isAllow != null) {
        return isAllow;
      }
    }
    
    return false;
  }
  
  public void setWhitelist(boolean isWhitelist)
  {
    _isWhitelist = isWhitelist;
    
    initAllow();
  }
  
  /**
   * Allow a class or package based on a pattern.
   * 
   * Examples: "java.util.*", "com.foo.io.Bean"
   */
  public void allow(String pattern)
  {
    initAllow();
    
    synchronized (this) {
      _allowList.add(new Allow(toPattern(pattern), true));
    }
  }
  
  /**
   * Deny a class or package based on a pattern.
   * 
   * Examples: "java.util.*", "com.foo.io.Bean"
   */
  public void deny(String pattern)
  {
    initAllow();
    
    synchronized (this) {
      _allowList.add(new Allow(toPattern(pattern), false));
    }
  }
  
  private String toPattern(String pattern)
  {
    pattern = pattern.replace(".", "\\.");
    pattern = pattern.replace("*", ".*");
    
    return pattern;
  }
  
  private void initAllow()
  {
    synchronized (this) {
      if (_allowList == null) {
        _allowList = new ArrayList<Allow>();
        _allowList.addAll(_staticAllowList);
      }
    }
  }
  
  static class Allow {
    private Boolean _isAllow;
    private Pattern _pattern;
    
    private Allow(String pattern, boolean isAllow)
    {
      _isAllow = isAllow;
      _pattern = Pattern.compile(pattern);
    }
    
    Boolean allow(String className)
    {
      if (_pattern.matcher(className).matches()) {
        return _isAllow;
      }
      else {
        return null;
      }
    }
  }
  
  static {
    _staticAllowList = new ArrayList<Allow>();
    
    _staticAllowList.add(new Allow("java\\..+", true));
    _staticAllowList.add(new Allow("javax\\.management\\..+", true));
  }
}
