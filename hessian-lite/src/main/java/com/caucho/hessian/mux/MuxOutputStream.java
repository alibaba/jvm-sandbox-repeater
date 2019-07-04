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

package com.caucho.hessian.mux;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Output stream to a specific channel.
 */
public class MuxOutputStream extends OutputStream {
  private MuxServer server;
  private int channel;
  private OutputStream os;

  /**
   * Null argument constructor.
   */
  public MuxOutputStream()
  {
  }

  /**
   * Initialize the multiplexor with input and output streams.
   */
  protected void init(MuxServer server, int channel)
    throws IOException
  {
    this.server = server;
    this.channel = channel;
    this.os = null;
  }

  /**
   * Gets the raw output stream.  Clients will normally not call
   * this.
   */
  protected OutputStream getOutputStream()
    throws IOException
  {
    if (os == null && server != null)
      os = server.writeChannel(channel);
    
    return os;
  }

  /**
   * Gets the channel of the connection.
   */
  public int getChannel()
  {
    return channel;
  }

  /**
   * Writes a URL to the stream.
   */
  public void writeURL(String url)
    throws IOException
  {
    writeUTF('U', url);
  }
  
  /**
   * Writes a data byte to the output stream.
   */
  public void write(int ch)
    throws IOException
  {
    OutputStream os = getOutputStream();
    
    os.write('D');
    os.write(0);
    os.write(1);
    os.write(ch);
  }

  /**
   * Writes data to the output stream.
   */
  public void write(byte []buffer, int offset, int length)
    throws IOException
  {
    OutputStream os = getOutputStream();
    
    for (; length > 0x8000; length -= 0x8000) {
      os.write('D');
      os.write(0x80);
      os.write(0x00);
      os.write(buffer, offset, 0x8000);
      
      offset += 0x8000;
    }

    os.write('D');
    os.write(length >> 8);
    os.write(length);
    os.write(buffer, offset, length);
  }

  /**
   * Flush data to the output stream.
   */
  public void yield()
    throws IOException
  {
    OutputStream os = this.os;
    this.os = null;

    if (os != null)
      server.yield(channel);
  }

  /**
   * Flush data to the output stream.
   */
  public void flush()
    throws IOException
  {
    OutputStream os = this.os;
    this.os = null;

    if (os != null)
      server.flush(channel);
  }

  /**
   * Complete writing to the stream, closing the channel.
   */
  public void close()
    throws IOException
  {
    if (server != null) {
      OutputStream os = getOutputStream();
      this.os = null;
      
      MuxServer server = this.server;
      this.server = null;

      server.close(channel);
    }
  }

  /**
   * Writes a UTF-8 string.
   *
   * @param code the HMUX code identifying the string
   * @param string the string to write
   */
  protected void writeUTF(int code, String string)
    throws IOException
  {
    OutputStream os = getOutputStream();
    
    os.write(code);
    
    int charLength = string.length();

    int length = 0;
    for (int i = 0; i < charLength; i++) {
      char ch = string.charAt(i);

      if (ch < 0x80)
        length++;
      else if (ch < 0x800)
        length += 2;
      else
        length += 3;
    }
    
    os.write(length >> 8);
    os.write(length);
    
    for (int i = 0; i < length; i++) {
      char ch = string.charAt(i);

      if (ch < 0x80)
        os.write(ch);
      else if (ch < 0x800) {
        os.write(0xc0 + (ch >> 6) & 0x1f);
        os.write(0x80 + (ch & 0x3f));
      }
      else {
        os.write(0xe0 + (ch >> 12) & 0xf);
        os.write(0x80 + ((ch >> 6) & 0x3f));
        os.write(0x80 + (ch & 0x3f));
      }
    }
  }
}
