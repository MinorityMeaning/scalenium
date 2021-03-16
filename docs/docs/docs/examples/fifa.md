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

Переход на заданную страницу, если она реализует trait `org.scalatestplus.selenium.Page`, может осуществляться так: 

```scala
class RankingListPage(implicit val webDriver: WebDriver) extends Page {
   val url = "https://www.transfermarkt.com/statistik/weltrangliste/statistik"
}

val rankingListPage = new RankingListPage()
go to rankingListPage
```

После перехода прежде чем работать со страницей необходимо дождаться окончания отрисовки её элементов. 
Будем ориентироваться на кнопку **Compact** и дождемся, когда она станет видима. 

<img src="./doc/src/main/resources/microsite/img/fifa_ranking_list.png" alt="Compact button"></img>

Xpath локатор кнопки будет таким:
```scala
val compactQuery: Query = xpath("//div[.='Compact']")
```
   
Ожидание видимости элемента осуществляется так (`timeout` можно задать в конфиге, `query` - заданный элемент):
```scala
new WebDriverWait(driver, Duration.ofSeconds(timeout)).until(ExpectedConditions.visibilityOfElementLocated(query.by))
```

Рассмотрим переход на закладку **Compact**.

Переход на закладку будет состоять из следующих шагов:
- Проверяем, активна ли закладка (активная закладка в данном случае в классе содержит "active").
- Если да, то ничего не делаем — переход осуществлен.
- Если нет, то кликаем на закладку и ждём, когда закладка станет активна

Проверить, что элемент `query` содержит заданное значение в атрибуте `class` можно так:
```scala
def doesClassContain(value: String): Boolean =
    (for {
      element   <- find(query)
      attribute <- element.attribute("class")
    } yield attribute.contains(value)).contains(true)
```

Кликнуть на элемент можно так: `clickOn(query)`

Ожидание, когда атрибут `class` элемента будет содержать заданное значение, можно реализовать так:
```scala
def waitClassContain(value: String): Boolean =
  new WebDriverWait(driver, Duration.ofSeconds(timeout)).until(ExpectedConditions.attributeContains(query.by, "class", value))
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
Для этого нужно последовательно обойти все страницы рейтинга и с каждой считать список.
   
Логика будет такой:
- Проверяем, достигли ли последней страницы
- Если нет, считываем список со страницы, переходим на следующую и возвращаемся в первый пункт
- Если дошли до последней страницы, то считываем список с неё

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

def items(): Seq[(String, Option[String])] =
  findAll(tableQuery).map(el => (el.text.trim, el.attribute("href"))).toSeq
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

Соединяем все воедино и получаем следующий список (на 13 марта 2021):

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

Эта задача ещё более простая, потому что страница сборной страны содержит практически все те же элементы,
что и страница рейтинга сборных. Изменения будут касаться только элемента `tableQuery`.
Ссылка на страницу игрока - это xpath локатор `//table/tbody//span[@class='hide-for-small']/a[count(*)=0]`:
```scala
val tableQuery: Query = xpath("//table/tbody//span[@class='hide-for-small']/a[count(*)=0]")
```

### Player's citizenship

Осталось только определить гражданство игрока сборной, пользуясь его страницей, найденной на предыдущем этапе.

Для начала нужно перейти на закладку Profile. Мы уже переходили на закладку на предыдущих страницах.
Здесь будет то же самое, за исключением одного нюанса: если раньше у активной закладки менялся атрибут  `class`,
то теперь этот атрибут меняется не у самой ссылки, а у её родителя. Кстати, в этот раз немецкие разработчики сайта
значение класса активного атрибута не стали переводить на английский, а оставили на немецком - "aktiv".

Поэтому создадим два элемента: ссылку и её родителя, а затем определим переход на закладку так:

```scala
val profileQuery: Query     = xpath("//li[@id='profile']")
val profileLinkQuery: Query = xpath(s"${profileQuery.queryString}/a")

def clickProfile(): Unit = 
  if (!profileQuery.doesClassContain("aktiv")) {
    clickOn(profileLinkQuery)
    val _ = profileQuery.waitClassContain("aktiv")
  }
```

Теперь осталось только определить гражданство. Для этого возьмем картинку из соответствующего поля и считаем её атрибут
"title":

```scala
val citizenshipQuery: Query = xpath("//th[.='Citizenship:']/following-sibling::td/img")

def citizenship(): Seq[String] = findAll(citizenshipQuery).flatMap(_.attribute("title")).toSeq
```

Вот и все, все страницы заполнены, осталось только написать автотест.

### Application




