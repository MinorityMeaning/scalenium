package com.github.artemkorsakov.tests.examples

import com.github.artemkorsakov.containers.BaseContainer
import com.github.artemkorsakov.examples.fifa.{ Country, RankingListPage }
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.selenium.WebBrowser._
import com.github.artemkorsakov.query.UpQuery._

class FootballTeams extends AnyFlatSpec with BaseContainer with Matchers {

  "FootballTeams" should "make a url list of national teams" in {
    val rankingListPage = new RankingListPage()
    go to rankingListPage
    rankingListPage.waitLoad()
    rankingListPage.clickCompact()
    val urls = scala.collection.mutable.ArrayBuffer.empty[Country]
    while (rankingListPage.nextPageQuery.isPresent) {
      log.info(s"Page ${rankingListPage.selectedPageQuery.normalizeSpaceText}")
      urls ++= rankingListPage.countriesList().toBuffer
      rankingListPage.clickNextPage()
    }
    urls ++= rankingListPage.countriesList().toBuffer

    urls.length should be > 200
  }

}
