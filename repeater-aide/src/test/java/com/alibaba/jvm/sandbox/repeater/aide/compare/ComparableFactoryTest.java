package com.alibaba.jvm.sandbox.repeater.aide.compare;

import com.alibaba.jvm.sandbox.repeater.aide.compare.comparator.Comparator;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * {@link }
 * <p>
 *
 * @author zhaoyb1990
 */
public class ComparableFactoryTest {

    @Test
    public void testCreateDefault() throws ParseException {
        Comparable comparable = ComparableFactory.instance().create(Comparator.CompareMode.DEFAULT);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Student left = Student.builder()
                .name("java")
                .age(12)
                .birthday(dateFormat.parse("2007-12-12 00:00:01"))
                .school("w3c")
                .build();
        Student right = Student.builder()
                .name("python")
                .age(10)
                .birthday(dateFormat.parse("2009-12-12 00:00:01"))
                .school("w3c")
                .build();
        CompareResult result = comparable.compare(left, right);
        Assert.assertNotEquals(result, null);
        Assert.assertTrue(result.hasDifference());
        Assert.assertEquals(result.getDifferences().size(), 3);

    }


    @Test
    public void testCreate() throws ParseException {
        Comparable comparable = ComparableFactory.instance().create(Comparator.CompareMode.LENIENT_DATES);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Student left = Student.builder()
                .name("java")
                .age(12)
                .birthday(dateFormat.parse("2007-12-12 00:00:01"))
                .school("w3c")
                .build();
        Student right = Student.builder()
                .name("python")
                .age(10)
                .birthday(dateFormat.parse("2009-12-12 00:00:01"))
                .school("w3c")
                .build();
        CompareResult result = comparable.compare(left, right);
        Assert.assertNotEquals(result, null);
        Assert.assertTrue(result.hasDifference());
        Assert.assertEquals(result.getDifferences().size(), 2);
    }


    @Test
    public void testTimestamp() throws InterruptedException {
        Timestamp left = new Timestamp(System.currentTimeMillis());
        Thread.sleep(100);
        Timestamp right = new Timestamp(System.currentTimeMillis());
        Comparable comparable = ComparableFactory.instance().create(Comparator.CompareMode.DEFAULT);
        CompareResult result = comparable.compare(left, right);
        Assert.assertNotEquals(result, null);
        Assert.assertTrue(result.hasDifference());
        Assert.assertEquals(result.getDifferences().size(), 1);
    }

    @Test
    public void testTimestampIgnore() throws InterruptedException {
        Timestamp left = new Timestamp(System.currentTimeMillis());
        Thread.sleep(100);
        Timestamp right = new Timestamp(System.currentTimeMillis());
        Comparable comparable = ComparableFactory.instance().create(Comparator.CompareMode.LENIENT_DATES);
        CompareResult result = comparable.compare(left, right);
        Assert.assertNotEquals(result, null);
        Assert.assertFalse(result.hasDifference());
        Assert.assertEquals(result.getDifferences().size(), 0);
    }


    @Test
    public void testParentFieldCompare() {
        Son left = Son.builder()
                .name("java")
                .fatherAge(12)
                .sonName("groovy")
                .sonAge(10)
                .build();
        Son right = Son.builder()
                .name("java")
                .fatherAge(13)
                .sonName("kotlin")
                .sonAge(9)
                .build();
        Comparable comparable = ComparableFactory.instance().create(Comparator.CompareMode.DEFAULT);
        CompareResult result = comparable.compare(left, right);
        Assert.assertNotEquals(result, null);
        Assert.assertTrue(result.hasDifference());
        Assert.assertEquals(result.getDifferences().size(), 3);
    }
}