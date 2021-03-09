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

Create an `reference.conf` file and add it as a resource of your application 
(`src/main/resources` or `src/test/resources` (for tests)):

```text
selenium {
    browser = "chrome"
    video-logs = ""
    timeout = 30
}
```

#### Remote run in [Testcontainers](https://github.com/testcontainers/testcontainers-scala#selenium)

- Install [docker](https://docs.docker.com/get-docker/)
- Run tests
