package com.alibaba.jvm.sandbox.repeater.aide.compare;

import com.alibaba.jvm.sandbox.repeater.aide.compare.comparator.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link }
 * <p>
 *
 * @author zhaoyb1990
 */
public class IntegratedComparatorTest {

    @Test
    public void testCompare() {
        List<Comparator> comparators = new ArrayList<Comparator>();
        comparators.add(new SimpleComparator());
        comparators.add(new MapComparator());
        comparators.add(new ObjectComparator());
        IntegratedComparator comparator = new IntegratedComparator(comparators);

        Map<String, String> extensions = new HashMap<String, String>();
        extensions.put("name", "sandbox-repeater");
        extensions.put("version", "1.0.0");
        IntegratedCompareDO left = IntegratedCompareDO.builder()
                .name("sandbox")
                .character('s')
                .extentions(extensions)
                .salary(10000.0D)
                .count(10)
                .localDateTime(LocalDateTime.now())
                .money(new BigDecimal(10000.0))
                .student(Student.builder()
                        .name("YueBing")
                        .age(29)
                        .school("Peking University")
                        .build())
                .build();

        Map<String, String> extensions2 = new HashMap<String, String>();
        extensions2.put("name", "sandbox-repeater");
        extensions2.put("version", "1.0.1");
        extensions2.put("hacker", "YueBing");
        IntegratedCompareDO right = IntegratedCompareDO.builder()
                .name("repeater")
                .character('t')
                .extentions(extensions2)
                .salary(10000.1D)
                .count(12)
                .localDateTime(LocalDateTime.now())
                .money(new BigDecimal(10000.0))
                .student(Student.builder()
                        .name("YueBing")
                        .age(29)
                        .school("Tsinghua university")
                        .build())
                .build();
        CompareResult result = comparator.compare(left, right);
        Assert.assertNotEquals(result, null);
        Assert.assertTrue(result.hasDifference());
        Assert.assertEquals(result.getDifferences().size(), 8);
    }

    @Test
    public void testCycleReferenceCompare() {
        List<Comparator> comparators = new ArrayList<Comparator>();
        comparators.add(new SimpleComparator());
        comparators.add(new MapComparator());
        comparators.add(new ObjectComparator());
        IntegratedComparator comparator = new IntegratedComparator(comparators);
        Map<String, String> extensions = new HashMap<String, String>();
        extensions.put("name", "sandbox-repeater");
        extensions.put("version", "1.0.0");
        IntegratedCompareDO left = IntegratedCompareDO.builder()
                .name("sandbox")
                .character('s')
                .extentions(extensions)
                .salary(10000.0D)
                .count(10)
                .localDateTime(LocalDateTime.now())
                .money(new BigDecimal(10000.0))
                .student(Student.builder()
                        .name("YueBing")
                        .age(29)
                        .school("Peking University")
                        .build())
                .build();
        IntegratedCompareDO right = IntegratedCompareDO.builder()
                .name("repeater")
                .character('t')
                .extentions(extensions)
                .salary(10000.1D)
                .count(12)
                .localDateTime(LocalDateTime.now())
                .money(new BigDecimal(10000.0))
                .student(Student.builder()
                        .name("YueBing")
                        .age(29)
                        .school("Tsinghua university")
                        .build())
                .build();
        // make cycle reference
        left.setParent(right);
        right.setParent(left);
        CompareResult result = comparator.compare(left, right);
        Assert.assertNotEquals(result, null);
        Assert.assertTrue(result.hasDifference());
        Assert.assertEquals(result.getDifferences().size(), 10);
    }

    @Test
    public void testCollectionComparator() {
        List<Comparator> comparators = new ArrayList<Comparator>();
        comparators.add(new SimpleComparator());
        comparators.add(new CollectionComparator());
        comparators.add(new ObjectComparator());
        IntegratedComparator comparator = new IntegratedComparator(comparators);
        List<String> left = new ArrayList<String>();
        left.add("a");
        left.add("b");
        left.add("c");
        left.add("d");
        left.add("e");
        left.add("f");
        left.add("g");
        List<String> right = new ArrayList<String>();
        right.add("A");
        right.add("b");
        right.add("c");
        right.add("D");
        right.add("e");
        right.add("f");
        right.add("G");
        CompareResult result = comparator.compare(left, right);
        Assert.assertNotEquals(result, null);
        Assert.assertTrue(result.hasDifference());
        Assert.assertEquals(result.getDifferences().size(), 3);
        List<Student> leftStudents = new ArrayList<Student>();
        leftStudents.add(Student.builder()
                .name("java")
                .school("w3c")
                .age(10)
                .build());

        List<Student> rightStudents = new ArrayList<Student>();
        rightStudents.add(Student.builder()
                .name("java")
                .school("w3c")
                .age(12)
                .build());
        rightStudents.add(Student.builder()
                .name("java")
                .school("w3c")
                .age(12)
                .build());
        result = comparator.compare(leftStudents, rightStudents);
        Assert.assertNotEquals(result, null);
        Assert.assertTrue(result.hasDifference());
        Assert.assertEquals(result.getDifferences().size(), 2);
    }

    @Test
    public void testArrayComparator() {
        List<Comparator> comparators = new ArrayList<Comparator>();
        comparators.add(new SimpleComparator());
        comparators.add(new ArrayComparator());
        comparators.add(new ObjectComparator());
        IntegratedComparator comparator = new IntegratedComparator(comparators);
        String[] left = new String[]{"a", "b", "c", "d", "e", "f", "g"};
        String[] right = new String[]{"A", "b", "c", "D", "e", "f", "G"};
        CompareResult result = comparator.compare(left, right);
        Assert.assertNotEquals(result, null);
        Assert.assertTrue(result.hasDifference());
        Assert.assertEquals(result.getDifferences().size(), 3);

        Student[] leftStudents = new Student[]{Student.builder()
                .name("java")
                .school("w3c")
                .age(10)
                .build()};
        Student[] rightStudents = new Student[]{
                Student.builder()
                        .name("java")
                        .school("w3c")
                        .age(12)
                        .build(),
                Student.builder()
                        .name("java")
                        .school("w3c")
                        .age(12)
                        .build()
        };
        result = comparator.compare(leftStudents, rightStudents);
        Assert.assertNotEquals(result, null);
        Assert.assertTrue(result.hasDifference());
        Assert.assertEquals(result.getDifferences().size(), 2);
        int[] leftInt = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] rightInt = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0};
        result = comparator.compare(leftInt, rightInt);
        Assert.assertNotEquals(result, null);
        Assert.assertTrue(result.hasDifference());
        Assert.assertEquals(result.getDifferences().size(), 1);
    }
}