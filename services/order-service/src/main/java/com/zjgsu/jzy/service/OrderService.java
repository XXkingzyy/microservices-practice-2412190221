package com.zjgsu.jzy.service;

import com.zjgsu.jzy.client.DishServiceClient;
import com.zjgsu.jzy.client.UserServiceClient;
import com.zjgsu.jzy.dto.CustomerResponse;
import com.zjgsu.jzy.dto.DishResponse;
import com.zjgsu.jzy.dto.OrderRequest;
import com.zjgsu.jzy.exception.ConflictException;
import com.zjgsu.jzy.exception.NotFoundException;
import com.zjgsu.jzy.model.Order;
import com.zjgsu.jzy.model.OrderItem;
import com.zjgsu.jzy.model.OrderStatus;
import com.zjgsu.jzy.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * 下单业务逻辑（跨服务流程）：
 * 1. 调用 user-service 校验顾客存在，获取姓名快照
 * 2. 逐项调用 menu-service 校验菜品存在且可售，获取名称与单价快照
 * 3. 全部校验通过后才写入本地订单库；任一校验失败 → 抛出明确错误，不写入任何本地数据
 *
 * 业务规则：
 * - 菜品不存在 → 404；菜品不可售 → 409；依赖服务不可用 → 503
 * - 订单总价由后端按明细累加计算，不接受前端传入
 * - 只有 PENDING 状态的订单可以确认或取消；已确认/完成的订单不能取消
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserServiceClient userServiceClient;
    private final DishServiceClient dishServiceClient;

    public OrderService(OrderRepository orderRepository,
                        UserServiceClient userServiceClient,
                        DishServiceClient dishServiceClient) {
        this.orderRepository = orderRepository;
        this.userServiceClient = userServiceClient;
        this.dishServiceClient = dishServiceClient;
    }

    /**
     * 下单：先做全部跨服务校验，再一次性保存本地订单
     */
    @Transactional
    public Order placeOrder(String customerId, List<OrderRequest.ItemRequest> items) {
        // 1. 跨服务校验顾客，获取姓名快照
        CustomerResponse customer = userServiceClient.getCustomer(customerId);

        // 2. 构造订单（先不落库，收集明细）
        Order order = new Order(UUID.randomUUID().toString(), customerId,
                customer.name(), OrderStatus.PENDING, BigDecimal.ZERO, Instant.now());

        // 3. 逐项跨服务校验菜品并累加总价
        BigDecimal total = BigDecimal.ZERO;
        for (OrderRequest.ItemRequest itemReq : items) {
            DishResponse dish = dishServiceClient.getDish(itemReq.dishId());
            if (!dish.available()) {
                throw new ConflictException("菜品已下架不可售: " + dish.name());
            }
            OrderItem item = new OrderItem(UUID.randomUUID().toString(), order,
                    dish.id(), dish.name(), dish.price(), itemReq.quantity());
            order.addItem(item);
            total = total.add(item.subtotal());
        }
        order.setTotalPrice(total);

        // 4. 全部校验通过后，一次性写入本地订单库
        return orderRepository.save(order);
    }

    /** 查询全部订单（只读事务内序列化，避免 items 懒加载异常） */
    @Transactional(readOnly = true)
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    /** 按 ID 查询订单（只读事务内序列化，避免 items 懒加载异常） */
    @Transactional(readOnly = true)
    public Order findById(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("订单不存在: " + id));
    }

    /** 确认订单：仅 PENDING 状态可确认 */
    @Transactional
    public Order confirm(String id) {
        Order order = findById(id);
        if (!order.isPending()) {
            throw new ConflictException("只有 PENDING 状态的订单可以确认，当前状态: " + order.getStatus());
        }
        order.confirm();
        return orderRepository.save(order);
    }

    /** 完成订单：仅 CONFIRMED 状态可完成 */
    @Transactional
    public Order complete(String id) {
        Order order = findById(id);
        if (!order.isConfirmed()) {
            throw new ConflictException("只有 CONFIRMED 状态的订单可以完成，当前状态: " + order.getStatus());
        }
        order.complete();
        return orderRepository.save(order);
    }

    /** 取消订单：仅 PENDING 状态可取消 */
    @Transactional
    public void cancel(String id) {
        Order order = findById(id);
        if (!order.isPending()) {
            throw new ConflictException("只有 PENDING 状态的订单可以取消，当前状态: " + order.getStatus());
        }
        order.cancel();
        orderRepository.save(order);
    }
}
