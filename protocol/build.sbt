name := "protocol"
 
version := "1.0"

organization := "bazar"
 
scalaVersion := "2.10.2"
 
scalacOptions ++= Seq("-deprecation", "-feature")

org.scalastyle.sbt.ScalastylePlugin.Settings

net.virtualvoid.sbt.graph.Plugin.graphSettings

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
 
resolvers += "OSS Sonatype Public" at "https://oss.sonatype.org/content/groups/public/"

resolvers += "OSS Sonatype releases" at "https://oss.sonatype.org/content/repositories/releases"

resolvers += "Local Maven" at Path.userHome.asFile.toURI.toURL + ".m2/repository"

resolvers += "Mckesson Nexus" at "http://tempest.fmgtech.com:8081/nexus/content/groups/public"

libraryDependencies ++= Seq(
      "org.scalatest"                     %% "scalatest"                     % "1.9.1"           % "test",
      "junit"                             %  "junit"                         % "4.8.1"           % "test"
)