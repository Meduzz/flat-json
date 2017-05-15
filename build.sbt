name := "flat-json"

organization := "se.kodiak.tools"

version := "20170515"

scalaVersion := "2.11.11"

credentials += Credentials(Path.userHome / ".ivy2" / ".tools")

publishTo := Some("se.kodiak.tools" at "http://yamr.kodiak.se/maven")

publishArtifact in (Compile, packageDoc) := false

libraryDependencies ++= Seq(
	"org.json4s" %% "json4s-native" % "3.5.2",
	"org.scalatest" %% "scalatest" % "3.0.1" % "test"
)