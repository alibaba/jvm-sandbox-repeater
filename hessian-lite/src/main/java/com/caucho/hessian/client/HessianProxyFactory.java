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
import com.caucho.services.client.ServiceProxyFactory;

import javax.naming.*;
import javax.naming.spi.ObjectFactory;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.logging.Logger;

/**
 * Factory for creating Hessian client stubs.  The returned stub will
 * call the remote object for all methods.
 *
 * <pre>
 * String url = "http://localhost:8080/ejb/hello";
 * HelloHome hello = (HelloHome) factory.create(HelloHome.class, url);
 * </pre>
 *
 * After creation, the stub can be like a regular Java class.  Because
 * it makes remote calls, it can throw more exceptions than a Java class.
 * In particular, it may throw protocol exceptions.
 *
 * The factory can also be configured as a JNDI resource.  The factory
 * expects to parameters: "type" and "url", corresponding to the two
 * arguments to <code>create</code>
 *
 * In Resin 3.0, the above example would be configured as:
 * <pre>
 * &lt;reference>
 *   &lt;jndi-name>hessian/hello&lt;/jndi-name>
 *   &lt;factory>com.caucho.hessian.client.HessianProxyFactory&lt;/factory>
 *   &lt;init-param url="http://localhost:8080/ejb/hello"/>
 *   &lt;init-param type="test.HelloHome"/>
 * &lt;/reference>
 * </pre>
 *
 * To get the above resource, use JNDI as follows:
 * <pre>
 * Context ic = new InitialContext();
 * HelloHome hello = (HelloHome) ic.lookup("java:comp/env/hessian/hello");
 *
 * System.out.println("Hello: " + hello.helloWorld());
 * </pre>
 *
 * <h3>Authentication</h3>
 *
 * <p>The proxy can use HTTP basic authentication if the user and the
 * password are set.
 */
public class HessianProxyFactory implements ServiceProxyFactory, ObjectFactory {
  protected static Logger log
    = Logger.getLogger(HessianProxyFactory.class.getName());

  private final ClassLoader _loader;
  
  private SerializerFactory _serializerFactory;

  private HessianConnectionFactory _connFactory;
  
  private HessianRemoteResolver _resolver;

  private String _user;
  private String _password;
  private String _basicAuth;

  private boolean _isOverloadEnabled = false;

  private boolean _isHessian2Reply = true;
  private boolean _isHessian2Request = false;

  private boolean _isChunkedPost = true;
  private boolean _isDebug = false;

  private long _readTimeout = -1;
  private long _connectTimeout = -1;

  /**
   * Creates the new proxy factory.
   */
  public HessianProxyFactory()
  {
    this(Thread.currentThread().getContextClassLoader());
  }

  /**
   * Creates the new proxy factory.
   */
  public HessianProxyFactory(ClassLoader loader)
  {
    _loader = loader;
    _resolver = new HessianProxyResolver(this);
  }

  /**
   * Sets the user.
   */
  public void setUser(String user)
  {
    _user = user;
    _basicAuth = null;
  }

  /**
   * Sets the password.
   */
  public void setPassword(String password)
  {
    _password = password;
    _basicAuth = null;
  }

  public String getBasicAuth()
  {
    if (_basicAuth != null)
      return _basicAuth;

    else if (_user != null && _password != null)
      return "Basic " + base64(_user + ":" + _password);

    else
      return null;
  }

  /**
   * Sets the connection factory to use when connecting
   * to the Hessian service.
   */
  public void setConnectionFactory(HessianConnectionFactory factory)
  {
    _connFactory = factory;
  }

  /**
   * Returns the connection factory to be used for the HTTP request.
   */
  public HessianConnectionFactory getConnectionFactory()
  {
    if (_connFactory == null) {
      _connFactory = createHessianConnectionFactory();
      _connFactory.setHessianProxyFactory(this);
    }
    
    return _connFactory;
  }

  /**
   * Sets the debug
   */
  public void setDebug(boolean isDebug)
  {
    _isDebug = isDebug;
  }

  /**
   * Gets the debug
   */
  public boolean isDebug()
  {
    return _isDebug;
  }

  /**
   * Returns true if overloaded methods are allowed (using mangling)
   */
  public boolean isOverloadEnabled()
  {
    return _isOverloadEnabled;
  }

  /**
   * set true if overloaded methods are allowed (using mangling)
   */
  public void setOverloadEnabled(boolean isOverloadEnabled)
  {
    _isOverloadEnabled = isOverloadEnabled;
  }

  /**
   * Set true if should use chunked encoding on the request.
   */
  public void setChunkedPost(boolean isChunked)
  {
    _isChunkedPost = isChunked;
  }

  /**
   * Set true if should use chunked encoding on the request.
   */
  public boolean isChunkedPost()
  {
    return _isChunkedPost;
  }

  /**
   * The socket timeout on requests in milliseconds.
   */
  public long getReadTimeout()
  {
    return _readTimeout;
  }

  /**
   * The socket timeout on requests in milliseconds.
   */
  public void setReadTimeout(long timeout)
  {
    _readTimeout = timeout;
  }

  /**
   * The socket connection timeout in milliseconds.
   */
  public long getConnectTimeout()
  {
    return _connectTimeout;
  }

  /**
   * The socket connect timeout in milliseconds.
   */
  public void setConnectTimeout(long timeout)
  {
    _connectTimeout = timeout;
  }

  /**
   * True if the proxy can read Hessian 2 responses.
   */
  public void setHessian2Reply(boolean isHessian2)
  {
    _isHessian2Reply = isHessian2;
  }

  /**
   * True if the proxy should send Hessian 2 requests.
   */
  public void setHessian2Request(boolean isHessian2)
  {
    _isHessian2Request = isHessian2;

    if (isHessian2)
      _isHessian2Reply = true;
  }

  /**
   * Returns the remote resolver.
   */
  public HessianRemoteResolver getRemoteResolver()
  {
    return _resolver;
  }

  /**
   * Sets the serializer factory.
   */
  public void setSerializerFactory(SerializerFactory factory)
  {
    _serializerFactory = factory;
  }

  /**
   * Gets the serializer factory.
   */
  public SerializerFactory getSerializerFactory()
  {
    if (_serializerFactory == null)
      _serializerFactory = new SerializerFactory(_loader);

    return _serializerFactory;
  }

  protected HessianConnectionFactory createHessianConnectionFactory()
  {
    String className
      = System.getProperty(HessianConnectionFactory.class.getName());

    HessianConnectionFactory factory = null;
      
    try {
      if (className != null) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        Class<?> cl = Class.forName(className, false, loader);

        factory = (HessianConnectionFactory) cl.newInstance();

        return factory;
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return new HessianURLConnectionFactory();
  }
  /**
   * Creates a new proxy with the specified URL.  The API class uses
   * the java.api.class value from _hessian_
   *
   * @param url the URL where the client object is located.
   *
   * @return a proxy to the object with the specified interface.
   */
  public Object create(String url)
    throws MalformedURLException, ClassNotFoundException
  {
    HessianMetaInfoAPI metaInfo;

    metaInfo = (HessianMetaInfoAPI) create(HessianMetaInfoAPI.class, url);

    String apiClassName =
      (String) metaInfo._hessian_getAttribute("java.api.class");

    if (apiClassName == null)
      throw new HessianRuntimeException(url + " has an unknown api.");

    Class<?> apiClass = Class.forName(apiClassName, false, _loader);

    return create(apiClass, url);
  }

  /**
   * Creates a new proxy with the specified URL.  The returned object
   * is a proxy with the interface specified by api.
   *
   * <pre>
   * String url = "http://localhost:8080/ejb/hello");
   * HelloHome hello = (HelloHome) factory.create(HelloHome.class, url);
   * </pre>
   *
   * @param api the interface the proxy class needs to implement
   * @param url the URL where the client object is located.
   *
   * @return a proxy to the object with the specified interface.
   */
  public Object create(Class api, String urlName)
    throws MalformedURLException
  {
    return create(api, urlName, _loader);
  }

  /**
   * Creates a new proxy with the specified URL.  The returned object
   * is a proxy with the interface specified by api.
   *
   * <pre>
   * String url = "http://localhost:8080/ejb/hello");
   * HelloHome hello = (HelloHome) factory.create(HelloHome.class, url);
   * </pre>
   *
   * @param api the interface the proxy class needs to implement
   * @param url the URL where the client object is located.
   *
   * @return a proxy to the object with the specified interface.
   */
  public Object create(Class<?> api, String urlName, ClassLoader loader)
    throws MalformedURLException
  {
    URL url = new URL(urlName);
    
    return create(api, url, loader);
  }

  /**
   * Creates a new proxy with the specified URL.  The returned object
   * is a proxy with the interface specified by api.
   *
   * <pre>
   * String url = "http://localhost:8080/ejb/hello");
   * HelloHome hello = (HelloHome) factory.create(HelloHome.class, url);
   * </pre>
   *
   * @param api the interface the proxy class needs to implement
   * @param url the URL where the client object is located.
   *
   * @return a proxy to the object with the specified interface.
   */
  public Object create(Class<?> api, URL url, ClassLoader loader)
  {
    if (api == null)
      throw new NullPointerException("api must not be null for HessianProxyFactory.create()");
    InvocationHandler handler = null;

    handler = new HessianProxy(url, this, api);

    return Proxy.newProxyInstance(loader,
                                  new Class[] { api,
                                                HessianRemoteObject.class },
                                  handler);
  }

  public AbstractHessianInput getHessianInput(InputStream is)
  {
    return getHessian2Input(is);
  }

  public AbstractHessianInput getHessian1Input(InputStream is)
  {
    AbstractHessianInput in;

    if (_isDebug)
      is = new HessianDebugInputStream(is, new PrintWriter(System.out));

    in = new HessianInput(is);

    in.setRemoteResolver(getRemoteResolver());

    in.setSerializerFactory(getSerializerFactory());

    return in;
  }

  public AbstractHessianInput getHessian2Input(InputStream is)
  {
    AbstractHessianInput in;

    if (_isDebug)
      is = new HessianDebugInputStream(is, new PrintWriter(System.out));

    in = new Hessian2Input(is);

    in.setRemoteResolver(getRemoteResolver());

    in.setSerializerFactory(getSerializerFactory());

    return in;
  }

  public AbstractHessianOutput getHessianOutput(OutputStream os)
  {
    AbstractHessianOutput out;

    if (_isHessian2Request)
      out = new Hessian2Output(os);
    else {
      HessianOutput out1 = new HessianOutput(os);
      out = out1;

      if (_isHessian2Reply)
        out1.setVersion(2);
    }

    out.setSerializerFactory(getSerializerFactory());

    return out;
  }

  /**
   * JNDI object factory so the proxy can be used as a resource.
   */
  public Object getObjectInstance(Object obj, Name name,
                                  Context nameCtx, Hashtable<?,?> environment)
    throws Exception
  {
    Reference ref = (Reference) obj;

    String api = null;
    String url = null;
    
    for (int i = 0; i < ref.size(); i++) {
      RefAddr addr = ref.get(i);

      String type = addr.getType();
      String value = (String) addr.getContent();

      if (type.equals("type"))
        api = value;
      else if (type.equals("url"))
        url = value;
      else if (type.equals("user"))
        setUser(value);
      else if (type.equals("password"))
        setPassword(value);
    }

    if (url == null)
      throw new NamingException("`url' must be configured for HessianProxyFactory.");
    // XXX: could use meta protocol to grab this
    if (api == null)
      throw new NamingException("`type' must be configured for HessianProxyFactory.");

    Class apiClass = Class.forName(api, false, _loader);

    return create(apiClass, url);
  }

  /**
   * Creates the Base64 value.
   */
  private String base64(String value)
  {
    StringBuffer cb = new StringBuffer();

    int i = 0;
    for (i = 0; i + 2 < value.length(); i += 3) {
      long chunk = (int) value.charAt(i);
      chunk = (chunk << 8) + (int) value.charAt(i + 1);
      chunk = (chunk << 8) + (int) value.charAt(i + 2);

      cb.append(encode(chunk >> 18));
      cb.append(encode(chunk >> 12));
      cb.append(encode(chunk >> 6));
      cb.append(encode(chunk));
    }

    if (i + 1 < value.length()) {
      long chunk = (int) value.charAt(i);
      chunk = (chunk << 8) + (int) value.charAt(i + 1);
      chunk <<= 8;

      cb.append(encode(chunk >> 18));
      cb.append(encode(chunk >> 12));
      cb.append(encode(chunk >> 6));
      cb.append('=');
    }
    else if (i < value.length()) {
      long chunk = (int) value.charAt(i);
      chunk <<= 16;

      cb.append(encode(chunk >> 18));
      cb.append(encode(chunk >> 12));
      cb.append('=');
      cb.append('=');
    }

    return cb.toString();
  }

  public static char encode(long d)
  {
    d &= 0x3f;
    if (d < 26)
      return (char) (d + 'A');
    else if (d < 52)
      return (char) (d + 'a' - 26);
    else if (d < 62)
      return (char) (d + '0' - 52);
    else if (d == 62)
      return '+';
    else
      return '/';
  }
}

