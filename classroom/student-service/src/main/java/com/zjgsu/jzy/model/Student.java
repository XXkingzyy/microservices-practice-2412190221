package com.zjgsu.jzy.model;

import jakarta.persistence.*;

/**
 * 学生实体
 * studentNumber 学号唯一约束
 */
@Entity
@Table(name = "students", uniqueConstraints = @UniqueConstraint(columnNames = "studentNumber"))
public class Student {

    @Id
    private String id;               // 学生内部 ID（UUID）

    @Column(nullable = false)
    private String studentNumber;    // 学号

    @Column(nullable = false)
    private String name;             // 姓名

    private String major;             // 专业

    protected Student() {} // JPA 无参构造器

    public Student(String id, String studentNumber, String name, String major) {
        this.id = id;
        this.studentNumber = studentNumber;
        this.name = name;
        this.major = major;
    }

    public String getId() { return id; }
    public String getStudentNumber() { return studentNumber; }
    public String getName() { return name; }
    public String getMajor() { return major; }
}
