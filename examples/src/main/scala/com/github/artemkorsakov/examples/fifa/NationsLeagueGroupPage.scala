package com.github.artemkorsakov.examples.fifa

import com.github.artemkorsakov.query.Waiter
import org.openqa.selenium.{ By, WebDriver }
import org.openqa.selenium.support.ui.ExpectedConditions
import org.scalatestplus.selenium.WebBrowser._
import com.github.artemkorsakov.query.UpQuery._

import scala.collection.mutable
import scala.jdk.CollectionConverters._

class NationsLeagueGroupPage(implicit val webDriver: WebDriver) extends Waiter {
  val header: Query         = cssSelector("div.dataName > h1")
  val resultRow: Query      = xpath("//table//tr[td[@class='rechts']]")
  val selectSeason: Query   = cssSelector("a.chzn-single > div > b")
  val previousSeason: Query = xpath("//li[.='18/19']")
  val show: Query           = cssSelector("input[value='Show']")

  def results: mutable.Buffer[(Int, String)] =
    webDriver.findElements(resultRow.by).asScala.map { el =>
      {
        val place = el.findElement(By.xpath(".//td[@class='rechts']")).getText
        val name  = el.findElement(By.xpath(".//td[contains(@class, 'hauptlink')]")).getText
        (place.toInt, name)
      }
    }

  def selectPreviousSeason: Boolean = {
    selectSeason.waitVisible().click()
    previousSeason.waitVisible().click()
    previousSeason.waitNotVisible()
    clickOn(show)
    webDriverWait(webDriver).until(wd => wd.getCurrentUrl.endsWith("saison_id=2018"))
  }

  def waitLoad(group: String): Boolean =
    webDriverWait(webDriver).until(ExpectedConditions.textToBe(header.by, s"UEFA Nations League $group"))

}
