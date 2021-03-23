package com.github.artemkorsakov.conf

import pureconfig._
import pureconfig.generic.auto._
import pureconfig.generic.semiauto._
import pureconfig.module.enumeratum._

object Config {

  final case class SeleniumConf(browser: Browser = Browser.Chrome, videoLogs: String = "", timeout: Long = 30)

  final case class ServiceConf(selenium: SeleniumConf = SeleniumConf())

  implicit val configReader: ConfigReader[ServiceConf] = deriveReader[ServiceConf]

  val serviceConf: ServiceConf = ConfigSource.default.load[ServiceConf] match {
    case Right(value) => value
    case Left(value)  => throw new IllegalArgumentException(s"Invalid config:\n$value")
  }

}
