package com.zjgsu.jzy.controller;

import com.zjgsu.jzy.dto.CourseRequest;
import com.zjgsu.jzy.model.Course;
import com.zjgsu.jzy.model.Instructor;
import com.zjgsu.jzy.model.ScheduleSlot;
import com.zjgsu.jzy.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

/**
 * 课程 REST 接口
 */
@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /** 创建课程 → 201 */
    @PostMapping
    public ResponseEntity<Course> create(@Valid @RequestBody CourseRequest req,
                                         UriComponentsBuilder uriBuilder) {
        Instructor instructor = new Instructor(
                req.instructor().id(), req.instructor().name(), req.instructor().email());
        ScheduleSlot slot = new ScheduleSlot(
                DayOfWeek.valueOf(req.schedule().dayOfWeek().toUpperCase()),
                LocalTime.parse(req.schedule().startTime()),
                LocalTime.parse(req.schedule().endTime()));
        Course course = courseService.create(req.code(), req.title(),
                instructor, slot, req.capacity());
        return ResponseEntity.created(uriBuilder.path("/api/courses/{id}")
                .buildAndExpand(course.getId()).toUri()).body(course);
    }

    /** 查询所有课程 → 200 */
    @GetMapping
    public List<Course> list() {
        return courseService.findAll();
    }

    /** 按 ID 查询课程 → 200 */
    @GetMapping("/{id}")
    public Course getById(@PathVariable String id) {
        return courseService.findById(id);
    }
}
