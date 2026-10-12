package com.zjgsu.jzy.service;

import com.zjgsu.jzy.exception.ConflictException;
import com.zjgsu.jzy.exception.NotFoundException;
import com.zjgsu.jzy.model.Course;
import com.zjgsu.jzy.model.Enrollment;
import com.zjgsu.jzy.model.EnrollmentStatus;
import com.zjgsu.jzy.model.Student;
import com.zjgsu.jzy.repository.CourseRepository;
import com.zjgsu.jzy.repository.EnrollmentRepository;
import com.zjgsu.jzy.repository.StudentRepository;
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
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             CourseRepository courseRepository,
                             StudentRepository studentRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
    }

    /**
     * 选课：依次检查学生存在 → 课程存在 → 未重复选 → 课程未满 → 创建记录并加人数
     */
    @Transactional
    public Enrollment enroll(String studentId, String courseId) {
        // 1. 查询学生与课程，任一不存在则拒绝
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("学生不存在: " + studentId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("课程不存在: " + courseId));

        // 2. 检查是否已选同一门课
        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new ConflictException("学生已选该课程: " + courseId);
        }

        // 3. 检查是否满员
        if (course.isFull()) {
            throw new ConflictException("课程已满员: " + courseId);
        }

        // 4. 创建选课记录，课程已选人数加一
        course.enrollOne();
        courseRepository.save(course);

        Enrollment enrollment = new Enrollment(
                UUID.randomUUID().toString(),
                student.getId(), course.getId(),
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
        Course course = courseRepository.findById(enrollment.getCourseId())
                .orElseThrow(() -> new NotFoundException("课程不存在: " + enrollment.getCourseId()));

        course.withdrawOne();
        courseRepository.save(course);
        enrollmentRepository.delete(enrollment);
    }

    public List<Enrollment> findByStudentId(String studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }
}
