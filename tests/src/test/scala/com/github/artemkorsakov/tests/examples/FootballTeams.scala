package com.github.artemkorsakov.tests.examples

import com.github.artemkorsakov.containers.SeleniumContainerSuite
import com.github.artemkorsakov.examples.fifa._
import com.github.artemkorsakov.query.UpQuery._
import com.github.artemkorsakov.spec.Tags
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.selenium.WebBrowser._

import scala.collection.mutable.ArrayBuffer

class FootballTeams extends AnyFlatSpec with SeleniumContainerSuite with Matchers with Tags {

  "FootballTeams" should "make a url list of national teams" taggedAs example in {
    val rankingListPage = new RankingListPage()
    go to rankingListPage
    rankingListPage.waitLoad()
    rankingListPage.clickCompact()
    val urls = scala.collection.mutable.ArrayBuffer.empty[(String, Option[String])]
    while (rankingListPage.nextPageLink.isPresent) {
      log.info(s"Page ${rankingListPage.selectedPageLink.normalizeSpaceText}")
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

  it should "put it all together" taggedAs example in {
    val rankingListPage = new RankingListPage()
    go to rankingListPage
    rankingListPage.waitLoad()
    rankingListPage.clickCompact()
    val countriesUrls = scala.collection.mutable.ArrayBuffer.empty[(String, Option[String])]
    while (rankingListPage.nextPageLink.isPresent) {
      log.info(s"Ranking page ${rankingListPage.selectedPageLink.normalizeSpaceText}")
      countriesUrls ++= rankingListPage.items().toBuffer
      rankingListPage.clickNextPage()
    }
    log.info(s"Ranking page ${rankingListPage.selectedPageLink.normalizeSpaceText}")
    countriesUrls ++= rankingListPage.items().toBuffer
    log.info(s"Countries length - ${countriesUrls.length}")

    val result: ArrayBuffer[(String, Seq[Player])] = countriesUrls.flatMap {
      case (country, mayBeCountryUrl) if mayBeCountryUrl.isDefined =>
        val countryPage = CountryPage(mayBeCountryUrl.get)
        go to countryPage
        countryPage.waitLoad()
        countryPage.clickCompact()
        val playerUrls = countryPage.items()
        log.info(s"Players length - ${playerUrls.length}")

        val players: Seq[Player] = playerUrls.flatMap {
          case (playerName, mayBePlayerUrl) if mayBePlayerUrl.isDefined =>
            val playerPage = PlayerPage(mayBePlayerUrl.get)
            go to playerPage
            playerPage.clickProfile()
            val citizenship = playerPage.citizenship().filterNot(_ != country)
            val player      = Player(country, playerName, citizenship)
            log.info(s"player - $player")
            Some(player)
          case _ => None
        }

        Some((country, players))
      case _ => None
    }

    println("| Country name | Foreigners   |")
    println("| ------------ |:------------:|")
    result.foreach { case (country, players) =>
      val foreigners =
        players
          .flatMap(pl => pl.citizenship.map((_, pl.name)))
          .groupBy(_._1)
          .map { case (country, names) => s"$country${names.map(_._2).mkString("(", ", ", ")")}" }
      println(s"| $country | $foreigners")
    }
  }

}
