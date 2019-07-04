package com.caucho.hessian.test;

import java.util.HashMap;

/**
 * Cons-cell for testing
 */
public class TestCons implements java.io.Serializable {
  private Object _first;
  private Object _rest;

  public TestCons()
  {
  }

  public TestCons(Object first)
  {
    _first = first;
  }

  public TestCons(Object first, Object rest)
  {
    _first = first;
    _rest = rest;
  }

  public Object getFirst()
  {
    return _first;
  }

  public void setFirst(Object first)
  {
    _first = first;
  }

  public Object getRest()
  {
    return _rest;
  }

  public void setRest(Object rest)
  {
    _rest = rest;
  }

  public boolean equals(Object o)
  {
    return toString().equals(o.toString());
  }
  
  public String toString()
  {
    return toString(new HashMap());
  }
  
  public String toString(HashMap map)
  {
    Object ref = map.get(this);

    if (ref != null)
      return "#" + ref;

    map.put(this, map.size());

    StringBuilder sb = new StringBuilder();
    sb.append(getClass().getSimpleName()).append("[");

    if (_first instanceof TestCons)
      sb.append(((TestCons) _first).toString(map));
    else
      sb.append(_first);

    sb.append(",");

    if (_rest instanceof TestCons)
      sb.append(((TestCons) _rest).toString(map));
    else
      sb.append(_rest);

    sb.append("[");
    
    return sb.toString();
  }
}
