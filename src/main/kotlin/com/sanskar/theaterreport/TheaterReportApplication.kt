package com.sanskar.theaterreport

import com.sanskar.theaterreport.config.AWSCloudProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(AWSCloudProperties::class)
class TheaterReportApplication

fun main(args: Array<String>) {
	runApplication<TheaterReportApplication>(*args)
}
