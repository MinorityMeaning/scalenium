package com.github.artemkorsakov.examples.fifa

import com.github.artemkorsakov.query.UpQuery._
import com.github.artemkorsakov.query.Waiter
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.scalatestplus.selenium.Page
import org.scalatestplus.selenium.WebBrowser._

class RankingListPage(implicit override val webDriver: WebDriver) extends ListPage with Page with Waiter {
  val url                      = "https://www.transfermarkt.com/statistik/weltrangliste/statistik"
  val itemLink: Query        = xpath("//table/tbody//a[count(*)=0]")
  val nextPageLink: Query     = cssSelector("li.naechste-seite > a")
  val selectedPageLink: Query = cssSelector("li.selected > a")

  def clickNextPage(): Unit = {
    val nextPage = selectedPageLink.normalizeSpaceText.toInt + 1
    clickOn(nextPageLink)
    val _ = webDriverWait(webDriver).until(ExpectedConditions.textToBe(selectedPageLink.by, nextPage.toString))
  }

}
