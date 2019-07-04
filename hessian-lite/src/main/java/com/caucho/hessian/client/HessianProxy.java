/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2004 Caucho Technology, Inc.  All rights reserved.
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

package com.caucho.hessian.client;

import com.caucho.hessian.io.*;
import com.caucho.services.server.AbstractSkeleton;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * Proxy implementation for Hessian clients.  Applications will generally
 * use HessianProxyFactory to create proxy clients.
 */
public class HessianProxy implements InvocationHandler, Serializable {
  private static final Logger log
    = Logger.getLogger(HessianProxy.class.getName());
  
  protected HessianProxyFactory _factory;
  
  private WeakHashMap<Method,String> _mangleMap
    = new WeakHashMap<Method,String>();

  private Class<?> _type;
  private URL _url;

  /**
   * Protected constructor for subclassing
   */
  protected HessianProxy(URL url, HessianProxyFactory factory)
  {
    this(url, factory, null);
  }

  /**
   * Protected constructor for subclassing
   */
  protected HessianProxy(URL url,
                         HessianProxyFactory factory, 
                         Class<?> type)
  {
    _factory = factory;
    _url = url;
    _type = type;
  }

  /**
   * Returns the proxy's URL.
   */
  public URL getURL()
  {
    return _url;
  }

  /**
   * Handles the object invocation.
   *
   * @param proxy the proxy object to invoke
   * @param method the method to call
   * @param args the arguments to the proxy object
   */
  public Object invoke(Object proxy, Method method, Object []args)
    throws Throwable
  {
    String mangleName;

    synchronized (_mangleMap) {
      mangleName = _mangleMap.get(method);
    }

    if (mangleName == null) {
      String methodName = method.getName();
      Class<?> []params = method.getParameterTypes();

      // equals and hashCode are special cased
      if (methodName.equals("equals")
          && params.length == 1 && params[0].equals(Object.class)) {
        Object value = args[0];
        if (value == null || ! Proxy.isProxyClass(value.getClass()))
          return Boolean.FALSE;

        Object proxyHandler = Proxy.getInvocationHandler(value);

        if (! (proxyHandler instanceof HessianProxy))
          return Boolean.FALSE;

        HessianProxy handler = (HessianProxy) proxyHandler;

        return new Boolean(_url.equals(handler.getURL()));
      }
      else if (methodName.equals("hashCode") && params.length == 0)
        return new Integer(_url.hashCode());
      else if (methodName.equals("getHessianType"))
        return proxy.getClass().getInterfaces()[0].getName();
      else if (methodName.equals("getHessianURL"))
        return _url.toString();
      else if (methodName.equals("toString") && params.length == 0)
        return "HessianProxy[" + _url + "]";
      
      if (! _factory.isOverloadEnabled())
        mangleName = method.getName();
      else
        mangleName = mangleName(method);

      synchronized (_mangleMap) {
        _mangleMap.put(method, mangleName);
      }
    }

    InputStream is = null;
    HessianConnection conn = null;
    
    try {
      if (log.isLoggable(Level.FINER))
        log.finer("Hessian[" + _url + "] calling " + mangleName);
      
      conn = sendRequest(mangleName, args);

      is = getInputStream(conn);

      if (log.isLoggable(Level.FINEST)) {
        PrintWriter dbg = new PrintWriter(new LogWriter(log));
        HessianDebugInputStream dIs
          = new HessianDebugInputStream(is, dbg);

        dIs.startTop2();

        is = dIs;
      }

      AbstractHessianInput in;

      int code = is.read();

      if (code == 'H') {
        int major = is.read();
        int minor = is.read();

        in = _factory.getHessian2Input(is);

        Object value = in.readReply(method.getReturnType());

        return value;
      }
      else if (code == 'r') {
        int major = is.read();
        int minor = is.read();

        in = _factory.getHessianInput(is);

        in.startReplyBody();

        Object value = in.readObject(method.getReturnType());

        if (value instanceof InputStream) {
          value = new ResultInputStream(conn, is, in, (InputStream) value);
          is = null;
          conn = null;
        }
        else
          in.completeReply();

        return value;
      }
      else
        throw new HessianProtocolException("'" + (char) code + "' is an unknown code");
    } catch (HessianProtocolException e) {
      throw new HessianRuntimeException(e);
    } finally {
      try {
        if (is != null)
          is.close();
      } catch (Exception e) {
        log.log(Level.FINE, e.toString(), e);
      }
      
      try {
        if (conn != null)
          conn.destroy();
      } catch (Exception e) {
        log.log(Level.FINE, e.toString(), e);
      }
    }
  }
  
  protected InputStream getInputStream(HessianConnection conn)
    throws IOException
  {
    InputStream is = conn.getInputStream();

    if ("deflate".equals(conn.getContentEncoding())) {
      is = new InflaterInputStream(is, new Inflater(true));
    }
    
    return is;
  }

  protected String mangleName(Method method)
  {
    Class<?> []param = method.getParameterTypes();
    
    if (param == null || param.length == 0)
      return method.getName();
    else
      return AbstractSkeleton.mangleName(method, false);
  }

  /**
   * Sends the HTTP request to the Hessian connection.
   */
  protected HessianConnection sendRequest(String methodName, Object []args)
    throws IOException
  {
    HessianConnection conn = null;
    
    conn = _factory.getConnectionFactory().open(_url);
    boolean isValid = false;

    try {
      addRequestHeaders(conn);

      OutputStream os = null;

      try {
        os = conn.getOutputStream();
      } catch (Exception e) {
        throw new HessianRuntimeException(e);
      }

      if (log.isLoggable(Level.FINEST)) {
        PrintWriter dbg = new PrintWriter(new LogWriter(log));
        HessianDebugOutputStream dOs = new HessianDebugOutputStream(os, dbg);
        dOs.startTop2();
        os = dOs;
      }
      
      AbstractHessianOutput out = _factory.getHessianOutput(os);

      out.call(methodName, args);
      out.flush();

      conn.sendRequest();

      isValid = true;

      return conn;
    } finally {
      if (! isValid && conn != null)
        conn.destroy();
    }
  }

  /**
   * Method that allows subclasses to add request headers such as cookies.
   * Default implementation is empty. 
   */
  protected void addRequestHeaders(HessianConnection conn)
  {
    conn.addHeader("Content-Type", "x-application/hessian");
    conn.addHeader("Accept-Encoding", "deflate");

    String basicAuth = _factory.getBasicAuth();

    if (basicAuth != null)
      conn.addHeader("Authorization", basicAuth);
  }

  /**
   * Method that allows subclasses to parse response headers such as cookies.
   * Default implementation is empty. 
   * @param conn
   */
  protected void parseResponseHeaders(URLConnection conn)
  {
  }

  public Object writeReplace()
  {
    return new HessianRemote(_type.getName(), _url.toString());
  }

  static class ResultInputStream extends InputStream {
    private HessianConnection _conn;
    private InputStream _connIs;
    private AbstractHessianInput _in;
    private InputStream _hessianIs;

    ResultInputStream(HessianConnection conn,
                      InputStream is,
                      AbstractHessianInput in,
                      InputStream hessianIs)
    {
      _conn = conn;
      _connIs = is;
      _in = in;
      _hessianIs = hessianIs;
    }

    public int read()
      throws IOException
    {
      if (_hessianIs != null) {
        int value = _hessianIs.read();

        if (value < 0)
          close();

        return value;
      }
      else
        return -1;
    }

    public int read(byte []buffer, int offset, int length)
      throws IOException
    {
      if (_hessianIs != null) {
        int value = _hessianIs.read(buffer, offset, length);

        if (value < 0)
          close();

        return value;
      }
      else
        return -1;
    }

    public void close()
      throws IOException
    {
      HessianConnection conn = _conn;
      _conn = null;
      
      InputStream connIs = _connIs;
      _connIs = null;
      
      AbstractHessianInput in = _in;
      _in = null;
      
      InputStream hessianIs = _hessianIs;
      _hessianIs = null;

      try {
        if (hessianIs != null)
          hessianIs.close();
      } catch (Exception e) {
        log.log(Level.FINE, e.toString(), e);
      }

      try {
        if (in != null) {
          in.completeReply();
          in.close();
        }
      } catch (Exception e) {
        log.log(Level.FINE, e.toString(), e);
      }

      try {
        if (connIs != null) {
          connIs.close();
        }
      } catch (Exception e) {
        log.log(Level.FINE, e.toString(), e);
      }

      try {
        if (conn != null) {
          conn.close();
        }
      } catch (Exception e) {
        log.log(Level.FINE, e.toString(), e);
      }
    }
  }

  static class LogWriter extends Writer {
    private Logger _log;
    private Level _level = Level.FINEST;
    private StringBuilder _sb = new StringBuilder();

    LogWriter(Logger log)
    {
      _log = log;
    }

    public void write(char ch)
    {
      if (ch == '\n' && _sb.length() > 0) {
        _log.fine(_sb.toString());
        _sb.setLength(0);
      }
      else
        _sb.append((char) ch);
    }

    public void write(char []buffer, int offset, int length)
    {
      for (int i = 0; i < length; i++) {
        char ch = buffer[offset + i];

        if (ch == '\n' && _sb.length() > 0) {
          _log.log(_level, _sb.toString());
          _sb.setLength(0);
        }
        else
          _sb.append((char) ch);
      }
    }

    public void flush()
    {
    }

    public void close()
    {
      if (_sb.length() > 0)
        _log.log(_level, _sb.toString());
    }
  }
}
