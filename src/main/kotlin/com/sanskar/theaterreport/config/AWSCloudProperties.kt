package com.sanskar.theaterreport.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "cloud.aws")
data class AWSCloudProperties(val region: String,
                              val reportS3Bucket: String,
                              val reportDynamoDb: String)
