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

package com.caucho.hessian.io;

import com.caucho.hessian.util.IdentityIntMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * Output stream for Hessian 2 requests.
 *
 * <p>Since HessianOutput does not depend on any classes other than
 * in the JDK, it can be extracted independently into a smaller package.
 *
 * <p>HessianOutput is unbuffered, so any client needs to provide
 * its own buffering.
 *
 * <pre>
 * OutputStream os = ...; // from http connection
 * Hessian2Output out = new Hessian2Output(os);
 * String value;
 *
 * out.startCall("hello", 1); // start hello call
 * out.writeString("arg1");   // write a string argument
 * out.completeCall();        // complete the call
 * </pre>
 */
public class Hessian2Output
  extends AbstractHessianOutput
  implements Hessian2Constants
{
  // should match Resin buffer size for perf
  public final static int SIZE = 8 * 1024;

  // the output stream/
  protected OutputStream _os;

  // map of references
  private final IdentityIntMap _refs
    = new IdentityIntMap(256);
  
  private int _refCount = 0;

  private boolean _isCloseStreamOnClose;

  // map of classes
  private final IdentityIntMap _classRefs
    = new IdentityIntMap(256);

  // map of types
  private HashMap<String,Integer> _typeRefs;

  private final byte []_buffer = new byte[SIZE];
  private int _offset;

  private boolean _isPacket;
  
  private boolean _isUnshared;

  /**
   * Creates a new Hessian output stream, initialized with an
   * underlying output stream.
   *
   * @param os the underlying output stream.
   */
  public Hessian2Output()
  {
  }

  /**
   * Creates a new Hessian output stream, initialized with an
   * underlying output stream.
   *
   * @param os the underlying output stream.
   */
  public Hessian2Output(OutputStream os)
  {
    init(os);
  }

  @Override
  public void init(OutputStream os)
  {
    reset();

    _os = os;
  }

  public void initPacket(OutputStream os)
  {
    resetReferences();

    _os = os;
  }

  public void setCloseStreamOnClose(boolean isClose)
  {
    _isCloseStreamOnClose = isClose;
  }

  public boolean isCloseStreamOnClose()
  {
    return _isCloseStreamOnClose;
  }
  
  /**
   * Sets hessian to be "unshared", meaning it will not detect 
   * duplicate or circular references.
   */
  @Override
  public boolean setUnshared(boolean isUnshared)
  {
    boolean oldIsUnshared = _isUnshared;
    
    _isUnshared = isUnshared;
    
    return oldIsUnshared;
  }

  /**
   * Writes a complete method call.
   */
  @Override
  public void call(String method, Object []args)
    throws IOException
  {
    writeVersion();

    int length = args != null ? args.length : 0;

    startCall(method, length);

    for (int i = 0; i < length; i++) {
      writeObject(args[i]);
    }

    completeCall();

    flush();
  }

  /**
   * Starts the method call.  Clients would use <code>startCall</code>
   * instead of <code>call</code> if they wanted finer control over
   * writing the arguments, or needed to write headers.
   *
   * <code><pre>
   * C
   * string # method name
   * int    # arg count
   * </pre></code>
   *
   * @param method the method name to call.
   */
  @Override
  public void startCall(String method, int length)
    throws IOException
  {
    int offset = _offset;

    if (SIZE < offset + 32) {
      flushBuffer();
      offset = _offset;
    }

    byte []buffer = _buffer;

    buffer[_offset++] = (byte) 'C';

    writeString(method);
    writeInt(length);
  }

  /**
   * Writes the call tag.  This would be followed by the
   * method and the arguments
   *
   * <code><pre>
   * C
   * </pre></code>
   *
   * @param method the method name to call.
   */
  @Override
  public void startCall()
    throws IOException
  {
    flushIfFull();

    _buffer[_offset++] = (byte) 'C';
  }

  /**
   * Starts an envelope.
   *
   * <code><pre>
   * E major minor
   * m b16 b8 method-name
   * </pre></code>
   *
   * @param method the method name to call.
   */
  public void startEnvelope(String method)
    throws IOException
  {
    int offset = _offset;

    if (SIZE < offset + 32) {
      flushBuffer();
      offset = _offset;
    }

    _buffer[_offset++] = (byte) 'E';

    writeString(method);
  }

  /**
   * Completes an envelope.
   *
   * <p>A successful completion will have a single value:
   *
   * <pre>
   * Z
   * </pre>
   */
  public void completeEnvelope()
    throws IOException
  {
    flushIfFull();

    _buffer[_offset++] = (byte) 'Z';
  }

  /**
   * Writes the method tag.
   *
   * <code><pre>
   * string
   * </pre></code>
   *
   * @param method the method name to call.
   */
  public void writeMethod(String method)
    throws IOException
  {
    writeString(method);
  }

  /**
   * Completes.
   *
   * <code><pre>
   * z
   * </pre></code>
   */
  @Override
  public void completeCall()
    throws IOException
  {
    /*
    flushIfFull();

    _buffer[_offset++] = (byte) 'Z';
    */
  }

  /**
   * Starts the reply
   *
   * <p>A successful completion will have a single value:
   *
   * <pre>
   * R
   * </pre>
   */
  @Override
  public void startReply()
    throws IOException
  {
    writeVersion();

    flushIfFull();

    _buffer[_offset++] = (byte) 'R';
  }

  public void writeVersion()
    throws IOException
  {
    flushIfFull();

    _buffer[_offset++] = (byte) 'H';
    _buffer[_offset++] = (byte) 2;
    _buffer[_offset++] = (byte) 0;
  }

  /**
   * Completes reading the reply
   *
   * <p>A successful completion will have a single value:
   *
   * <pre>
   * z
   * </pre>
   */
  @Override
  public void completeReply()
    throws IOException
  {
  }

  /**
   * Starts a packet
   *
   * <p>A message contains several objects encapsulated by a length</p>
   *
   * <pre>
   * p x02 x00
   * </pre>
   */
  public void startMessage()
    throws IOException
  {
    flushIfFull();

    _buffer[_offset++] = (byte) 'p';
    _buffer[_offset++] = (byte) 2;
    _buffer[_offset++] = (byte) 0;
  }

  /**
   * Completes reading the message
   *
   * <p>A successful completion will have a single value:
   *
   * <pre>
   * z
   * </pre>
   */
  public void completeMessage()
    throws IOException
  {
    flushIfFull();

    _buffer[_offset++] = (byte) 'z';
  }

  /**
   * Writes a fault.  The fault will be written
   * as a descriptive string followed by an object:
   *
   * <code><pre>
   * F map
   * </pre></code>
   *
   * <code><pre>
   * F H
   * \x04code
   * \x10the fault code
   *
   * \x07message
   * \x11the fault message
   *
   * \x06detail
   * M\xnnjavax.ejb.FinderException
   *     ...
   * Z
   * Z
   * </pre></code>
   *
   * @param code the fault code, a three digit
   */
  public void writeFault(String code, String message, Object detail)
    throws IOException
  {
    flushIfFull();

    writeVersion();

    _buffer[_offset++] = (byte) 'F';
    _buffer[_offset++] = (byte) 'H';

    addRef(new Object(), _refCount++, false);

    writeString("code");
    writeString(code);

    writeString("message");
    writeString(message);

    if (detail != null) {
      writeString("detail");
      writeObject(detail);
    }

    flushIfFull();
    _buffer[_offset++] = (byte) 'Z';
  }

  /**
   * Writes any object to the output stream.
   */
  @Override
  public void writeObject(Object object)
    throws IOException
  {
    if (object == null) {
      writeNull();
      return;
    }

    Serializer serializer
      = findSerializerFactory().getObjectSerializer(object.getClass());

    serializer.writeObject(object, this);
  }

  /**
   * Writes the list header to the stream.  List writers will call
   * <code>writeListBegin</code> followed by the list contents and then
   * call <code>writeListEnd</code>.
   *
   * <code><pre>
   * list ::= V type value* Z
   *      ::= v type int value*
   * </pre></code>
   *
   * @return true for variable lists, false for fixed lists
   */
  public boolean writeListBegin(int length, String type)
    throws IOException
  {
    flushIfFull();

    if (length < 0) {
      if (type != null) {
        _buffer[_offset++] = (byte) BC_LIST_VARIABLE;
        writeType(type);
      }
      else
        _buffer[_offset++] = (byte) BC_LIST_VARIABLE_UNTYPED;

      return true;
    }
    else if (length <= LIST_DIRECT_MAX) {
      if (type != null) {
        _buffer[_offset++] = (byte) (BC_LIST_DIRECT + length);
        writeType(type);
      }
      else {
        _buffer[_offset++] = (byte) (BC_LIST_DIRECT_UNTYPED + length);
      }

      return false;
    }
    else {
      if (type != null) {
        _buffer[_offset++] = (byte) BC_LIST_FIXED;
        writeType(type);
      }
      else {
        _buffer[_offset++] = (byte) BC_LIST_FIXED_UNTYPED;
      }

      writeInt(length);

      return false;
    }
  }

  /**
   * Writes the tail of the list to the stream for a variable-length list.
   */
  public void writeListEnd()
    throws IOException
  {
    flushIfFull();

    _buffer[_offset++] = (byte) BC_END;
  }

  /**
   * Writes the map header to the stream.  Map writers will call
   * <code>writeMapBegin</code> followed by the map contents and then
   * call <code>writeMapEnd</code>.
   *
   * <code><pre>
   * map ::= M type (<value> <value>)* Z
   *     ::= H (<value> <value>)* Z
   * </pre></code>
   */
  public void writeMapBegin(String type)
    throws IOException
  {
    if (SIZE < _offset + 32)
      flushBuffer();

    if (type != null) {
      _buffer[_offset++] = BC_MAP;

      writeType(type);
    }
    else
      _buffer[_offset++] = BC_MAP_UNTYPED;
  }

  /**
   * Writes the tail of the map to the stream.
   */
  public void writeMapEnd()
    throws IOException
  {
    if (SIZE < _offset + 32)
      flushBuffer();

    _buffer[_offset++] = (byte) BC_END;
  }

  /**
   * Writes the object definition
   *
   * <code><pre>
   * C &lt;string> &lt;int> &lt;string>*
   * </pre></code>
   */
  @Override
  public int writeObjectBegin(String type)
    throws IOException
  {
    int newRef = _classRefs.size();
    int ref = _classRefs.put(type, newRef, false);

    if (newRef != ref) {
      if (SIZE < _offset + 32)
        flushBuffer();

      if (ref <= OBJECT_DIRECT_MAX) {
        _buffer[_offset++] = (byte) (BC_OBJECT_DIRECT + ref);
      }
      else {
        _buffer[_offset++] = (byte) 'O';
        writeInt(ref);
      }

      return ref;
    }
    else {
      if (SIZE < _offset + 32)
        flushBuffer();

      _buffer[_offset++] = (byte) 'C';

      writeString(type);

      return -1;
    }
  }

  /**
   * Writes the tail of the class definition to the stream.
   */
  @Override
  public void writeClassFieldLength(int len)
    throws IOException
  {
    writeInt(len);
  }

  /**
   * Writes the tail of the object definition to the stream.
   */
  @Override
  public void writeObjectEnd()
    throws IOException
  {
  }

  /**
   * <code><pre>
   * type ::= string
   *      ::= int
   * </code></pre>
   */
  private void writeType(String type)
    throws IOException
  {
    flushIfFull();

    int len = type.length();
    if (len == 0) {
      throw new IllegalArgumentException("empty type is not allowed");
    }

    if (_typeRefs == null)
      _typeRefs = new HashMap<String,Integer>();

    Integer typeRefV = (Integer) _typeRefs.get(type);

    if (typeRefV != null) {
      int typeRef = typeRefV.intValue();

      writeInt(typeRef);
    }
    else {
      _typeRefs.put(type, Integer.valueOf(_typeRefs.size()));

      writeString(type);
    }
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
  @Override
  public void writeBoolean(boolean value)
    throws IOException
  {
    if (SIZE < _offset + 16)
      flushBuffer();

    if (value)
      _buffer[_offset++] = (byte) 'T';
    else
      _buffer[_offset++] = (byte) 'F';
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
  @Override
  public void writeInt(int value)
    throws IOException
  {
    int offset = _offset;
    byte []buffer = _buffer;

    if (SIZE <= offset + 16) {
      flushBuffer();
      offset = _offset;
    }

    if (INT_DIRECT_MIN <= value && value <= INT_DIRECT_MAX)
      buffer[offset++] = (byte) (value + BC_INT_ZERO);
    else if (INT_BYTE_MIN <= value && value <= INT_BYTE_MAX) {
      buffer[offset++] = (byte) (BC_INT_BYTE_ZERO + (value >> 8));
      buffer[offset++] = (byte) (value);
    }
    else if (INT_SHORT_MIN <= value && value <= INT_SHORT_MAX) {
      buffer[offset++] = (byte) (BC_INT_SHORT_ZERO + (value >> 16));
      buffer[offset++] = (byte) (value >> 8);
      buffer[offset++] = (byte) (value);
    }
    else {
      buffer[offset++] = (byte) ('I');
      buffer[offset++] = (byte) (value >> 24);
      buffer[offset++] = (byte) (value >> 16);
      buffer[offset++] = (byte) (value >> 8);
      buffer[offset++] = (byte) (value);
    }

    _offset = offset;
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
    int offset = _offset;
    byte []buffer = _buffer;

    if (SIZE <= offset + 16) {
      flushBuffer();
      offset = _offset;
    }

    if (LONG_DIRECT_MIN <= value && value <= LONG_DIRECT_MAX) {
      buffer[offset++] = (byte) (value + BC_LONG_ZERO);
    }
    else if (LONG_BYTE_MIN <= value && value <= LONG_BYTE_MAX) {
      buffer[offset++] = (byte) (BC_LONG_BYTE_ZERO + (value >> 8));
      buffer[offset++] = (byte) (value);
    }
    else if (LONG_SHORT_MIN <= value && value <= LONG_SHORT_MAX) {
      buffer[offset++] = (byte) (BC_LONG_SHORT_ZERO + (value >> 16));
      buffer[offset++] = (byte) (value >> 8);
      buffer[offset++] = (byte) (value);
    }
    else if (-0x80000000L <= value && value <= 0x7fffffffL) {
      buffer[offset + 0] = (byte) BC_LONG_INT;
      buffer[offset + 1] = (byte) (value >> 24);
      buffer[offset + 2] = (byte) (value >> 16);
      buffer[offset + 3] = (byte) (value >> 8);
      buffer[offset + 4] = (byte) (value);

      offset += 5;
    }
    else {
      buffer[offset + 0] = (byte) 'L';
      buffer[offset + 1] = (byte) (value >> 56);
      buffer[offset + 2] = (byte) (value >> 48);
      buffer[offset + 3] = (byte) (value >> 40);
      buffer[offset + 4] = (byte) (value >> 32);
      buffer[offset + 5] = (byte) (value >> 24);
      buffer[offset + 6] = (byte) (value >> 16);
      buffer[offset + 7] = (byte) (value >> 8);
      buffer[offset + 8] = (byte) (value);

      offset += 9;
    }

    _offset = offset;
  }

  /**
   * Writes a double value to the stream.  The double will be written
   * with the following syntax:
   *
   * <code><pre>
   * D b64 b56 b48 b40 b32 b24 b16 b8
   * </pre></code>
   *
   * @param value the double value to write.
   */
  public void writeDouble(double value)
    throws IOException
  {
    int offset = _offset;
    byte []buffer = _buffer;

    if (SIZE <= offset + 16) {
      flushBuffer();
      offset = _offset;
    }

    int intValue = (int) value;

    if (intValue == value) {
      if (intValue == 0) {
        buffer[offset++] = (byte) BC_DOUBLE_ZERO;

        _offset = offset;

        return;
      }
      else if (intValue == 1) {
        buffer[offset++] = (byte) BC_DOUBLE_ONE;

        _offset = offset;

        return;
      }
      else if (-0x80 <= intValue && intValue < 0x80) {
        buffer[offset++] = (byte) BC_DOUBLE_BYTE;
        buffer[offset++] = (byte) intValue;

        _offset = offset;

        return;
      }
      else if (-0x8000 <= intValue && intValue < 0x8000) {
        buffer[offset + 0] = (byte) BC_DOUBLE_SHORT;
        buffer[offset + 1] = (byte) (intValue >> 8);
        buffer[offset + 2] = (byte) intValue;

        _offset = offset + 3;

        return;
      }
    }

    int mills = (int) (value * 1000);

    if (0.001 * mills == value) {
      buffer[offset + 0] = (byte) (BC_DOUBLE_MILL);
      buffer[offset + 1] = (byte) (mills >> 24);
      buffer[offset + 2] = (byte) (mills >> 16);
      buffer[offset + 3] = (byte) (mills >> 8);
      buffer[offset + 4] = (byte) (mills);

      _offset = offset + 5;

      return;
    }

    long bits = Double.doubleToLongBits(value);

    buffer[offset + 0] = (byte) 'D';
    buffer[offset + 1] = (byte) (bits >> 56);
    buffer[offset + 2] = (byte) (bits >> 48);
    buffer[offset + 3] = (byte) (bits >> 40);
    buffer[offset + 4] = (byte) (bits >> 32);
    buffer[offset + 5] = (byte) (bits >> 24);
    buffer[offset + 6] = (byte) (bits >> 16);
    buffer[offset + 7] = (byte) (bits >> 8);
    buffer[offset + 8] = (byte) (bits);

    _offset = offset + 9;
  }

  /**
   * Writes a date to the stream.
   *
   * <code><pre>
   * date ::= d   b7 b6 b5 b4 b3 b2 b1 b0
   *      ::= x65 b3 b2 b1 b0
   * </pre></code>
   *
   * @param time the date in milliseconds from the epoch in UTC
   */
  public void writeUTCDate(long time)
    throws IOException
  {
    if (SIZE < _offset + 32)
      flushBuffer();

    int offset = _offset;
    byte []buffer = _buffer;

    if (time % 60000L == 0) {
      // compact date ::= x65 b3 b2 b1 b0

      long minutes = time / 60000L;

      if ((minutes >> 31) == 0 || (minutes >> 31) == -1) {
        buffer[offset++] = (byte) BC_DATE_MINUTE;
        buffer[offset++] = ((byte) (minutes >> 24));
        buffer[offset++] = ((byte) (minutes >> 16));
        buffer[offset++] = ((byte) (minutes >> 8));
        buffer[offset++] = ((byte) (minutes >> 0));

        _offset = offset;
        return;
      }
    }

    buffer[offset++] = (byte) BC_DATE;
    buffer[offset++] = ((byte) (time >> 56));
    buffer[offset++] = ((byte) (time >> 48));
    buffer[offset++] = ((byte) (time >> 40));
    buffer[offset++] = ((byte) (time >> 32));
    buffer[offset++] = ((byte) (time >> 24));
    buffer[offset++] = ((byte) (time >> 16));
    buffer[offset++] = ((byte) (time >> 8));
    buffer[offset++] = ((byte) (time));

    _offset = offset;
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
    int offset = _offset;
    byte []buffer = _buffer;

    if (SIZE <= offset + 16) {
      flushBuffer();
      offset = _offset;
    }

    buffer[offset++] = 'N';

    _offset = offset;
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
    int offset = _offset;
    byte []buffer = _buffer;

    if (SIZE <= offset + 16) {
      flushBuffer();
      offset = _offset;
    }

    if (value == null) {
      buffer[offset++] = (byte) 'N';

      _offset = offset;
    }
    else {
      int length = value.length();
      int strOffset = 0;

      while (length > 0x8000) {
        int sublen = 0x8000;

        offset = _offset;

        if (SIZE <= offset + 16) {
          flushBuffer();
          offset = _offset;
        }

        // chunk can't end in high surrogate
        char tail = value.charAt(strOffset + sublen - 1);

        if (0xd800 <= tail && tail <= 0xdbff)
          sublen--;

        buffer[offset + 0] = (byte) BC_STRING_CHUNK;
        buffer[offset + 1] = (byte) (sublen >> 8);
        buffer[offset + 2] = (byte) (sublen);

        _offset = offset + 3;

        printString(value, strOffset, sublen);

        length -= sublen;
        strOffset += sublen;
      }

      offset = _offset;

      if (SIZE <= offset + 16) {
        flushBuffer();
        offset = _offset;
      }

      if (length <= STRING_DIRECT_MAX) {
        buffer[offset++] = (byte) (BC_STRING_DIRECT + length);
      }
      else if (length <= STRING_SHORT_MAX) {
        buffer[offset++] = (byte) (BC_STRING_SHORT + (length >> 8));
        buffer[offset++] = (byte) (length);
      }
      else {
        buffer[offset++] = (byte) ('S');
        buffer[offset++] = (byte) (length >> 8);
        buffer[offset++] = (byte) (length);
      }

      _offset = offset;

      printString(value, strOffset, length);
    }
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
  public void writeString(char []buffer, int offset, int length)
    throws IOException
  {
    if (buffer == null) {
      if (SIZE < _offset + 16)
        flushBuffer();

      _buffer[_offset++] = (byte) ('N');
    }
    else {
      while (length > 0x8000) {
        int sublen = 0x8000;

        if (SIZE < _offset + 16)
          flushBuffer();

        // chunk can't end in high surrogate
        char tail = buffer[offset + sublen - 1];

        if (0xd800 <= tail && tail <= 0xdbff)
          sublen--;

        _buffer[_offset++] = (byte) BC_STRING_CHUNK;
        _buffer[_offset++] = (byte) (sublen >> 8);
        _buffer[_offset++] = (byte) (sublen);

        printString(buffer, offset, sublen);

        length -= sublen;
        offset += sublen;
      }

      if (SIZE < _offset + 16)
        flushBuffer();

      if (length <= STRING_DIRECT_MAX) {
        _buffer[_offset++] = (byte) (BC_STRING_DIRECT + length);
      }
      else if (length <= STRING_SHORT_MAX) {
        _buffer[_offset++] = (byte) (BC_STRING_SHORT + (length >> 8));
        _buffer[_offset++] = (byte) length;
      }
      else {
        _buffer[_offset++] = (byte) ('S');
        _buffer[_offset++] = (byte) (length >> 8);
        _buffer[_offset++] = (byte) (length);
      }

      printString(buffer, offset, length);
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
    if (buffer == null) {
      if (SIZE < _offset + 16)
        flushBuffer();

      _buffer[_offset++] = 'N';
    }
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
      if (SIZE < _offset + 16)
        flushBuffer();

      _buffer[_offset++] = (byte) 'N';
    }
    else {
      while (SIZE - _offset - 3 < length) {
        int sublen = SIZE - _offset - 3;

        if (sublen < 16) {
          flushBuffer();

          sublen = SIZE - _offset - 3;

          if (length < sublen)
            sublen = length;
        }

        _buffer[_offset++] = (byte) BC_BINARY_CHUNK;
        _buffer[_offset++] = (byte) (sublen >> 8);
        _buffer[_offset++] = (byte) sublen;

        System.arraycopy(buffer, offset, _buffer, _offset, sublen);
        _offset += sublen;

        length -= sublen;
        offset += sublen;

        flushBuffer();
      }

      if (SIZE < _offset + 16)
        flushBuffer();

      if (length <= BINARY_DIRECT_MAX) {
        _buffer[_offset++] = (byte) (BC_BINARY_DIRECT + length);
      }
      else if (length <= BINARY_SHORT_MAX) {
        _buffer[_offset++] = (byte) (BC_BINARY_SHORT + (length >> 8));
        _buffer[_offset++] = (byte) (length);
      }
      else {
        _buffer[_offset++] = (byte) 'B';
        _buffer[_offset++] = (byte) (length >> 8);
        _buffer[_offset++] = (byte) (length);
      }

      System.arraycopy(buffer, offset, _buffer, _offset, length);

      _offset += length;
    }
  }

  /**
   * Writes a byte buffer to the stream.
   *
   * <code><pre>
   * </pre></code>
   */
  public void writeByteBufferStart()
    throws IOException
  {
  }

  /**
   * Writes a byte buffer to the stream.
   *
   * <code><pre>
   * b b16 b18 bytes
   * </pre></code>
   */
  public void writeByteBufferPart(byte []buffer, int offset, int length)
    throws IOException
  {
    while (length > 0) {
      flushIfFull();

      int sublen = _buffer.length - _offset;

      if (length < sublen)
        sublen = length;

      _buffer[_offset++] = BC_BINARY_CHUNK;
      _buffer[_offset++] = (byte) (sublen >> 8);
      _buffer[_offset++] = (byte) sublen;

      System.arraycopy(buffer, offset, _buffer, _offset, sublen);

     _offset += sublen;
      length -= sublen;
      offset += sublen;
    }
  }

  /**
   * Writes a byte buffer to the stream.
   *
   * <code><pre>
   * b b16 b18 bytes
   * </pre></code>
   */
  public void writeByteBufferEnd(byte []buffer, int offset, int length)
    throws IOException
  {
    writeBytes(buffer, offset, length);
  }

  /**
   * Returns an output stream to write binary data.
   */
  public OutputStream getBytesOutputStream()
    throws IOException
  {
    return new BytesOutputStream();
  }

  /**
   * Writes a full output stream.
   */
  @Override
  public void writeByteStream(InputStream is)
    throws IOException
  {
    while (true) {
      int len = SIZE - _offset - 3;

      if (len < 16) {
        flushBuffer();
        len = SIZE - _offset - 3;
      }

      len = is.read(_buffer, _offset + 3, len);

      if (len <= 0) {
        _buffer[_offset++] = BC_BINARY_DIRECT;
        return;
      }

      _buffer[_offset + 0] = (byte) BC_BINARY_CHUNK;
      _buffer[_offset + 1] = (byte) (len >> 8);
      _buffer[_offset + 2] = (byte) (len);

      _offset += len + 3;
    }
  }

  /**
   * Writes a reference.
   *
   * <code><pre>
   * x51 &lt;int>
   * </pre></code>
   *
   * @param value the integer value to write.
   */
  @Override
  protected void writeRef(int value)
    throws IOException
  {
    if (SIZE < _offset + 16)
      flushBuffer();

    _buffer[_offset++] = (byte) BC_REF;

    writeInt(value);
  }

  /**
   * If the object has already been written, just write its ref.
   *
   * @return true if we're writing a ref.
   */
  @Override
  public boolean addRef(Object object)
    throws IOException
  {
    int ref = _refs.get(object);

    if (ref >= 0) {
      writeRef(ref);

      return true;
    } else {
      _refs.put(object, _refs.size(),false);

      return false;
    }
  }
  
  @Override
  public int getRef(Object obj)
  {
    if (_isUnshared)
      return -1;
    
    return _refs.get(obj);
  }

  /**
   * Removes a reference.
   */
  @Override
  public boolean removeRef(Object obj)
    throws IOException
  {
    if (_isUnshared) {
      return false;
    }
    else if (_refs != null) {
      _refs.remove(obj);

      return true;
    }
    else
      return false;
  }

  /**
   * Replaces a reference from one object to another.
   */
  @Override
  public boolean replaceRef(Object oldRef, Object newRef)
    throws IOException
  {
    if (_isUnshared) {
      return false;
    }
    
    int value = _refs.get(oldRef);

    if (value >= 0) {
      addRef(newRef, value, true);
      
      _refs.remove(oldRef);
      
      return true;
    }
    else
      return false;
  }
  
  private int addRef(Object value, int newRef, boolean isReplace)
  {
    int prevRef = _refs.put(value, newRef, isReplace);
    
    return prevRef;
  }

  /**
   * Starts the streaming message
   *
   * <p>A streaming message starts with 'P'</p>
   *
   * <pre>
   * P x02 x00
   * </pre>
   */
  public void writeStreamingObject(Object obj)
    throws IOException
  {
    startPacket();

    writeObject(obj);

    endPacket();
  }

  /**
   * Starts a streaming packet
   *
   * <p>A streaming contains a set of chunks, ending with a zero chunk.
   * Each chunk is a length followed by data where the length is
   * encoded by (b1xxxxxxxx)* b0xxxxxxxx</p>
   */
  public void startPacket()
    throws IOException
  {
    if (_refs != null) {
      _refs.clear();
      _refCount = 0;
    }

    flushBuffer();

    _isPacket = true;
    _offset = 4;
    _buffer[0] = (byte) 0x05; // 0x05 = binary
    _buffer[1] = (byte) 0x55;
    _buffer[2] = (byte) 0x55;
    _buffer[3] = (byte) 0x55;
  }

  public void endPacket()
    throws IOException
  {
    int offset = _offset;

    OutputStream os = _os;

    if (os == null) {
      _offset = 0;
      return;
    }

    int len = offset - 4;

    if (len < 0x7e) {
      _buffer[2] = _buffer[0];
      _buffer[3] = (byte) (len);
    } else {
      _buffer[1] = (byte) (0x7e);
      _buffer[2] = (byte) (len >> 8);
      _buffer[3] = (byte) (len);
    }

    _isPacket = false;
    _offset = 0;

    if (os == null) {
    }
    else if (len < 0x7e) {
      os.write(_buffer, 2, offset - 2);
    }
    else {
      os.write(_buffer, 0, offset);
    }
  }

  /**
   * Prints a string to the stream, encoded as UTF-8 with preceeding length
   *
   * @param v the string to print.
   */
  public void printLenString(String v)
    throws IOException
  {
    if (SIZE < _offset + 16)
      flushBuffer();

    if (v == null) {
      _buffer[_offset++] = (byte) (0);
      _buffer[_offset++] = (byte) (0);
    }
    else {
      int len = v.length();
      _buffer[_offset++] = (byte) (len >> 8);
      _buffer[_offset++] = (byte) (len);

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
  public void printString(String v, int strOffset, int length)
    throws IOException
  {
    int offset = _offset;
    byte []buffer = _buffer;

    for (int i = 0; i < length; i++) {
      if (SIZE <= offset + 16) {
        _offset = offset;
        flushBuffer();
        offset = _offset;
      }

      char ch = v.charAt(i + strOffset);

      if (ch < 0x80)
        buffer[offset++] = (byte) (ch);
      else if (ch < 0x800) {
        buffer[offset++] = (byte) (0xc0 + ((ch >> 6) & 0x1f));
        buffer[offset++] = (byte) (0x80 + (ch & 0x3f));
      }
      else {
        buffer[offset++] = (byte) (0xe0 + ((ch >> 12) & 0xf));
        buffer[offset++] = (byte) (0x80 + ((ch >> 6) & 0x3f));
        buffer[offset++] = (byte) (0x80 + (ch & 0x3f));
      }
    }

    _offset = offset;
  }

  /**
   * Prints a string to the stream, encoded as UTF-8
   *
   * @param v the string to print.
   */
  public void printString(char []v, int strOffset, int length)
    throws IOException
  {
    int offset = _offset;
    byte []buffer = _buffer;

    for (int i = 0; i < length; i++) {
      if (SIZE <= offset + 16) {
        _offset = offset;
        flushBuffer();
        offset = _offset;
      }

      char ch = v[i + strOffset];

      if (ch < 0x80)
        buffer[offset++] = (byte) (ch);
      else if (ch < 0x800) {
        buffer[offset++] = (byte) (0xc0 + ((ch >> 6) & 0x1f));
        buffer[offset++] = (byte) (0x80 + (ch & 0x3f));
      }
      else {
        buffer[offset++] = (byte) (0xe0 + ((ch >> 12) & 0xf));
        buffer[offset++] = (byte) (0x80 + ((ch >> 6) & 0x3f));
        buffer[offset++] = (byte) (0x80 + (ch & 0x3f));
      }
    }

    _offset = offset;
  }

  private final void flushIfFull()
    throws IOException
  {
    int offset = _offset;

    if (SIZE < offset + 32) {
      flushBuffer();
    }
  }

  public final void flush()
    throws IOException
  {
    flushBuffer();

    if (_os != null)
      _os.flush();
  }

  public final void flushBuffer()
    throws IOException
  {
    int offset = _offset;

    OutputStream os = _os;

    if (! _isPacket && offset > 0) {
      _offset = 0;
      if (os != null)
        os.write(_buffer, 0, offset);
    }
    else if (_isPacket && offset > 4) {
      int len = offset - 4;
      
      _buffer[0] |= (byte) 0x80;
      _buffer[1] = (byte) (0x7e);
      _buffer[2] = (byte) (len >> 8);
      _buffer[3] = (byte) (len);
      _offset = 4;

      if (os != null)
        os.write(_buffer, 0, offset);

      _buffer[0] = (byte) 0x00;
      _buffer[1] = (byte) 0x56;
      _buffer[2] = (byte) 0x56;
      _buffer[3] = (byte) 0x56;
    }
  }

  @Override
  public void close()
    throws IOException
  {
    // hessian/3a8c
    flush();

    OutputStream os = _os;
    _os = null;

    if (os != null) {
      if (_isCloseStreamOnClose)
        os.close();
    }
  }

  public void free()
  {
    reset();

    _os = null;
    _isCloseStreamOnClose = false;
  }

  /**
   * Resets the references for streaming.
   */
  @Override
  public void resetReferences()
  {
    if (_refs != null) {
      _refs.clear();
      _refCount = 0;
    }
  }

  /**
   * Resets all counters and references
   */
  public void reset()
  {
    if (_refs != null) {
      _refs.clear();
      _refCount = 0;
    }

    _classRefs.clear();
    _typeRefs = null;
    _offset = 0;
    _isPacket = false;
    _isUnshared = false;
  }

  class BytesOutputStream extends OutputStream {
    private int _startOffset;

    BytesOutputStream()
      throws IOException
    {
      if (SIZE < _offset + 16) {
        Hessian2Output.this.flushBuffer();
      }

      _startOffset = _offset;
      _offset += 3; // skip 'b' xNN xNN
    }

    @Override
    public void write(int ch)
      throws IOException
    {
      if (SIZE <= _offset) {
        int length = (_offset - _startOffset) - 3;

        _buffer[_startOffset] = (byte) BC_BINARY_CHUNK;
        _buffer[_startOffset + 1] = (byte) (length >> 8);
        _buffer[_startOffset + 2] = (byte) (length);

        Hessian2Output.this.flushBuffer();

        _startOffset = _offset;
        _offset += 3;
      }

      _buffer[_offset++] = (byte) ch;
    }

    @Override
    public void write(byte []buffer, int offset, int length)
      throws IOException
    {
      while (length > 0) {
        int sublen = SIZE - _offset;

        if (length < sublen)
          sublen = length;

        if (sublen > 0) {
          System.arraycopy(buffer, offset, _buffer, _offset, sublen);
          _offset += sublen;
        }

        length -= sublen;
        offset += sublen;

        if (SIZE <= _offset) {
          int chunkLength = (_offset - _startOffset) - 3;

          _buffer[_startOffset] = (byte) BC_BINARY_CHUNK;
          _buffer[_startOffset + 1] = (byte) (chunkLength >> 8);
          _buffer[_startOffset + 2] = (byte) (chunkLength);

          Hessian2Output.this.flushBuffer();

          _startOffset = _offset;
          _offset += 3;
        }
      }
    }

    @Override
    public void close()
      throws IOException
    {
      int startOffset = _startOffset;
      _startOffset = -1;

      if (startOffset < 0)
        return;

      int length = (_offset - startOffset) - 3;

      _buffer[startOffset] = (byte) 'B';
      _buffer[startOffset + 1] = (byte) (length >> 8);
      _buffer[startOffset + 2] = (byte) (length);

      Hessian2Output.this.flushBuffer();
    }
  }
}
