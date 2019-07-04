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

import com.caucho.hessian.util.HessianFreeList;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Factory for creating HessianInput and HessianOutput streams.
 */
public class HessianFactory
{
  public static final Logger log
    = Logger.getLogger(HessianFactory.class.getName());

  private SerializerFactory _serializerFactory;
  private SerializerFactory _defaultSerializerFactory;

  private final HessianFreeList<Hessian2Output> _freeHessian2Output
    = new HessianFreeList<Hessian2Output>(32);

  private final HessianFreeList<HessianOutput> _freeHessianOutput
    = new HessianFreeList<HessianOutput>(32);

  private final HessianFreeList<Hessian2Input> _freeHessian2Input
    = new HessianFreeList<Hessian2Input>(32);

  private final HessianFreeList<HessianInput> _freeHessianInput
    = new HessianFreeList<HessianInput>(32);

  public HessianFactory()
  {
    _defaultSerializerFactory = SerializerFactory.createDefault();
    _serializerFactory = _defaultSerializerFactory;
  }

  public void setSerializerFactory(SerializerFactory factory)
  {
    _serializerFactory = factory;
  }

  public SerializerFactory getSerializerFactory()
  {
    // the default serializer factory cannot be modified by external
    // callers
    if (_serializerFactory == _defaultSerializerFactory) {
      _serializerFactory = new SerializerFactory();
    }

    return _serializerFactory;
  }
  
  /**
   * Enable whitelist deserialization mode. Only classes matching the whitelist
   * will be allowed.
   */
  public void setWhitelist(boolean isWhitelist)
  {
    getSerializerFactory().getClassFactory().setWhitelist(isWhitelist);
  }
  
  /**
   * Allow a class or package based on a pattern.
   * 
   * Examples: "java.util.*", "com.foo.io.Bean"
   */
  public void allow(String pattern)
  {
    getSerializerFactory().getClassFactory().allow(pattern);
  }
  
  
  /**
   * Deny a class or package based on a pattern.
   * 
   * Examples: "java.util.*", "com.foo.io.Bean"
   */
  public void deny(String pattern)
  {
    getSerializerFactory().getClassFactory().deny(pattern);
  }

  /**
   * Creates a new Hessian 2.0 deserializer.
   */
  public Hessian2Input createHessian2Input(InputStream is)
  {
    Hessian2Input in = _freeHessian2Input.allocate();
    
    if (in == null) {
      in = new Hessian2Input(is);
      in.setSerializerFactory(getSerializerFactory());
    }
    else {
      in.init(is);
    }

    return in;
  }

  /**
   * Frees a Hessian 2.0 deserializer
   */
  public void freeHessian2Input(Hessian2Input in)
  {
    if (in == null)
      return;

    in.free();

    _freeHessian2Input.free(in);
  }

  /**
   * Creates a new Hessian 2.0 deserializer.
   */
  public Hessian2StreamingInput createHessian2StreamingInput(InputStream is)
  {
    Hessian2StreamingInput in = new Hessian2StreamingInput(is);
    in.setSerializerFactory(getSerializerFactory());

    return in;
  }

  /**
   * Frees a Hessian 2.0 deserializer
   */
  public void freeHessian2StreamingInput(Hessian2StreamingInput in)
  {
  }

  /**
   * Creates a new Hessian 1.0 deserializer.
   */
  public HessianInput createHessianInput(InputStream is)
  {
    return new HessianInput(is);
  }

  /**
   * Creates a new Hessian 2.0 serializer.
   */
  public Hessian2Output createHessian2Output(OutputStream os)
  {
    Hessian2Output out = createHessian2Output();
    
    out.init(os);
    
    return out;
  }

  /**
   * Creates a new Hessian 2.0 serializer.
   */
  public Hessian2Output createHessian2Output()
  {
    Hessian2Output out = _freeHessian2Output.allocate();

    if (out == null) {
      out = new Hessian2Output();

      out.setSerializerFactory(getSerializerFactory());
    }

    return out;
  }

  /**
   * Frees a Hessian 2.0 serializer
   */
  public void freeHessian2Output(Hessian2Output out)
  {
    if (out == null)
      return;

    out.free();

    _freeHessian2Output.free(out);
  }

  /**
   * Creates a new Hessian 2.0 serializer.
   */
  public Hessian2StreamingOutput createHessian2StreamingOutput(OutputStream os)
  {
    Hessian2Output out = createHessian2Output(os);

    return new Hessian2StreamingOutput(out);
  }

  /**
   * Frees a Hessian 2.0 serializer
   */
  public void freeHessian2StreamingOutput(Hessian2StreamingOutput out)
  {
    if (out == null)
      return;

    freeHessian2Output(out.getHessian2Output());
  }

  /**
   * Creates a new Hessian 1.0 serializer.
   */
  public HessianOutput createHessianOutput(OutputStream os)
  {
    return new HessianOutput(os);
  }

  public OutputStream createHessian2DebugOutput(OutputStream os,
                                                Logger log,
                                                Level level)
  {
    HessianDebugOutputStream out
      = new HessianDebugOutputStream(os, log, level);

    out.startTop2();

    return out;
  }
}
