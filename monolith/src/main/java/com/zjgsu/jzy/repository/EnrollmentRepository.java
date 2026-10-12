package com.zjgsu.jzy.repository;

import com.zjgsu.jzy.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 选课记录 Repository
 */
public interface EnrollmentRepository extends JpaRepository<Enrollment, String> {
    // 检查某学生是否已选某课程
    boolean existsByStudentIdAndCourseId(String studentId, String courseId);
    // 按学生 ID 查询所有选课记录
    List<Enrollment> findByStudentId(String studentId);
}
