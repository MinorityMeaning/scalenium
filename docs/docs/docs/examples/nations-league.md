---
layout: docsplus
title: "Nations league"
realization: test/scala/com/github/artemkorsakov/examples/tests/NationsLeagueSpec.scala
---

### Task: Какой самый стремительный взлет в Лиги наций УЕФА?

Т.к. цель этой статьи показать, как пишутся Selenium-автотесты на Scala, а не поставить интригующий вопрос, 
продержать читателя в неведении, а в конце статьи дать неожиданный ответ, то дальше будут спойлеры.
С момента запуска Лиги наций УЕФА прошло целых два розыгрыша и уже можно подвести промежуточные результаты:
- Сборная России два раза подряд занимала второе место в группе дивизиона B 
(оба раза блестяще начиная и столь же блестяще-позорно заканчивая) и в третьем розыгрыше
предсказуемо выступит в том же дивизионе B - вот это стабильность (со слезами на глазах)
- Сборная Турции два раза подряд занимала последнее место в своей группе (оба раза в той же самой, где была и сборная России), 
  но в третьем розыгрыше выступит всего лишь одним дивизионом ниже - в дивизионе С
- А вот и обещанный спойлер: сборная Венгрии в первом розыгрыше заняла второе место в третьем по силе дивизионе С, 
но в третьем розыгрыше выступит в самом сильном дивизионе А вместе с топ-сборными - вот это феерический взлет. 
  Жаль что он произошел в той же самой группе, где была и сборная России.
  
А как же выступили остальные сборные? После того, как ингрига была напрочь уничтожена, можно заняться автоматизацией.

За основу возьмем данные с сайта [transfermarkt.com](https://www.transfermarkt.com/):
- [результаты сборных в Лиге наций](https://www.transfermarkt.com/uefa-nations-league-a/gesamtspielplan/pokalwettbewerb/UNLA/saison_id/2020)

#### Определимся с понятиями

В лиге наций УЕФА 4 по силе дивизиона: A (самый сильный), B, C, D. 
Победитель дивизиона поднимается в более сильный дивизион, занявший последнее место - опускается дивизионом ниже.
В первом розыгрыше в сильных дивизионах было только по 3 команды, но после этого УЕФА решила, что топ-матчей должно
быть больше и во втором розыгрыше во всех дивизионах, кроме самого слабого (D), стало выступать по 4 команды.
Именно по причине необходимой доукомплектации более сильных дивизионов после первого розыгрыша Турция, занявшая последнее место, не вылетела, 
а Венгрия, занявшая второе - поднялась выше.

Считаем результаты первых двух розыгрышей, определим с помощью спортивного принципа состав групп третьего розыгрыша
и сформируем статистику выступлений каждой сборной в следующем виде: Россия (B-B-B).

#### Страница дивизиона Лиги Наций УЕФА

Для того, чтобы перейти на страницу группы **X** необходимо выполнить следующее:
- Перейти на главную страницу сайта [transfermarkt.com](https://www.transfermarkt.com/)
- В верхнем меню нажать на пункт **Competitions**
- Во всплывающем меню выбрать пункт "UEFA Nations League **X**"

![Compact button](https://raw.githubusercontent.com/artemkorsakov/scalenium/master/docs/src/main/resources/microsite/img/examples/nations_league/menu_1.png)

Xpath локаторы для ссылок меню **Competitions** и **UEFA Nations League X** будут следующие:

```scala
val competitionsLink: Query         = xpath("//a[normalize-space(.)='Competitions']")
def groupLink(group: String): Query = xpath(s"//a[normalize-space(.)='UEFA Nations League $group']")
```

Мы вынуждены использовать функцию `normalize-space`, потому что ссылка меню **Competitions** содержит не только 
слово _Competitions_, но ещё и кучу пробелов с одним переносом строки: 

![Competitions](https://raw.githubusercontent.com/artemkorsakov/scalenium/master/docs/src/main/resources/microsite/img/examples/nations_league/competitions.png)

Именно поэтому в локаторе `//a[normalize-space(.)='Competitions']` мы ищем элемент с тэгом `a`, 
текстовое содержимое которого после нормализации пробелов равно `Competitions`.

Ожидание появления элемента query можно реализовать так:

```scala
new WebDriverWait(driver, Duration.ofSeconds(timeout)).until(ExpectedConditions.visibilityOfElementLocated(query.by))
```

в результате вернется видимый элемент WebElement, на который затем можно кликнуть и получится:

```scala
competitionsLink.waitVisible().click()
groupLink(group).waitVisible().click()
```

Конечно, после перехода на страницу группы Лиги Наций нужно подождать, когда эта страница загрузится.
Для этого возьмем элемент заглавия страницы с css-локатором `div.dataName > h1`:

![Header](https://raw.githubusercontent.com/artemkorsakov/scalenium/master/docs/src/main/resources/microsite/img/examples/nations_league/header.png)

и подождем, когда в нём отобразится название группы:

```scala
val header: Query = cssSelector("div.dataName > h1")

new WebDriverWait(driver, Duration.ofSeconds(timeout)).until(ExpectedConditions.textToBe(header.by, s"UEFA Nations League $group"))
```

#### Результаты в дивизионе

В правом нижнем углу страницы дивизиона есть результаты выступлений сборных:

![Results](https://raw.githubusercontent.com/artemkorsakov/scalenium/master/docs/src/main/resources/microsite/img/examples/nations_league/results.png)

Строка с результатами сборной будет иметь xpath-локатор `//table//tr[td[@class='rechts']]`.
Всего у нас будет до 16 строк и каждую строку нужно обработать - выудить из строки место сборной (оно необходимо,
чтобы определить состав дивизионов в третьем розыгрыше) и, конечно, название страны.

К сожалению, [Scala библиотека для Selenium](https://www.scalatest.org/plus/selenium) довольно бедная и не позволяет
искать подэлементы относительно заданного, поэтому воспользуемся Java-библиотекой:

```scala
val resultRow: Query = xpath("//table//tr[td[@class='rechts']]")

import scala.jdk.CollectionConverters._
import org.openqa.selenium._

val webDriver: WebDriver = ???

def results: mutable.Buffer[(Int, String)] =
  webDriver.findElements(resultRow.by).asScala.map { el =>
    {
      val place = el.findElement(By.xpath(".//td[@class='rechts']")).getText
      val name  = el.findElement(By.xpath(".//td[contains(@class, 'hauptlink')]")).getText
      (place.toInt, name)
    }
  }
```

Здесь происходит следующее:
- находим все элементы с xpath-локатором `//table//tr[td[@class='rechts']]`
- конвертируем `java.util.List` в `scala.collection.mutable.Buffer`
- для каждого такого элемента находим два его дочерних подэлемента: `.//td[@class='rechts']` - место в таблице,
  `.//td[contains(@class, 'hauptlink')]` - название страны
- считываем текст из дочерних элементов

Получим следующую картину, например, для дивизиона D:

| Place | Country       |
| ----- |:-------------:|
| 1     | Faroe Islands |
| 2     | Malta         |
| 3     | Latvia        |
| 4     | Andorra       |
| 1     | Gibraltar     |
| 2     | Liechtenstein |
| 3     | San Marino    |

#### Previous results

Для того, чтобы получить результаты предыдущего сезона, необходимо в фильтре **Filter by season:** выбрать 
значение **18/19** и нажать на кнопку **Show**:

![Previous](https://raw.githubusercontent.com/artemkorsakov/scalenium/master/docs/src/main/resources/microsite/img/examples/nations_league/previous.png)

Здесь будет больше действий и проверок:
- В первую очередь нужно раскрыть список доступных сезонов (нажать на кнопку с css-локатором `a.chzn-single > div > b`)
- Затем кликнуть на предыдущий сезон (нажать на ссылку с xpath-локатором `//li[.='18/19']`)
- После этого необходимо дождаться, когда скроется список доступных сезонов
- Затем необходимо нажать на кнопку **Show** (css-локатор `input[value='Show']`)
- И подождать, когда произойдет переход на страницу предыдущего сезона (для этого будем ждать, когда url текущей
  страницы станет оканчиваться на "saison_id=2018")
  
```scala
val selectSeason: Query   = cssSelector("a.chzn-single > div > b")
val previousSeason: Query = xpath("//li[.='18/19']")
val show: Query           = cssSelector("input[value='Show']")

def selectPreviousSeason: Boolean = {
  selectSeason.waitVisible().click()
  previousSeason.waitVisible().click()
  new WebDriverWait(driver, Duration.ofSeconds(timeout)).until(invisibilityOfElementLocated(previousSeason.by))
  clickOn(show)
  new WebDriverWait(driver, Duration.ofSeconds(timeout)).until(wd => wd.getCurrentUrl.endsWith("saison_id=2018"))
}
```

Теперь можно собрать все воедино и получим следующее:
- Переходим на главную страницу сайта
- Для каждой из групп 'A', 'B', 'C', 'D' выполняем:
  - Переходим в заданную группу
  - Считываем результаты второго розыгрыша
  - Сохраняем их
  - Переходим в предыдущий сезон
  - Считываем результаты первого розыгрыша
  - Сохраняем их
  
P.S. Забегая вперед, скажу, что за это время страна "Macedonia" сменила имя на "North Macedonia" - это пришлось учесть.
  
```scala
case class Result(number: Int, group: Char, place: Int, country: String)

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
```

Теперь осталось только обработать результаты.

Результаты у нас в виде коллекции `case class Result(number: Int, group: Char, place: Int, country: String)` -
приведем эту коллекцию к коллекции `case class ParsedResult(country: String, firstSeason: (Char, Int),
secondSeason: (Char, Int), thirdSeason: Char, progress: (Int, Int))`, где сезон представлен в виде 
_tuple (дивизион, итоговое место)_, а прогресс - из двух цифр, обозначающих прогресс по итогам розыгрыша:
_1 (повышение в классе) | 0 | -1 (понижение)_




```scala
import scala.collection.mutable.ArrayBuffer

case class Result(number: Int = 0, group: Char = 'E', place: Int = 0, country: String = "")
case class ParsedResult(country: String,
                        firstSeason: (Char, Int),
                        secondSeason: (Char, Int),
                        thirdSeason: Char,
                        progress: (Int, Int))

val results: ArrayBuffer[Result] = ???

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
```








##### Group (1,1) - 2

| Country | 1st | 2nd | 3rd |
| -----   |:----|:----|:---:|
| Hungary | C(2) | B(1) | A |
| Armenia | D(2) | C(1) | B |


#### Group (1,0) - 13

| Country | 1st | 2nd | 3rd |
| -----   |:----|:----|:---:|
| Denmark | B(1) | A(2) | A |
| Romania | C(2) | B(3) | B |
| Serbia | C(1) | B(3) | B |
| Scotland | C(1) | B(2) | B |
| Finland | C(1) | B(2) | B |
| Israel | C(2) | B(3) | B |
| Norway | C(1) | B(2) | B |
| Luxembourg | D(2) | C(2) | C |
| Azerbaijan | D(2) | C(3) | C |
| Georgia | D(1) | C(3) | C |
| Belarus | D(1) | C(2) | C |
| Kosovo | D(1) | C(3) | C |
| North Macedonia | D(1) | C(2) | C |


#### Group (0,1) - 8

| Country | 1st | 2nd | 3rd |
| -----   |:----|:----|:---:|
| Wales | B(2) | B(1) | A |
| Czech Republic | B(2) | B(1) | A |
| Austria | B(2) | B(1) | A |
| Slovenia | C(4) | C(1) | B |
| Albania | C(3) | C(1) | B |
| Montenegro | C(3) | C(1) | B |
| Gibraltar | D(3) | D(1) | C |
| Faroe Islands | D(3) | D(1) | C |


#### Group (1,-1) - 6

| Country | 1st | 2nd | 3rd |
| -----   |:----|:----|:---:|
| Ukraine | B(1) | A(4) | B |
| Sweden | B(1) | A(4) | B |
| Bosnia | B(1) | A(4) | B |
| Bulgaria | C(2) | B(4) | C |
| Kazakhstan | D(2) | C(4) | D |
| Moldova | D(3) | C(4) | D |


#### Group (0,0) - 20

| Country | 1st | 2nd | 3rd |
| -----   |:----|:----|:---:|
| Poland | A(3) | A(3) | A |
| Spain | A(2) | A(1) | A |
| Italy | A(2) | A(1) | A |
| Netherlands | A(1) | A(2) | A |
| England | A(1) | A(3) | A |
| Croatia | A(3) | A(3) | A |
| Belgium | A(2) | A(1) | A |
| France | A(2) | A(1) | A |
| Switzerland | A(1) | A(3) | A |
| Germany | A(3) | A(2) | A |
| Portugal | A(1) | A(2) | A |
| Russia | B(2) | B(2) | B |
| Ireland | B(3) | B(3) | B |
| Greece | C(3) | C(2) | C |
| Lithuania | C(4) | C(3) | C |
| Latvia | D(3) | D(3) | D |
| Andorra | D(4) | D(4) | D |
| Liechtenstein | D(4) | D(2) | D |
| Malta | D(4) | D(2) | D |
| San Marino | D(4) | D(3) | D |


##### Group (-1,1) - 0

| Country | 1st | 2nd | 3rd |
| -----   |:----|:----|:---:|


##### Group (-1,0) - 0

| Country | 1st | 2nd | 3rd |
| -----   |:----|:----|:---:|


##### Group (0,-1) - 6

| Country | 1st | 2nd | 3rd |
| -----   |:----|:----|:---:|
| Iceland | A(3) | A(4) | B |
| N. Ireland | B(3) | B(4) | C |
| Slovakia | B(3) | B(4) | C |
| Turkey | B(3) | B(4) | C |
| Estonia | C(4) | C(4) | D |
| Cyprus | C(3) | C(4) | D |


##### Group (-1,-1) - 0

| Country | 1st | 2nd | 3rd |
| -----   |:----|:----|:---:|


