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

package com.caucho.hessian.client;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Internal factory for creating connections to the server.  The default
 * factory is java.net
 */
public class HessianURLConnectionFactory implements HessianConnectionFactory {
  private static final Logger log
    = Logger.getLogger(HessianURLConnectionFactory.class.getName());
  
  private HessianProxyFactory _proxyFactory;

  public void setHessianProxyFactory(HessianProxyFactory factory)
  {
    _proxyFactory = factory;
  }
  
  /**
   * Opens a new or recycled connection to the HTTP server.
   */
  public HessianConnection open(URL url)
    throws IOException
  {
    if (log.isLoggable(Level.FINER))
      log.finer(this + " open(" + url + ")");

    URLConnection conn = url.openConnection();

    // HttpURLConnection httpConn = (HttpURLConnection) conn;
    // httpConn.setRequestMethod("POST");
    // conn.setDoInput(true);

    long connectTimeout = _proxyFactory.getConnectTimeout();

    if (connectTimeout >= 0)
      conn.setConnectTimeout((int) connectTimeout);

    conn.setDoOutput(true);

    long readTimeout = _proxyFactory.getReadTimeout();

    if (readTimeout > 0) {
      try {
        conn.setReadTimeout((int) readTimeout);
      } catch (Throwable e) {
      }
    }

    /*
    // Used chunked mode when available, i.e. JDK 1.5.
    if (_proxyFactory.isChunkedPost() && conn instanceof HttpURLConnection) {
      try {
        HttpURLConnection httpConn = (HttpURLConnection) conn;

        httpConn.setChunkedStreamingMode(8 * 1024);
      } catch (Throwable e) {
      }
    }
    */
    
    return new HessianURLConnection(url, conn);
  }
}
