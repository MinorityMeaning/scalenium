package com.github.artemkorsakov.spec

import org.scalatest.Tag

trait Tags {
  val healthCheckTest: Tag = Tag("HealthCheckTest")
  val example: Tag         = Tag("example")
}
