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

package com.caucho.burlap.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Input stream for Burlap requests, compatible with microedition
 * Java.  It only uses classes and types available to J2ME.  In
 * particular, it does not have any support for the &lt;double> type.
 *
 * <p>MicroBurlapInput does not depend on any classes other than
 * in J2ME, so it can be extracted independently into a smaller package.
 *
 * <p>MicroBurlapInput is unbuffered, so any client needs to provide
 * its own buffering.
 *
 * <pre>
 * InputStream is = ...; // from http connection
 * MicroBurlapInput in = new MicroBurlapInput(is);
 * String value;
 *
 * in.startReply();         // read reply header
 * value = in.readString(); // read string value
 * in.completeReply();      // read reply footer
 * </pre>
 */
public class MicroBurlapInput {
  private static int base64Decode[];
  
  private InputStream is;
  protected int peek;
  protected boolean peekTag;
  protected Date date;
  protected Calendar utcCalendar;
  private Calendar localCalendar;
  protected Vector refs;
  protected String method;
  protected StringBuffer sbuf = new StringBuffer();
  protected StringBuffer entity = new StringBuffer();

  /**
   * Creates a new Burlap input stream, initialized with an
   * underlying input stream.
   *
   * @param is the underlying input stream.
   */
  public MicroBurlapInput(InputStream is)
  {
    init(is);
  }

  /**
   * Creates an uninitialized Burlap input stream.
   */
  public MicroBurlapInput()
  {
  }

  /**
   * Returns a call's method.
   */
  public String getMethod()
  {
    return method;
  }

  /**
   * Initialize the Burlap input stream with a new underlying stream.
   * Applications can use <code>init(InputStream)</code> to reuse
   * MicroBurlapInput to save garbage collection.
   */
  public void init(InputStream is)
  {
    this.is = is;
    this.refs = null;
  }

  /**
   * Starts reading the call
   *
   * <p>A successful completion will have a single value:
   *
   * <pre>
   * &lt;burlap:call>
   * &lt;method>method&lt;/method>
   * </pre>
   */
  public void startCall()
    throws IOException
  {
    expectStartTag("burlap:call");
    expectStartTag("method");
    method = parseString();
    expectEndTag("method");
    this.refs = null;
  }

  /**
   * Completes reading the call.
   *
   * <pre>
   * &lt;/burlap:call>
   * </pre>
   */
  public void completeCall()
    throws IOException
  {
    expectEndTag("burlap:call");
  }

  /**
   * Reads a reply as an object.
   * If the reply has a fault, throws the exception.
   */
  public Object readReply(Class expectedClass)
    throws Exception
  {
    if (startReply()) {
      Object value = readObject(expectedClass);
      completeReply();
      return value;
    }
    else {
      Hashtable fault = readFault();

      Object detail = fault.get("detail");
      if (detail instanceof Exception)
        throw (Exception) detail;

      else {
        String code = (String) fault.get("code");
        String message = (String) fault.get("message");
        
        throw new BurlapServiceException(message, code, detail);
      }
    }
  }

  /**
   * Starts reading the reply.
   *
   * <p>A successful completion will have a single value.  An unsuccessful
   * one will have a fault:
   *
   * <pre>
   * &lt;burlap:reply>
   * </pre>
   *
   * @return true if success, false for fault.
   */
  public boolean startReply()
    throws IOException
  {
    this.refs = null;
    
    expectStartTag("burlap:reply");

    if (! parseTag())
      throw new BurlapProtocolException("expected <value>");

    String tag = sbuf.toString();
    if (tag.equals("fault")) {
      peekTag = true;
      return false;
    }
    else {
      peekTag = true;
      return true;
    }
  }

  /**
   * Completes reading the reply.
   *
   * <pre>
   * &lt;/burlap:reply>
   * </pre>
   */
  public void completeReply()
    throws IOException
  {
    expectEndTag("burlap:reply");
  }

  /**
   * Reads a boolean value from the input stream.
   */
  public boolean readBoolean()
    throws IOException
  {
    expectStartTag("boolean");

    int value = parseInt();
    
    expectEndTag("boolean");

    return value != 0;
  }

  /**
   * Reads an integer value from the input stream.
   */
  public int readInt()
    throws IOException
  {
    expectStartTag("int");

    int value = parseInt();
    
    expectEndTag("int");

    return value;
  }

  /**
   * Reads a long value from the input stream.
   */
  public long readLong()
    throws IOException
  {
    expectStartTag("long");

    long value = parseLong();
    
    expectEndTag("long");

    return value;
  }

  /**
   * Reads a date value from the input stream.
   */
  public long readUTCDate()
    throws IOException
  {
    expectStartTag("date");

    if (utcCalendar == null)
      utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

    long value = parseDate(utcCalendar);
    
    expectEndTag("date");

    return value;
  }

  /**
   * Reads a date value from the input stream.
   */
  public long readLocalDate()
    throws IOException
  {
    expectStartTag("date");

    if (localCalendar == null)
      localCalendar = Calendar.getInstance();

    long value = parseDate(localCalendar);
    
    expectEndTag("date");

    return value;
  }

  /**
   * Reads a remote value from the input stream.
   */
  public BurlapRemote readRemote()
    throws IOException
  {
    expectStartTag("remote");

    String type = readType();
    String url = readString();
    
    expectEndTag("remote");

    return new BurlapRemote(type, url);
  }

  /**
   * Reads a string value from the input stream.
   *
   * <p>The two valid possibilities are either a &lt;null>
   * or a &lt;string>.  The string value is encoded in utf-8, and
   * understands the basic XML escapes: "&123;", "&lt;", "&gt;",
   * "&apos;", "&quot;".
   *
   * <pre>
   * &lt;null>&lt;/null>
   * &lt;string>a utf-8 encoded string&lt;/string>
   * </pre>
   */
  public String readString()
    throws IOException
  {
    if (! parseTag())
      throw new BurlapProtocolException("expected <string>");

    String tag = sbuf.toString();
    if (tag.equals("null")) {
      expectEndTag("null");
      return null;
    }
    else if (tag.equals("string")) {
      sbuf.setLength(0);
      parseString(sbuf);
      String value = sbuf.toString();
      expectEndTag("string");
      return value;
    }
    else
      throw expectBeginTag("string", tag);
  }

  /**
   * Reads a byte array from the input stream.
   *
   * <p>The two valid possibilities are either a &lt;null>
   * or a &lt;base64>.
   */
  public byte []readBytes()
    throws IOException
  {
    if (! parseTag())
      throw new BurlapProtocolException("expected <base64>");

    String tag = sbuf.toString();
    if (tag.equals("null")) {
      expectEndTag("null");
      return null;
    }
    else if (tag.equals("base64")) {
      sbuf.setLength(0);
      byte []value = parseBytes();
      expectEndTag("base64");
      return value;
    }
    else
      throw expectBeginTag("base64", tag);
  }

  /**
   * Reads an arbitrary object the input stream.
   */
  public Object readObject(Class expectedClass)
    throws IOException
  {
    if (! parseTag())
      throw new BurlapProtocolException("expected <tag>");

    String tag = sbuf.toString();
    if (tag.equals("null")) {
      expectEndTag("null");
      return null;
    }
    else if (tag.equals("boolean")) {
      int value = parseInt();
      expectEndTag("boolean");
      return new Boolean(value != 0);
    }
    else if (tag.equals("int")) {
      int value = parseInt();
      expectEndTag("int");
      return new Integer(value);
    }
    else if (tag.equals("long")) {
      long value = parseLong();
      expectEndTag("long");
      return new Long(value);
    }
    else if (tag.equals("string")) {
      sbuf.setLength(0);
      parseString(sbuf);
      String value = sbuf.toString();
      expectEndTag("string");
      return value;
    }
    else if (tag.equals("xml")) {
      sbuf.setLength(0);
      parseString(sbuf);
      String value = sbuf.toString();
      expectEndTag("xml");
      return value;
    }
    else if (tag.equals("date")) {
      if (utcCalendar == null)
        utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
      
      long value = parseDate(utcCalendar);
      expectEndTag("date");
      return new Date(value);
    }
    else if (tag.equals("map")) {
      String type = readType();

      return readMap(expectedClass, type);
    }
    else if (tag.equals("list")) {
      String type = readType();
      
      int length = readLength();

      return readList(expectedClass, type, length);
    }
    else if (tag.equals("ref")) {
      int value = parseInt();
      expectEndTag("ref");

      return refs.elementAt(value);
    }
    else if (tag.equals("remote")) {
      String type = readType();
      String url = readString();

      expectEndTag("remote");

      return resolveRemote(type, url);
    }
    else
      return readExtensionObject(expectedClass, tag);
  }

  /**
   * Reads a type value from the input stream.
   *
   * <pre>
   * &lt;type>a utf-8 encoded string&lt;/type>
   * </pre>
   */
  public String readType()
    throws IOException
  {
    if (! parseTag())
      throw new BurlapProtocolException("expected <type>");

    String tag = sbuf.toString();
    if (! tag.equals("type"))
      throw new BurlapProtocolException("expected <type>");
    
    sbuf.setLength(0);
    parseString(sbuf);
    String value = sbuf.toString();
    expectEndTag("type");
    
    return value;
  }

  /**
   * Reads a length value from the input stream.  If the length isn't
   * specified, returns -1.
   *
   * <pre>
   * &lt;length>integer&lt;/length>
   * </pre>
   */
  public int readLength()
    throws IOException
  {
    expectStartTag("length");

    int ch = skipWhitespace();

    peek = ch;
    
    if (ch == '<') {
      expectEndTag("length");
      return -1;
    }

    int value = parseInt();
      
    expectEndTag("length");
    
    return value;
  }

  /**
   * Resolves a remote object.
   */
  public Object resolveRemote(String type, String url)
    throws IOException
  {
    return new BurlapRemote(type, url);
  }

  /**
   * Reads a fault.
   */
  public Hashtable readFault()
    throws IOException
  {
    expectStartTag("fault");
    
    Hashtable map = new Hashtable();

    while (parseTag()) {
      peekTag = true;
      Object key = readObject(null);
      Object value = readObject(null);

      if (key != null && value != null)
        map.put(key, value);
    }
    
    if (! sbuf.toString().equals("fault"))
      throw new BurlapProtocolException("expected </fault>");

    return map;
  }
  
  /**
   * Reads an object from the input stream.
   *
   * @param expectedClass the calling routine's expected class
   * @param type the type from the stream
   */
  public Object readMap(Class expectedClass, String type)
    throws IOException
  {
    Hashtable map = new Hashtable();
    if (refs == null)
      refs = new Vector();
    refs.addElement(map);

    while (parseTag()) {
      peekTag = true;
      Object key = readObject(null);
      Object value = readObject(null);

      map.put(key, value);
    }
    if (! sbuf.toString().equals("map"))
      throw new BurlapProtocolException("expected </map>");

    return map;
  }

  /**
   * Reads object unknown to MicroBurlapInput.
   */
  protected Object readExtensionObject(Class expectedClass, String tag)
    throws IOException
  {
    throw new BurlapProtocolException("unknown object tag <" + tag + ">");
  }
  
  /**
   * Reads a list object from the input stream.
   *
   * @param expectedClass the calling routine's expected class
   * @param type the type from the stream
   * @param length the expected length, -1 for unspecified length
   */
  public Object readList(Class expectedClass, String type, int length)
    throws IOException
  {
    Vector list = new Vector();
    if (refs == null)
      refs = new Vector();
    refs.addElement(list);

    while (parseTag()) {
      peekTag = true;
      Object value = readObject(null);

      list.addElement(value);
    }
    
    if (! sbuf.toString().equals("list"))
      throw new BurlapProtocolException("expected </list>");

    return list;
  }

  /**
   * Parses an integer value from the stream.
   */
  protected int parseInt()
    throws IOException
  {
    int sign = 1;
    int value = 0;

    int ch = skipWhitespace();
    if (ch == '+')
      ch = read();
    else if (ch == '-') {
      sign = -1;
      ch = read();
    }

    for (; ch >= '0' && ch <= '9'; ch = read())
      value = 10 * value + ch - '0';
    
    peek = ch;

    return sign * value;
  }

  /**
   * Parses a long value from the stream.
   */
  protected long parseLong()
    throws IOException
  {
    long sign = 1;
    long value = 0;

    int ch = skipWhitespace();
    if (ch == '+')
      ch = read();
    else if (ch == '-') {
      sign = -1;
      ch = read();
    }

    for (; ch >= '0' && ch <= '9'; ch = read()) {
      value = 10 * value + ch - '0';
    }

    peek = ch;

    return sign * value;
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

    for (; ch > 0 && ch != '<'; ch = read()) {
    }

    peek = ch;

    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.MONTH, month - 1);
    calendar.set(Calendar.DAY_OF_MONTH, day);
    calendar.set(Calendar.HOUR_OF_DAY, hour);
    calendar.set(Calendar.MINUTE, minute);
    calendar.set(Calendar.SECOND, second);
    calendar.set(Calendar.MILLISECOND, 0);

    return calendar.getTime().getTime();
  }

  /**
   * Parses a string value from the stream.
   * string buffer is used for the result.
   */
  protected String parseString()
    throws IOException
  {
    StringBuffer sbuf = new StringBuffer();

    return parseString(sbuf).toString();
  }
  
  /**
   * Parses a string value from the stream.  The burlap object's
   * string buffer is used for the result.
   */
  protected StringBuffer parseString(StringBuffer sbuf)
    throws IOException
  {
    int ch = read();

    for (; ch >= 0 && ch != '<'; ch = read()) {
      if (ch == '&') {
        ch = read();

        if (ch == '#') {
          ch = read();

          if (ch >= '0' && ch <= '9') {
            int v = 0;
            for (; ch >= '0' && ch <= '9'; ch = read()) {
              v = 10 * v + ch - '0';
            }

            sbuf.append((char) v);
          }
        }
        else {
          StringBuffer entityBuffer = new StringBuffer();

          for (; ch >= 'a' && ch <= 'z'; ch = read())
            entityBuffer.append((char) ch);

          String entity = entityBuffer.toString();
          if (entity.equals("amp"))
            sbuf.append('&');
          else if (entity.equals("apos"))
            sbuf.append('\'');
          else if (entity.equals("quot"))
            sbuf.append('"');
          else if (entity.equals("lt"))
            sbuf.append('<');
          else if (entity.equals("gt"))
            sbuf.append('>');
          else
            throw new BurlapProtocolException("unknown XML entity &" + entity + "; at `" + (char) ch + "'");
        }

        if (ch != ';')
          throw expectedChar("';'", ch);
      }
      else if (ch < 0x80)
        sbuf.append((char) ch);
      else if ((ch & 0xe0) == 0xc0) {
        int ch1 = read();
        int v = ((ch & 0x1f) << 6) + (ch1 & 0x3f);

        sbuf.append((char) v);
      }
      else if ((ch & 0xf0) == 0xe0) {
        int ch1 = read();
        int ch2 = read();
        int v = ((ch & 0x0f) << 12) + ((ch1 & 0x3f) << 6) + (ch2 & 0x3f);

        sbuf.append((char) v);
      }
      else
        throw new BurlapProtocolException("bad utf-8 encoding");
    }

    peek = ch;

    return sbuf;
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
    for (ch = read(); ch >= 0 && ch != '<'; ch = read()) {
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
        int chunk = ((base64Decode[b1] << 12) +
                     (base64Decode[b2] << 6) +
                     (base64Decode[b3]));

        bos.write(chunk >> 8);
        bos.write(chunk);
      }
      else {
        int chunk = ((base64Decode[b1] << 6) +
                     (base64Decode[b2]));

        bos.write(chunk);
      }
    }

    if (ch == '<')
      peek = ch;
    
    return bos;
  }

  protected void expectStartTag(String tag)
    throws IOException
  {
    if (! parseTag())
      throw new BurlapProtocolException("expected <" + tag + ">");

    if (! sbuf.toString().equals(tag))
      throw new BurlapProtocolException("expected <" + tag + "> at <" + sbuf + ">");
  }

  protected void expectEndTag(String tag)
    throws IOException
  {
    if (parseTag())
      throw new BurlapProtocolException("expected </" + tag + ">");

    if (! sbuf.toString().equals(tag))
      throw new BurlapProtocolException("expected </" + tag + "> at </" + sbuf + ">");
  }

  /**
   * Parses a tag.  Returns true if it's a start tag.
   */
  protected boolean parseTag()
    throws IOException
  {
    if (peekTag) {
      peekTag = false;
      return true;
    }
    
    int ch = skipWhitespace();
    boolean isStartTag = true;

    if (ch != '<')
      throw expectedChar("'<'", ch);

    ch = read();
    if (ch == '/') {
      isStartTag = false;
      ch = is.read();
    }
    
    if (! isTagChar(ch))
      throw expectedChar("tag", ch);
      
    sbuf.setLength(0);
    for (; isTagChar(ch); ch = read())
      sbuf.append((char) ch);

    if (ch != '>')
      throw expectedChar("'>'", ch);

    return isStartTag;
  }

  protected IOException expectedChar(String expect, int actualChar)
  {
    return new BurlapProtocolException("expected " + expect + " at " +
                           (char) actualChar + "'");
  }

  protected IOException expectBeginTag(String expect, String tag)
  {
    return new BurlapProtocolException("expected <" + expect + "> at <" + tag + ">");
  }

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

  protected int read()
    throws IOException
  {
    if (peek > 0) {
      int value = peek;
      peek = 0;
      return value;
    }
    
    return is.read();
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
}
