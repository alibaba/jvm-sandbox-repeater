package com.caucho.hessian.test;

import java.io.IOException;

/**
 * Empty object for short-encoding testing
 */
public class A10 implements java.io.Serializable {
  public boolean equals(Object v)
  {
    return v != null && getClass().equals(v.getClass());
  }
  
  public String toString()
  {
    return getClass().getName() + "[]";
  }
}
