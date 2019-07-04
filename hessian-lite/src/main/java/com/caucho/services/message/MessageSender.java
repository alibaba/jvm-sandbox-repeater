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

package com.caucho.services.message;

import java.util.HashMap;

/**
 * Service API for a bare-bones message server.
 *
 * <p>A minimal message server only needs to implement the MessageSender
 * interface.  Keeping the server API simple makes it easier for
 * implementations to start up message servers.
 *
 * <p>The MessageSender service is queue or topic specific.  So there's no
 * need for a <b>To</b> header if there's no routing involved.
 * In other words, the service URL generally includes the queue
 * or topic identifier, e.g.
 *
 * <pre>
 * http://www.caucho.com/hessian/hessian/message?ejbid=topic-a
 * <pre>
 *
 * <p>More complicated messaging topologies, including configurations
 * with routing mesaging servers, will use the headers to define the
 * final destination.  The headers have the same functional purpose as
 * RFC-822 mail headers.
 */
public interface MessageSender {
  /**
   * Send a message to the server.
   *
   * @param headers any message headers
   * @param message the message data
   *
   * @exception MessageServiceException if the message cannot be delivered
   */
  public void send(HashMap headers, Object message)
    throws MessageServiceException;
}
