name := "masterprojekt_sigspatial"

version := "1.0"

scalaVersion := "2.10.5"

libraryDependencies += "org.apache.spark" %% "spark-core" % "1.6.1" % "provided"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "1.6.1" % "provided"
libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "2.12.0"
libraryDependencies += "org.scalactic" %% "scalactic" % "2.2.6" % "test"
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test"
libraryDependencies += "com.typesafe" % "config" % "1.3.0"
libraryDependencies += "org.apache.commons" % "commons-math3" % "3.2"
libraryDependencies += "com.databricks" %% "spark-csv" % "1.4.0"
