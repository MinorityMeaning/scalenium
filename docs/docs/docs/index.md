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

Create an `application.conf` file and add it as a resource of your application 
(`src/main/resources` or `src/test/resources` (for tests)):

```text
selenium {
    is-remote = false
    hub = "http://localhost:4444/wd/hub"
    browser = "chrome"
    timeout = 30
}
```

#### Download drivers for local run

#### Remote run in Docker

