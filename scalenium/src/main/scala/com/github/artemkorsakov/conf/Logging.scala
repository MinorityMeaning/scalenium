package com.github.artemkorsakov.conf

import com.typesafe.scalalogging._
import org.slf4j.LoggerFactory

trait Logging {
  protected val log: Logger = Logger(LoggerFactory.getLogger(this.getClass))

}
