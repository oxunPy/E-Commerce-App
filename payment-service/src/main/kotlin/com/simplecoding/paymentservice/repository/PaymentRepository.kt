package com.simplecoding.paymentservice.repository

import com.simplecoding.paymentservice.domain.entity.Payment
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentRepository : JpaRepository<Payment, Long>