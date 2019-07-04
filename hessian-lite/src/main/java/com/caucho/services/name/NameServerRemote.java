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
 * 4. The names "Burlap", "Hessian", "Resin", and "Caucho" must not be
 *    used to endorse or promote products derived from this software
 *    without prior written permission. For written permission, please contact
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

package com.caucho.services.name;

import java.rmi.RemoteException;

/**
 * A read-only name service.  The name service provides hierarchical
 * object lookup.  The path names are separated by '/'.
 *
 * <p>Because the name service is hierarchical, a lookup of an intermediate
 * node will return a NameServer instance.
 *
 * <p>The following example is a simple use of the NameServer:
 * <pre>
 * /dir-1/1 - where foo contains the string "foo-1"
 * /dir-1/2 - where foo contains the string "foo-2"
 * /dir-2/1 - where foo contains the string "foo-1"
 * /dir-2/2 - where foo contains the string "foo-2"
 * </pre>
 *
 * <p/>The root server might have a URL like:
 * <pre>
 * http://www.caucho.com/hessian/hessian/name?ejbid=/
 * </pre>
 *
 * <p/>So <code>root.lookup("/dir-1/1")</code> will return the string
 * "foo-1", and <code>root.lookup("/dir-1")</code> will return the
 * NameServer with the URL:
 * <pre>
 * http://www.caucho.com/hessian/hessian/name?ejbid=/dir-1
 * </pre>
 */
public interface NameServerRemote {
  /**
   * Lookup an object from the name server.
   *
   * @param name the relative path name
   *
   * @return the matching object or null if no object maches
   *
   * @exception NameServiceException if there's an error
   */
  public Object lookup(String name)
    throws NameServiceException, RemoteException;

  /**
   * Lists all the object name components directly below the current context.
   * The names are the relative compent name.
   *
   * <p>For example, if the name server context is "/dir-1", the returned
   * values will be ["1", "2"].
   */
  public String []list()
    throws NameServiceException, RemoteException;
}
