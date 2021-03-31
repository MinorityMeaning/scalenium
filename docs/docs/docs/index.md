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
libraryDependencies += "com.github.artemkorsakov" %% "scalenium" % "@VERSION@"
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


#### Additional methods for Query

```scala
import com.github.artemkorsakov.query.UpQuery._

val query: Query = ???

// Does the element exist on the page?
query.isPresent // Boolean

// Waiting for the element to present on the page
query.waitPresent() // WebElement 

// Does the element visible on the page?
query.isVisible // Boolean

// Waiting for the element to visible on the page
query.waitVisible() // WebElement 

// Wait for the element to become invisible on the page
query.waitNotVisible() // Boolean
  
// The text content of the element after whitespace normalization.
// For example: if element's text is "\n\t Press \n \n enter \n\n" normalizeSpaceText returns "Press enter"
query.normalizeSpaceText // String

// The text content of all elements with the given locator
query.allTexts // IndexedSeq[String]

// Element attribute
query.attribute(name: String) // Option[String]

// Placeholder attribute of the element
query.placeholder // Option[String]

// Does element's attribute "class" contain the given value?
query.doesClassContain(value: String) // Boolean

// Waiting for element's attribute "class" to contain the given value
query.waitClassContain(value: String) // Boolean

// Scroll to element
query.scrollToElement() // Object 

```
