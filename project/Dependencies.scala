import sbt._

object Dependencies {

  object Version {
    val Scalatestplus = "3.2.2.0"
    val Selenium      = "4.0.0-alpha-7"
    val Pureconfig    = "0.14.0"
  }

  val root = Def.setting(
    Seq(
      "org.scalatestplus"      %% "selenium-3-141"        % Version.Scalatestplus,
      "org.seleniumhq.selenium" % "selenium-java"         % Version.Selenium,
      "com.github.pureconfig"  %% "pureconfig"            % Version.Pureconfig,
      "com.github.pureconfig"  %% "pureconfig-enumeratum" % Version.Pureconfig
    )
  )

}
