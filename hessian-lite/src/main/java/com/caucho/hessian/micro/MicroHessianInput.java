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

package com.caucho.hessian.micro;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * Input stream for Hessian requests, compatible with microedition
 * Java.  It only uses classes and types available to J2ME.  In
 * particular, it does not have any support for the &lt;double> type.
 *
 * <p>MicroHessianInput does not depend on any classes other than
 * in J2ME, so it can be extracted independently into a smaller package.
 *
 * <p>MicroHessianInput is unbuffered, so any client needs to provide
 * its own buffering.
 *
 * <pre>
 * InputStream is = ...; // from http connection
 * MicroHessianInput in = new MicroHessianInput(is);
 * String value;
 *
 * in.startReply();         // read reply header
 * value = in.readString(); // read string value
 * in.completeReply();      // read reply footer
 * </pre>
 */
public class MicroHessianInput {
  protected InputStream is;
  /**
   * Creates a new Hessian input stream, initialized with an
   * underlying input stream.
   *
   * @param is the underlying input stream.
   */
  public MicroHessianInput(InputStream is)
  {
    init(is);
  }

  /**
   * Creates an uninitialized Hessian input stream.
   */
  public MicroHessianInput()
  {
  }

  /**
   * Initialize the hessian stream with the underlying input stream.
   */
  public void init(InputStream is)
  {
    this.is = is;
  }

  /**
   * Starts reading the reply
   *
   * <p>A successful completion will have a single value:
   *
   * <pre>
   * r x01 x00
   * </pre>
   */
  public void startReply()
    throws IOException
  {
    int tag = is.read();
    
    if (tag != 'r')
      protocolException("expected hessian reply");

    int major = is.read();
    int minor = is.read();
  }

  /**
   * Completes reading the call
   *
   * <p>A successful completion will have a single value:
   *
   * <pre>
   * z
   * </pre>
   */
  public void completeReply()
    throws IOException
  {
    int tag = is.read();
    
    if (tag != 'z')
      protocolException("expected end of reply");
  }

  /**
   * Reads a boolean
   *
   * <pre>
   * T
   * F
   * </pre>
   */
  public boolean readBoolean()
    throws IOException
  {
    int tag = is.read();

    switch (tag) {
    case 'T': return true;
    case 'F': return false;
    default:
      throw expect("boolean", tag);
    }
  }

  /**
   * Reads an integer
   *
   * <pre>
   * I b32 b24 b16 b8
   * </pre>
   */
  public int readInt()
    throws IOException
  {
    int tag = is.read();

    if (tag != 'I')
      throw expect("integer", tag);

    int b32 = is.read();
    int b24 = is.read();
    int b16 = is.read();
    int b8 = is.read();

    return (b32 << 24) + (b24 << 16) + (b16 << 8) + b8;
  }

  /**
   * Reads a long
   *
   * <pre>
   * L b64 b56 b48 b40 b32 b24 b16 b8
   * </pre>
   */
  public long readLong()
    throws IOException
  {
    int tag = is.read();

    if (tag != 'L')
      throw protocolException("expected long");

    long b64 = is.read();
    long b56 = is.read();
    long b48 = is.read();
    long b40 = is.read();
    long b32 = is.read();
    long b24 = is.read();
    long b16 = is.read();
    long b8 = is.read();

    return ((b64 << 56) +
            (b56 << 48) +
            (b48 << 40) +
            (b40 << 32) +
            (b32 << 24) +
            (b24 << 16) +
            (b16 << 8) +
            b8);
  }

  /**
   * Reads a date.
   *
   * <pre>
   * T b64 b56 b48 b40 b32 b24 b16 b8
   * </pre>
   */
  public long readUTCDate()
    throws IOException
  {
    int tag = is.read();

    if (tag != 'd')
      throw protocolException("expected date");

    long b64 = is.read();
    long b56 = is.read();
    long b48 = is.read();
    long b40 = is.read();
    long b32 = is.read();
    long b24 = is.read();
    long b16 = is.read();
    long b8 = is.read();

    return ((b64 << 56) +
            (b56 << 48) +
            (b48 << 40) +
            (b40 << 32) +
            (b32 << 24) +
            (b24 << 16) +
            (b16 << 8) +
            b8);
  }

  /**
   * Reads a string
   *
   * <pre>
   * S b16 b8 string value
   * </pre>
   */
  public String readString()
    throws IOException
  {
    int tag = is.read();

    if (tag == 'N')
      return null;

    if (tag != 'S')
      throw expect("string", tag);

    int b16 = is.read();
    int b8 = is.read();

    int len = (b16 << 8) + b8;

    return readStringImpl(len);
  }

  /**
   * Reads a byte array
   *
   * <pre>
   * B b16 b8 data value
   * </pre>
   */
  public byte []readBytes()
    throws IOException
  {
    int tag = is.read();

    if (tag == 'N')
      return null;

    if (tag != 'B')
      throw expect("bytes", tag);

    int b16 = is.read();
    int b8 = is.read();

    int len = (b16 << 8) + b8;

    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    for (int i = 0; i < len; i++)
      bos.write(is.read());

    return bos.toByteArray();
  }

  /**
   * Reads an arbitrary object the input stream.
   */
  public Object readObject(Class expectedClass)
    throws IOException
  {
    int tag = is.read();

    switch (tag) {
    case 'N':
      return null;
      
    case 'T':
      return new Boolean(true);
      
    case 'F':
      return new Boolean(false);
      
    case 'I': {
      int b32 = is.read();
      int b24 = is.read();
      int b16 = is.read();
      int b8 = is.read();

      return new Integer((b32 << 24) + (b24 << 16) + (b16 << 8) + b8);
    }
    
    case 'L': {
      long b64 = is.read();
      long b56 = is.read();
      long b48 = is.read();
      long b40 = is.read();
      long b32 = is.read();
      long b24 = is.read();
      long b16 = is.read();
      long b8 = is.read();

      return new Long((b64 << 56) +
                      (b56 << 48) +
                      (b48 << 40) +
                      (b40 << 32) +
                      (b32 << 24) +
                      (b24 << 16) +
                      (b16 << 8) +
                      b8);
    }
    
    case 'd': {
      long b64 = is.read();
      long b56 = is.read();
      long b48 = is.read();
      long b40 = is.read();
      long b32 = is.read();
      long b24 = is.read();
      long b16 = is.read();
      long b8 = is.read();

      return new Date((b64 << 56) +
                      (b56 << 48) +
                      (b48 << 40) +
                      (b40 << 32) +
                      (b32 << 24) +
                      (b24 << 16) +
                      (b16 << 8) +
                      b8);
    }
    
    case 'S':
    case 'X': {
      int b16 = is.read();
      int b8 = is.read();

      int len = (b16 << 8) + b8;

      return readStringImpl(len);
    }
    
    case 'B': {
      if (tag != 'B')
        throw expect("bytes", tag);

      int b16 = is.read();
      int b8 = is.read();

      int len = (b16 << 8) + b8;

      ByteArrayOutputStream bos = new ByteArrayOutputStream();

      for (int i = 0; i < len; i++)
        bos.write(is.read());

      return bos.toByteArray();
    }
    default:
      throw new IOException("unknown code:" + (char) tag);
    }
  }

  /**
   * Reads a string from the underlying stream.
   */
  protected String readStringImpl(int length)
    throws IOException
  {
    StringBuffer sb = new StringBuffer();
    
    for (int i = 0; i < length; i++) {
      int ch = is.read();

      if (ch < 0x80)
        sb.append((char) ch);
      else if ((ch & 0xe0) == 0xc0) {
        int ch1 = is.read();
        int v = ((ch & 0x1f) << 6) + (ch1 & 0x3f);

        sb.append((char) v);
      }
      else if ((ch & 0xf0) == 0xe0) {
        int ch1 = is.read();
        int ch2 = is.read();
        int v = ((ch & 0x0f) << 12) + ((ch1 & 0x3f) << 6) + (ch2 & 0x3f);

        sb.append((char) v);
      }
      else
        throw new IOException("bad utf-8 encoding");
    }

    return sb.toString();
  }

  protected IOException expect(String expect, int ch)
  {
    if (ch < 0)
      return protocolException("expected " + expect + " at end of file");
    else
      return protocolException("expected " + expect + " at " + (char) ch);
  }
  
  protected IOException protocolException(String message)
  {
    return new IOException(message);
  }
}
