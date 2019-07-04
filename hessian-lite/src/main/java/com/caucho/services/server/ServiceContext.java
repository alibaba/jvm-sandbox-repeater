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

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.HashMap;

/**
 * Context for a service, to handle request-specific information.
 */
public class ServiceContext {
  private static final ThreadLocal<ServiceContext> _localContext
    = new ThreadLocal<ServiceContext>();

  private ServletRequest _request;
  private ServletResponse _response;
  private String _serviceName;
  private String _objectId;
  private int _count;
  private HashMap _headers = new HashMap();

  private ServiceContext()
  {
  }
  
  /**
   * Sets the request object prior to calling the service's method.
   *
   * @param request the calling servlet request
   * @param serviceId the service identifier
   * @param objectId the object identifier
   */
  public static void begin(ServletRequest request,
                           ServletResponse response,
                           String serviceName,
                           String objectId)
    throws ServletException
  {
    ServiceContext context = (ServiceContext) _localContext.get();

    if (context == null) {
      context = new ServiceContext();
      _localContext.set(context);
    }

    context._request = request;
    context._response = response;
    context._serviceName = serviceName;
    context._objectId = objectId;
    context._count++;
  }

  /**
   * Returns the service request.
   */
  public static ServiceContext getContext()
  {
    return (ServiceContext) _localContext.get();
  }

  /**
   * Adds a header.
   */
  public void addHeader(String header, Object value)
  {
    _headers.put(header, value);
  }

  /**
   * Gets a header.
   */
  public Object getHeader(String header)
  {
    return _headers.get(header);
  }

  /**
   * Gets a header from the context.
   */
  public static Object getContextHeader(String header)
  {
    ServiceContext context = (ServiceContext) _localContext.get();

    if (context != null)
      return context.getHeader(header);
    else
      return null;
  }

  /**
   * Returns the service request.
   */
  public static ServletRequest getContextRequest()
  {
    ServiceContext context = (ServiceContext) _localContext.get();

    if (context != null)
      return context._request;
    else
      return null;
  }

  /**
   * Returns the service request.
   */
  public static ServletResponse getContextResponse()
  {
    ServiceContext context = (ServiceContext) _localContext.get();

    if (context != null)
      return context._response;
    else
      return null;
  }

  /**
   * Returns the service id, corresponding to the pathInfo of the URL.
   */
  public static String getContextServiceName()
  {
    ServiceContext context = (ServiceContext) _localContext.get();

    if (context != null)
      return context._serviceName;
    else
      return null;
  }

  /**
   * Returns the object id, corresponding to the ?id= of the URL.
   */
  public static String getContextObjectId()
  {
    ServiceContext context = (ServiceContext) _localContext.get();

    if (context != null)
      return context._objectId;
    else
      return null;
  }

  /**
   * Cleanup at the end of a request.
   */
  public static void end()
  {
    ServiceContext context = (ServiceContext) _localContext.get();

    if (context != null && --context._count == 0) {
      context._request = null;
      context._response = null;

      context._headers.clear();
      
      _localContext.set(null);
    }
  }

  /**
   * Returns the service request.
   *
   * @deprecated
   */
  public static ServletRequest getRequest()
  {
    ServiceContext context = (ServiceContext) _localContext.get();

    if (context != null)
      return context._request;
    else
      return null;
  }

  /**
   * Returns the service id, corresponding to the pathInfo of the URL.
   *
   * @deprecated
   */
  public static String getServiceName()
  {
    ServiceContext context = (ServiceContext) _localContext.get();

    if (context != null)
      return context._serviceName;
    else
      return null;
  }

  /**
   * Returns the object id, corresponding to the ?id= of the URL.
   *
   * @deprecated
   */
  public static String getObjectId()
  {
    ServiceContext context = (ServiceContext) _localContext.get();

    if (context != null)
      return context._objectId;
    else
      return null;
  }
}
