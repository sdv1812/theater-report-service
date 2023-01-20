package com.sanskar.theaterreport.reporting

import com.sanskar.theaterreport.reporting.model.Booking
import org.springframework.data.jpa.repository.JpaRepository

interface BookingRepository: JpaRepository<Booking, Long>