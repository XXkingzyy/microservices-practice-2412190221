package com.zjgsu.jzy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 用户服务 API 测试（H2 内存库）：
 * 注册顾客 → 201；按 ID 查询 → 200；不存在 → 404；姓名空白 → 400
 */
@SpringBootTest
class UserServiceApiTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void registerAndQueryCustomer() throws Exception {
        String location = mockMvc.perform(post("/api/customers")
                        .contentType("application/json")
                        .content("""
                                {"name":"张三","phone":"13800000001"}"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("张三"))
                .andReturn().getResponse().getHeader("Location");

        String id = location.substring(location.lastIndexOf('/') + 1);

        mockMvc.perform(get("/api/customers/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone").value("13800000001"));

        mockMvc.perform(get("/api/customers/not-exist"))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectBlankName() throws Exception {
        mockMvc.perform(post("/api/customers")
                        .contentType("application/json")
                        .content("""
                                {"name":"","phone":"13800000002"}"""))
                .andExpect(status().isBadRequest());
    }
}
