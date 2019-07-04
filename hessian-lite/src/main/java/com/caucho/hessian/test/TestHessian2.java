package com.caucho.hessian.test;

/**
 * The Test service is a quick sanity check service.  Developers of a
 * new Hessian implementation can use this service as an initial test.
 *
 * http://hessian.caucho.com/test/test2
 */
public interface TestHessian2 {
  /**
   * trivial null method call
   *
   * <code><pre>
   * H x02 x00
   * C
   *   x0a methodNull
   *   x90
   * </pre></code>
   *
   * <code><pre>
   * R N
   * </pre></code>
   */
  public void methodNull();

  //
  // result values
  //

  /**
   * Result of null
   *
   * <code><pre>
   * R N
   * </pre></code>
   */
  public void replyNull();

  //
  // boolean
  //

  /**
   * Boolean true
   *
   * <code><pre>
   * T
   * </pre></code>
   */
  public Object replyTrue();

  /**
   * Boolean false
   *
   * <code><pre>
   * F
   * </pre></code>
   */
  public Object replyFalse();

  //
  // integers
  //

  /**
   * Result of integer 0
   *
   * <code><pre>
   * R x90
   * </pre></code>
   */
  public int replyInt_0();

  /**
   * Result of integer 1
   *
   * <code><pre>
   * R x91
   * </pre></code>
   */
  public int replyInt_1();

  /**
   * Result of integer 47
   *
   * <code><pre>
   * R xbf
   * </pre></code>
   */
  public int replyInt_47();

  /**
   * Result of integer -16
   *
   * <code><pre>
   * R x80
   * </pre></code>
   */
  public int replyInt_m16();

  // two byte integers

  /**
   * Result of integer 0x30
   *
   * <code><pre>
   * R xc8 x30
   * </pre></code>
   */
  public int replyInt_0x30();

  /**
   * Result of integer x7ff
   *
   * <code><pre>
   * R xcf xff
   * </pre></code>
   */
  public int replyInt_0x7ff();

  /**
   * Result of integer -17
   *
   * <code><pre>
   * R xc7 xef
   * </pre></code>
   */
  public int replyInt_m17();

  /**
   * Result of integer -0x800
   *
   * <code><pre>
   * R xc0 x00
   * </pre></code>
   */
  public int replyInt_m0x800();

  /**
   * Result of integer 0x800
   *
   * <code><pre>
   * R xd4 x08 x00
   * </pre></code>
   */
  public int replyInt_0x800();

  /**
   * Result of integer 0x3ffff
   *
   * <code><pre>
   * R xd7 xff xff
   * </pre></code>
   */
  public int replyInt_0x3ffff();

  /**
   * Result of integer -0x801
   *
   * <code><pre>
   * R xd3 xf8 x00
   * </pre></code>
   */
  public int replyInt_m0x801();

  /**
   * Result of integer m0x40000
   *
   * <code><pre>
   * R xd0 x00 x00
   * </pre></code>
   */
  public int replyInt_m0x40000();

  // 5 byte integers

  /**
   * Result of integer 0x40000
   *
   * <code><pre>
   * R I x00 x04 x00 x00
   * </pre></code>
   */
  public int replyInt_0x40000();

  /**
   * Result of integer 0x7fffffff
   *
   * <code><pre>
   * R I x7f xff xff xff
   * </pre></code>
   */
  public int replyInt_0x7fffffff();

  /**
   * Result of integer m0x40001
   *
   * <code><pre>
   * R I xff xf3 xff xf
   * </pre></code>
   */
  public int replyInt_m0x40001();

  /**
   * Result of integer -0x80000000
   *
   * <code><pre>
   * R I x80 x00 x00 x00
   * </pre></code>
   */
  public int replyInt_m0x80000000();

  //
  // longs
  //

  /**
   * Result of long 0
   *
   * <code><pre>
   * R xe0
   * </pre></code>
   */
  public long replyLong_0();

  /**
   * Result of long 1
   *
   * <code><pre>
   * R xe1
   * </pre></code>
   */
  public long replyLong_1();

  /**
   * Result of long 15
   *
   * <code><pre>
   * R xef
   * </pre></code>
   */
  public long replyLong_15();

  /**
   * Result of long -8
   *
   * <code><pre>
   * R xd8
   * </pre></code>
   */
  public long replyLong_m8();

  // two byte longs

  /**
   * Result of long 0x10
   *
   * <code><pre>
   * R xf8 x10
   * </pre></code>
   */
  public long replyLong_0x10();

  /**
   * Result of long x7ff
   *
   * <code><pre>
   * R xff xff
   * </pre></code>
   */
  public long replyLong_0x7ff();

  /**
   * Result of long -9
   *
   * <code><pre>
   * R xf7 xf7
   * </pre></code>
   */
  public long replyLong_m9();

  /**
   * Result of long -0x800
   *
   * <code><pre>
   * R xf0 x00
   * </pre></code>
   */
  public long replyLong_m0x800();

  /**
   * Result of long 0x800
   *
   * <code><pre>
   * R x3c x08 x00
   * </pre></code>
   */
  public long replyLong_0x800();

  /**
   * Result of long 0x3ffff
   *
   * <code><pre>
   * R x3f xff xff
   * </pre></code>
   */
  public long replyLong_0x3ffff();

  /**
   * Result of long -0x801
   *
   * <code><pre>
   * R x3b xf7 xff
   * </pre></code>
   */
  public long replyLong_m0x801();

  /**
   * Result of long m0x40000
   *
   * <code><pre>
   * R x38 x00 x00
   * </pre></code>
   */
  public long replyLong_m0x40000();

  // 5 byte longs

  /**
   * Result of long 0x40000
   *
   * <code><pre>
   * R x59 x00 x04 x00 x00
   * </pre></code>
   */
  public long replyLong_0x40000();

  /**
   * Result of long 0x7fffffff
   *
   * <code><pre>
   * R x59 x7f xff xff xff
   * </pre></code>
   */
  public long replyLong_0x7fffffff();

  /**
   * Result of long m0x40001
   *
   * <code><pre>
   * R x59 xff xf3 xff xf
   * </pre></code>
   */
  public long replyLong_m0x40001();

  /**
   * Result of long -0x80000000
   *
   * <code><pre>
   * R x59 x80 x00 x00 x00
   * </pre></code>
   */
  public long replyLong_m0x80000000();

  /**
   * Result of long 0x80000000
   *
   * <code><pre>
   * R L x00 x00 x00 x00 x80 x00 x00 x00
   * </pre></code>
   */
  public long replyLong_0x80000000();

  /**
   * Result of long -0x80000001
   *
   * <code><pre>
   * R L xff xff xff xff x7f xff xff xff
   * </pre></code>
   */
  public long replyLong_m0x80000001();

  //
  // doubles
  //

  /**
   * Result of double 0.0
   *
   * <code><pre>
   * R x5b
   * </pre></code>
   */
  public double replyDouble_0_0();

  /**
   * Result of double 1.0
   *
   * <code><pre>
   * R x5c
   * </pre></code>
   */
  public double replyDouble_1_0();

  /**
   * Result of double 2.0
   *
   * <code><pre>
   * R x5d x02
   * </pre></code>
   */
  public double replyDouble_2_0();

  /**
   * Result of double 127.0
   *
   * <code><pre>
   * R x5d x7f
   * </pre></code>
   */
  public double replyDouble_127_0();

  /**
   * Result of double -128.0
   *
   * <code><pre>
   * R x5d x80
   * </pre></code>
   */
  public double replyDouble_m128_0();

  /**
   * Result of double 128.0
   *
   * <code><pre>
   * R x5e x00 x80
   * </pre></code>
   */
  public double replyDouble_128_0();

  /**
   * Result of double -129.0
   *
   * <code><pre>
   * R x5e xff x7f
   * </pre></code>
   */
  public double replyDouble_m129_0();

  /**
   * Result of double 32767.0
   *
   * <code><pre>
   * R x5e x7f xff
   * </pre></code>
   */
  public double replyDouble_32767_0();

  /**
   * Result of double -32768.0
   *
   * <code><pre>
   * R x5e x80 x80
   * </pre></code>
   */
  public double replyDouble_m32768_0();

  /**
   * Result of double 0.001
   *
   * <code><pre>
   * R x5f x00 x00 x00 x01
   * </pre></code>
   */
  public double replyDouble_0_001();

  /**
   * Result of double -0.001
   *
   * <code><pre>
   * R x5f xff xff xff xff
   * </pre></code>
   */
  public double replyDouble_m0_001();

  /**
   * Result of double 65.536
   *
   * <code><pre>
   * R x5f x00 x01 x00 x00
   * </pre></code>
   */
  public double replyDouble_65_536();

  /**
   * Result of double 3.14159
   *
   * <code><pre>
   * D x40 x09 x21 xf9 xf0 x1b x86 x6e
   * </pre></code>
   */
  public double replyDouble_3_14159();

  //
  // date
  //

  /**
   * date 0 (01-01-1970 00:00 GMT)
   *
   * <code><pre>
   * x4a x00 x00 x00 x00
   * </pre></code>
   */
  public Object replyDate_0();

  /**
   * Date by millisecond (05-08-1998 07:51:31.000 GMT)
   *
   * <code><pre>
   * x4a x00 x00 x00 xd0 x4b x92 x84 xb8
   * </pre></code>
   */
  public Object replyDate_1();

  /**
   * Date by minute (05-08-1998 07:51:00.000 GMT)
   *
   * <code><pre>
   * x4b x00 xe3 x83 x8f
   * </pre></code>
   */
  public Object replyDate_2();

  //
  // string length
  //

  /**
   * A zero-length string
   *
   * <code><pre>
   * x00
   * </pre></code>
   */
  public String replyString_0();

  /**
   * A null string
   *
   * <code><pre>
   * N
   * </pre></code>
   */
  public String replyString_null();

  /**
   * A one-length string
   *
   * <code><pre>
   * x01 a
   * </pre></code>
   */
  public String replyString_1();

  /**
   * A 31-length string
   *
   * <code><pre>
   * x0f 0123456789012345678901234567890
   * </pre></code>
   */
  public String replyString_31();

  /**
   * A 32-length string
   *
   * <code><pre>
   * x30 x02 01234567890123456789012345678901
   * </pre></code>
   */
  public String replyString_32();

  /**
   * A 1023-length string
   *
   * <code><pre>
   * x33 xff 000 01234567890123456789012345678901...
   * </pre></code>
   */
  public String replyString_1023();

  /**
   * A 1024-length string
   *
   * <code><pre>
   * S x04 x00 000 01234567890123456789012345678901...
   * </pre></code>
   */
  public String replyString_1024();

  /**
   * A 65536-length string
   *
   * <code><pre>
   * R x80 x00 000 ...
   * S x04 x00 000 01234567890123456789012345678901...
   * </pre></code>
   */
  public String replyString_65536();

  //
  // binary length
  //

  /**
   * A zero-length binary
   *
   * <code><pre>
   * x20
   * </pre></code>
   */
  public Object replyBinary_0();

  /**
   * A null string
   *
   * <code><pre>
   * N
   * </pre></code>
   */
  public Object replyBinary_null();

  /**
   * A one-length string
   *
   * <code><pre>
   * x01 0
   * </pre></code>
   */
  public Object replyBinary_1();

  /**
   * A 15-length binary
   *
   * <code><pre>
   * x2f 0123456789012345
   * </pre></code>
   */
  public Object replyBinary_15();

  /**
   * A 16-length binary
   *
   * <code><pre>
   * x34 x10 01234567890123456789012345678901
   * </pre></code>
   */
  public Object replyBinary_16();

  /**
   * A 1023-length binary
   *
   * <code><pre>
   * x37 xff 000 01234567890123456789012345678901...
   * </pre></code>
   */
  public Object replyBinary_1023();

  /**
   * A 1024-length binary
   *
   * <code><pre>
   * B x04 x00 000 01234567890123456789012345678901...
   * </pre></code>
   */
  public Object replyBinary_1024();

  /**
   * A 65536-length binary
   *
   * <code><pre>
   * A x80 x00 000 ...
   * B x04 x00 000 01234567890123456789012345678901...
   * </pre></code>
   */
  public Object replyBinary_65536();

  //
  // lists
  //

  /**
   * Zero-length untyped list
   *
   * <code><pre>
   * x78
   * </pre></code>
   */
  public Object replyUntypedFixedList_0();

  /**
   * 1-length untyped list
   *
   * <code><pre>
   * x79 x01 1
   * </pre></code>
   */
  public Object replyUntypedFixedList_1();

  /**
   * 7-length untyped list
   *
   * <code><pre>
   * x7f x01 1 x01 2 x01 3 x01 4 x01 5 x01 6 x01 7
   * </pre></code>
   */
  public Object replyUntypedFixedList_7();

  /**
   * 8-length untyped list
   *
   * <code><pre>
   * X x98 x01 1 x01 2 x01 3 x01 4 x01 5 x01 6 x01 7 x01 8
   * </pre></code>
   */
  public Object replyUntypedFixedList_8();

  /**
   * Zero-length typed list (String array)
   *
   * <code><pre>
   * x70 x07 [string
   * </pre></code>
   */
  public Object replyTypedFixedList_0();

  /**
   * 1-length typed list (String array)
   *
   * <code><pre>
   * x71 x07 [string x01 1
   * </pre></code>
   */
  public Object replyTypedFixedList_1();

  /**
   * 7-length typed list (String array)
   *
   * <code><pre>
   * x77 x07 [string x01 1 x01 2 x01 3 x01 4 x01 5 x01 6 x01 7
   * </pre></code>
   */
  public Object replyTypedFixedList_7();

  /**
   * 8-length typed list (String array)
   *
   * <code><pre>
   * V x07 [stringx98 x01 1 x01 2 x01 3 x01 4 x01 5 x01 6 x01 7 x01 8
   * </pre></code>
   */
  public Object replyTypedFixedList_8();

  //
  // untyped maps
  //

  /**
   * zero-length untyped map
   *
   * <code><pre>
   * H Z
   * </pre></code>
   */
  public Object replyUntypedMap_0();

  /**
   * untyped map with string key
   *
   * <code><pre>
   * H x01 a x90 Z
   * </pre></code>
   */
  public Object replyUntypedMap_1();

  /**
   * untyped map with int key
   *
   * <code><pre>
   * H x90 x01 a x91 x01 b Z
   * </pre></code>
   */
  public Object replyUntypedMap_2();

  /**
   * untyped map with list key
   *
   * <code><pre>
   * H x71 x01 a x90 Z
   * </pre></code>
   */
  public Object replyUntypedMap_3();

  //
  // typed maps
  //

  /**
   * zero-length typed map
   *
   * <code><pre>
   * M x13 java.lang.Hashtable Z
   * </pre></code>
   */
  public Object replyTypedMap_0();

  /**
   * untyped map with string key
   *
   * <code><pre>
   * M x13 java.lang.Hashtable x01 a x90 Z
   * </pre></code>
   */
  public Object replyTypedMap_1();

  /**
   * typed map with int key
   *
   * <code><pre>
   * M x13 java.lang.Hashtable x90 x01 a x91 x01 b Z
   * </pre></code>
   */
  public Object replyTypedMap_2();

  /**
   * typed map with list key
   *
   * <code><pre>
   * M x13 java.lang.Hashtable x71 x01 a x90 Z
   * </pre></code>
   */
  public Object replyTypedMap_3();

  //
  // objects
  //

  /**
   * Returns a single object
   *
   * <code><pre>
   * C x1a com.caucho.hessian.test.A0 x90 x60
   * </pre></code>
   */
  public Object replyObject_0();
  
  /**
   * Returns 16 object types
   *
   * <code><pre>
   * X xa0
   *  C x1a com.caucho.hessian.test.A0 x90 x60
   *  C x1a com.caucho.hessian.test.A1 x90 x61
   *  C x1a com.caucho.hessian.test.A2 x90 x62
   *  C x1a com.caucho.hessian.test.A3 x90 x63
   *  C x1a com.caucho.hessian.test.A4 x90 x64
   *  C x1a com.caucho.hessian.test.A5 x90 x65
   *  C x1a com.caucho.hessian.test.A6 x90 x66
   *  C x1a com.caucho.hessian.test.A7 x90 x67
   *  C x1a com.caucho.hessian.test.A8 x90 x68
   *  C x1a com.caucho.hessian.test.A9 x90 x69
   *  C x1b com.caucho.hessian.test.A10 x90 x6a
   *  C x1b com.caucho.hessian.test.A11 x90 x6b
   *  C x1b com.caucho.hessian.test.A12 x90 x6c
   *  C x1b com.caucho.hessian.test.A13 x90 x6d
   *  C x1b com.caucho.hessian.test.A14 x90 x6e
   *  C x1b com.caucho.hessian.test.A15 x90 x6f
   *  C x1b com.caucho.hessian.test.A16 x90 O xa0
   */
  public Object replyObject_16();

  /**
   * Simple object with one field
   *
   * <code><pre>
   * C x22 com.caucho.hessian.test.TestObject x91 x06 _value x60 x90
   * </pre></code>
   */
  public Object replyObject_1();

  /**
   * Simple two objects with one field
   *
   * <code><pre>
   * x7a
   *   C x22 com.caucho.hessian.test.TestObject x91 x06 _value
   *   x60 x90
   *   x60 x91
   * </pre></code>
   */
  public Object replyObject_2();

  /**
   * Simple repeated object
   *
   * <code><pre>
   * x7a
   *   C x22 com.caucho.hessian.test.TestObject x91 x06 _value
   *   x60 x90
   *   Q x91
   * </pre></code>
   */
  public Object replyObject_2a();

  /**
   * Two object with equals
   *
   * <code><pre>
   * x7a
   *   C x22 com.caucho.hessian.test.TestObject x91 x06 _value
   *   x60 x90
   *   x60 x90
   * </pre></code>
   */
  public Object replyObject_2b();

  /**
   * Circular object
   *
   * <code><pre>
   * C x20 com.caucho.hessian.test.TestCons x91 x06 _first x05 _rest
   *   x60 x01 a Q \x90x
   * </pre></code>
   */
  public Object replyObject_3();

  //
  // arguments
  //

  /**
   * Null
   *
   * <code><pre>
   * N
   * </pre></code>
   */
  public Object argNull(Object v);

  //
  // boolean
  //

  /**
   * Boolean true
   *
   * <code><pre>
   * T
   * </pre></code>
   */
  public Object argTrue(Object v);

  /**
   * Boolean false
   *
   * <code><pre>
   * F
   * </pre></code>
   */
  public Object argFalse(Object v);

  //
  // integer
  //
  
  /**
   * Integer 0
   *
   * <code><pre>
   * x90
   * </pre></code>
   */
  public Object argInt_0(Object v);

  /**
   * Integer 1
   *
   * <code><pre>
   * x91
   * </pre></code>
   */
  public Object argInt_1(Object v);

  /**
   * integer 47
   *
   * <code><pre>
   * xbf
   * </pre></code>
   */
  public Object argInt_47(Object v);

  /**
   * Result of integer -16
   *
   * <code><pre>
   * R x80
   * </pre></code>
   */
  public Object argInt_m16(Object v);

  // two byte integers

  /**
   * Integer 0x30
   *
   * <code><pre>
   * xc8 x30
   * </pre></code>
   */
  public Object argInt_0x30(Object v);

  /**
   * Result of integer x7ff
   *
   * <code><pre>
   * xcf xff
   * </pre></code>
   */
  public Object argInt_0x7ff(Object v);

  /**
   * integer -17
   *
   * <code><pre>
   * xc7 xef
   * </pre></code>
   */
  public Object argInt_m17(Object v);

  /**
   * Integer -0x800
   *
   * <code><pre>
   * xc0 x00
   * </pre></code>
   */
  public Object argInt_m0x800(Object v);

  /**
   * Integer 0x800
   *
   * <code><pre>
   * xd4 x08 x00
   * </pre></code>
   */
  public Object argInt_0x800(Object v);

  /**
   * Integer 0x3ffff
   *
   * <code><pre>
   * xd7 xff xff
   * </pre></code>
   */
  public Object argInt_0x3ffff(Object v);

  /**
   * Integer -0x801
   *
   * <code><pre>
   * xd3 xf8 x00
   * </pre></code>
   */
  public Object argInt_m0x801(Object v);

  /**
   * Integer m0x40000
   *
   * <code><pre>
   * xd0 x00 x00
   * </pre></code>
   */
  public Object argInt_m0x40000(Object v);

  // 5 byte integers

  /**
   * integer 0x40000
   *
   * <code><pre>
   * I x00 x04 x00 x00
   * </pre></code>
   */
  public Object argInt_0x40000(Object v);

  /**
   * Integer 0x7fffffff
   *
   * <code><pre>
   * I x7f xff xff xff
   * </pre></code>
   */
  public Object argInt_0x7fffffff(Object v);

  /**
   * Integer m0x40001
   *
   * <code><pre>
   * I xff xfb xff xff
   * </pre></code>
   */
  public Object argInt_m0x40001(Object v);

  /**
   * Result of integer -0x80000000
   *
   * <code><pre>
   * I x80 x00 x00 x00
   * </pre></code>
   */
  public Object argInt_m0x80000000(Object v);

  //
  // longs
  //

  /**
   * long 0
   *
   * <code><pre>
   * xe0
   * </pre></code>
   */
  public Object argLong_0(Object v);

  /**
   * long 1
   *
   * <code><pre>
   * xe1
   * </pre></code>
   */
  public Object argLong_1(Object v);

  /**
   * long 15
   *
   * <code><pre>
   * xef
   * </pre></code>
   */
  public Object argLong_15(Object v);

  /**
   * long -8
   *
   * <code><pre>
   * xd8
   * </pre></code>
   */
  public Object argLong_m8(Object v);

  // two byte longs

  /**
   * long 0x10
   *
   * <code><pre>
   * xf8 x10
   * </pre></code>
   */
  public Object argLong_0x10(Object v);

  /**
   * long x7ff
   *
   * <code><pre>
   * xff xff
   * </pre></code>
   */
  public Object argLong_0x7ff(Object v);

  /**
   * long -9
   *
   * <code><pre>
   * xf7 xf7
   * </pre></code>
   */
  public Object argLong_m9(Object v);

  /**
   * long -0x800
   *
   * <code><pre>
   * xf0 x00
   * </pre></code>
   */
  public Object argLong_m0x800(Object v);

  /**
   * long 0x800
   *
   * <code><pre>
   * x3c x08 x00
   * </pre></code>
   */
  public Object argLong_0x800(Object v);

  /**
   * long 0x3ffff
   *
   * <code><pre>
   * x3f xff xff
   * </pre></code>
   */
  public Object argLong_0x3ffff(Object v);

  /**
   * long -0x801
   *
   * <code><pre>
   * x3b xf7 xff
   * </pre></code>
   */
  public Object argLong_m0x801(Object v);

  /**
   * long m0x40000
   *
   * <code><pre>
   * x38 x00 x00
   * </pre></code>
   */
  public Object argLong_m0x40000(Object v);

  // 5 byte longs

  /**
   * long 0x40000
   *
   * <code><pre>
   * x59 x00 x04 x00 x00
   * </pre></code>
   */
  public Object argLong_0x40000(Object v);

  /**
   * long 0x7fffffff
   *
   * <code><pre>
   * x59 x7f xff xff xff
   * </pre></code>
   */
  public Object argLong_0x7fffffff(Object v);

  /**
   * long m0x40001
   *
   * <code><pre>
   * x59 xff xfb xff xf
   * </pre></code>
   */
  public Object argLong_m0x40001(Object v);

  /**
   * long -0x80000000
   *
   * <code><pre>
   * x59 x80 x00 x00 x00
   * </pre></code>
   */
  public Object argLong_m0x80000000(Object v);

  /**
   * Result of long 0x80000000
   *
   * <code><pre>
   * L x00 x00 x00 x00 x80 x00 x00 x00
   * </pre></code>
   */
  public Object argLong_0x80000000(Object v);

  /**
   * Result of long -0x80000001
   *
   * <code><pre>
   * L xff xff xff xff x7f xff xff xff
   * </pre></code>
   */
  public Object argLong_m0x80000001(Object v);

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
  public Object argDouble_0_0(Object v);

  /**
   * double 1.0
   *
   * <code><pre>
   * x5c
   * </pre></code>
   */
  public Object argDouble_1_0(Object v);

  /**
   * double 2.0
   *
   * <code><pre>
   * x5d x02
   * </pre></code>
   */
  public Object argDouble_2_0(Object v);

  /**
   * double 127.0
   *
   * <code><pre>
   * x5d x7f
   * </pre></code>
   */
  public Object argDouble_127_0(Object v);

  /**
   * double -128.0
   *
   * <code><pre>
   * x5d x80
   * </pre></code>
   */
  public Object argDouble_m128_0(Object v);

  /**
   * double 128.0
   *
   * <code><pre>
   * x5e x00 x80
   * </pre></code>
   */
  public Object argDouble_128_0(Object v);

  /**
   * double -129.0
   *
   * <code><pre>
   * x5e xff x7f
   * </pre></code>
   */
  public Object argDouble_m129_0(Object v);

  /**
   * double 32767.0
   *
   * <code><pre>
   * x5e x7f xff
   * </pre></code>
   */
  public Object argDouble_32767_0(Object v);

  /**
   * Double -32768.0
   *
   * <code><pre>
   * x5e x80 x80
   * </pre></code>
   */
  public Object argDouble_m32768_0(Object v);

  /**
   * double 0.001
   *
   * <code><pre>
   * x5f x00 x00 x00 x01
   * </pre></code>
   */
  public Object argDouble_0_001(Object v);

  /**
   * double -0.001
   *
   * <code><pre>
   * x5f xff xff xff xff
   * </pre></code>
   */
  public Object argDouble_m0_001(Object v);

  /**
   * double 65.536
   *
   * <code><pre>
   * x5f x00 x01 x00 x00
   * </pre></code>
   */
  public Object argDouble_65_536(Object v);

  /**
   * Result of double 3.14159
   *
   * <code><pre>
   * D x40 x09 x21 xf9 xf0 x1b x86 x6e
   * </pre></code>
   */
  public Object argDouble_3_14159(Object v);

  //
  // date
  //

  /**
   * date 0 (01-01-1970 00:00 GMT)
   *
   * <code><pre>
   * x4a x00 x00 x00 x00
   * </pre></code>
   */
  public Object argDate_0(Object v);

  /**
   * Date by millisecond (05-08-1998 07:51 GMT)
   *
   * <code><pre>
   * x4a x00 x00 x00 xd0 x4b x92 x84 xb8
   * </pre></code>
   */
  public Object argDate_1(Object v);

  /**
   * Date by minute (05-08-1998 07:51 GMT)
   *
   * <code><pre>
   * x4b x00 xe3 x83 x8f
   * </pre></code>
   */
  public Object argDate_2(Object v);

  //
  // string length
  //

  /**
   * A zero-length string
   *
   * <code><pre>
   * x00
   * </pre></code>
   */
  public Object argString_0(Object v);

  /**
   * A one-length string
   *
   * <code><pre>
   * x01 a
   * </pre></code>
   */
  public Object argString_1(Object v);

  /**
   * A 31-length string
   *
   * <code><pre>
   * x0f 0123456789012345678901234567890
   * </pre></code>
   */
  public Object argString_31(Object v);

  /**
   * A 32-length string
   *
   * <code><pre>
   * x30 x02 01234567890123456789012345678901
   * </pre></code>
   */
  public Object argString_32(Object v);

  /**
   * A 1023-length string
   *
   * <code><pre>
   * x33 xff 000 01234567890123456789012345678901...
   * </pre></code>
   */
  public Object argString_1023(Object v);

  /**
   * A 1024-length string
   *
   * <code><pre>
   * S x04 x00 000 01234567890123456789012345678901...
   * </pre></code>
   */
  public Object argString_1024(Object v);

  /**
   * A 65536-length string
   *
   * <code><pre>
   * R x80 x00 000 ...
   * S x04 x00 000 01234567890123456789012345678901...
   * </pre></code>
   */
  public Object argString_65536(Object v);

  //
  // binary length
  //

  /**
   * A zero-length binary
   *
   * <code><pre>
   * x20
   * </pre></code>
   */
  public Object argBinary_0(Object v);

  /**
   * A one-length string
   *
   * <code><pre>
   * x21 0
   * </pre></code>
   */
  public Object argBinary_1(Object v);

  /**
   * A 15-length binary
   *
   * <code><pre>
   * x2f 0123456789012345
   * </pre></code>
   */
  public Object argBinary_15(Object v);

  /**
   * A 16-length binary
   *
   * <code><pre>
   * x34 x10 01234567890123456789012345678901
   * </pre></code>
   */
  public Object argBinary_16(Object v);

  /**
   * A 1023-length binary
   *
   * <code><pre>
   * x37 xff 000 01234567890123456789012345678901...
   * </pre></code>
   */
  public Object argBinary_1023(Object v);

  /**
   * A 1024-length binary
   *
   * <code><pre>
   * B x04 x00 000 01234567890123456789012345678901...
   * </pre></code>
   */
  public Object argBinary_1024(Object v);

  /**
   * A 65536-length binary
   *
   * <code><pre>
   * A x80 x00 000 ...
   * B x04 x00 000 01234567890123456789012345678901...
   * </pre></code>
   */
  public Object argBinary_65536(Object v);

  //
  // lists
  //

  /**
   * Zero-length untyped list
   *
   * <code><pre>
   * x78
   * </pre></code>
   */
  public Object argUntypedFixedList_0(Object v);

  /**
   * 1-length untyped list
   *
   * <code><pre>
   * x79 x01 1
   * </pre></code>
   */
  public Object argUntypedFixedList_1(Object v);

  /**
   * 7-length untyped list
   *
   * <code><pre>
   * x7f x01 1 x01 2 x01 3 x01 4 x01 5 x01 6 x01 7
   * </pre></code>
   */
  public Object argUntypedFixedList_7(Object v);

  /**
   * 8-length untyped list
   *
   * <code><pre>
   * X x98 x01 1 x01 2 x01 3 x01 4 x01 5 x01 6 x01 7 x01 8
   * </pre></code>
   */
  public Object argUntypedFixedList_8(Object v);

  /**
   * Zero-length typed list (String array)
   *
   * <code><pre>
   * x70 x07 [string
   * </pre></code>
   */
  public Object argTypedFixedList_0(Object v);

  /**
   * 1-length typed list (String array)
   *
   * <code><pre>
   * x71 x07 [string x01 1
   * </pre></code>
   */
  public Object argTypedFixedList_1(Object v);

  /**
   * 7-length typed list (String array)
   *
   * <code><pre>
   * x77 x07 [string x01 1 x01 2 x01 3 x01 4 x01 5 x01 6 x01 7
   * </pre></code>
   */
  public Object argTypedFixedList_7(Object v);

  /**
   * 8-length typed list (String array)
   *
   * <code><pre>
   * V x07 [stringx98 x01 1 x01 2 x01 3 x01 4 x01 5 x01 6 x01 7 x01 8
   * </pre></code>
   */
  public Object argTypedFixedList_8(Object v);

  //
  // untyped maps
  //

  /**
   * zero-length untyped map
   *
   * <code><pre>
   * H Z
   * </pre></code>
   */
  public Object argUntypedMap_0(Object v);

  /**
   * untyped map with string key
   *
   * <code><pre>
   * H x01 a x90 Z
   * </pre></code>
   */
  public Object argUntypedMap_1(Object v);

  /**
   * untyped map with int key
   *
   * <code><pre>
   * H x90 x01 a x91 x01 b Z
   * </pre></code>
   */
  public Object argUntypedMap_2(Object v);

  /**
   * untyped map with list key
   *
   * <code><pre>
   * H x71 x01 a x90 Z
   * </pre></code>
   */
  public Object argUntypedMap_3(Object v);

  //
  // typed maps
  //

  /**
   * zero-length typed map
   *
   * <code><pre>
   * M x13 java.lang.Hashtable Z
   * </pre></code>
   */
  public Object argTypedMap_0(Object v);

  /**
   * untyped map with string key
   *
   * <code><pre>
   * M x13 java.lang.Hashtable x01 a x90 Z
   * </pre></code>
   */
  public Object argTypedMap_1(Object v);

  /**
   * typed map with int key
   *
   * <code><pre>
   * M x13 java.lang.Hashtable x90 x01 a x91 x01 b Z
   * </pre></code>
   */
  public Object argTypedMap_2(Object v);

  /**
   * typed map with list key
   *
   * <code><pre>
   * M x13 java.lang.Hashtable x79 x01 a x90 Z
   * </pre></code>
   */
  public Object argTypedMap_3(Object v);

  //
  // objects
  //

  /**
   * Returns a single object
   *
   * <code><pre>
   * C x1a com.caucho.hessian.test.A0 x90 x60
   * </pre></code>
   */
  public Object argObject_0(Object v);
  
  /**
   * Returns 16 object types
   *
   * <code><pre>
   * X xa0
   *  C x1a com.caucho.hessian.test.A0 x90 x60
   *  C x1a com.caucho.hessian.test.A1 x90 x61
   *  C x1a com.caucho.hessian.test.A2 x90 x62
   *  C x1a com.caucho.hessian.test.A3 x90 x63
   *  C x1a com.caucho.hessian.test.A4 x90 x64
   *  C x1a com.caucho.hessian.test.A5 x90 x65
   *  C x1a com.caucho.hessian.test.A6 x90 x66
   *  C x1a com.caucho.hessian.test.A7 x90 x67
   *  C x1a com.caucho.hessian.test.A8 x90 x68
   *  C x1a com.caucho.hessian.test.A9 x90 x69
   *  C x1b com.caucho.hessian.test.A10 x90 x6a
   *  C x1b com.caucho.hessian.test.A11 x90 x6b
   *  C x1b com.caucho.hessian.test.A12 x90 x6c
   *  C x1b com.caucho.hessian.test.A13 x90 x6d
   *  C x1b com.caucho.hessian.test.A14 x90 x6e
   *  C x1b com.caucho.hessian.test.A15 x90 x6f
   *  C x1b com.caucho.hessian.test.A16 x90 O xa0
   */
  public Object argObject_16(Object v);

  /**
   * Simple object with one field
   *
   * <code><pre>
   * C x30 x22 com.caucho.hessian.test.TestObject x91 x06 _value x60 x90
   * </pre></code>
   */
  public Object argObject_1(Object v);

  /**
   * Simple two objects with one field
   *
   * <code><pre>
   * x7a
   *   C x30 x22 com.caucho.hessian.test.TestObject x91 x06 _value
   *   x60 x90
   *   x60 x91
   * </pre></code>
   */
  public Object argObject_2(Object v);

  /**
   * Simple repeated object
   *
   * <code><pre>
   * x7a
   *   C x30 x22 com.caucho.hessian.test.TestObject x91 x06 _value
   *   x60 x90
   *   Q x91
   * </pre></code>
   */
  public Object argObject_2a(Object v);

  /**
   * Two object with equals
   *
   * <code><pre>
   * x7a
   *   C x22 com.caucho.hessian.test.TestObject x91 x06 _value
   *   x60 x90
   *   x60 x90
   * </pre></code>
   */
  public Object argObject_2b(Object v);

  /**
   * Circular object
   *
   * <code><pre>
   * C x20 com.caucho.hessian.test.TestCons x91 x06 _first x05 _rest
   *   x60 x01 a Q x90
   * </pre></code>
   */
  public Object argObject_3(Object v);
}
