package com.github.artemkorsakov.examples.fifa

import com.github.artemkorsakov.query.UpQuery._
import org.openqa.selenium.WebDriver
import org.scalatestplus.selenium.WebBrowser._

abstract class ListPage(implicit val webDriver: WebDriver) {
  val compactTab: Query = xpath("//div[.='Compact']")
  val itemLink: Query

  def clickCompact(): Unit =
    if (!compactTab.doesClassContain("active")) {
      clickOn(compactTab)
      val _ = compactTab.waitClassContain("active")
    }

  def items(): Seq[(String, Option[String])] =
    findAll(itemLink).map(el => (el.text.trim, el.attribute("href"))).toSeq

  def waitLoad(): Unit = {
    val _ = compactTab.waitVisible()
  }

}
