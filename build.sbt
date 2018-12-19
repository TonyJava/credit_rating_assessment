name := "credit_rating_assessment"

version := "0.1"

scalaVersion := "2.10.7"


resolvers ++= Seq(
  "cloudera" at "https://repository.cloudera.com/content/repositories/releases/",
  "alibaba" at "http://maven.aliyun.com/nexus/content/groups/public/",
  "spring" at "http://repo.spring.io/plugins-release/"
)


// Spark-Core
libraryDependencies += "org.apache.spark" %% "spark-core" % "1.6.0-cdh5.13.2" /*% "provided"*/

// Spark-Sql
libraryDependencies += "org.apache.spark" %% "spark-sql" % "1.6.0-cdh5.13.2" /*% "provided"*/

// MySQL - JDBC
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.46"

libraryDependencies += "org.apache.spark" %% "spark-hive" % "1.6.0-cdh5.13.2" // % "provided"

libraryDependencies += "org.apache.hive" % "hive-exec" % "1.1.0-cdh5.13.2"// % "provided"