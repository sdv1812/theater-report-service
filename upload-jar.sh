#! /bin/sh

./gradlew clean bootJar

aws s3api create-bucket --bucket theater-report-jar --region ap-southeast-2 --create-bucket-configuration LocationConstraint=ap-southeast-2
aws s3api create-bucket --bucket theater-jar-archive --region ap-southeast-2 --create-bucket-configuration LocationConstraint=ap-southeast-2

aws s3 mv s3://theater-report-jar s3://theater-jar-archive --recursive

aws s3 cp build/libs/theater-report-*.jar s3://theater-report-jar
