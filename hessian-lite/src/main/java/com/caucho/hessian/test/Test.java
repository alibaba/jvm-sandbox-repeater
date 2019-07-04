package com.caucho.hessian.test;

import java.io.IOException;

/**
 * The Test service is a quick sanity check service.  Developers of a
 * new Hessian implementation can use this service as an initial test.
 */
public interface Test {
  /**
   * Does nothing.
   */
  public void nullCall();
  
  /**
   * Hello, World.
   */
  public String hello();
  
  /**
   * Subtraction
   */
  public int subtract(int a, int b);
  
  /**
   * Echos the object to the server.
   * <pre>
   */
  public Object echo(Object value);
  
  /**
   * Throws an application fault.
   */
  public void fault()
    throws IOException;
}
