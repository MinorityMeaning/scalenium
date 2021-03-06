import sbt.{ url, Credentials }
import sbt.Keys.credentials
ThisBuild / organization := "com.github.artemkorsakov"
ThisBuild / organizationName := "Artem Korsakov"
ThisBuild / organizationHomepage := Some(url("https://github.com/artemkorsakov"))

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/artemkorsakov/scalenium"),
    "scm:git@github.com:artemkorsakov/scalenium.git"
  )
)

ThisBuild / description := "Selenium on Scala examples."
ThisBuild / homepage := Some(url("https://artemkorsakov.github.io/scalenium/"))
ThisBuild / licenses += apache

ThisBuild / credentials += Credentials(
  "Sonatype Nexus Repository Manager",
  "oss.sonatype.org",
  sys.env.getOrElse("SONATYPE_USER", ""),
  sys.env.getOrElse("SONATYPE_PASSWORD", "")
)

import xerial.sbt.Sonatype._
ThisBuild / sonatypeSessionName := s"[sbt-sonatype] ${name.value} ${version.value}"
ThisBuild / sonatypeProjectHosting := Some(GitHubHosting("artemkorsakov", "scalenium", "artemkorsakov@mail.ru"))
ThisBuild / sonatypeProfileName := "com.github.artemkorsakov"
ThisBuild / publishTo := sonatypePublishToBundle.value
ThisBuild / sonatypeTimeoutMillis := 2 * 60 * 1000

import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._
run / javaOptions += "-Xmx8G"
ThisBuild / releaseCrossBuild := true
ThisBuild / releaseVersionBump := sbtrelease.Version.Bump.Next
ThisBuild / releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  releaseStepCommandAndRemaining("+test"),
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommand("publishSigned"),
  releaseStepCommand("sonatypeBundleRelease"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)
