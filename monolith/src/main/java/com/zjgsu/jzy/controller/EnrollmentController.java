package com.zjgsu.jzy.controller;

import com.zjgsu.jzy.dto.EnrollmentRequest;
import com.zjgsu.jzy.model.Enrollment;
import com.zjgsu.jzy.service.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * 选课记录 REST 接口
 */
@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    /** 选课 → 201 */
    @PostMapping
    public ResponseEntity<Enrollment> enroll(@Valid @RequestBody EnrollmentRequest req,
                                             UriComponentsBuilder uriBuilder) {
        Enrollment enrollment = enrollmentService.enroll(req.studentId(), req.courseId());
        return ResponseEntity.created(uriBuilder.path("/api/enrollments/{id}")
                .buildAndExpand(enrollment.getId()).toUri()).body(enrollment);
    }

    /** 按学生查询选课记录 → 200 */
    @GetMapping("/student/{studentId}")
    public List<Enrollment> listByStudent(@PathVariable String studentId) {
        return enrollmentService.findByStudentId(studentId);
    }

    /** 退课 → 204 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> withdraw(@PathVariable String id) {
        enrollmentService.withdraw(id);
        return ResponseEntity.noContent().build();
    }
}
