package com.github.artemkorsakov.conf

import pureconfig._
import pureconfig.generic.auto._
import pureconfig.generic.semiauto._

object Config {

  /** @param isRemote true if tests will run in Selenium Hub
    * @param hub Selenium Hub's url
    * @param browser the browser in which the tests will run
    * @param timeout element timeout
    */
  final case class Selenium(isRemote: Boolean, hub: String, browser: String, timeout: Long)

  final case class ServiceConf(selenium: Selenium)

  implicit val configReader: ConfigReader[ServiceConf] = deriveReader[ServiceConf]

  val serviceConf: ServiceConf = ConfigSource.default.load[ServiceConf] match {
    case Right(value) => value
    case Left(value)  => throw new IllegalArgumentException(s"Invalid config:\n$value")
  }

}
