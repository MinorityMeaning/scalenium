package com.github.artemkorsakov.examples.fifa

import com.github.artemkorsakov.query.UpQuery._
import com.github.artemkorsakov.query.Waiter
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.scalatestplus.selenium.Page
import org.scalatestplus.selenium.WebBrowser._

class RankingListPage(implicit override val webDriver: WebDriver) extends ListPage with Page with Waiter {
  val url                      = "https://www.transfermarkt.com/statistik/weltrangliste/statistik"
  val tableQuery: Query        = xpath("//table/tbody//a[count(*)=0]")
  val nextPageQuery: Query     = cssSelector("li.naechste-seite > a")
  val selectedPageQuery: Query = cssSelector("li.selected > a")

  def clickNextPage(): Unit = {
    val nextPage = selectedPageQuery.normalizeSpaceText.toInt + 1
    clickOn(nextPageQuery)
    val _ = webDriverWait(webDriver).until(ExpectedConditions.textToBe(selectedPageQuery.by, nextPage.toString))
  }

}
