---
layout: docsplus 
title: "Football teams"
realization: examples/FootballTeams.scala
---

### Task: 
Сколько человек в каждой футбольной сборной имеют более одного гражданства и что это за гражданства?

За основу возьмем данные с сайта [transfermarkt.com](https://www.transfermarkt.com/):
- [рейтинг сборных](https://www.transfermarkt.com/statistik/weltrangliste/statistik)
- [страница сборной страны](https://www.transfermarkt.com/belgien/startseite/verein/3382)
- [страница игрока](https://www.transfermarkt.com/dedryck-boyata/profil/spieler/88262)
- На странице игрока есть поле **Citizenship**, которое предоставляет необходимую информацию

### List of football/soccer teams

Согласно [доке _ScalaTest + Selenium_](https://www.scalatest.org/plus/selenium) переход на заданную страницу осуществляется так: 

```scala
class RankingListPage(implicit val webDriver: WebDriver) extends Page {
   val url = "https://www.transfermarkt.com/statistik/weltrangliste/statistik"
}

val rankingListPage = new RankingListPage()
go to rankingListPage
```

`RankingListPage` должен реализовывать `trait org.scalatestplus.selenium.Page`

Далее нужно дождаться окончания загрузки страницы. Возьмем кнопку **Compact** и дождемся, когда она станет видима. 
Xpath локатор кнопки будет таким:
```scala
val compactQuery: Query = xpath("//div[.='Compact']")
```
   
Ожидание видимости элемента осуществляется так (`timeout` можно задать в конфиге, `query` - заданный элемент):
```scala
def webDriverWait(implicit driver: WebDriver) =
  new WebDriverWait(driver, Duration.ofSeconds(timeout))

webDriverWait(driver).until(ExpectedConditions.visibilityOfElementLocated(query.by))
```

Рассмотрим переход на закладку **Compact** (да, она уже активна, просто для примера).

Переход на закладку будет состоять из следующих шагов:
- Проверяем, активна ли закладка. Если да, то ничего не делаем - переход осуществлен. Если нет, переходим к следующему шагу
- Кликаем на закладку
- Ждём, когда закладка станет активна

Проверить, что элемент `query` содержит заданное значение в атрибуте `class` можно так:
```scala
def doesClassContain(value: String): Boolean =
    (for {
      element   <- find(query)
      attribute <- element.attribute("class")
    } yield attribute.contains(value)).contains(true)
```

Клик на элемент проходит стандартно: `clickOn(query)`

Ожидание, когда атрибут `class` элемента будет содержать заданное значение, можно реализовать так:
```scala
def waitClassContain(value: String): Boolean =
   webDriverWait(driver).until(ExpectedConditions.attributeContains(query.by, "class", value))
```

Итого:
```scala
def clickCompact(): Unit =
    if (!compactQuery.doesClassContain("active")) {
      clickOn(compactQuery)
      val _ = compactQuery.waitClassContain("active")
    }
```

Теперь, когда мы находимся на закладке **Compact** можно начать вычисление списка всех сборных. 
Для этого нужно последовательно пройти все страницы рейтинга и с каждой считать список.
   
Логика будет такой:
- Проверяем, достигли ли последней страницы
- Если нет, считываем список со страницы
- Переходим на следующую страницу и возвращаемся в первый пункт
- Если дошли до последней страницы, то считываем список с неё

```scala
case class Country(name: String, url: Option[String])

val urls = scala.collection.mutable.ArrayBuffer.empty[Country]
while (rankingListPage.nextPageQuery.isPresent) {
  urls ++= rankingListPage.countriesList().toBuffer
  rankingListPage.clickNextPage()
}
urls ++= rankingListPage.countriesList().toBuffer
```

Для того, чтобы проверить, достигли ли мы последней страницы, достаточно проверить, есть ли кнопка перехода 
на следующую страницу (css локатор `li.naechste-seite > a`):
```scala
val nextPageQuery: Query = cssSelector("li.naechste-seite > a")

def isPresent: Boolean = find(nextPageQuery).isDefined
```

Для того, чтобы считать список стран, необходимо найти все элементы с xpath локатором `//table/tbody//a[count(*)=0]` 
и у каждого элемента считать **text** и значение атрибута **href**:
```scala
val tableQuery: Query = xpath("//table/tbody//a[count(*)=0]")

def countriesList(): Seq[Country] =
  findAll(tableQuery).map(el => Country(el.text.trim, el.attribute("href"))).toSeq
```

Для перехода на следующую страницу мало кликнуть на кнопку `nextPageQuery`, необходимо ещё дождаться, когда этот 
переход произойдет. Мы можем считать номер текущей страницы (css локатор `li.selected > a`), а после клика
на следующую страницу дождаться, когда номер текущей страницы станет на 1 больше:
```scala
val selectedPageQuery: Query = cssSelector("li.selected > a")

def clickNextPage(): Unit = {
  val nextPage = find(selectedPageQuery).map(_.text).get().toInt + 1
  clickOn(nextPageQuery)
  val _ = webDriverWait(driver).until(ExpectedConditions.textToBe(selectedPageQuery.by, nextPage.toString))
}
```

Соединяем все воедино:

```scala
import org.openqa.selenium.WebDriver
import com.github.artemkorsakov.containers.BaseContainer
import org.scalatestplus.selenium.WebBrowser._
import com.github.artemkorsakov.query.UpQuery._
import com.github.artemkorsakov.query.Waiter
import org.scalatestplus.selenium.Page
import org.openqa.selenium.support.ui.ExpectedConditions

case class Country(name: String, url: Option[String])

class RankingListPage(implicit val webDriver: WebDriver) extends Page with Waiter {
  val url                      = "https://www.transfermarkt.com/statistik/weltrangliste/statistik"
  val compactQuery: Query      = xpath("//div[.='Compact']")
  val tableQuery: Query        = xpath("//table/tbody//a[count(*)=0]")
  val nextPageQuery: Query     = cssSelector("li.naechste-seite > a")
  val selectedPageQuery: Query = cssSelector("li.selected > a")

  def clickCompact(): Unit =
    if (!compactQuery.doesClassContain("active")) {
      clickOn(compactQuery)
      val _ = compactQuery.waitClassContain("active")
    }

  def countriesList(): Seq[Country] =
    findAll(tableQuery).map(el => Country(el.text.trim, el.attribute("href"))).toSeq

  def clickNextPage(): Unit = {
    val nextPage = selectedPageQuery.normalizeSpaceText.toInt + 1
    clickOn(nextPageQuery)
    val _ = webDriverWait(webDriver).until(ExpectedConditions.textToBe(selectedPageQuery.by, nextPage.toString))
  }

  def waitLoad(): Unit = {
    val _ = compactQuery.waitVisible()
  }
}

implicit lazy val webDriver: WebDriver = ??? /** from container */

val rankingListPage = new RankingListPage()
go to rankingListPage
rankingListPage.waitLoad()
rankingListPage.clickCompact()
val urls = scala.collection.mutable.ArrayBuffer.empty[Country]
while (rankingListPage.nextPageQuery.isPresent) {
  urls ++= rankingListPage.countriesList().toBuffer
  rankingListPage.clickNextPage()
}
urls ++= rankingListPage.countriesList().toBuffer
```

Получаем следующий список (на 13 марта 2021):

| Country name | Url                                                                                    |
| ------------ |:--------------------------------------------------------------------------------------:|
| Belgium      | [link](https://www.transfermarkt.com/belgien/startseite/verein/3382)                   |
| France       | [link](https://www.transfermarkt.com/frankreich/startseite/verein/3377)                |
| Brazil       | [link](https://www.transfermarkt.com/brasilien/startseite/verein/3439)                 |
| ...          | ...                                                                                    |
| B. Virgin    | [link](https://www.transfermarkt.com/britische-jungferninseln/startseite/verein/17750) |
| Anguilla     | [link](https://www.transfermarkt.com/anguilla/startseite/verein/17748)                 |
| San Marino   | [link](https://www.transfermarkt.com/san-marino/startseite/verein/10521)               |


### Team player list

Теперь, когда у нас есть url страны можно составить список игроков сборной.