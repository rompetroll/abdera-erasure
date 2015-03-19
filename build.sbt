// Project name (artifact name in Maven)
name := "abdera-erasure"

version := "1.0-SNAPSHOT"

// project description
description := "Demonstrating erasure problems in Abdera"

// Do not append Scala versions to the generated artifacts
crossPaths := false

// This forbids including Scala related libraries into the dependency
autoScalaLibrary := false

// library dependencies. (orginization name) % (project name) % (version)
libraryDependencies ++= Seq(
   "org.apache.abdera" % "abdera-parser" % "1.1.3",
   "org.apache.abdera" % "abdera-core" % "1.1.3"
)

mainClass in (Compile, run) := Some("Test")
