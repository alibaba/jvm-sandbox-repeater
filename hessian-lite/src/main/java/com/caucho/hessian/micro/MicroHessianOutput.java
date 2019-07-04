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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Output stream for Hessian requests, compatible with microedition
 * Java.  It only uses classes and types available to J2ME.  In
 * particular, it does not have any support for the &lt;double> type.
 *
 * <p>MicroHessianOutput does not depend on any classes other than
 * in J2ME, so it can be extracted independently into a smaller package.
 *
 * <p>MicroHessianOutput is unbuffered, so any client needs to provide
 * its own buffering.
 *
 * <pre>
 * OutputStream os = ...; // from http connection
 * MicroHessianOutput out = new MicroHessianOutput(os);
 * String value;
 *
 * out.startCall("hello");  // start hello call
 * out.writeString("arg1"); // write a string argument
 * out.completeCall();      // complete the call
 * </pre>
 */
public class MicroHessianOutput {
  protected OutputStream os;

  /**
   * Creates a new Hessian output stream, initialized with an
   * underlying output stream.
   *
   * @param os the underlying output stream.
   */
  public MicroHessianOutput(OutputStream os)
  {
    init(os);
  }

  /**
   * Creates an uninitialized Hessian output stream.
   */
  public MicroHessianOutput()
  {
  }

  public void init(OutputStream os)
  {
    this.os = os;
  }

  /**
   * Writes the method call:
   *
   * <code><pre>
   * c major minor
   * m b16 b8 method-namek
   * </pre></code>
   *
   * @param method the method name to call.
   */
  public void startCall(String method)
    throws IOException
  {
    os.write('c');
    os.write(0);
    os.write(1);

    os.write('m');
    int len = method.length();
    os.write(len >> 8);
    os.write(len);
    printString(method, 0, len);
  }

  /**
   * Writes the method call:
   *
   * <code><pre>
   * z
   * </pre></code>
   */
  public void completeCall()
    throws IOException
  {
    os.write('z');
  }

  /**
   * Writes a boolean value to the stream.  The boolean will be written
   * with the following syntax:
   *
   * <code><pre>
   * T
   * F
   * </pre></code>
   *
   * @param value the boolean value to write.
   */
  public void writeBoolean(boolean value)
    throws IOException
  {
    if (value)
      os.write('T');
    else
      os.write('F');
  }

  /**
   * Writes an integer value to the stream.  The integer will be written
   * with the following syntax:
   *
   * <code><pre>
   * I b32 b24 b16 b8
   * </pre></code>
   *
   * @param value the integer value to write.
   */
  public void writeInt(int value)
    throws IOException
  {
    os.write('I');
    os.write(value >> 24);
    os.write(value >> 16);
    os.write(value >> 8);
    os.write(value);
  }

  /**
   * Writes a long value to the stream.  The long will be written
   * with the following syntax:
   *
   * <code><pre>
   * L b64 b56 b48 b40 b32 b24 b16 b8
   * </pre></code>
   *
   * @param value the long value to write.
   */
  public void writeLong(long value)
    throws IOException
  {
    os.write('L');
    os.write((byte) (value >> 56));
    os.write((byte) (value >> 48));
    os.write((byte) (value >> 40));
    os.write((byte) (value >> 32));
    os.write((byte) (value >> 24));
    os.write((byte) (value >> 16));
    os.write((byte) (value >> 8));
    os.write((byte) (value));
  }

  /**
   * Writes a date to the stream.
   *
   * <code><pre>
   * T  b64 b56 b48 b40 b32 b24 b16 b8
   * </pre></code>
   *
   * @param time the date in milliseconds from the epoch in UTC
   */
  public void writeUTCDate(long time)
    throws IOException
  {
    os.write('d');
    os.write((byte) (time >> 56));
    os.write((byte) (time >> 48));
    os.write((byte) (time >> 40));
    os.write((byte) (time >> 32));
    os.write((byte) (time >> 24));
    os.write((byte) (time >> 16));
    os.write((byte) (time >> 8));
    os.write((byte) (time));
  }

  /**
   * Writes a null value to the stream.
   * The null will be written with the following syntax
   *
   * <code><pre>
   * N
   * </pre></code>
   *
   * @param value the string value to write.
   */
  public void writeNull()
    throws IOException
  {
    os.write('N');
  }

  /**
   * Writes a string value to the stream using UTF-8 encoding.
   * The string will be written with the following syntax:
   *
   * <code><pre>
   * S b16 b8 string-value
   * </pre></code>
   *
   * If the value is null, it will be written as
   *
   * <code><pre>
   * N
   * </pre></code>
   *
   * @param value the string value to write.
   */
  public void writeString(String value)
    throws IOException
  {
    if (value == null) {
      os.write('N');
    }
    else {
      int len = value.length();

      os.write('S');
      os.write(len >> 8);
      os.write(len);

      printString(value);
    }
  }

  /**
   * Writes a byte array to the stream.
   * The array will be written with the following syntax:
   *
   * <code><pre>
   * B b16 b18 bytes
   * </pre></code>
   *
   * If the value is null, it will be written as
   *
   * <code><pre>
   * N
   * </pre></code>
   *
   * @param value the string value to write.
   */
  public void writeBytes(byte []buffer)
    throws IOException
  {
    if (buffer == null)
      os.write('N');
    else
      writeBytes(buffer, 0, buffer.length);
  }
  /**
   * Writes a byte array to the stream.
   * The array will be written with the following syntax:
   *
   * <code><pre>
   * B b16 b18 bytes
   * </pre></code>
   *
   * If the value is null, it will be written as
   *
   * <code><pre>
   * N
   * </pre></code>
   *
   * @param value the string value to write.
   */
  public void writeBytes(byte []buffer, int offset, int length)
    throws IOException
  {
    if (buffer == null) {
      os.write('N');
    }
    else {
      os.write('B');
      os.write(length << 8);
      os.write(length);
      os.write(buffer, offset, length);
    }
  }

  /**
   * Writes a reference.
   *
   * <code><pre>
   * R b32 b24 b16 b8
   * </pre></code>
   *
   * @param value the integer value to write.
   */
  public void writeRef(int value)
    throws IOException
  {
    os.write('R');
    os.write(value << 24);
    os.write(value << 16);
    os.write(value << 8);
    os.write(value);
  }

  /**
   * Writes a generic object to the output stream.
   */
  public void writeObject(Object object)
    throws IOException
  {
    if (object == null)
      writeNull();
    else if (object instanceof String)
      writeString((String) object);
    else if (object instanceof Boolean)
      writeBoolean(((Boolean) object).booleanValue());
    else if (object instanceof Integer)
      writeInt(((Integer) object).intValue());
    else if (object instanceof Long)
      writeLong(((Long) object).longValue());
    else if (object instanceof Date)
      writeUTCDate(((Date) object).getTime());
    else if (object instanceof byte[]) {
      byte []data = (byte []) object;
      writeBytes(data, 0, data.length);
    }
    else if (object instanceof Vector) {
      Vector vector = (Vector) object;

      int size = vector.size();
      writeListBegin(size, null);
      for (int i = 0; i < size; i++)
        writeObject(vector.elementAt(i));
      
      writeListEnd();
    }
    else if (object instanceof Hashtable) {
      Hashtable hashtable = (Hashtable) object;

      writeMapBegin(null);
      Enumeration e = hashtable.keys();
      while (e.hasMoreElements()) {
        Object key = e.nextElement();
        Object value = hashtable.get(key);

        writeObject(key);
        writeObject(value);
      }
      writeMapEnd();
    }
    else
      writeCustomObject(object);
  }
  
  /**
   * Applications which override this can do custom serialization.
   *
   * @param object the object to write.
   */
  public void writeCustomObject(Object object)
    throws IOException
  {
    throw new IOException("unexpected object: " + object);
  }

  /**
   * Writes the list header to the stream.  List writers will call
   * <code>writeListBegin</code> followed by the list contents and then
   * call <code>writeListEnd</code>.
   *
   * <code><pre>
   * &lt;list>
   *   &lt;type>java.util.ArrayList&lt;/type>
   *   &lt;length>3&lt;/length>
   *   &lt;int>1&lt;/int>
   *   &lt;int>2&lt;/int>
   *   &lt;int>3&lt;/int>
   * &lt;/list>
   * </pre></code>
   */
  public void writeListBegin(int length, String type)
    throws IOException
  {
    os.write('V');
    os.write('t');
    printLenString(type);
    
    os.write('l');
    os.write(length >> 24);
    os.write(length >> 16);
    os.write(length >> 8);
    os.write(length);
  }

  /**
   * Writes the tail of the list to the stream.
   */
  public void writeListEnd()
    throws IOException
  {
    os.write('z');
  }

  /**
   * Writes the map header to the stream.  Map writers will call
   * <code>writeMapBegin</code> followed by the map contents and then
   * call <code>writeMapEnd</code>.
   *
   * <code><pre>
   * Mt b16 b8 type (<key> <value>)z
   * </pre></code>
   */
  public void writeMapBegin(String type)
    throws IOException
  {
    os.write('M');
    os.write('t');
    printLenString(type);
  }

  /**
   * Writes the tail of the map to the stream.
   */
  public void writeMapEnd()
    throws IOException
  {
    os.write('z');
  }

  /**
   * Writes a remote object reference to the stream.  The type is the
   * type of the remote interface.
   *
   * <code><pre>
   * 'r' 't' b16 b8 type url
   * </pre></code>
   */
  public void writeRemote(String type, String url)
    throws IOException
  {
    os.write('r');
    os.write('t');
    printLenString(type);
    os.write('S');
    printLenString(url);
  }

  /**
   * Prints a string to the stream, encoded as UTF-8 with preceeding length
   *
   * @param v the string to print.
   */
  public void printLenString(String v)
    throws IOException
  {
    if (v == null) {
      os.write(0);
      os.write(0);
    }
    else {
      int len = v.length();
      os.write(len >> 8);
      os.write(len);

      printString(v, 0, len);
    }
  }

  /**
   * Prints a string to the stream, encoded as UTF-8
   *
   * @param v the string to print.
   */
  public void printString(String v)
    throws IOException
  {
    printString(v, 0, v.length());
  }
  
  /**
   * Prints a string to the stream, encoded as UTF-8
   *
   * @param v the string to print.
   */
  public void printString(String v, int offset, int length)
    throws IOException
  {
    for (int i = 0; i < length; i++) {
      char ch = v.charAt(i + offset);

      if (ch < 0x80)
        os.write(ch);
      else if (ch < 0x800) {
        os.write(0xc0 + ((ch >> 6) & 0x1f));
        os.write(0x80 + (ch & 0x3f));
      }
      else {
        os.write(0xe0 + ((ch >> 12) & 0xf));
        os.write(0x80 + ((ch >> 6) & 0x3f));
        os.write(0x80 + (ch & 0x3f));
      }
    }
  }
}
