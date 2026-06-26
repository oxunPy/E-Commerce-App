package com.simplecoding.orderservice.repository

import com.simplecoding.orderservice.domain.entity.Order
import com.simplecoding.orderservice.domain.entity.OrderItem
import com.simplecoding.orderservice.domain.entity.OrderStatus
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder
import org.springframework.stereotype.Repository
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement
import java.time.ZoneOffset

@Repository
class JdbcOrderRepository(
    private val jdbcTemplate: JdbcTemplate
) {
    companion object {
        private val log = LoggerFactory.getLogger(JdbcOrderRepository::class.java)

        const val SET_ORDER: String = """
            INSERT INTO orders(status, created_at, order_uuid)
            VALUES(?, ?, ?)
        """

        const val SET_ORDER_ITEM: String = """
            INSERT INTO order_items(order_id, product_id, product_name, quantity, price)
            VALUES(?, ?, ?, ?, ?)     
        """
    }

    private val orderRowMapper: RowMapper<Order?> = RowMapper { rs: ResultSet?, rowNum: Int ->
        val order = Order()
        order.id = (rs!!.getLong("id"))
        order.status = (OrderStatus.valueOf(rs.getString("status")))
        order.orderUuid = rs.getString("order_uuid")
        order
    }

    private val orderItemRowMapper: RowMapper<OrderItem?> = RowMapper { rs: ResultSet?, rowNum: Int ->
        val item = OrderItem()
        item.id = rs!!.getLong("id")
        item.productId = rs.getLong("product_id")
        item.productName = rs.getString("product_name")
        item.quantity = rs.getInt("quantity")
        item.price = rs.getBigDecimal("price")
        item
    }


    @Transactional
    fun createOrder(order: Order): Order {
        val keyHolder: KeyHolder = GeneratedKeyHolder()
        jdbcTemplate.update({ connection: Connection? ->
            val ps = connection!!.prepareStatement(SET_ORDER, Statement.RETURN_GENERATED_KEYS)
            ps.setString(1, order.status?.name)
            ps.setTimestamp(2, java.sql.Timestamp.from(order.createdAt?.toInstant(ZoneOffset.UTC)))
            ps.setString(3, order.orderUuid)
            ps
        }, keyHolder)

        val keys = keyHolder.keys
        var orderId: Long? = null

        if (keys != null) {
            if (keys.containsKey("id")) {
                orderId = (keys["id"] as Number).toLong()
            }

            for (value in keys.values) {
                if (value is Number) {
                    orderId = value.toLong()
                    break
                }
            }
        }

        if (orderId == null) {
            throw RuntimeException("Failed to get generated order id")
        }

        order.id = orderId
        if (order.items.isNotEmpty()) {
            val finalOrderId = orderId

            val batchArgs: List<Array<out Any>> = order.items.map { item ->
                arrayOf<Any>(finalOrderId, item.productId!!, item.productName!!, item.quantity ?: 0, item.price ?: 0.0)
            }

            jdbcTemplate.batchUpdate(SET_ORDER_ITEM, batchArgs)
        }

        return order
    }
}

