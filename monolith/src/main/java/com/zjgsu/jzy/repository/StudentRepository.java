package com.zjgsu.jzy.repository;

import com.zjgsu.jzy.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 学生 Repository
 */
public interface StudentRepository extends JpaRepository<Student, String> {
    Optional<Student> findByStudentNumber(String studentNumber); // 按学号查
    boolean existsByStudentNumber(String studentNumber);          // 检查学号是否已存在
}
