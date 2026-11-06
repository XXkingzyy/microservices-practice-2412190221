package com.zjgsu.jzy.repository;

import com.zjgsu.jzy.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 课程 Repository
 */
public interface CourseRepository extends JpaRepository<Course, String> {
    Optional<Course> findByCode(String code); // 按课程编号查
    boolean existsByCode(String code);         // 检查课程编号是否已存在
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select c from Course c where c.id = :id")
    java.util.Optional<Course> findLocked(@org.springframework.data.repository.query.Param("id") String id);
}
