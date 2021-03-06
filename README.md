# Master Projekt: SIGSPATIAL

plan: build/assemble locally, copy to cluster, run on cluster

## build locally

* install sbt
* _install dependencies?_
* specify scala version in build.sbt file
* assemble: `sbt assembly`

## copy to cluster

* use scp: `scp target/scala-2.11/masterprojekt_sigspatial-assembly-1.0.jar sXXXXX@clusterIP:/data/home/sXXXXX/mp.jar`
* protip: insert correct username and use correct artifact version

## run on cluster

basic invocation scheme:

    spark-submit [spark properties] --class [submission class] [submission jar] [path to input] [path to output] [cell size in degrees] [time step size in millis]

* ssh into cluster and in homedir run: `spark-submit --class Main mp.jar yellow_tripdata_2015-01.csv ./ 0.001 7200000`
* after a succesful run you\'ll find your results printed to stdout and written to `~/hdfs_home/mp_out.csv`
