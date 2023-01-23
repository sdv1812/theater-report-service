package com.sanskar.theaterreport.reporting

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.smithy.kotlin.runtime.content.ByteStream
import com.sanskar.theaterreport.config.AWSCloudProperties
import kotlinx.coroutines.runBlocking
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import kotlin.reflect.full.declaredMemberFunctions

@RestController
@RequestMapping("/reports")
class ReportsController(
    private var reportingService: ReportsService,
    private var awsCloudProperties: AWSCloudProperties
) {

    private fun getListOfReports() = reportingService::class.declaredMemberFunctions.map { it.name }

    @RequestMapping("")
    fun main() = ResponseEntity.ok().body(getListOfReports())

    @RequestMapping("/generateReport/{report}", produces = [MediaType.TEXT_HTML_VALUE])
    fun getReport(@PathVariable report: String) {
        val matchedReport = reportingService::class.declaredMemberFunctions.firstOrNull { it.name == report }
        val rawResult = matchedReport?.call(reportingService) ?: return
        runBlocking {
            S3Client.fromEnvironment { region = awsCloudProperties.region }
                .use { s3 ->
                    val keyStr = "${matchedReport.name}-${ZonedDateTime.now(
                        ZoneId.of("Australia/Sydney")
                    ).format(
                        DateTimeFormatter
                            .ofLocalizedDateTime(FormatStyle.LONG)
                            .withLocale( Locale.ENGLISH )
                    ) }"
                    s3.putObject(PutObjectRequest {
                        bucket = awsCloudProperties.reportS3Bucket
                        key = keyStr
                        body = ByteStream.fromString(rawResult.toString())
                    })

                    println("Object ${awsCloudProperties.reportS3Bucket}/$keyStr created successfully!")

                }
        }
    }
}