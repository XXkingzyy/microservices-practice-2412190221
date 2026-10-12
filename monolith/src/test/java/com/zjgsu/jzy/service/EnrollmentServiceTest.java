package com.zjgsu.jzy.service;

import com.zjgsu.jzy.exception.ConflictException;
import com.zjgsu.jzy.exception.NotFoundException;
import com.zjgsu.jzy.model.*;
import com.zjgsu.jzy.repository.CourseRepository;
import com.zjgsu.jzy.repository.EnrollmentRepository;
import com.zjgsu.jzy.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * EnrollmentService 业务规则测试
 * 覆盖：正常选课、重复选课、满员、对象不存在、退课恢复人数
 */
@SpringBootTest
@Transactional // 每个测试回滚，互不影响
class EnrollmentServiceTest {

    @Autowired private EnrollmentService enrollmentService;
    @Autowired private CourseService courseService;
    @Autowired private StudentService studentService;
    @Autowired private CourseRepository courseRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private EnrollmentRepository enrollmentRepository;

    private String studentId;
    private String courseId;

    @BeforeEach
    void setUp() {
        // 清空数据
        enrollmentRepository.deleteAll();
        courseRepository.deleteAll();
        studentRepository.deleteAll();

        // 创建一名学生
        Student s = studentService.create("2026001", "张三", "软件工程");
        studentId = s.getId();

        // 创建一门容量为 1 的课程
        Instructor ins = new Instructor("T001", "赵老师", "zhao@example.edu");
        ScheduleSlot slot = new ScheduleSlot(DayOfWeek.TUESDAY, LocalTime.of(14,0), LocalTime.of(16,0));
        Course c = courseService.create("CS404", "软件工程", ins, slot, 1);
        courseId = c.getId();
    }

    @Test
    void 正常选课_应成功且人数加一() {
        Enrollment e = enrollmentService.enroll(studentId, courseId);
        assertNotNull(e.getId());
        assertEquals(EnrollmentStatus.ACTIVE, e.getStatus());
        Course c = courseService.findById(courseId);
        assertEquals(1, c.getEnrolled());
    }

    @Test
    void 重复选课_应拒绝409() {
        enrollmentService.enroll(studentId, courseId);
        ConflictException ex = assertThrows(ConflictException.class,
                () -> enrollmentService.enroll(studentId, courseId));
        assertTrue(ex.getMessage().contains("已选"));
    }

    @Test
    void 课程满员_应拒绝409() {
        // 第一个学生选满
        enrollmentService.enroll(studentId, courseId);
        // 第二个学生再选应满员
        Student s2 = studentService.create("2026002", "李四", "计算机");
        ConflictException ex = assertThrows(ConflictException.class,
                () -> enrollmentService.enroll(s2.getId(), courseId));
        assertTrue(ex.getMessage().contains("满员"));
    }

    @Test
    void 学生不存在_应拒绝404() {
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> enrollmentService.enroll("no-such-id", courseId));
        assertTrue(ex.getMessage().contains("学生不存在"));
    }

    @Test
    void 课程不存在_应拒绝404() {
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> enrollmentService.enroll(studentId, "no-such-id"));
        assertTrue(ex.getMessage().contains("课程不存在"));
    }

    @Test
    void 退课后_人数应恢复且可再次选课() {
        Enrollment e = enrollmentService.enroll(studentId, courseId);
        assertEquals(1, courseService.findById(courseId).getEnrolled());

        // 退课
        enrollmentService.withdraw(e.getId());
        assertEquals(0, courseService.findById(courseId).getEnrolled());
        assertFalse(enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId));

        // 再次选课应成功
        Enrollment e2 = enrollmentService.enroll(studentId, courseId);
        assertNotNull(e2.getId());
        assertEquals(1, courseService.findById(courseId).getEnrolled());
    }

    @Test
    void 按学生查询选课记录_应返回列表() {
        enrollmentService.enroll(studentId, courseId);
        List<Enrollment> list = enrollmentService.findByStudentId(studentId);
        assertEquals(1, list.size());
        assertEquals(courseId, list.get(0).getCourseId());
    }
}
