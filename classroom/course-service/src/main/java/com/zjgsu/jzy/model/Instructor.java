package com.zjgsu.jzy.model;

import jakarta.persistence.Embeddable;

/**
 * 教师信息（值对象，嵌入课程表）
 */
@Embeddable
public class Instructor {

    private String id;       // 教师编号
    private String name;     // 教师姓名
    private String email;    // 教师邮箱

    protected Instructor() {} // JPA 无参构造器

    public Instructor(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
}
