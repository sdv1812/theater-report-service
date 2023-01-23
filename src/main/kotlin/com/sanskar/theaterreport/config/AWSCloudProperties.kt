package com.sanskar.theaterreport.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan

@ConfigurationProperties(prefix = "cloud.aws")
@ConfigurationPropertiesScan
data class AWSCloudProperties(val region: String,
                              val reportS3Bucket: String)
