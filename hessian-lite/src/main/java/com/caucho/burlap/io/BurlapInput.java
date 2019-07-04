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

package com.caucho.burlap.io;

import com.caucho.hessian.io.Deserializer;
import com.caucho.hessian.io.HessianRemoteResolver;
import com.caucho.hessian.io.SerializerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Input stream for Burlap requests.
 *
 * <p>BurlapInput is unbuffered, so any client needs to provide
 * its own buffering.
 *
 * <pre>
 * InputStream is = ...; // from http connection
 * BurlapInput in = new BurlapInput(is);
 * String value;
 *
 * in.startReply();         // read reply header
 * value = in.readString(); // read string value
 * in.completeReply();      // read reply footer
 * </pre>
 */
public class BurlapInput extends AbstractBurlapInput {
  private static int []base64Decode;
  
  public final static int TAG_EOF = -1;
  
  public final static int TAG_NULL = 0;
  public final static int TAG_BOOLEAN = 1;
  public final static int TAG_INT = 2;
  public final static int TAG_LONG = 3;
  public final static int TAG_DOUBLE = 4;
  public final static int TAG_DATE = 5;
  public final static int TAG_STRING = 6;
  public final static int TAG_XML = 7;
  public final static int TAG_BASE64 = 8;
  public final static int TAG_MAP = 9;
  public final static int TAG_LIST = 10;
  public final static int TAG_TYPE = 11;
  public final static int TAG_LENGTH = 12;
  
  public final static int TAG_REF = 13;
  public final static int TAG_REMOTE = 14;
  
  public final static int TAG_CALL = 15;
  public final static int TAG_REPLY = 16;
  public final static int TAG_FAULT = 17;
  public final static int TAG_METHOD = 18;
  public final static int TAG_HEADER = 19;
  
  public final static int TAG_NULL_END = TAG_NULL + 100;
  public final static int TAG_BOOLEAN_END = TAG_BOOLEAN + 100;
  public final static int TAG_INT_END = TAG_INT + 100;
  public final static int TAG_LONG_END = TAG_LONG + 100;
  public final static int TAG_DOUBLE_END = TAG_DOUBLE + 100;
  public final static int TAG_DATE_END = TAG_DATE + 100;
  public final static int TAG_STRING_END = TAG_STRING + 100;
  public final static int TAG_XML_END = TAG_XML + 100;
  public final static int TAG_BASE64_END = TAG_BASE64 + 100;
  public final static int TAG_MAP_END = TAG_MAP + 100;
  public final static int TAG_LIST_END = TAG_LIST + 100;
  public final static int TAG_TYPE_END = TAG_TYPE + 100;
  public final static int TAG_LENGTH_END = TAG_LENGTH + 100;
  
  public final static int TAG_REF_END = TAG_REF + 100;
  public final static int TAG_REMOTE_END = TAG_REMOTE + 100;
  
  public final static int TAG_CALL_END = TAG_CALL + 100;
  public final static int TAG_REPLY_END = TAG_REPLY + 100;
  public final static int TAG_FAULT_END = TAG_FAULT + 100;
  public final static int TAG_METHOD_END = TAG_METHOD + 100;
  public final static int TAG_HEADER_END = TAG_HEADER + 100;

  private static HashMap _tagMap;

  private static Field _detailMessageField;
  
  protected SerializerFactory _serializerFactory;
  
  protected ArrayList _refs;
  
  // the underlying input stream
  private InputStream _is;
  // a peek character
  protected int _peek = -1;
  
  // the method for a call
  private String _method;

  private int _peekTag;

  private Throwable _replyFault;

  protected StringBuffer _sbuf = new StringBuffer();
  protected StringBuffer _entityBuffer = new StringBuffer();
  
  protected Calendar _utcCalendar;
  protected Calendar _localCalendar;

  /**
   * Creates an uninitialized Burlap input stream.
   */
  public BurlapInput()
  {
  }
  
  /**
   * Creates a new Burlap input stream, initialized with an
   * underlying input stream.
   *
   * @param is the underlying input stream.
   */
  public BurlapInput(InputStream is)
  {
    init(is);
  }

  /**
   * Sets the serializer factory.
   */
  public void setSerializerFactory(SerializerFactory factory)
  {
    _serializerFactory = factory;
  }

  /**
   * Gets the serializer factory.
   */
  public SerializerFactory getSerializerFactory()
  {
    return _serializerFactory;
  }

  /**
   * Initialize the burlap stream with the underlying input stream.
   */
  public void init(InputStream is)
  {
    _is = is;
    _method = null;
    _peek = -1;
    _peekTag = -1;
    _refs = null;
    _replyFault = null;

    if (_serializerFactory == null)
      _serializerFactory = new SerializerFactory();
  }

  /**
   * Returns the calls method
   */
  public String getMethod()
  {
    return _method;
  }

  /**
   * Returns any reply fault.
   */
  public Throwable getReplyFault()
  {
    return _replyFault;
  }

  /**
   * Starts reading the call
   *
   * <pre>
   * &lt;burlap:call>
   * &lt;method>method&lt;/method>
   * </pre>
   */
  public void startCall()
    throws IOException
  {
    readCall();

    while ((readHeader() != null))
      readObject();

    readMethod();
  }

  /**
   * Starts reading the call
   *
   * <p>A successful completion will have a single value:
   *
   * <pre>
   * &lt;burlap:call>
   * </pre>
   */
  public int readCall()
    throws IOException
  {
    expectTag(TAG_CALL);

    int major = 1;
    int minor = 0;

    return (major << 16) + minor;
  }

  /**
   * Reads the method
   *
   * <pre>
   * &lt;method>method&lt;/method>
   * </pre>
   */
  public String readMethod()
    throws IOException
  {
    expectTag(TAG_METHOD);

    _method = parseString();

    expectTag(TAG_METHOD_END);

    return _method;
  }

  /**
   * Completes reading the call
   *
   * <p>A successful completion will have a single value:
   *
   * <pre>
   * &lt;/burlap:call>
   * </pre>
   */
  public void completeCall()
    throws IOException
  {
    expectTag(TAG_CALL_END);
  }

  /**
   * Reads a reply as an object.
   * If the reply has a fault, throws the exception.
   */
  public Object readReply(Class expectedClass)
    throws Throwable
  {
    expectTag(TAG_REPLY);

    int tag = parseTag();

    if (tag == TAG_FAULT)
      throw prepareFault();
    else {
      _peekTag = tag;
      Object value = readObject(expectedClass);

      expectTag(TAG_REPLY_END);
      
      return value;
    }
  }

  /**
   * Starts reading the reply
   *
   * <p>A successful completion will have a single value:
   *
   * <pre>
   * &lt;burlap:reply>
   * &lt;value>
   * </pre>
   */
  public void startReply()
    throws Throwable
  {
    expectTag(TAG_REPLY);
    
    int tag = parseTag();
    if (tag == TAG_FAULT)
      throw prepareFault();
    else
      _peekTag = tag;
  }

  /**
   * Prepares the fault.
   */
  private Throwable prepareFault()
    throws IOException
  {
    HashMap fault = readFault();

    Object detail = fault.get("detail");
    String message = (String) fault.get("message");

    if (detail instanceof Throwable) {
      _replyFault = (Throwable) detail;
      
      if (message != null && _detailMessageField != null) {
        try {
          _detailMessageField.set(_replyFault, message);
        } catch (Throwable e) {
        }
      }

      return _replyFault;
    }

    else {
      String code = (String) fault.get("code");
        
      _replyFault = new BurlapServiceException(message, code, detail);

      return _replyFault;
    }
  }

  /**
   * Completes reading the call
   *
   * <p>A successful completion will have a single value:
   *
   * <pre>
   * &lt;/burlap:reply>
   * </pre>
   */
  public void completeReply()
    throws IOException
  {
    expectTag(TAG_REPLY_END);
  }

  /**
   * Reads a header, returning null if there are no headers.
   *
   * <pre>
   * &lt;header>value&lt;/header>
   * </pre>
   */
  public String readHeader()
    throws IOException
  {
    int tag = parseTag();

    if (tag == TAG_HEADER) {
      _sbuf.setLength(0);
      String value = parseString(_sbuf).toString();
      expectTag(TAG_HEADER_END);
      return value;
    }

    _peekTag = tag;

    return null;
  }

  /**
   * Reads a null
   *
   * <pre>
   * &lt;null>&lt;/null>
   * </pre>
   */
  public void readNull()
    throws IOException
  {
    int tag = parseTag();

    switch (tag) {
    case TAG_NULL:
      expectTag(TAG_NULL_END);
      return;
      
    default:
      throw expectedTag("null", tag);
    }
  }

  /**
   * Reads a boolean
   *
   * <pre>
   * &lt;boolean>0&lt;/boolean>
   * &lt;boolean>1&lt;/boolean>
   * </pre>
   */
  public boolean readBoolean()
    throws IOException
  {
    int tag = parseTag();

    boolean value;

    switch (tag) {
    case TAG_NULL:
      value = false;
      expectTag(TAG_NULL_END);
      return value;

    case TAG_BOOLEAN:
      value = parseInt() != 0;
      expectTag(TAG_BOOLEAN_END);
      return value;
      
    case TAG_INT:
      value = parseInt() != 0;
      expectTag(TAG_INT_END);
      return value;
      
    case TAG_LONG:
      value = parseLong() != 0;
      expectTag(TAG_LONG_END);
      return value;
      
    case TAG_DOUBLE:
      value = parseDouble() != 0;
      expectTag(TAG_DOUBLE_END);
      return value;
      
    default:
      throw expectedTag("boolean", tag);
    }
  }

  /**
   * Reads a byte
   *
   * <pre>
   * &lt;int>value&lt;/int>
   * </pre>
   */
  public byte readByte()
    throws IOException
  {
    return (byte) readInt();
  }

  /**
   * Reads a short
   *
   * <pre>
   * &lt;int>value&lt;/int>
   * </pre>
   */
  public short readShort()
    throws IOException
  {
    return (short) readInt();
  }

  /**
   * Reads an integer
   *
   * <pre>
   * &lt;int>value&lt;/int>
   * </pre>
   */
  public int readInt()
    throws IOException
  {
    int tag = parseTag();

    int value;

    switch (tag) {
    case TAG_NULL:
      value = 0;
      expectTag(TAG_NULL_END);
      return value;
      
    case TAG_BOOLEAN:
      value = parseInt();
      expectTag(TAG_BOOLEAN_END);
      return value;
      
    case TAG_INT:
      value = parseInt();
      expectTag(TAG_INT_END);
      return value;
      
    case TAG_LONG:
      value = (int) parseLong();
      expectTag(TAG_LONG_END);
      return value;
      
    case TAG_DOUBLE:
      value = (int) parseDouble();
      expectTag(TAG_DOUBLE_END);
      return value;
      
    default:
      throw expectedTag("int", tag);
    }
  }

  /**
   * Reads a long
   *
   * <pre>
   * &lt;long>value&lt;/long>
   * </pre>
   */
  public long readLong()
    throws IOException
  {
    int tag = parseTag();

    long value;

    switch (tag) {
    case TAG_NULL:
      value = 0;
      expectTag(TAG_NULL_END);
      return value;
      
    case TAG_BOOLEAN:
      value = parseInt();
      expectTag(TAG_BOOLEAN_END);
      return value;
      
    case TAG_INT:
      value = parseInt();
      expectTag(TAG_INT_END);
      return value;
      
    case TAG_LONG:
      value = parseLong();
      expectTag(TAG_LONG_END);
      return value;
      
    case TAG_DOUBLE:
      value = (long) parseDouble();
      expectTag(TAG_DOUBLE_END);
      return value;
      
    default:
      throw expectedTag("long", tag);
    }
  }

  /**
   * Reads a float
   *
   * <pre>
   * &lt;double>value&lt;/double>
   * </pre>
   */
  public float readFloat()
    throws IOException
  {
    return (float) readDouble();
  }

  /**
   * Reads a double
   *
   * <pre>
   * &lt;double>value&lt;/double>
   * </pre>
   */
  public double readDouble()
    throws IOException
  {
    int tag = parseTag();

    double value;

    switch (tag) {
    case TAG_NULL:
      value = 0;
      expectTag(TAG_NULL_END);
      return value;
      
    case TAG_BOOLEAN:
      value = parseInt();
      expectTag(TAG_BOOLEAN_END);
      return value;
      
    case TAG_INT:
      value = parseInt();
      expectTag(TAG_INT_END);
      return value;
      
    case TAG_LONG:
      value = parseLong();
      expectTag(TAG_LONG_END);
      return value;
      
    case TAG_DOUBLE:
      value = parseDouble();
      expectTag(TAG_DOUBLE_END);
      return value;
      
    default:
      throw expectedTag("double", tag);
    }
  }

  /**
   * Reads a date.
   *
   * <pre>
   * &lt;date>ISO-8609 date&lt;/date>
   * </pre>
   */
  public long readUTCDate()
    throws IOException
  {
    int tag = parseTag();

    if (tag != TAG_DATE)
      throw error("expected date");

    if (_utcCalendar == null)
      _utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

    long value = parseDate(_utcCalendar);

    expectTag(TAG_DATE_END);

    return value;
  }

  /**
   * Reads a date.
   *
   * <pre>
   * &lt;date>ISO-8609 date&lt;/date>
   * </pre>
   */
  public long readLocalDate()
    throws IOException
  {
    int tag = parseTag();

    if (tag != TAG_DATE)
      throw error("expected date");

    if (_localCalendar == null)
      _localCalendar = Calendar.getInstance();

    long value = parseDate(_localCalendar);

    expectTag(TAG_DATE_END);

    return value;
  }

  /**
   * Reads a string
   *
   * <pre>
   * &lt;string>value&lt;/string>
   * </pre>
   */
  public String readString()
    throws IOException
  {
    int tag = parseTag();

    String value;
    
    switch (tag) {
    case TAG_NULL:
      expectTag(TAG_NULL_END);
      return null;

    case TAG_STRING:
      _sbuf.setLength(0);
      value = parseString(_sbuf).toString();
      expectTag(TAG_STRING_END);
      return value;

    case TAG_XML:
      _sbuf.setLength(0);
      value = parseString(_sbuf).toString();
      expectTag(TAG_XML_END);
      return value;

    default:
      throw expectedTag("string", tag);
    }
  }

  /**
   * Reads an XML node.
   *
   * <pre>
   * &xml;xml string&lt;/xml>
   * </pre>
   */
  public org.w3c.dom.Node readNode()
    throws IOException
  {
    int tag = read();

    switch (tag) {
    case 'N':
      return null;

    case 'S':
    case 's':
    case 'X':
    case 'x':
      throw error("can't cope");

    default:
      throw expectedTag("string", tag);
    }
  }

  /**
   * Reads a byte array
   *
   * <pre>
   * &lt;base64>...&lt;/base64>
   * </pre>
   */
  public byte []readBytes()
    throws IOException
  {
    int tag = parseTag();

    switch (tag) {
    case TAG_NULL:
      expectTag(TAG_NULL_END);
      return null;

    case TAG_BASE64:
      byte []data = parseBytes();
      expectTag(TAG_BASE64_END);

      return data;
      
    default:
      throw expectedTag("bytes", tag);
    }
  }

  /**
   * Reads a length
   *
   * <pre>
   * &lt;length>value&lt;/length>
   * </pre>
   */
  public int readLength()
    throws IOException
  {
    int tag = parseTag();

    if (tag != TAG_LENGTH) {
      _peekTag = tag;
      return -1;
    }

    int value = parseInt();

    expectTag(TAG_LENGTH_END);

    return value;
  }

  /**
   * Reads a fault.
   */
  private HashMap readFault()
    throws IOException
  {
    HashMap map = new HashMap();

    int code = parseTag();
    for (; code >= 0 && code != TAG_FAULT_END; code = parseTag()) {
      _peekTag = code;
      
      Object key = readObject();
      Object value = readObject();

      if (key != null && value != null)
        map.put(key, value);
    }

    if (code != TAG_FAULT_END)
      throw expectedTag("fault", code);

    return map;
  }

  /**
   * Reads an object from the input stream with an expected type.
   */
  public Object readObject(Class cl)
    throws IOException
  {
    if (cl == null || cl.equals(Object.class))
      return readObject();
    
    int tag = parseTag();

    switch (tag) {
    case TAG_NULL:
      expectTag(TAG_NULL_END);
      return null;

    case TAG_MAP:
    {
      String type = readType();
      Deserializer reader;
      
      reader = _serializerFactory.getObjectDeserializer(type, cl);

      return reader.readMap(this);
    }

    case TAG_LIST:
    {
      String type = readType();
      int length = readLength();
      
      Deserializer reader;
      reader = _serializerFactory.getObjectDeserializer(type, cl);

      return reader.readList(this, length);
    }

    case TAG_REF:
    {
      int ref = parseInt();

      expectTag(TAG_REF_END);

      return _refs.get(ref);
    }

    case TAG_REMOTE:
    {
      String type = readType();
      String url = readString();

      expectTag(TAG_REMOTE_END);

      Object remote = resolveRemote(type, url);
      
      return remote;
    }
    }
    
    _peekTag = tag;

    Object value = _serializerFactory.getDeserializer(cl).readObject(this);

    return value;
  }
  
  /**
   * Reads an arbitrary object from the input stream when the type
   * is unknown.
   */
  public Object readObject()
    throws IOException
  {
    int tag = parseTag();

    switch (tag) {
    case TAG_NULL:
      expectTag(TAG_NULL_END);
      return null;
      
    case TAG_BOOLEAN:
    {
      int value = parseInt();
      expectTag(TAG_BOOLEAN_END);
      return new Boolean(value != 0);
    }
    
    case TAG_INT:
    {
      int value = parseInt();
      expectTag(TAG_INT_END);
      return new Integer(value);
    }
    
    case TAG_LONG:
    {
      long value = parseLong();
      expectTag(TAG_LONG_END);
      return new Long(value);
    }
    
    case TAG_DOUBLE:
    {
      double value = parseDouble();
      expectTag(TAG_DOUBLE_END);
      return new Double(value);
    }
    
    case TAG_DATE:
    {
      long value = parseDate();
      expectTag(TAG_DATE_END);
      return new Date(value);
    }
    
    case TAG_XML:
    {
      return parseXML();
    }

    case TAG_STRING:
    {
      _sbuf.setLength(0);

      String value = parseString(_sbuf).toString();

      expectTag(TAG_STRING_END);

      return value;
    }

    case TAG_BASE64:
    {
      byte []data = parseBytes();

      expectTag(TAG_BASE64_END);

      return data;
    }

    case TAG_LIST:
    {
      String type = readType();
      int length = readLength();

      return _serializerFactory.readList(this, length, type);
    }

    case TAG_MAP:
    {
      String type = readType();
      Deserializer deserializer;
      deserializer = _serializerFactory.getObjectDeserializer(type);

      return deserializer.readMap(this);
    }

    case TAG_REF:
    {
      int ref = parseInt();

      expectTag(TAG_REF_END);

      return _refs.get(ref);
    }

    case TAG_REMOTE:
    {
      String type = readType();
      String url = readString();

      expectTag(TAG_REMOTE_END);

      return resolveRemote(type, url);
    }

    default:
      throw error("unknown code:" + tagName(tag));
    }
  }

  /**
   * Reads a remote object.
   */
  public Object readRemote()
    throws IOException
  {
    String type = readType();
    String url = readString();

    return resolveRemote(type, url);
  }

  /**
   * Reads a reference.
   */
  public Object readRef()
    throws IOException
  {
    return _refs.get(parseInt());
  }

  /**
   * Reads the start of a list.
   */
  public int readListStart()
    throws IOException
  {
    return parseTag();
  }

  /**
   * Reads the start of a map.
   */
  public int readMapStart()
    throws IOException
  {
    return parseTag();
  }

  /**
   * Returns true if this is the end of a list or a map.
   */
  public boolean isEnd()
    throws IOException
  {
    int code = parseTag();

    _peekTag = code;

    return (code < 0 || code >= 100);
  }

  /**
   * Reads the end byte.
   */
  public void readEnd()
    throws IOException
  {
    int code = parseTag();

    if (code < 100)
      throw error("unknown code:" + (char) code);
  }

  /**
   * Reads the end of the map
   */
  public void readMapEnd()
    throws IOException
  {
    expectTag(TAG_MAP_END);
  }

  /**
   * Reads the end of the map
   */
  public void readListEnd()
    throws IOException
  {
    expectTag(TAG_LIST_END);
  }

  /**
   * Adds a list/map reference.
   */
  public int addRef(Object ref)
  {
    if (_refs == null)
      _refs = new ArrayList();
    
    _refs.add(ref);

    return _refs.size() - 1;
  }

  /**
   * Adds a list/map reference.
   */
  public void setRef(int i, Object ref)
  {
    _refs.set(i, ref);
  }

  /**
   * Resolves a remote object.
   */
  public Object resolveRemote(String type, String url)
    throws IOException
  {
    HessianRemoteResolver resolver = getRemoteResolver();

    if (resolver != null)
      return resolver.lookup(type, url);
    else
      return new BurlapRemote(type, url);
  }

  /**
   * Parses a type from the stream.
   *
   * <pre>
   * &lt;type>type&lt;/type>
   * </pre>
   */
  public String readType()
    throws IOException
  {
    int code = parseTag();

    if (code != TAG_TYPE) {
      _peekTag = code;
      return "";
    }

    _sbuf.setLength(0);
    int ch;
    while ((ch = readChar()) >= 0)
      _sbuf.append((char) ch);
    String type = _sbuf.toString();
    
    expectTag(TAG_TYPE_END);

    return type;
  }

  /**
   * Parses a 32-bit integer value from the stream.
   */
  private int parseInt()
    throws IOException
  {
    int sign = 1;

    int ch = read();
    if (ch == '-') {
      sign = -1;
      ch = read();
    }

    int value = 0;
    for (; ch >= '0' && ch <= '9'; ch = read())
      value = 10 * value + ch - '0';

    _peek = ch;

    return sign * value;
  }

  /**
   * Parses a 64-bit long value from the stream.
   */
  private long parseLong()
    throws IOException
  {
    int sign = 1;

    int ch = read();
    if (ch == '-') {
      sign = -1;
      ch = read();
    }

    long value = 0;
    for (; ch >= '0' && ch <= '9'; ch = read())
      value = 10 * value + ch - '0';

    _peek = ch;

    return sign * value;
  }
  
  /**
   * Parses a 64-bit double value from the stream.
   *
   * <pre>
   * b64 b56 b48 b40 b32 b24 b16 b8
   * </pre>
   */
  private double parseDouble()
    throws IOException
  {
    int ch = skipWhitespace();

    _sbuf.setLength(0);
    
    for (; ! isWhitespace(ch) && ch != '<'; ch = read())
      _sbuf.append((char) ch);

    _peek = ch;
    
    return new Double(_sbuf.toString()).doubleValue();
  }

  /**
   * Parses a date value from the stream.
   */
  protected long parseDate()
    throws IOException
  {
    if (_utcCalendar == null)
      _utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

    return parseDate(_utcCalendar);
  }

  /**
   * Parses a date value from the stream.
   */
  protected long parseDate(Calendar calendar)
    throws IOException
  {
    int ch = skipWhitespace();
    
    int year = 0;
    for (int i = 0; i < 4; i++) {
      if (ch >= '0' && ch <= '9')
        year = 10 * year + ch - '0';
      else
        throw expectedChar("year", ch);

      ch = read();
    }

    int month = 0;
    for (int i = 0; i < 2; i++) {
      if (ch >= '0' && ch <= '9')
        month = 10 * month + ch - '0';
      else
        throw expectedChar("month", ch);

      ch = read();
    }

    int day = 0;
    for (int i = 0; i < 2; i++) {
      if (ch >= '0' && ch <= '9')
        day = 10 * day + ch - '0';
      else
        throw expectedChar("day", ch);

      ch = read();
    }

    if (ch != 'T')
      throw expectedChar("`T'", ch);

    ch = read();

    int hour = 0;
    for (int i = 0; i < 2; i++) {
      if (ch >= '0' && ch <= '9')
        hour = 10 * hour + ch - '0';
      else
        throw expectedChar("hour", ch);

      ch = read();
    }

    int minute = 0;
    for (int i = 0; i < 2; i++) {
      if (ch >= '0' && ch <= '9')
        minute = 10 * minute + ch - '0';
      else
        throw expectedChar("minute", ch);

      ch = read();
    }

    int second = 0;
    for (int i = 0; i < 2; i++) {
      if (ch >= '0' && ch <= '9')
        second = 10 * second + ch - '0';
      else
        throw expectedChar("second", ch);

      ch = read();
    }

    int ms = 0;
    if (ch == '.') {
      ch = read();

      while (ch >= '0' && ch <= '9') {
        ms = 10 * ms + ch - '0';

        ch = read();
      }
    }

    for (; ch > 0 && ch != '<'; ch = read()) {
    }

    _peek = ch;

    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.MONTH, month - 1);
    calendar.set(Calendar.DAY_OF_MONTH, day);
    calendar.set(Calendar.HOUR_OF_DAY, hour);
    calendar.set(Calendar.MINUTE, minute);
    calendar.set(Calendar.SECOND, second);
    calendar.set(Calendar.MILLISECOND, ms);

    return calendar.getTime().getTime();
  }
  
  protected String parseString()
    throws IOException
  {
    _sbuf.setLength(0);

    return parseString(_sbuf).toString();
  }
  
  /**
   * Parses a string value from the stream.  The burlap object's
   * string buffer is used for the result.
   */
  protected StringBuffer parseString(StringBuffer sbuf)
    throws IOException
  {
    int ch;

    while ((ch = readChar()) >= 0)
      sbuf.append((char) ch);

    return sbuf;
  }

  org.w3c.dom.Node parseXML()
    throws IOException
  {
    throw error("help!");
  }
  
  /**
   * Reads a character from the underlying stream.
   */
  int readChar()
    throws IOException
  {
    int ch = read();

    if (ch == '<' || ch < 0) {
      _peek = ch;
      return -1;
    }
    
    if (ch == '&') {
      ch = read();

      if (ch == '#') {
        ch = read();

        if (ch >= '0' && ch <= '9') {
          int v = 0;
          for (; ch >= '0' && ch <= '9'; ch = read()) {
            v = 10 * v + ch - '0';
          }

          if (ch != ';')
            throw error("expected ';' at " + (char) ch);

          return (char) v;
        }
        else
          throw error("expected digit at " + (char) ch);
      }
      else {
        _entityBuffer.setLength(0);

        for (; ch >= 'a' && ch <= 'z'; ch = read())
          _entityBuffer.append((char) ch);

        String entity = _entityBuffer.toString();

        if (ch != ';')
          throw expectedChar("';'", ch);
        
        if (entity.equals("amp"))
          return '&';
        else if (entity.equals("apos"))
          return '\'';
        else if (entity.equals("quot"))
          return '"';
        else if (entity.equals("lt"))
          return '<';
        else if (entity.equals("gt"))
          return '>';
        else
          throw new BurlapProtocolException("unknown XML entity &" + entity + "; at `" + (char) ch + "'");
      }
    }
    else if (ch < 0x80)
      return (char) ch;
    else if ((ch & 0xe0) == 0xc0) {
      int ch1 = read();
      int v = ((ch & 0x1f) << 6) + (ch1 & 0x3f);

      return (char) v;
    }
    else if ((ch & 0xf0) == 0xe0) {
      int ch1 = read();
      int ch2 = read();
      int v = ((ch & 0x0f) << 12) + ((ch1 & 0x3f) << 6) + (ch2 & 0x3f);

      return (char) v;
    }
    else
      throw new BurlapProtocolException("bad utf-8 encoding");
  }
  
  /**
   * Parses a byte array.
   */
  protected byte []parseBytes()
    throws IOException
  {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    parseBytes(bos);

    return bos.toByteArray();
  }
  
  /**
   * Parses a byte array.
   */
  protected ByteArrayOutputStream parseBytes(ByteArrayOutputStream bos)
    throws IOException
  {
    int ch;
    for (ch = skipWhitespace(); ch >= 0 && ch != '<'; ch = skipWhitespace()) {
      int b1 = ch;
      int b2 = read();
      int b3 = read();
      int b4 = read();

      if (b4 != '=') {
        int chunk = ((base64Decode[b1] << 18) +
                     (base64Decode[b2] << 12) +
                     (base64Decode[b3] << 6) +
                     (base64Decode[b4]));

        bos.write(chunk >> 16);
        bos.write(chunk >> 8);
        bos.write(chunk);
      }
      else if (b3 != '=') {
        int chunk = ((base64Decode[b1] << 10) +
                     (base64Decode[b2] << 4) +
                     (base64Decode[b3] >> 2));

        bos.write(chunk >> 8);
        bos.write(chunk);
      }
      else {
        int chunk = ((base64Decode[b1] << 2) +
                     (base64Decode[b2] >> 4));

        bos.write(chunk);
      }
    }

    if (ch == '<')
      _peek = ch;
    
    return bos;
  }
  
  public void expectTag(int expectTag)
    throws IOException
  {
    int tag = parseTag();

    if (tag != expectTag)
      throw error("expected " + tagName(expectTag) + " at " + tagName(tag));
  }

  /**
   * Parses a tag.  Returns true if it's a start tag.
   */
  protected int parseTag()
    throws IOException
  {
    if (_peekTag >= 0) {
      int tag = _peekTag;
      _peekTag = -1;
      return tag;
    }
    
    int ch = skipWhitespace();
    int endTagDelta = 0;

    if (ch != '<')
      throw expectedChar("'<'", ch);

    ch = read();
    if (ch == '/') {
      endTagDelta = 100;
      ch = _is.read();
    }
    
    if (! isTagChar(ch))
      throw expectedChar("tag", ch);
      
    _sbuf.setLength(0);
    for (; isTagChar(ch); ch = read())
      _sbuf.append((char) ch);

    if (ch != '>')
      throw expectedChar("'>'", ch);

    Integer value = (Integer) _tagMap.get(_sbuf.toString());
    if (value == null)
      throw error("Unknown tag <" + _sbuf + ">");

    return value.intValue() + endTagDelta;
  }

  /**
   * Returns true if the character is a valid tag character.
   */
  private boolean isTagChar(int ch)
  {
    return (ch >= 'a' && ch <= 'z' ||
            ch >= 'A' && ch <= 'Z' ||
            ch >= '0' && ch <= '9' ||
            ch == ':' || ch == '-');
  }

  protected int skipWhitespace()
    throws IOException
  {
    int ch = read();

    for (;
         ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r';
         ch = read()) {
    }

    return ch;
  }

  protected boolean isWhitespace(int ch)
    throws IOException
  {
    return ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r';
  }
  
  /**
   * Reads bytes from the underlying stream.
   */
  int read(byte []buffer, int offset, int length)
    throws IOException
  {
    throw new UnsupportedOperationException();
  }

  int read()
    throws IOException
  {
    if (_peek >= 0) {
      int value = _peek;
      _peek = -1;
      return value;
    }

    int ch = _is.read();
    return ch;
  }

  public Reader getReader()
  {
    return null;
  }

  public InputStream readInputStream()
  {
    return null;
  }

  public InputStream getInputStream()
  {
    return null;
  }

  protected IOException expectBeginTag(String expect, String tag)
  {
    return new BurlapProtocolException("expected <" + expect + "> at <" + tag + ">");
  }
  
  protected IOException expectedChar(String expect, int ch)
  {
    if (ch < 0)
      return error("expected " + expect + " at end of file");
    else
      return error("expected " + expect + " at " + (char) ch);
  }
  
  protected IOException expectedTag(String expect, int tag)
  {
    return error("expected " + expect + " at " + tagName(tag));
  }
  
  protected IOException error(String message)
  {
    return new BurlapProtocolException(message);
  }

  protected static String tagName(int tag)
  {
    switch (tag) {
    case TAG_NULL:
      return "<null>";
    case TAG_NULL_END:
      return "</null>";
      
    case TAG_BOOLEAN:
      return "<boolean>";
    case TAG_BOOLEAN_END:
      return "</boolean>";
      
    case TAG_INT:
      return "<int>";
    case TAG_INT_END:
      return "</int>";
      
    case TAG_LONG:
      return "<long>";
    case TAG_LONG_END:
      return "</long>";
      
    case TAG_DOUBLE:
      return "<double>";
    case TAG_DOUBLE_END:
      return "</double>";
      
    case TAG_STRING:
      return "<string>";
    case TAG_STRING_END:
      return "</string>";
      
    case TAG_XML:
      return "<xml>";
    case TAG_XML_END:
      return "</xml>";
      
    case TAG_BASE64:
      return "<base64>";
    case TAG_BASE64_END:
      return "</base64>";
      
    case TAG_MAP:
      return "<map>";
    case TAG_MAP_END:
      return "</map>";
      
    case TAG_LIST:
      return "<list>";
    case TAG_LIST_END:
      return "</list>";
      
    case TAG_TYPE:
      return "<type>";
    case TAG_TYPE_END:
      return "</type>";
      
    case TAG_LENGTH:
      return "<length>";
    case TAG_LENGTH_END:
      return "</length>";
      
    case TAG_REF:
      return "<ref>";
    case TAG_REF_END:
      return "</ref>";
      
    case TAG_REMOTE:
      return "<remote>";
    case TAG_REMOTE_END:
      return "</remote>";
      
    case TAG_CALL:
      return "<burlap:call>";
    case TAG_CALL_END:
      return "</burlap:call>";
      
    case TAG_REPLY:
      return "<burlap:reply>";
    case TAG_REPLY_END:
      return "</burlap:reply>";
      
    case TAG_HEADER:
      return "<header>";
    case TAG_HEADER_END:
      return "</header>";
      
    case TAG_FAULT:
      return "<fault>";
    case TAG_FAULT_END:
      return "</fault>";

    case -1:
      return "end of file";

    default:
      return "unknown " + tag;
    }
  }
      

  static {
    _tagMap = new HashMap();
    _tagMap.put("null", new Integer(TAG_NULL));
    
    _tagMap.put("boolean", new Integer(TAG_BOOLEAN));
    _tagMap.put("int", new Integer(TAG_INT));
    _tagMap.put("long", new Integer(TAG_LONG));
    _tagMap.put("double", new Integer(TAG_DOUBLE));
    
    _tagMap.put("date", new Integer(TAG_DATE));
    
    _tagMap.put("string", new Integer(TAG_STRING));
    _tagMap.put("xml", new Integer(TAG_XML));
    _tagMap.put("base64", new Integer(TAG_BASE64));
    
    _tagMap.put("map", new Integer(TAG_MAP));
    _tagMap.put("list", new Integer(TAG_LIST));
    
    _tagMap.put("type", new Integer(TAG_TYPE));
    _tagMap.put("length", new Integer(TAG_LENGTH));
    
    _tagMap.put("ref", new Integer(TAG_REF));
    _tagMap.put("remote", new Integer(TAG_REMOTE));
    
    _tagMap.put("burlap:call", new Integer(TAG_CALL));
    _tagMap.put("burlap:reply", new Integer(TAG_REPLY));
    _tagMap.put("fault", new Integer(TAG_FAULT));
    _tagMap.put("method", new Integer(TAG_METHOD));
    _tagMap.put("header", new Integer(TAG_HEADER));
  }
  
  static {
    base64Decode = new int[256];
    for (int i = 'A'; i <= 'Z'; i++)
      base64Decode[i] = i - 'A';
    for (int i = 'a'; i <= 'z'; i++)
      base64Decode[i] = i - 'a' + 26;
    for (int i = '0'; i <= '9'; i++)
      base64Decode[i] = i - '0' + 52;
    base64Decode['+'] = 62;
    base64Decode['/'] = 63;
  }

  static {
    try {
      _detailMessageField = Throwable.class.getDeclaredField("detailMessage");
      _detailMessageField.setAccessible(true);
    } catch (Throwable e) {
    }
  }
}
