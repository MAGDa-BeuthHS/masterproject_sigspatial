# Master Projekt: SIGSPATIAL

plan: build/assemble locally, copy to cluster, run on cluster

## build locally

* install sbt
* _install dependencies?_
* assemble: `sbt assembly`

## copy to cluster

* use scp: `scp target/scala-2.11/masterprojekt_sigspatial-assembly-1.0.jar sXXXXX@141.64.5.200:/data/home/sXXXXX/mp.jar`
* protip: insert correct username and use correct artifact version

## run on cluster

basic invocation scheme:

    spark-submit [spark properties] --class [submission class] [submission jar] [path to input] [path to output] [cell size in degrees] [time step size in days]

* ssh into cluster and in homedir run: `spark-submit --class Main mp.jar yellow_tripdata_2015-01.csv ./ 0.01 7`
* remember: spark currently only prints to stdout. **lose the connection and you'll lose your output.**
