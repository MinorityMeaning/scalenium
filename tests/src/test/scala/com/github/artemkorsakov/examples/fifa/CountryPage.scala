package com.github.artemkorsakov.examples.fifa

import com.github.artemkorsakov.query.Waiter
import org.openqa.selenium.WebDriver
import org.scalatestplus.selenium.Page
import org.scalatestplus.selenium.WebBrowser._

case class CountryPage(url: String)(implicit override val webDriver: WebDriver) extends ListPage with Page with Waiter {
  val tableQuery: Query = xpath("//table/tbody//span[@class='hide-for-small']/a[count(*)=0]")

}
