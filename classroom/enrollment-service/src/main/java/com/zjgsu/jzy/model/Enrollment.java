package com.zjgsu.jzy.model;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * 选课记录实体
 * studentId + courseId 组合唯一约束
 */
@Entity
@Table(name = "enrollments",
       uniqueConstraints = @UniqueConstraint(columnNames = {"studentId", "courseId"}))
public class Enrollment {

    @Id
    private String id;                // 选课记录 ID（UUID）

    @Column(nullable = false)
    private String studentId;         // 学生 ID（不直接关联 Student 对象）

    @Column(nullable = false)
    private String courseId;          // 课程 ID（不直接关联 Course 对象）

    private Instant enrolledAt;        // 选课时间

    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status;  // 选课状态

    protected Enrollment() {} // JPA 无参构造器

    public Enrollment(String id, String studentId, String courseId,
                      Instant enrolledAt, EnrollmentStatus status) {
        this.id = id;
        this.studentId = studentId;
        this.courseId = courseId;
        this.enrolledAt = enrolledAt;
        this.status = status;
    }

    public String getId() { return id; }
    public String getStudentId() { return studentId; }
    public String getCourseId() { return courseId; }
    public Instant getEnrolledAt() { return enrolledAt; }
    public EnrollmentStatus getStatus() { return status; }
}
