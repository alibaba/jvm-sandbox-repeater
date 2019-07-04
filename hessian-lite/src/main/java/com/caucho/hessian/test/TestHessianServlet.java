package com.caucho.hessian.test;

import com.caucho.hessian.io.*;
import com.caucho.hessian.server.HessianServlet;
import java.io.*;
import java.util.*;

/**
 * The test service is a Hessian 2.0 protocol test for developers of
 * Hessian 2.0 clients.  For a new client the recommended order is:
 *
 * <ul>
 * <li>methodNull
 * <li>methodHello
 * </ul>
 */
public class TestHessianServlet
  extends HessianServlet
  implements Test
{
  private ThreadLocal<CharArrayWriter> _threadWriter
    = new ThreadLocal<CharArrayWriter>();
  
  /**
   * Does nothing.
   */
  public void nullCall()
  {
  }
  
  /**
   * Hello, World.
   */
  public String hello()
  {
    return "Hello, World";
  }
  
  /**
   * Subtraction
   */
  public int subtract(int a, int b)
  {
    return a - b;
  }
  
  /**
   * Echos the object to the server.
   * <pre>
   */
  public Object echo(Object value)
  {
    return value;
  }
  
  /**
   * Throws an application fault.
   */
  public void fault()
    throws IOException
  {
    throw new NullPointerException("sample exception");
  }
}
