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
import java.io.InputStream;

/**
 * Input stream to a specific channel.
 */
public class MuxInputStream extends InputStream {
  private MuxServer server;
  protected InputStream is;
  private int channel;
  
  private String url;
  
  private int chunkLength;

  /**
   * Null argument constructor.
   */
  public MuxInputStream()
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

    this.url = null;

    chunkLength = 0;
  }

  /**
   * Gets the raw input stream.  Clients will normally not call
   * this.
   */
  protected InputStream getInputStream()
    throws IOException
  {
    if (is == null && server != null)
      is = server.readChannel(channel);
    
    return is;
  }

  void setInputStream(InputStream is)
  {
    this.is = is;
  }

  /**
   * Gets the channel of the connection.
   */
  public int getChannel()
  {
    return channel;
  }

  /**
   * Returns the request's URL
   */
  public String getURL()
  {
    return url;
  }

  /**
   * Writes a data byte to the output stream.
   */
  public int read()
    throws IOException
  {
    if (chunkLength <= 0) {
      readToData(false);

      if (chunkLength <= 0)
        return -1;
    }
    
    chunkLength--;
    return is.read();
  }

  /**
   * Complete writing to the stream, closing the channel.
   */
  public void close()
    throws IOException
  {
    skipToEnd();
  }

  /**
   * Skips data until the end of the channel.
   */
  private void skipToEnd()
    throws IOException
  {
    InputStream is = getInputStream();
    
    if (is == null)
      return;

    if (chunkLength > 0)
      is.skip(chunkLength);

    for (int tag = is.read(); tag >= 0; tag = is.read()) {
      switch (tag) {
      case 'Y':
        server.freeReadLock();
        this.is = is = server.readChannel(channel);
        if (is == null) {
          this.server = null;
          return;
        }
        break;
        
      case 'Q':
        server.freeReadLock();
        this.is = null;
        this.server = null;
        return;

      case -1:
        server.freeReadLock();
        this.is = null;
        this.server = null;
        return;
        
      default:
        int length = (is.read() << 8) + is.read();
        is.skip(length);
        break;
      }
    }
  }

  /**
   * Reads tags, until getting data.
   */
  void readToData(boolean returnOnYield)
    throws IOException
  {
    InputStream is = getInputStream();
    
    if (is == null)
      return;
    
    for (int tag = is.read(); tag >= 0; tag = is.read()) {
      switch (tag) {
      case 'Y':
        server.freeReadLock();
        if (returnOnYield)
          return;
        server.readChannel(channel);
        break;
        
      case 'Q':
        server.freeReadLock();
        this.is = null;
        this.server = null;
        return;
        
      case 'U':
        this.url = readUTF();
        break;

      case 'D':
        chunkLength = (is.read() << 8) + is.read();
        return;

      default:
        readTag(tag);
        break;
      }
    }
  }

  /**
   * Subclasses will extend this to read values.
   */
  protected void readTag(int tag)
    throws IOException
  {
    int length = (is.read() << 8) + is.read();
    is.skip(length);
  }

  /**
   * Reads a UTF-8 string.
   *
   * @return the utf-8 encoded string
   */
  protected String readUTF()
    throws IOException
  {
    int len = (is.read() << 8) + is.read();

    StringBuffer sb = new StringBuffer();

    while (len > 0) {
      int d1 = is.read();

      if (d1 < 0)
        return sb.toString();
      else if (d1 < 0x80) {
        len--;
        sb.append((char) d1);
      }
      else if ((d1 & 0xe0) == 0xc0) {
        len -= 2;
        sb.append(((d1 & 0x1f) << 6) + (is.read() & 0x3f));
      }
      else if ((d1 & 0xf0) == 0xe0) {
        len -= 3;
        sb.append(((d1 & 0x0f) << 12) +
                  ((is.read() & 0x3f) << 6) +
                  (is.read() & 0x3f));
      }
      else
        throw new IOException("utf-8 encoding error");
    }

    return sb.toString();
  }
}
