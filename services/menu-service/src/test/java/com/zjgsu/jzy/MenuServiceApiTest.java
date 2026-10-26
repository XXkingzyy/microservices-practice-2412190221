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
 * 菜单服务 API 测试（H2 内存库）：
 * 新增菜品 → 201；按 ID 查询 → 200；不存在 → 404；负价格 → 400
 */
@SpringBootTest
class MenuServiceApiTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void createAndQueryDish() throws Exception {
        String location = mockMvc.perform(post("/api/dishes")
                        .contentType("application/json")
                        .content("""
                                {"name":"宫保鸡丁","category":"川菜","price":28.00,"available":true}"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("宫保鸡丁"))
                .andExpect(jsonPath("$.price").value(28.00))
                .andReturn().getResponse().getHeader("Location");

        String id = location.substring(location.lastIndexOf('/') + 1);

        mockMvc.perform(get("/api/dishes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true));

        mockMvc.perform(get("/api/dishes/not-exist"))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectNegativePrice() throws Exception {
        mockMvc.perform(post("/api/dishes")
                        .contentType("application/json")
                        .content("""
                                {"name":"测试菜","category":"测试","price":-1.0,"available":true}"""))
                .andExpect(status().isBadRequest());
    }
}
