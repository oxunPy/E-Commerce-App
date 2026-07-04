package com.simplecoding.paymentservice.domain.entity

import com.simplecoding.paymentservice.domain.PaymentStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "payment")
class Payment() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    var status: PaymentStatus = PaymentStatus.PENDING

    @Column(name = "order_id")
    var orderId: Long? = null

    @Column(name = "amount", nullable = false)
    var amount: BigDecimal? = null

    @CreatedDate
    @Column(name = "created_at", columnDefinition = "timestamp DEFAULT CURRENT_TIMESTAMP")
    var createdAt: LocalDateTime = LocalDateTime.now()

    constructor(orderId: Long, amount: BigDecimal) : this() {
        this.orderId = orderId
        this.amount = amount
    }

}