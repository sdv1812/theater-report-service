package com.sanskar.theaterreport.reporting

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.smithy.kotlin.runtime.content.ByteStream
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.PutItemRequest
import aws.smithy.kotlin.runtime.time.Instant
import aws.smithy.kotlin.runtime.time.epochMilliseconds
import com.sanskar.theaterreport.config.AWSCloudProperties
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus
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

    @RequestMapping("/generateReport/{report}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getReport(@PathVariable report: String): ResponseEntity<ReportResponse> {
        val matchedReport = reportingService::class.declaredMemberFunctions.firstOrNull { it.name == report }
        val rawResult = matchedReport?.call(reportingService)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ReportResponse("No report were found for this type"))
        if (report == "premium_bookings") {
            runBlocking {
                putItemInTable(
                    awsCloudProperties.reportDynamoDb,
                    "report_id",
                    UUID.randomUUID().toString(),
                    "timestamp",
                    Instant.now().epochMilliseconds.toString(),
                    "report",
                    rawResult.toString()
                )
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ReportResponse("Premium report was successfully saved in dynamodb"))
        }
        runBlocking {
            S3Client.fromEnvironment { region = awsCloudProperties.region }
                .use { s3 ->
                    val keyStr = "${matchedReport.name}-${
                        ZonedDateTime.now(
                            ZoneId.of("Australia/Sydney")
                        ).format(
                            DateTimeFormatter
                                .ofLocalizedDateTime(FormatStyle.LONG)
                                .withLocale(Locale.ENGLISH)
                        )
                    }"
                    s3.putObject(PutObjectRequest {
                        bucket = awsCloudProperties.reportS3Bucket
                        key = keyStr
                        body = ByteStream.fromString(rawResult.toString())
                    })

                    println("Object ${awsCloudProperties.reportS3Bucket}/$keyStr created successfully!")

                }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ReportResponse(" Report was successfully saved in S3"))
    }

    private suspend fun putItemInTable(
        tableNameVal: String,
        reportIdKey: String,
        reportIdValue: String,
        timestampKey: String,
        timestampValue: String,
        reportKey: String,
        reportValue: String
    ) {
        val itemValues = mutableMapOf<String, AttributeValue>()

        // Add all content to the table.
        itemValues[reportIdKey] = AttributeValue.S(reportIdValue)
        itemValues[timestampKey] = AttributeValue.N(timestampValue)
        itemValues[reportKey] = AttributeValue.S(reportValue)

        val request = PutItemRequest {
            tableName = tableNameVal
            item = itemValues
        }

        DynamoDbClient { region = awsCloudProperties.region }.use { ddb ->
            ddb.putItem(request)
            println(" A new item was placed into $tableNameVal.")
        }
    }

    data class ReportResponse(val msg: String)
}