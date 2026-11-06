package com.zjgsu.jzy.service;

import com.zjgsu.jzy.exception.ConflictException;
import com.zjgsu.jzy.exception.NotFoundException;
import com.zjgsu.jzy.model.Enrollment;
import com.zjgsu.jzy.model.EnrollmentStatus;
import com.zjgsu.jzy.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * 选课业务逻辑（核心规则全部写在这里，不放到 Controller）
 */
@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final com.zjgsu.jzy.client.CourseClient client;
    public EnrollmentService(EnrollmentRepository repository, com.zjgsu.jzy.client.CourseClient client) {
        this.enrollmentRepository=repository; this.client=client;
    }

    /**
     * 选课：依次检查学生存在 → 课程存在 → 未重复选 → 课程未满 → 创建记录并加人数
     */
    @Transactional
    public Enrollment enroll(String studentId, String courseId) {
        client.get("student-service", "/api/students/" + studentId);
        client.get("course-service", "/api/courses/" + courseId);
        // 2. 检查是否已选同一门课
        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new ConflictException("学生已选该课程: " + courseId);
        }

        client.post("course-service", "/api/courses/" + courseId + "/reserve");
        Enrollment enrollment = new Enrollment(
                UUID.randomUUID().toString(),
                studentId, courseId,
                Instant.now(), EnrollmentStatus.ACTIVE);
        return enrollmentRepository.save(enrollment);
    }

    /**
     * 退课：删除记录，课程人数减一
     */
    @Transactional
    public void withdraw(String enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new NotFoundException("选课记录不存在: " + enrollmentId));
        client.post("course-service", "/api/courses/" + enrollment.getCourseId() + "/release");
        enrollmentRepository.delete(enrollment);
    }

    public List<Enrollment> findByStudentId(String studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }
}
