package com.github.artemkorsakov.tests.examples

import com.github.artemkorsakov.containers.BaseContainer
import com.github.artemkorsakov.examples.fifa._
import com.github.artemkorsakov.query.UpQuery._
import com.github.artemkorsakov.spec.Tags
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.selenium.WebBrowser._

class FootballTeams extends AnyFlatSpec with BaseContainer with Matchers with Tags {

  "FootballTeams" should "make a url list of national teams" taggedAs example in {
    val rankingListPage = new RankingListPage()
    go to rankingListPage
    rankingListPage.waitLoad()
    rankingListPage.clickCompact()
    val urls = scala.collection.mutable.ArrayBuffer.empty[NameWithLink]
    while (rankingListPage.nextPageQuery.isPresent) {
      log.info(s"Page ${rankingListPage.selectedPageQuery.normalizeSpaceText}")
      urls ++= rankingListPage.items().toBuffer
      rankingListPage.clickNextPage()
    }
    urls ++= rankingListPage.items().toBuffer
    log.info(s"Countries length - ${urls.length}")
    urls.length should be > 200
  }

  it should "make a team player list" taggedAs example in {
    val url         = "https://www.transfermarkt.com/belgien/startseite/verein/3382"
    val countryPage = CountryPage(url)
    go to countryPage
    countryPage.waitLoad()
    countryPage.clickCompact()
    val urls = countryPage.items()
    log.info(s"Players length - ${urls.length}")
    urls.length should be > 20
  }

  it should "get citizenship from the player's page" taggedAs example in {
    val url        = "https://www.transfermarkt.com/christian-benteke/profil/spieler/50201"
    val playerPage = PlayerPage(url)
    go to playerPage
    playerPage.clickProfile()
    val citizenship = playerPage.citizenship()
    log.info(s"citizenship - $citizenship")
    citizenship.should(contain("Belgium"))
  }

  it should "put it all together" taggedAs example in {}

}
