package com.caucho.hessian.test;

import com.caucho.hessian.io.HessianDebugInputStream;
import com.caucho.hessian.io.SerializerFactory;
import com.caucho.hessian.server.HessianServlet;

import java.io.CharArrayWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
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
public class TestHessian2Servlet
  extends HessianServlet
  implements TestHessian2
{
  private ThreadLocal<CharArrayWriter> _threadWriter
    = new ThreadLocal<CharArrayWriter>();
  
  public void methodNull()
  {
  }

  //
  // reply tests, testing serialization output
  //

  public void replyNull()
  {
  }

  //
  // boolean
  //

  public Object replyTrue()
  {
    return true;
  }

  public Object replyFalse()
  {
    return false;
  }

  //
  // integers
  //
  
  // single byte integers

  public int replyInt_0()
  {
    return 0;
  }

  public int replyInt_1()
  {
    return 1;
  }

  public int replyInt_47()
  {
    return 47;
  }

  public int replyInt_m16()
  {
    return -16;
  }

  // two byte integers

  public int replyInt_0x30()
  {
    return 0x30;
  }

  public int replyInt_0x7ff()
  {
    return 0x7ff;
  }

  public int replyInt_m17()
  {
    return -17;
  }

  public int replyInt_m0x800()
  {
    return -0x800;
  }

  // three byte integers

  public int replyInt_0x800()
  {
    return 0x800;
  }

  public int replyInt_0x3ffff()
  {
    return 0x3ffff;
  }

  public int replyInt_m0x801()
  {
    return -0x801;
  }
  
  public int replyInt_m0x40000()
  {
    return - 0x40000;
  }

  // 5 byte integers

  public int replyInt_0x40000()
  {
    return 0x40000;
  }

  public int replyInt_0x7fffffff()
  {
    return 0x7fffffff;
  }

  public int replyInt_m0x40001()
  {
    return - 0x40001;
  }

  public int replyInt_m0x80000000()
  {
    return - 0x80000000;
  }

  //
  // longs
  //
  
  // single byte longs

  public long replyLong_0()
  {
    return 0;
  }

  public long replyLong_1()
  {
    return 1;
  }

  public long replyLong_15()
  {
    return 15;
  }

  public long replyLong_m8()
  {
    return -8;
  }

  // two byte longs

  public long replyLong_0x10()
  {
    return 0x10;
  }

  public long replyLong_0x7ff()
  {
    return 0x7ff;
  }

  public long replyLong_m9()
  {
    return -9;
  }

  public long replyLong_m0x800()
  {
    return -0x800;
  }

  // three byte longs

  public long replyLong_0x800()
  {
    return 0x800;
  }

  public long replyLong_0x3ffff()
  {
    return 0x3ffff;
  }

  public long replyLong_m0x801()
  {
    return -0x801;
  }
  
  public long replyLong_m0x40000()
  {
    return - 0x40000;
  }

  // 5 byte longs

  public long replyLong_0x40000()
  {
    return 0x40000;
  }

  public long replyLong_0x7fffffff()
  {
    return 0x7fffffff;
  }

  public long replyLong_m0x40001()
  {
    return - 0x40001;
  }

  public long replyLong_m0x80000000()
  {
    return - 0x80000000;
  }

  public long replyLong_0x80000000()
  {
    return 0x80000000L;
  }

  public long replyLong_m0x80000001()
  {
    return - 0x80000001L;
  }

  //
  // doubles
  //

  public double replyDouble_0_0()
  {
    return 0;
  }

  public double replyDouble_1_0()
  {
    return 1;
  }

  public double replyDouble_2_0()
  {
    return 2;
  }
  
  public double replyDouble_127_0()
  {
    return 127;
  }

  public double replyDouble_m128_0()
  {
    return -128;
  }

  public double replyDouble_128_0()
  {
    return 128;
  }

  public double replyDouble_m129_0()
  {
    return -129;
  }

  public double replyDouble_32767_0()
  {
    return 32767;
  }

  public double replyDouble_m32768_0()
  {
    return -32768;
  }

  public double replyDouble_0_001()
  {
    return 0.001;
  }

  public double replyDouble_m0_001()
  {
    return - 0.001;
  }

  public double replyDouble_65_536()
  {
    return 65.536;
  }

  public double replyDouble_3_14159()
  {
    return 3.14159;
  }

  // date

  public Object replyDate_0()
  {
    return new Date(0);
  }

  public Object replyDate_1()
  {
    long time = 894621091000L;

    return new Date(time);
  }

  public Object replyDate_2()
  {
    long time = 894621091000L;

    time -= time % 60000L;
    
    return new Date(time);
  }

  // strings by length

  public String replyString_0()
  {
    return "";
  }

  public String replyString_null()
  {
    return null;
  }

  public String replyString_1()
  {
    return "0";
  }

  public String replyString_31()
  {
    return "0123456789012345678901234567890";
  }

  public String replyString_32()
  {
    return "01234567890123456789012345678901";
  }
  
  public String replyString_1023()
  {
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < 16; i++) {
      sb.append("" + (i / 10) + (i % 10) + " 456789012345678901234567890123456789012345678901234567890123\n");
    }

    sb.setLength(1023);

    return sb.toString();
  }
  
  public String replyString_1024()
  {
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < 16; i++) {
      sb.append("" + (i / 10) + (i % 10) + " 456789012345678901234567890123456789012345678901234567890123\n");
    }

    sb.setLength(1024);

    return sb.toString();
  }
  
  public String replyString_65536()
  {
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < 64 * 16; i++) {
      sb.append("" + (i / 100) + (i / 10 % 10) + (i % 10) + " 56789012345678901234567890123456789012345678901234567890123\n");
    }

    sb.setLength(65536);

    return sb.toString();
  }

  // binarys by length

  public Object replyBinary_0()
  {
    return new byte[0];
  }

  public Object replyBinary_null()
  {
    return null;
  }

  public Object replyBinary_1()
  {
    return toBinary("0");
  }

  public Object replyBinary_15()
  {
    return toBinary("012345678901234");
  }

  public Object replyBinary_16()
  {
    return toBinary("0123456789012345");
  }
  
  public Object replyBinary_1023()
  {
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < 16; i++) {
      sb.append("" + (i / 10) + (i % 10) + " 456789012345678901234567890123456789012345678901234567890123\n");
    }

    sb.setLength(1023);

    return toBinary(sb.toString());
  }
  
  public Object replyBinary_1024()
  {
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < 16; i++) {
      sb.append("" + (i / 10) + (i % 10) + " 456789012345678901234567890123456789012345678901234567890123\n");
    }

    sb.setLength(1024);

    return toBinary(sb.toString());
  }
  
  public Object replyBinary_65536()
  {
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < 64 * 16; i++) {
      sb.append("" + (i / 100) + (i / 10 % 10) + (i % 10) + " 56789012345678901234567890123456789012345678901234567890123\n");
    }

    sb.setLength(65536);

    return toBinary(sb.toString());
  }

  private byte []toBinary(String s)
  {
    byte []buffer = new byte[s.length()];

    for (int i = 0; i < s.length(); i++)
      buffer[i] = (byte) s.charAt(i);

    return buffer;
  }

  //
  // lists
  //

  public Object replyUntypedFixedList_0()
  {
    ArrayList list = new ArrayList();

    return list;
  }

  public Object replyUntypedFixedList_1()
  {
    ArrayList list = new ArrayList();

    list.add("1");

    return list;
  }

  public Object replyUntypedFixedList_7()
  {
    ArrayList list = new ArrayList();

    list.add("1");
    list.add("2");
    list.add("3");
    list.add("4");
    list.add("5");
    list.add("6");
    list.add("7");

    return list;
  }

  public Object replyUntypedFixedList_8()
  {
    ArrayList list = new ArrayList();

    list.add("1");
    list.add("2");
    list.add("3");
    list.add("4");
    list.add("5");
    list.add("6");
    list.add("7");
    list.add("8");

    return list;
  }

  public Object replyTypedFixedList_0()
  {
    return new String[] {};
  }

  public Object replyTypedFixedList_1()
  {
    return new String[] { "1" };
  }

  public Object replyTypedFixedList_7()
  {
    return new String[] { "1", "2", "3", "4", "5", "6", "7" };
  }

  public Object replyTypedFixedList_8()
  {
    return new String[] { "1", "2", "3", "4", "5", "6", "7", "8" };
  }

  // untyped map

  public Object replyUntypedMap_0()
  {
    return new HashMap();
  }

  public Object replyUntypedMap_1()
  {
    HashMap map = new HashMap();
    map.put("a", 0);

    return map;
  }

  public Object replyUntypedMap_2()
  {
    HashMap map = new HashMap();
    
    map.put(0, "a");
    map.put(1, "b");

    return map;
  }

  public Object replyUntypedMap_3()
  {
    HashMap map = new HashMap();

    ArrayList list = new ArrayList();
    list.add("a");
    
    map.put(list, 0);

    return map;
  }

  // typed map

  public Object replyTypedMap_0()
  {
    Hashtable map = new Hashtable();
    
    return map;
  }

  public Object replyTypedMap_1()
  {
    Map map = new Hashtable();
    
    map.put("a", 0);

    return map;
  }

  public Object replyTypedMap_2()
  {
    Map map = new Hashtable();
    
    map.put(0, "a");
    map.put(1, "b");

    return map;
  }

  public Object replyTypedMap_3()
  {
    Map map = new Hashtable();

    ArrayList list = new ArrayList();
    list.add("a");
    
    map.put(list, 0);

    return map;
  }

  //
  // objects
  //
  
  public Object replyObject_0()
  {
    return new A0();
  }
  
  public Object replyObject_16()
  {
    ArrayList list = new ArrayList();
    
    list.add(new A0());
    list.add(new A1());
    list.add(new A2());
    list.add(new A3());
    list.add(new A4());
    list.add(new A5());
    list.add(new A6());
    list.add(new A7());
    list.add(new A8());
    list.add(new A9());
    list.add(new A10());
    list.add(new A11());
    list.add(new A12());
    list.add(new A13());
    list.add(new A14());
    list.add(new A15());
    list.add(new A16());
    
    return list;
  }
  
  public Object replyObject_1()
  {
    return new TestObject(0);
  }
  
  public Object replyObject_2()
  {
    ArrayList list = new ArrayList();

    list.add(new TestObject(0));
    list.add(new TestObject(1));
    
    return list;
  }
  
  public Object replyObject_2a()
  {
    ArrayList list = new ArrayList();

    TestObject obj = new TestObject(0);
    
    list.add(obj);
    list.add(obj);
    
    return list;
  }
  
  public Object replyObject_2b()
  {
    ArrayList list = new ArrayList();

    list.add(new TestObject(0));
    list.add(new TestObject(0));
    
    return list;
  }
  
  public Object replyObject_3()
  {
    TestCons cons = new TestCons();

    cons.setFirst("a");
    cons.setRest(cons);
    
    return cons;
  }

  //
  // arguments
  //
  
  public Object argNull(Object v)
  {
    if (v == null)
      return true;
    
    return getInputDebug();
  }
  
  public Object argTrue(Object v)
  {
    if (Boolean.TRUE.equals(v))
      return true;
    
    return getInputDebug();
  }
  
  public Object argFalse(Object v)
  {
    if (Boolean.FALSE.equals(v))
      return true;
    
    return getInputDebug();
  }

  // integers
  
  public Object argInt_0(Object v)
  {
    if (v instanceof Integer) {
      Integer value = (Integer) v;

      if (value == 0)
        return true;
    }
    
    return getInputDebug();
  }

  public Object argInt_1(Object v)
  {
    if (v instanceof Integer) {
      Integer value = (Integer) v;

      if (value == 1)
        return true;
    }
    
    return getInputDebug();
  }

  public Object argInt_47(Object v)
  {
    if (v instanceof Integer) {
      Integer value = (Integer) v;

      if (value == 47)
        return true;
    }
    
    return getInputDebug();
  }

  public Object argInt_m16(Object v)
  {
    if (v instanceof Integer) {
      Integer value = (Integer) v;

      if (value == -16)
        return true;
    }
    
    return getInputDebug();
  }

  // two byte integers

  public Object argInt_0x30(Object v)
  {
    if (v instanceof Integer) {
      Integer value = (Integer) v;

      if (value == 0x30)
        return true;
    }
    
    return getInputDebug();
  }

  public Object argInt_0x7ff(Object v)
  {
    if (v instanceof Integer) {
      Integer value = (Integer) v;

      if (value == 0x7ff)
        return true;
    }
    
    return getInputDebug();
  }

  public Object argInt_m17(Object v)
  {
    if (v instanceof Integer) {
      Integer value = (Integer) v;

      if (value == -17)
        return true;
    }
    
    return getInputDebug();
  }

  public Object argInt_m0x800(Object v)
  {
    if (v instanceof Integer) {
      Integer value = (Integer) v;

      if (value == -0x800)
        return true;
    }
    
    return getInputDebug();
  }

  public Object argInt_0x800(Object v)
  {
    if (v instanceof Integer) {
      Integer value = (Integer) v;

      if (value == 0x800)
        return true;
    }
    
    return getInputDebug();
  }

  public Object argInt_0x3ffff(Object v)
  {
    if (v instanceof Integer) {
      Integer value = (Integer) v;

      if (value == 0x3ffff)
        return true;
    }
    
    return getInputDebug();
  }

  public Object argInt_m0x801(Object v)
  {
    if (v instanceof Integer) {
      Integer value = (Integer) v;

      if (value == -0x801)
        return true;
    }
    
    return getInputDebug();
  }

  public Object argInt_m0x40000(Object v)
  {
    if (v instanceof Integer) {
      Integer value = (Integer) v;

      if (value == -0x40000)
        return true;
    }
    
    return getInputDebug();
  }

  // 5 byte integers

  public Object argInt_0x40000(Object v)
  {
    if (v instanceof Integer) {
      Integer value = (Integer) v;

      if (value == 0x40000)
        return true;
    }
    
    return getInputDebug();
  }

  public Object argInt_0x7fffffff(Object v)
  {
    if (v instanceof Integer) {
      Integer value = (Integer) v;

      if (value == 0x7fffffff)
        return true;
    }
    
    return getInputDebug();
  }

  public Object argInt_m0x40001(Object v)
  {
    if (v instanceof Integer) {
      Integer value = (Integer) v;

      if (value == -0x40001)
        return true;
    }
    
    return getInputDebug();
  }

  public Object argInt_m0x80000000(Object v)
  {
    if (v instanceof Integer) {
      Integer value = (Integer) v;

      if (value == -0x80000000)
        return true;
    }
    
    return getInputDebug();
  }

  //
  // longs
  //

  public Object argLong_0(Object v)
  {
    if (v.equals(replyLong_0()))
      return true;

    return getInputDebug();
  }

  public Object argLong_1(Object v)
  {
    if (v.equals(replyLong_1()))
      return true;

    return getInputDebug();
  }

  public Object argLong_15(Object v)
  {
    if (v.equals(replyLong_15()))
      return true;

    return getInputDebug();
  }

  public Object argLong_m8(Object v)
  {
    if (v.equals(replyLong_m8()))
      return true;

    return getInputDebug();
  }

  // two byte longs

  public Object argLong_0x10(Object v)
  {
    if (v.equals(replyLong_0x10()))
      return true;

    return getInputDebug();
  }

  public Object argLong_0x7ff(Object v)
  {
    if (v.equals(replyLong_0x7ff()))
      return true;

    return getInputDebug();
  }

  public Object argLong_m9(Object v)
  {
    if (v.equals(replyLong_m9()))
      return true;

    return getInputDebug();
  }

  public Object argLong_m0x800(Object v)
  {
    if (v.equals(replyLong_m0x800()))
      return true;

    return getInputDebug();
  }

  public Object argLong_0x800(Object v)
  {
    if (v.equals(replyLong_0x800()))
      return true;

    return getInputDebug();
  }

  public Object argLong_0x3ffff(Object v)
  {
    if (v.equals(replyLong_0x3ffff()))
      return true;

    return getInputDebug();
  }

  public Object argLong_m0x801(Object v)
  {
    if (v.equals(replyLong_m0x801()))
      return true;

    return getInputDebug();
  }

  public Object argLong_m0x40000(Object v)
  {
    if (v.equals(replyLong_m0x40000()))
      return true;

    return getInputDebug();
  }

  // 5 byte longs

  public Object argLong_0x40000(Object v)
  {
    if (v.equals(replyLong_0x40000()))
      return true;

    return getInputDebug();
  }

  public Object argLong_0x7fffffff(Object v)
  {
    if (v.equals(replyLong_0x7fffffff()))
      return true;

    return getInputDebug();
  }

  public Object argLong_m0x40001(Object v)
  {
    if (v.equals(replyLong_m0x40001()))
      return true;

    return getInputDebug();
  }

  public Object argLong_m0x80000000(Object v)
  {
    if (v.equals(replyLong_m0x80000000()))
      return true;

    return getInputDebug();
  }

  public Object argLong_0x80000000(Object v)
  {
    if (v.equals(replyLong_0x80000000()))
      return true;

    return getInputDebug();
  }

  public Object argLong_m0x80000001(Object v)
  {
    if (v.equals(replyLong_m0x80000001()))
      return true;

    return getInputDebug();
  }

  //
  // doubles
  //

  /**
   * double 0.0
   *
   * <code><pre>
   * x5b
   * </pre></code>
   */
  public Object argDouble_0_0(Object v)
  {
    if (v.equals(replyDouble_0_0()))
      return true;

    return getInputDebug();
  }

  /**
   * double 1.0
   *
   * <code><pre>
   * x5c
   * </pre></code>
   */
  public Object argDouble_1_0(Object v)
  {
    if (v.equals(replyDouble_1_0()))
      return true;

    return getInputDebug();
  }

  /**
   * double 2.0
   *
   * <code><pre>
   * x5d x02
   * </pre></code>
   */
  public Object argDouble_2_0(Object v)
  {
    if (v.equals(replyDouble_2_0()))
      return true;

    return getInputDebug();
  }

  /**
   * double 127.0
   *
   * <code><pre>
   * x5d x7f
   * </pre></code>
   */
  public Object argDouble_127_0(Object v)
  {
    if (v.equals(replyDouble_127_0()))
      return true;

    return getInputDebug();
  }

  /**
   * double -128.0
   *
   * <code><pre>
   * x5d x80
   * </pre></code>
   */
  public Object argDouble_m128_0(Object v)
  {
    if (v.equals(replyDouble_m128_0()))
      return true;

    return getInputDebug();
  }

  /**
   * double 128.0
   *
   * <code><pre>
   * x5e x00 x80
   * </pre></code>
   */
  public Object argDouble_128_0(Object v)
  {
    if (v.equals(replyDouble_128_0()))
      return true;

    return getInputDebug();
  }

  /**
   * double -129.0
   *
   * <code><pre>
   * x5e xff x7f
   * </pre></code>
   */
  public Object argDouble_m129_0(Object v)
  {
    if (v.equals(replyDouble_m129_0()))
      return true;

    return getInputDebug();
  }

  /**
   * double 32767.0
   *
   * <code><pre>
   * x5e x7f xff
   * </pre></code>
   */
  public Object argDouble_32767_0(Object v)
  {
    if (v.equals(replyDouble_32767_0()))
      return true;

    return getInputDebug();
  }

  /**
   * Double -32768.0
   *
   * <code><pre>
   * x5e x80 x80
   * </pre></code>
   */
  public Object argDouble_m32768_0(Object v)
  {
    if (v.equals(replyDouble_m32768_0()))
      return true;

    return getInputDebug();
  }

  /**
   * double 0.001
   *
   * <code><pre>
   * x5f x00 x00 x00 x01
   * </pre></code>
   */
  public Object argDouble_0_001(Object v)
  {
    if (v.equals(replyDouble_0_001()))
      return true;

    return getInputDebug();
  }

  /**
   * double -0.001
   *
   * <code><pre>
   * x5f xff xff xff xff
   * </pre></code>
   */
  public Object argDouble_m0_001(Object v)
  {
    if (v.equals(replyDouble_m0_001()))
      return true;

    return getInputDebug();
  }

  /**
   * double 65.536
   *
   * <code><pre>
   * x5f x00 x01 x00 x00
   * </pre></code>
   */
  public Object argDouble_65_536(Object v)
  {
    if (v.equals(replyDouble_65_536()))
      return true;

    return getInputDebug();
  }

  /**
   * Result of double 3.14159
   *
   * <code><pre>
   * D x00 x01 x00 x00 x00 x00 x00 x00
   * </pre></code>
   */
  public Object argDouble_3_14159(Object v)
  {
    if (v.equals(replyDouble_3_14159()))
      return true;

    return getInputDebug();
  }

  //
  // date
  //

  public Object argDate_0(Object v)
  {
    if (v.equals(replyDate_0()))
      return true;

    return getInputDebug();
  }

  public Object argDate_1(Object v)
  {
    if (v.equals(replyDate_1()))
      return true;

    return getInputDebug();
  }

  public Object argDate_2(Object v)
  {
    if (v.equals(replyDate_2()))
      return true;

    return getInputDebug();
  }

  //
  // string length
  //

  public Object argString_0(Object v)
  {
    String expect = "";
    
    if (expect.equals(v)) {
      return true;
    }
    
    return getInputDebug();
  }

  public Object argString_1(Object v)
  {
    String expect = "0";
    
    if (expect.equals(v)) {
      return true;
    }
    
    return getInputDebug();
  }

  public Object argString_31(Object v)
  {
    String expect = "0123456789012345678901234567890";
    
    if (expect.equals(v)) {
      return true;
    }
    
    return getInputDebug();
  }

  public Object argString_32(Object v)
  {
    String expect = "01234567890123456789012345678901";
    
    if (expect.equals(v)) {
      return true;
    }
    
    return getInputDebug();
  }

  public Object argString_1023(Object v)
  {
    String expect = replyString_1023();
    
    if (expect.equals(v)) {
      return true;
    }
    
    return getInputDebug();
  }
    
  public Object argString_1024(Object v)
  {
    String expect = replyString_1024();
    
    if (expect.equals(v)) {
      return true;
    }
    
    return getInputDebug();
  }
    
  public Object argString_65536(Object v)
  {
    String expect = replyString_65536();
    
    if (expect.equals(v)) {
      return true;
    }
    
    return getInputDebug();
  }

  //
  // binary length
  //

  public Object argBinary_0(Object v)
  {
    byte []expect = (byte []) replyBinary_0();
    
    if (equals(expect, v))
      return true;
    
    return getInputDebug();
  }

  public Object argBinary_1(Object v)
  {
    byte []expect = (byte []) replyBinary_1();
    
    if (equals(expect, v))
      return true;
    
    return getInputDebug();
  }

  public Object argBinary_15(Object v)
  {
    byte []expect = (byte []) replyBinary_15();
    
    if (equals(expect, v))
      return true;
    
    return getInputDebug();
  }

  public Object argBinary_16(Object v)
  {
    byte []expect = (byte []) replyBinary_16();
    
    if (equals(expect, v))
      return true;
    
    return getInputDebug();
  }

  public Object argBinary_1023(Object v)
  {
    byte []expect = (byte []) replyBinary_1023();
    
    if (equals(expect, v))
      return true;
    
    return getInputDebug();
  }

  public Object argBinary_1024(Object v)
  {
    byte []expect = (byte []) replyBinary_1024();
    
    if (equals(expect, v))
      return true;
    
    return getInputDebug();
  }

  public Object argBinary_65536(Object v)
  {
    byte []expect = (byte []) replyBinary_65536();
    
    if (equals(expect, v))
      return true;
    
    return getInputDebug();
  }

  //
  // lists
  //

  public Object argUntypedFixedList_0(Object v)
  {
    Object expect = replyUntypedFixedList_0();
    
    if (expect.equals(v)) {
      return true;
    }
    
    return getInputDebug();
  }

  public Object argUntypedFixedList_1(Object v)
  {
    Object expect = replyUntypedFixedList_1();

    if (expect.equals(v)) {
      return true;
    }
    
    return getInputDebug();
  }

  public Object argUntypedFixedList_7(Object v)
  {
    Object expect = replyUntypedFixedList_7();
    
    if (expect.equals(v)) {
      return true;
    }
    
    return getInputDebug();
  }

  public Object argUntypedFixedList_8(Object v)
  {
    Object expect = replyUntypedFixedList_8();
    
    if (expect.equals(v)) {
      return true;
    }
    
    return getInputDebug();
  }

  public Object argTypedFixedList_0(Object v)
  {
    String []expect = (String []) replyTypedFixedList_0();
    
    if (v instanceof String[] && equals(expect, (String []) v)) {
      return true;
    }
    
    return getInputDebug();
  }

  public Object argTypedFixedList_1(Object v)
  {
    String []expect = (String []) replyTypedFixedList_1();
    
    if (v instanceof String[] && equals(expect, (String []) v)) {
      return true;
    }
    
    return getInputDebug();
  }

  public Object argTypedFixedList_7(Object v)
  {
    String []expect = (String []) replyTypedFixedList_7();
    
    if (v instanceof String[] && equals(expect, (String []) v)) {
      return true;
    }
    
    return getInputDebug();
  }

  public Object argTypedFixedList_8(Object v)
  {
    String []expect = (String []) replyTypedFixedList_8();
    
    if (v instanceof String[] && equals(expect, (String []) v)) {
      return true;
    }
    
    return getInputDebug();
  }

  //
  // untyped maps
  //

  public Object argUntypedMap_0(Object v)
  {
    Object expect = replyUntypedMap_0();
    
    if (expect.equals(v)) {
      return true;
    }
    
    return getInputDebug();
  }

  public Object argUntypedMap_1(Object v)
  {
    Object expect = replyUntypedMap_1();
    
    if (expect.equals(v)) {
      return true;
    }
    
    return getInputDebug();
  }

  public Object argUntypedMap_2(Object v)
  {
    Object expect = replyUntypedMap_2();
    
    if (expect.equals(v)) {
      return true;
    }
    
    return getInputDebug();
  }

  public Object argUntypedMap_3(Object v)
  {
    Object expect = replyUntypedMap_3();
    
    if (expect.equals(v)) {
      return true;
    }
    
    return getInputDebug();
  }

  //
  // typed maps
  //

  public Object argTypedMap_0(Object v)
  {
    Object expect = replyTypedMap_0();
    
    if (expect.equals(v)) {
      return true;
    }
    
    return getInputDebug();
  }

  public Object argTypedMap_1(Object v)
  {
    Object expect = replyTypedMap_1();
    
    if (expect.equals(v)) {
      return true;
    }
    
    return getInputDebug();
  }

  public Object argTypedMap_2(Object v)
  {
    Object expect = replyTypedMap_2();

    if (expect.equals(v)) {
      return true;
    }
    
    return getInputDebug();
  }

  public Object argTypedMap_3(Object v)
  {
    Object expect = replyTypedMap_3();
    
    if (expect.equals(v)) {
      return true;
    }
    
    return getInputDebug();
  }

  //
  // objects
  //

  public Object argObject_0(Object v)
  {
    Object expect = replyObject_0();
    
    if (expect.equals(v)) {
      return true;
    }
    
    return getInputDebug();
  }
  
  public Object argObject_16(Object v)
  {
    Object expect = replyObject_16();
    
    if (expect.equals(v)) {
      return true;
    }
    
    return getInputDebug();
  }

  public Object argObject_1(Object v)
  {
    Object expect = replyObject_1();
    
    if (expect.equals(v)) {
      return true;
    }
    
    return getInputDebug();
  }

  public Object argObject_2(Object v)
  {
    Object expect = replyObject_2();
    
    if (expect.equals(v)) {
      return true;
    }
    
    return getInputDebug();
  }

  public Object argObject_2a(Object v)
  {
    Object expect = replyObject_2a();
    
    if (expect.equals(v)) {
      return true;
    }
    
    return getInputDebug();
  }

  public Object argObject_2b(Object v)
  {
    Object expect = replyObject_2b();
    
    if (expect.equals(v)) {
      return true;
    }
    
    return getInputDebug();
  }

  public Object argObject_3(Object v)
  {
    Object expect = replyObject_3();
    
    if (expect.equals(v)) {
      return true;
    }
    
    return getInputDebug();
  }

  private boolean equals(String []a, String []b)
  {
    if (a == null || b == null)
      return false;

    if (a.length != b.length)
      return false;

    for (int i = 0; i < a.length; i++) {
      if (! a[i].equals(b[i]))
        return false;
    }

    return true;
  }

  private boolean equals(byte []a, Object obj)
  {
    if (! (obj instanceof byte[]))
      return false;

    byte []b = (byte []) obj;
    
    if (a == null || b == null)
      return false;

    if (a.length != b.length)
      return false;

    for (int i = 0; i < a.length; i++) {
      if (a[i] != b[i]) {
        return false;
      }
    }

    return true;
  }

  protected String getInputDebug()
  {
    CharArrayWriter writer = _threadWriter.get();
    if (writer != null)
      return writer.toString();
    else
      return null;
  }

  /**
   * Invoke the object with the request from the input stream.
   *
   * @param in the Hessian input stream
   * @param out the Hessian output stream
   */
  @Override
  public void invoke(InputStream is, OutputStream os, String objectId,
                     SerializerFactory serializerFactory)
    throws Exception
  {
    CharArrayWriter writer = new CharArrayWriter();

    _threadWriter.set(writer);

    PrintWriter dbg = new PrintWriter(writer);

    HessianDebugInputStream debug = new HessianDebugInputStream(is, dbg);
    debug.startTop2();
    
    super.invoke(debug, os, objectId, serializerFactory);
  }
}
