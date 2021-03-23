package com.github.artemkorsakov.query

import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.{ JavascriptExecutor, WebDriver, WebElement }
import org.scalatestplus.selenium.WebBrowser._

case class UpQuery(query: Query)(implicit driver: WebDriver) extends Waiter {
  def isPresent: Boolean = find(query).isDefined

  def waitPresent(): WebElement =
    webDriverWait(driver).until(ExpectedConditions.presenceOfElementLocated(query.by))

  def isVisible: Boolean = find(query).exists(_.isDisplayed)

  def waitVisible(): WebElement =
    webDriverWait(driver).until(ExpectedConditions.visibilityOfElementLocated(query.by))

  def normalizeSpaceText: String =
    find(query).map(_.text.replaceAll("\\s", " ").trim).getOrElse("")

  def allTexts: IndexedSeq[String] =
    findAll(query).map(_.text.trim).toIndexedSeq

  def placeholder: Option[String] = attribute("placeholder")

  def attribute(name: String): Option[String] = find(query).flatMap(_.attribute(name))

  def doesClassContain(value: String): Boolean =
    (for {
      element   <- find(query)
      attribute <- element.attribute("class")
    } yield attribute.contains(value)).contains(true)

  def waitClassContain(value: String): Boolean =
    webDriverWait(driver).until(ExpectedConditions.attributeContains(query.by, "class", value))

  def scrollToElement(): Object = {
    val element    = driver.findElement(query.by)
    val jsExecutor = driver.asInstanceOf[JavascriptExecutor]
    jsExecutor.executeScript("arguments[0].scrollIntoView();", element)
  }

}

object UpQuery {
  implicit def query2UpQuery(query: Query)(implicit driver: WebDriver): UpQuery = UpQuery(query)
}
