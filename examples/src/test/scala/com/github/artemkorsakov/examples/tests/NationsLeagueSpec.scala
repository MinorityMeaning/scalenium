package com.github.artemkorsakov.examples.tests

import com.github.artemkorsakov.examples.fifa._
import com.github.artemkorsakov.spec.LocalDebugSpec
import org.scalatest.Inspectors.forAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.selenium.WebBrowser._

import scala.collection.mutable.ArrayBuffer

class NationsLeagueSpec extends AnyFlatSpec with LocalDebugSpec with Matchers {
  case class Result(number: Int = 0, group: Char = 'E', place: Int = 0, country: String = "")
  case class ParsedResult(country: String,
                          firstSeason: (Char, Int),
                          secondSeason: (Char, Int),
                          thirdSeason: Char,
                          progress: (Int, Int))

  "NationsLeagueSpec" should "make a url list of national teams" in {
    val mainPage = new MainPage
    go to mainPage

    val results: ArrayBuffer[Result] = ArrayBuffer.empty[Result]

    Seq('A', 'B', 'C', 'D').foreach(group => {
      val leagueGroupPage = mainPage.goToGroup(group.toString)
      val groupResult     = leagueGroupPage.results
      groupResult.foreach { case (place, country) => results += Result(2, group, place, country) }
      leagueGroupPage.selectPreviousSeason
      leagueGroupPage.waitLoad(group.toString)
      val previousSeasonResult = leagueGroupPage.results
      previousSeasonResult.foreach {
        case (place, country) =>
          results += Result(1, group, place, country.replace("Macedonia", "North Macedonia"))
      }
    })

    forAll(results) { result =>
      {
        result.group.shouldNot(equal('E'))
        result.place should be > 0
      }
    }

    val parsedResults = results
      .groupBy(_.country)
      .view
      .mapValues(seq => {
        val country: String           = seq.head.country
        val firstRes                  = seq.find(_.number == 1).getOrElse(Result())
        val firstSeason: (Char, Int)  = (firstRes.group, firstRes.place)
        val secondRes                 = seq.find(_.number == 2).getOrElse(Result())
        val secondSeason: (Char, Int) = (secondRes.group, secondRes.place)
        val thirdSeason: Char =
          if (secondSeason._2 == 1 && secondSeason._1 != 'A') (secondSeason._1 - 1).toChar
          else if (secondSeason._2 == 4 && secondSeason._1 != 'D') (secondSeason._1 + 1).toChar
          else secondSeason._1
        val progress: (Int, Int) = (firstSeason._1 - secondSeason._1, secondSeason._1 - thirdSeason)
        ParsedResult(country, firstSeason, secondSeason, thirdSeason, progress)
      })
      .values
      .groupBy(_.progress)

    printResult(parsedResults, (1, 1))
    printResult(parsedResults, (1, 0))
    printResult(parsedResults, (0, 1))
    printResult(parsedResults, (1, -1))
    printResult(parsedResults, (0, 0))
    printResult(parsedResults, (-1, 1))
    printResult(parsedResults, (-1, 0))
    printResult(parsedResults, (0, -1))
    printResult(parsedResults, (-1, -1))
  }

  def printResult(parsedResults: Map[(Int, Int), Iterable[ParsedResult]], progress: (Int, Int)): Unit = {
    val results = parsedResults.getOrElse(progress, Seq.empty[ParsedResult]).toSeq
    println(s"Group $progress - ${results.length}")
    println("| Country | 1st | 2nd | 3rd |\n| -----   |:----|:----|:---:|")
    results
      .sortBy(_.thirdSeason)
      .foreach(pr =>
        println(
          s"| ${pr.country} | ${pr.firstSeason._1}(${pr.firstSeason._2}) | ${pr.secondSeason._1}(${pr.secondSeason._2}) | ${pr.thirdSeason} |"))
  }
}
