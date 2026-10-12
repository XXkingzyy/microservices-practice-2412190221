package com.zjgsu.jzy.model;

import jakarta.persistence.*;

/**
 * 课程实体
 * code 唯一约束，capacity 最大人数，enrolled 当前已选人数
 */
@Entity
@Table(name = "courses", uniqueConstraints = @UniqueConstraint(columnNames = "code"))
public class Course {

    @Id
    private String id;              // 课程 ID（UUID）

    @Column(nullable = false)
    private String code;            // 课程编号，如 CS404

    @Column(nullable = false)
    private String title;           // 课程名称

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "instructor_id"))
    private Instructor instructor;  // 授课教师（嵌入对象，id 列重命名避免冲突）

    @Embedded
    private ScheduleSlot schedule;  // 上课时段（嵌入对象）

    private int capacity;            // 最大可选人数

    private int enrolled;            // 当前已选人数

    protected Course() {} // JPA 无参构造器

    public Course(String id, String code, String title, Instructor instructor,
                  ScheduleSlot schedule, int capacity) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.instructor = instructor;
        this.schedule = schedule;
        this.capacity = capacity;
        this.enrolled = 0;
    }

    /** 判断课程是否已满员 */
    public boolean isFull() {
        return enrolled >= capacity;
    }

    /** 选课成功，已选人数加一 */
    public void enrollOne() {
        this.enrolled++;
    }

    /** 退课成功，已选人数减一 */
    public void withdrawOne() {
        if (this.enrolled > 0) this.enrolled--;
    }

    // getter
    public String getId() { return id; }
    public String getCode() { return code; }
    public String getTitle() { return title; }
    public Instructor getInstructor() { return instructor; }
    public ScheduleSlot getSchedule() { return schedule; }
    public int getCapacity() { return capacity; }
    public int getEnrolled() { return enrolled; }
}
