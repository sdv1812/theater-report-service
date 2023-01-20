package com.sanskar.theaterreport.reporting

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.smithy.kotlin.runtime.content.ByteStream
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import kotlin.reflect.full.declaredMemberFunctions

@RestController
@RequestMapping("/reports")
class ReportsController {



    @Autowired
    lateinit var reportingService: ReportsService
    private fun getListOfReports() = reportingService::class.declaredMemberFunctions.map { it.name }

    @RequestMapping("")
    fun main() = ResponseEntity.ok().body(getListOfReports())

    @RequestMapping("/generateReport/{report}", produces = [MediaType.TEXT_HTML_VALUE])
    fun getReport(@PathVariable report: String) {
        val matchedReport = reportingService::class.declaredMemberFunctions.firstOrNull { it.name == report }
        val result = matchedReport?.call(reportingService).toString()
        runBlocking {
            S3Client.fromEnvironment { region = REGION }
                .use { s3 ->

                    s3.putObject(PutObjectRequest {
                        bucket = BUCKET
                        key = KEY
                        body = ByteStream.fromString(result)
                    })

                    println("Object ${BUCKET}/$KEY created successfully!")

                }
        }
    }

    companion object {
        const val REGION = "us-west-2"
        val KEY = "key-${UUID.randomUUID()}"
        const val BUCKET = "bucket-sanskar"
    }
}