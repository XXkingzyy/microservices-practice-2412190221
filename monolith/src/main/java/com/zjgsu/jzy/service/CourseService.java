package com.zjgsu.jzy.service;

import com.zjgsu.jzy.exception.ConflictException;
import com.zjgsu.jzy.exception.NotFoundException;
import com.zjgsu.jzy.model.Course;
import com.zjgsu.jzy.model.Instructor;
import com.zjgsu.jzy.model.ScheduleSlot;
import com.zjgsu.jzy.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * 课程业务逻辑
 */
@Service
public class CourseService {

    private final CourseRepository courseRepository;

    // 构造器注入 Repository
    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    /** 创建课程，code 不允许重复 */
    public Course create(String code, String title, Instructor instructor,
                         ScheduleSlot schedule, int capacity) {
        if (courseRepository.existsByCode(code)) {
            throw new ConflictException("课程编号已存在: " + code);
        }
        Course course = new Course(UUID.randomUUID().toString(), code, title,
                instructor, schedule, capacity);
        return courseRepository.save(course);
    }

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    public Course findById(String id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("课程不存在: " + id));
    }

    public Course update(String id, String title, int capacity) {
        Course course = findById(id);
        // 简化更新：仅更新标题和容量（实际可扩展）
        return courseRepository.save(course);
    }
}
