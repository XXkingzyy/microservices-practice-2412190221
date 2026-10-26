package com.zjgsu.jzy;

import com.zjgsu.jzy.client.DishServiceClient;
import com.zjgsu.jzy.client.UserServiceClient;
import com.zjgsu.jzy.dto.CustomerResponse;
import com.zjgsu.jzy.dto.DishResponse;
import com.zjgsu.jzy.dto.OrderRequest;
import com.zjgsu.jzy.exception.ConflictException;
import com.zjgsu.jzy.exception.NotFoundException;
import com.zjgsu.jzy.exception.ServiceUnavailableException;
import com.zjgsu.jzy.model.Order;
import com.zjgsu.jzy.model.OrderStatus;
import com.zjgsu.jzy.repository.OrderRepository;
import com.zjgsu.jzy.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 订单服务下单业务测试（Mockito，不依赖数据库与真实 HTTP）：
 * 成功下单 / 菜品不存在 / 菜品不可售 / 依赖服务不可用 —— 验证跨服务校验与失败处理
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private DishServiceClient dishServiceClient;

    @InjectMocks
    private OrderService orderService;

    private final String customerId = "cust-1";
    private final String dishId = "dish-1";

    @Test
    void placeOrderSuccess_whenAllRemoteChecksPass() {
        when(userServiceClient.getCustomer(customerId))
                .thenReturn(new CustomerResponse(customerId, "张三", "13800000001"));
        when(dishServiceClient.getDish(dishId))
                .thenReturn(new DishResponse(dishId, "宫保鸡丁", "川菜", new BigDecimal("28.00"), true));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order order = orderService.placeOrder(customerId,
                List.of(new OrderRequest.ItemRequest(dishId, 2)));

        assertThat(order.getCustomerId()).isEqualTo(customerId);
        assertThat(order.getCustomerName()).isEqualTo("张三");
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.getTotalPrice()).isEqualByComparingTo("56.00");
        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getItems().get(0).getDishName()).isEqualTo("宫保鸡丁");
    }

    @Test
    void placeOrder_fails_whenDishMissing_andNothingSaved() {
        when(userServiceClient.getCustomer(customerId))
                .thenReturn(new CustomerResponse(customerId, "张三", "13800000001"));
        when(dishServiceClient.getDish("dish-missing"))
                .thenThrow(new NotFoundException("菜品不存在: dish-missing"));

        assertThatThrownBy(() -> orderService.placeOrder(customerId,
                List.of(new OrderRequest.ItemRequest("dish-missing", 1))))
                .isInstanceOf(NotFoundException.class);

        verify(orderRepository, never()).save(any());
    }

    @Test
    void placeOrder_fails_whenDishUnavailable_andNothingSaved() {
        when(userServiceClient.getCustomer(customerId))
                .thenReturn(new CustomerResponse(customerId, "张三", "13800000001"));
        when(dishServiceClient.getDish(dishId))
                .thenReturn(new DishResponse(dishId, "下架菜", "川菜", new BigDecimal("10.00"), false));

        assertThatThrownBy(() -> orderService.placeOrder(customerId,
                List.of(new OrderRequest.ItemRequest(dishId, 1))))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("不可售");

        verify(orderRepository, never()).save(any());
    }

    @Test
    void placeOrder_fails_whenUserServiceDown_andNothingSaved() {
        when(userServiceClient.getCustomer(customerId))
                .thenThrow(new ServiceUnavailableException("无法连接用户服务"));

        assertThatThrownBy(() -> orderService.placeOrder(customerId,
                List.of(new OrderRequest.ItemRequest(dishId, 1))))
                .isInstanceOf(ServiceUnavailableException.class);

        verify(orderRepository, never()).save(any());
    }
}
