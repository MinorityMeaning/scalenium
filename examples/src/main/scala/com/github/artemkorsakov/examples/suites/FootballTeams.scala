package com.github.artemkorsakov.examples.suites

import com.github.artemkorsakov.containers.SeleniumContainerSuite
import com.github.artemkorsakov.examples.fifa._
import com.github.artemkorsakov.query.UpQuery._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.selenium.WebBrowser._

import scala.collection.mutable.ArrayBuffer

class FootballTeams extends AnyFlatSpec with SeleniumContainerSuite with Matchers {

  "FootballTeams" should "make a url list of national teams" in {
    val urls = calculateCountriesUrls
    log.info(s"Countries length - ${urls.length}")
    urls.length should be > 0
  }

  it should "make a team player list" in {
    val url  = "https://www.transfermarkt.com/belgien/startseite/verein/3382"
    val urls = calculatePlayersUrls(url)
    log.info(s"Players length - ${urls.length}")
    urls.length should be > 20
  }

  it should "get citizenship from the player's page" in {
    val url         = "https://www.transfermarkt.com/christian-benteke/profil/spieler/50201"
    val citizenship = calculateCitizenship(url)
    log.info(s"citizenship - $citizenship")
    citizenship.should(contain("Belgium"))
  }

  it should "put it all together" in {
    val countriesUrls = calculateCountriesUrls
    log.info(s"Countries length - ${countriesUrls.length}")

    println("| Country name | Foreigners   |")
    println("| ------------ |:------------:|")
    countriesUrls.foreach { countryWithUrl =>
      println(toMarkdownRow(countryWithUrl))
    }
  }

  it should "for Russia, Ukraine and Belarus" in {
    val countriesUrls = ArrayBuffer(
      ("Russia", Some("https://www.transfermarkt.com/russland/startseite/verein/3448")),
      ("Ukraine", Some("https://www.transfermarkt.com/ukraine/startseite/verein/3699")),
      ("Belarus", Some("https://www.transfermarkt.com/weissrussland/startseite/verein/3450"))
    )

    println("| Country name | Foreigners   |")
    println("| ------------ |:------------:|")
    countriesUrls.foreach { countryWithUrl =>
      println(toMarkdownRow(countryWithUrl))
    }
  }

  private def calculateCountriesUrls: ArrayBuffer[(String, Option[String])] = {
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
  }

  private def calculatePlayersUrls(countryUrl: String): Seq[(String, Option[String])] = {
    val countryPage = CountryPage(countryUrl)
    go to countryPage
    countryPage.waitLoad()
    countryPage.clickCompact()
    countryPage.items()
  }

  private def calculateCitizenship(playerUrl: String): Seq[String] = {
    val playerPage = PlayerPage(playerUrl)
    go to playerPage
    playerPage.clickProfile()
    playerPage.citizenship()
  }

  private def toMarkdownRow(countryWithUrl: (String, Option[String])): String = {
    val country     = countryWithUrl._1
    val playersUrls = countryWithUrl._2.map(calculatePlayersUrls).getOrElse(Seq.empty[(String, Option[String])])
    log.info(s"Players length - ${playersUrls.length}")

    val foreigners = playersUrls
      .map { case (name, playerUrl) =>
        log.info(s"Calculate citizenship for $name")
        val citizenshipWithoutGivenCountry =
          playerUrl.map(calculateCitizenship(_).filterNot(_ == country)).getOrElse(Seq.empty[String])
        (name, citizenshipWithoutGivenCountry)
      }
      .filterNot(_._2.isEmpty)
      .flatMap { case (name, seq) => seq.map((name, _)) }
      .groupBy(_._2)
      .map { case (countryName, players) =>
        val count = players.length
        val names = players.map(_._1)
        s"$countryName ($count) -> ${names.mkString("(", ", ", ")")}"
      }
      .mkString("(", ", ", ")")

    s"| $country | $foreigners |"
  }
}
