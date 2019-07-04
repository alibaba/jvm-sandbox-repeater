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

package com.caucho.services.server;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;

/**
 * Interface for a service, including lifecycle.
 */
public class GenericService implements Service {
  protected ServletConfig config;
  
  /**
   * Initialize the service instance.
   */
  public void init(ServletConfig config)
    throws ServletException
  {
    this.config = config;

    init();
  }
  
  /**
   * Initialize the service instance.
   */
  public void init()
    throws ServletException
  {
  }

  /**
   * Returns the named initialization parameter.
   */
  public String getInitParameter(String name)
  {
    return this.config.getInitParameter(name);
  }

  /**
   * Returns the servlet context.
   */
  public ServletConfig getServletConfig()
  {
    return this.config;
  }

  /**
   * Returns the servlet context.
   */
  public ServletContext getServletContext()
  {
    return this.config.getServletContext();
  }

  /**
   * Logs a message to the error stream.
   */
  public void log(String message)
  {
    getServletContext().log(message);
  }

  /**
   * Returns the servlet request object for the request.
   */
  public ServletRequest getRequest()
  {
    return ServiceContext.getRequest();
  }

  /**
   * Returns the service identifier for the request.
   */
  public String getServiceName()
  {
    return ServiceContext.getServiceName();
  }

  /**
   * Returns the service identifier for the request.
   *
   * @deprecated
   */
  public String getServiceId()
  {
    return getServiceName();
  }

  /**
   * Returns the object identifier for the request.
   */
  public String getObjectId()
  {
    return ServiceContext.getObjectId();
  }
  
  /**
   * Cleanup the service instance.
   */
  public void destroy()
  {
  }
}
