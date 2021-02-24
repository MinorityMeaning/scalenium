package com.github.artemkorsakov.query

import org.openqa.selenium.{ JavascriptExecutor, WebDriver }
import org.scalatestplus.selenium.WebBrowser._

case class UpQuery(query: Query)(implicit driver: WebDriver) {
  def normalizeSpaceText: String = find(query).map(_.text.replaceAll("\\s", " ").trim).getOrElse("")

  def placeholder: Option[String] = attribute("placeholder")

  def attribute(name: String): Option[String] = find(query).flatMap(_.attribute(name))

  def isPresent: Boolean = find(query).isDefined

  def allTexts: IndexedSeq[String] = findAll(query).map(_.text.trim).toIndexedSeq

  def doesClassContain(value: String): Boolean =
    (for {
      element   <- find(query)
      attribute <- element.attribute("class")
    } yield attribute.contains(value)).contains(true)

  def scrollToElement(): Object = {
    val element    = driver.findElement(query.by)
    val jsExecutor = driver.asInstanceOf[JavascriptExecutor]
    jsExecutor.executeScript("arguments[0].scrollIntoView();", element)
  }
}

object UpQuery {
  implicit def query2KryptoQuery(query: Query)(implicit driver: WebDriver): UpQuery = UpQuery(query)
}
