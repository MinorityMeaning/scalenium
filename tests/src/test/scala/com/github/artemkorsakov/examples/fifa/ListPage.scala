package com.github.artemkorsakov.examples.fifa

import com.github.artemkorsakov.query.UpQuery._
import org.openqa.selenium.WebDriver
import org.scalatestplus.selenium.WebBrowser._

abstract class ListPage(implicit val webDriver: WebDriver) {
  val compactQuery: Query = xpath("//div[.='Compact']")
  val tableQuery: Query

  def clickCompact(): Unit =
    if (!compactQuery.doesClassContain("active")) {
      clickOn(compactQuery)
      val _ = compactQuery.waitClassContain("active")
    }

  def items(): Seq[NameWithLink] =
    findAll(tableQuery).map(el => NameWithLink(el.text.trim, el.attribute("href"))).toSeq

  def waitLoad(): Unit = {
    val _ = compactQuery.waitVisible()
  }

}
