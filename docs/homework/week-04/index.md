# Week 04 实践：单体应用与数据库

## 业务建模

- **核心业务流程**：学生选课 → 检查学生/课程存在 → 检查未重复选 → 检查课程未满 → 创建选课记录并加人数 → 退课删除记录并减人数。
- **核心实体**：Course（课程）、Student（学生）、Enrollment（选课记录），嵌入值对象 Instructor（教师）和 ScheduleSlot（上课时段）。
- **业务规则**（写在 EnrollmentService 中）：
  1. 学生或课程不存在 → 404
  2. 学生已选同一门课 → 409
  3. 课程已满员（enrolled >= capacity）→ 409
  4. 退课后人数减一，可再次选课

## REST API

| 方法 | 路径 | 用途 | 成功状态 |
|---|---|---|---|
| POST | /api/courses | 创建课程 | 201 |
| GET | /api/courses | 查询所有课程 | 200 |
| GET | /api/courses/{id} | 按 ID 查询课程 | 200 |
| POST | /api/students | 创建学生 | 201 |
| GET | /api/students/{id} | 按 ID 查询学生 | 200 |
| POST | /api/enrollments | 选课 | 201 |
| GET | /api/enrollments/student/{studentId} | 查询学生选课记录 | 200 |
| DELETE | /api/enrollments/{id} | 退课 | 204 |

## 数据库设计

- courses 表：id(PK), code(唯一), title, instructor_id, instructor_name, instructor_email, day_of_week, start_time, end_time, capacity, enrolled
- students 表：id(PK), student_number(唯一), name, major
- enrollments 表：id(PK), student_id, course_id, enrolled_at, status；student_id + course_id 组合唯一约束

## 测试与验证

运行命令：
```
./mvnw.cmd clean test
```

测试结果：Tests run: 10, Failures: 0, Errors: 0, BUILD SUCCESS

覆盖：
- GreetingControllerTest（2）：原有问候接口测试
- FoodOrderingApplicationTests（1）：上下文加载测试
- EnrollmentServiceTest（7）：正常选课、重复选课、满员、学生不存在、课程不存在、退课恢复人数、按学生查询
