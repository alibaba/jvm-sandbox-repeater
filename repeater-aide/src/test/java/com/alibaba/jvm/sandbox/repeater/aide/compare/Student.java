package com.alibaba.jvm.sandbox.repeater.aide.compare;

import java.util.Date;

/**
 * {@link Student}
 * <p>
 *
 * @author zhaoyb1990
 */
public class Student {

    private String name;

    private int age;

    private String school;

    private Date birthday;

    public static StudentBuilder builder() {
        return new StudentBuilder();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public static final class StudentBuilder {
        private String name;
        private int age;
        private String school;
        private Date birthday;

        private StudentBuilder() {
        }

        public StudentBuilder name(String name) {
            this.name = name;
            return this;
        }

        public StudentBuilder age(int age) {
            this.age = age;
            return this;
        }

        public StudentBuilder school(String school) {
            this.school = school;
            return this;
        }

        public StudentBuilder birthday(Date birthday) {
            this.birthday = birthday;
            return this;
        }

        public Student build() {
            Student student = new Student();
            student.setName(name);
            student.setAge(age);
            student.setSchool(school);
            student.setBirthday(birthday);
            return student;
        }
    }
}
