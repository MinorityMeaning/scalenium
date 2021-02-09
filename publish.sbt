import sbt.Credentials
import sbt.Keys.credentials
//ThisBuild / organization := "com.github.artemkorsakov"
ThisBuild / organizationName := "Artem Korsakov"
ThisBuild / organizationHomepage := Some(url("https://github.com/artemkorsakov"))

/*
ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/artemkorsakov/scalenium"),
    "scm:git@github.com:artemkorsakov/scalenium.git"
  )
)
 */
ThisBuild / developers := List(
  Developer(
    id = "@artemkorsakov",
    name = "Artem Korsakov",
    email = "artemkorsakov@mail.ru",
    url = url("http://github.com/artemkorsakov")
  )
)

ThisBuild / description := "Selenium on Scala examples."
//ThisBuild / homepage := Some(url("https://artemkorsakov.github.io/scalenium/"))

//ThisBuild / pomIncludeRepository := { _ => false }
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
//ThisBuild / publishMavenStyle := true

import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._
ThisBuild / releaseCrossBuild := true
ThisBuild / releaseVersionBump := sbtrelease.Version.Bump.Next
ThisBuild / releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommand("publishSigned"),
  releaseStepCommand("sonatypeBundleRelease"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)
