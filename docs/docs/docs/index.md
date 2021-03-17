---
layout: docs
title: Introduction
---

## {{page.title}}

@DESCRIPTION@

## Quick Start

The library is available in the central repository - @MAVEN_BADGE@
<br>It is available on scala @SCALA_VERSIONS@.
<br>Add the following to your `build.sbt`
```scala
libraryDependencies += "com.github.artemkorsakov" %% "scalenium-core" % "@VERSION@"
```

#### Set configuration

Create a `reference.conf` file and add it as a resource of your application 
(`src/main/resources` or `src/test/resources` (for tests)):

```text
selenium {
    browser = "chrome"
    video-logs = ""
    timeout = 30
}
```

#### Create test

- Install [docker](https://docs.docker.com/get-docker/)
- Create spec extending `SeleniumContainerSuite`. For example:
  
```scala
import com.github.artemkorsakov.containers.SeleniumContainerSuite
import com.github.artemkorsakov.query.UpQuery._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.selenium.WebBrowser._

class SeleniumDriverSpec extends AnyFlatSpec with SeleniumContainerSuite {

  "Browser" should "show google" in {
    go to "https://www.google.com/"
    name("q").waitVisible()
  }
}
```

- Run spec
