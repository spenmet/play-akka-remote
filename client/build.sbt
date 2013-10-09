name := "client"

version := "1.0"

organization := "bazar"

play.Project.playScalaSettings

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
 
resolvers += "OSS Sonatype Public" at "https://oss.sonatype.org/content/groups/public/"

resolvers += "OSS Sonatype releases" at "https://oss.sonatype.org/content/repositories/releases"

resolvers += "Local Maven" at Path.userHome.asFile.toURI.toURL + ".m2/repository"  

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "com.typesafe.akka"                 %% "akka-actor"               % "2.2.0",
  "com.typesafe.akka"                 %% "akka-remote"              % "2.2.0",
  "com.typesafe.akka"                 %% "akka-kernel"              % "2.2.0"
)     

