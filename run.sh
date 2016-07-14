#!/bin/bash

# USAGE:
# $1: output directory
# $2: cell size
# $3: time step size
#
# Example:
# ./run.sh ./ 0.1 0.1

# please insert correct path
JAR=/data/home/s61569/mp.jar

TIMESTAMP=$(date +%s)

OPTIONS=
OPTIONS="$OPTIONS --total-executor-cores 100"
OPTIONS="$OPTIONS --executor-memory 29g"
OPTIONS="$OPTIONS --driver-memory 10g"
OPTIONS="$OPTIONS --conf spark.task.maxFailures=20"
OPTIONS="$OPTIONS --conf spark.shuffle.io.maxRetries=20"
OPTIONS="$OPTIONS --conf spark.storage.memoryFraction=0.4"
OPTIONS="$OPTIONS --conf spark.shuffle.memoryFraction=0.4"
OPTIONS="$OPTIONS --deploy-mode client"
OPTIONS="$OPTIONS --master spark://fb6-005-200.beuth-hochschule.de:7077"

INPUT="yellow_tripdata_2015-01.csv,yellow_tripdata_2015-02.csv,yellow_tripdata_2015-03.csv,yellow_tripdata_2015-04.csv,yellow_tripdata_2015-05.csv,yellow_tripdata_2015-06.csv,yellow_tripdata_2015-07.csv,yellow_tripdata_2015-08.csv,yellow_tripdata_2015-09.csv,yellow_tripdata_2015-10.csv,yellow_tripdata_2015-11.csv,yellow_tripdata_2015-12.csv"

spark-submit $OPTIONS --class Main $JAR $1 $2 0.001 7