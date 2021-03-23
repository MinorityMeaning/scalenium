import org.typelevel.sbtcatalysts.Libraries

object Dependencies {

  object Version {
    val Selenium       = "4.0.0-beta-2"
    val Scalatestplus  = "3.2.6.0"
    val Pureconfig     = "0.14.1"
    val ScalaLogging   = "3.9.2"
    val Logback        = "1.2.3"
    val Testcontainers = "0.39.3"
  }

  val libs: Libraries = org.typelevel.libraries
    .addJava(
      name = "selenium-java",
      version = Version.Selenium,
      org = "org.seleniumhq.selenium",
      modules = "selenium-java"
    )
    .add(
      name = "selenium-scala",
      version = Version.Scalatestplus,
      org = "org.scalatestplus",
      modules = "selenium-3-141"
    )
    .add(
      name = "pureconfig",
      version = Version.Pureconfig,
      org = "com.github.pureconfig",
      modules = "pureconfig",
      "pureconfig-enumeratum"
    )
    .add(
      name = "scala-logging",
      version = Version.ScalaLogging,
      org = "com.typesafe.scala-logging",
      modules = "scala-logging"
    )
    .addJava(name = "logback", version = Version.Logback, org = "ch.qos.logback", modules = "logback-classic")
    .add(
      name = "testcontainers",
      version = Version.Testcontainers,
      org = "com.dimafeng",
      modules = "testcontainers-scala-scalatest-selenium"
    )

}
