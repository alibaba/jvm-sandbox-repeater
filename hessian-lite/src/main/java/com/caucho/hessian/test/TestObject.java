package com.caucho.hessian.test;

/**
 * Empty object for short-encoding testing
 */
public class TestObject implements java.io.Serializable {
  private Object _value;

  public TestObject()
  {
  }

  public TestObject(Object value)
  {
    _value = value;
  }

  public Object getValue()
  {
    return _value;
  }

  public int hashCode()
  {
    if (_value != null)
      return _value.hashCode();
    else
      return 0;
  }

  public boolean equals(Object o)
  {
    if (! (o instanceof TestObject))
      return false;

    TestObject obj = (TestObject) o;

    if (_value != null)
      return _value.equals(obj._value);
    else
      return _value == obj._value;
  }
  
  public String toString()
  {
    return getClass().getName() + "[" + _value + "]";
  }
}
