package com.zjgsu.jzy.service;

import com.zjgsu.jzy.exception.ConflictException;
import com.zjgsu.jzy.exception.NotFoundException;
import com.zjgsu.jzy.model.Student;
import com.zjgsu.jzy.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 学生业务逻辑
 */
@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    /** 创建学生，学号不允许重复 */
    public Student create(String studentNumber, String name, String major) {
        if (studentRepository.existsByStudentNumber(studentNumber)) {
            throw new ConflictException("学号已存在: " + studentNumber);
        }
        Student student = new Student(UUID.randomUUID().toString(),
                studentNumber, name, major);
        return studentRepository.save(student);
    }

    public Student findById(String id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("学生不存在: " + id));
    }
}
