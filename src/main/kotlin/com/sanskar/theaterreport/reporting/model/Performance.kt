package com.sanskar.theaterreport.reporting.model



import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
data class Performance(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long,
    val title: String,
) {
    @OneToMany(mappedBy = "performance")
    lateinit var bookings: List<Booking>
}
