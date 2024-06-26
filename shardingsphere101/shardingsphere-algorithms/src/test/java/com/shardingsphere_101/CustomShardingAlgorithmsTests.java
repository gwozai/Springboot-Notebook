package com.shardingsphere_101;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shardingsphere_101.dao.OrderMapper;
import com.shardingsphere_101.entity.OrderPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.shardingsphere.infra.hint.HintManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 自动义分片算法测试类
 *
 * @author 公众号：程序员小富
 * @date 2023/12/31 17:25
 */
@Slf4j
@DisplayName("自定义分片算法测试类")
@SpringBootTest
class CustomShardingAlgorithmsTests {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private OrderMapper orderMapper;

    @DisplayName("测试自定义标准分片算法插入数据")
    @Test
    public void orderComplexCustomAlgorithmTest() {

        int randomId = RandomUtils.nextInt();
        OrderPo order = new OrderPo();
        order.setOrderId(20L);
        order.setUserId(100L);
        order.setOrderNumber(String.valueOf(randomId));
        order.setCustomerId((long) randomId);
        order.setOrderDate(new Date());
        order.setTotalAmount(new BigDecimal("0"));
        order.setIntervalValue("2024-03-01 00:00:00");
        orderMapper.insert(order);
    }

    /**
     * 查询标准策略
     *
     * @author 公众号：程序员小富
     */
    @DisplayName("自动义分片算法-范围查询")
    @Test
    public void queryTableTest() {
        QueryWrapper<OrderPo> queryWrapper = new QueryWrapper<OrderPo>()
//                .le("order_id", 10)
//                        .ge("order_id", 1)
                        .eq("user_id", 1);
        queryWrapper.orderByAsc("order_id");
//                .eq("id", 1771023476480950274L);
        List<OrderPo> orderPos = orderMapper.selectList(queryWrapper);
        log.info("查询结果：{}", JSON.toJSONString(orderPos));
    }

    /**
     * Hint 强制路由查询
     *
     * @author 公众号：程序员小富
     */
    @DisplayName("Hint 自动义分片算法-范围查询")
    @Test
    public void queryHintTableTest() {

        HintManager hintManager = HintManager.getInstance();
        // 指定分表时的分片值
        hintManager.addTableShardingValue("t_order",2L);
        // 指定分库时的分片值
        hintManager.addDatabaseShardingValue("t_order", 100L);

        QueryWrapper<OrderPo> queryWrapper = new QueryWrapper<OrderPo>()
                .eq("user_id", 20).eq("order_id", 10);
        List<OrderPo> orderPos = orderMapper.selectList(queryWrapper);
        log.info("查询结果：{}", JSON.toJSONString(orderPos));
    }
}
