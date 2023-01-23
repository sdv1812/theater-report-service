package com.sanskar.theaterreport

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class TheaterReportApplication

fun main(args: Array<String>) {
	runApplication<TheaterReportApplication>(*args)
}
