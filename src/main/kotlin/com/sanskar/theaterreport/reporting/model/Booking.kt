package com.sanskar.theaterreport.reporting.model

import jakarta.persistence.*

@Entity
data class Booking(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long,
    val customerName: String
) {
    @ManyToOne
    lateinit var seat: Seat
    @ManyToOne
    lateinit var performance: Performance
}
