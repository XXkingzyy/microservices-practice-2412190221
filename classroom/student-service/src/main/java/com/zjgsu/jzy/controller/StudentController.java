package com.zjgsu.jzy.controller;

import com.zjgsu.jzy.dto.StudentRequest;
import com.zjgsu.jzy.model.Student;
import com.zjgsu.jzy.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * 学生 REST 接口
 */
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /** 创建学生 → 201 */
    @PostMapping
    public ResponseEntity<Student> create(@Valid @RequestBody StudentRequest req,
                                          UriComponentsBuilder uriBuilder) {
        Student student = studentService.create(req.studentNumber(), req.name(), req.major());
        return ResponseEntity.created(uriBuilder.path("/api/students/{id}")
                .buildAndExpand(student.getId()).toUri()).body(student);
    }

    /** 按 ID 查询学生 → 200 */
    @GetMapping("/{id}")
    public Student getById(@PathVariable String id) {
        return studentService.findById(id);
    }
}
