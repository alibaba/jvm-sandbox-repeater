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
import com.caucho.services.server.Service;
import com.caucho.services.server.ServiceContext;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Servlet for serving Burlap services.
 */
public class BurlapServlet extends GenericServlet {
  private Class<?> _apiClass;
  private Object _service;
  
  private BurlapSkeleton _skeleton;

  public String getServletInfo()
  {
    return "Burlap Servlet";
  }

  /**
   * Sets the service class.
   */
  public void setService(Object service)
  {
    _service = service;
  }

  /**
   * Sets the api-class.
   */
  public void setAPIClass(Class<?> apiClass)
  {
    _apiClass = apiClass;
  }

  /**
   * Initialize the service, including the service object.
   */
  public void init(ServletConfig config)
    throws ServletException
  {
    super.init(config);
    
    try {
      if (_service == null) {
        String className = getInitParameter("service-class");
        Class<?> serviceClass = null;

        if (className != null) {
          ClassLoader loader = Thread.currentThread().getContextClassLoader();

          if (loader != null)
            serviceClass = Class.forName(className, false, loader);
          else
            serviceClass = Class.forName(className);
        }
        else {
          if (getClass().equals(BurlapServlet.class))
            throw new ServletException("server must extend BurlapServlet");

          serviceClass = getClass();
        }

        _service = serviceClass.newInstance();

        if (_service instanceof BurlapServlet)
          ((BurlapServlet) _service).setService(this);
        if (_service instanceof Service)
          ((Service) _service).init(getServletConfig());
        else if (_service instanceof Servlet)
          ((Servlet) _service).init(getServletConfig());
      }
      
      if (_apiClass == null) {
        String className = getInitParameter("api-class");

        if (className != null) {
          ClassLoader loader = Thread.currentThread().getContextClassLoader();

          if (loader != null)
            _apiClass = Class.forName(className, false, loader);
          else
            _apiClass = Class.forName(className);
        }
        else
          _apiClass = _service.getClass();
      }

      _skeleton = new BurlapSkeleton(_service, _apiClass);
    } catch (ServletException e) {
      throw e;
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }
  
  /**
   * Execute a request.  The path-info of the request selects the bean.
   * Once the bean's selected, it will be applied.
   */
  public void service(ServletRequest request, ServletResponse response)
    throws IOException, ServletException
  {
    HttpServletRequest req = (HttpServletRequest) request;
    HttpServletResponse res = (HttpServletResponse) response;

    if (! req.getMethod().equals("POST")) {
      res.setStatus(500, "Burlap Requires POST");
      PrintWriter out = res.getWriter();

      res.setContentType("text/html");
      out.println("<h1>Burlap Requires POST</h1>");
      
      return;
    }

    String serviceId = req.getPathInfo();
    String objectId = req.getParameter("id");
    if (objectId == null)
      objectId = req.getParameter("ejbid");

    ServiceContext.begin(req, res, serviceId, objectId);

    try {
      InputStream is = request.getInputStream();
      OutputStream os = response.getOutputStream();

      BurlapInput in = new BurlapInput(is);
      BurlapOutput out = new BurlapOutput(os);

      _skeleton.invoke(in, out);
    } catch (RuntimeException e) {
      throw e;
    } catch (ServletException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServletException(e);
    } finally {
      ServiceContext.end();
    }
  }
}
