import sbt._

object Dependencies {

  object Version {
    val Scala213 = "2.13.4"
    val Scala212 = "2.12.13"

    val Scalatestplus  = "3.2.6.0"
    val Selenium       = "4.0.0-beta-1"
    val Pureconfig     = "0.14.0"
    val Logback        = "1.2.3"
    val ScalaLogging   = "3.9.2"
    val Testcontainers = "0.39.3"
  }

  val root = Def.setting(
    Seq(
      "org.scalatestplus"          %% "selenium-3-141"                          % Version.Scalatestplus,
      "org.seleniumhq.selenium"    % "selenium-java"                            % Version.Selenium,
      "com.github.pureconfig"      %% "pureconfig"                              % Version.Pureconfig,
      "com.github.pureconfig"      %% "pureconfig-enumeratum"                   % Version.Pureconfig,
      "ch.qos.logback"             % "logback-classic"                          % Version.Logback,
      "com.typesafe.scala-logging" %% "scala-logging"                           % Version.ScalaLogging,
      "com.dimafeng"               %% "testcontainers-scala-scalatest-selenium" % Version.Testcontainers
    )
  )

}
