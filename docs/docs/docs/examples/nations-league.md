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

Получим следующую картину для дивизиона D:

| Place | Country       |
| ----- |:-------------:|
| 1     | Faroe Islands |
| 2     | Malta         |
| 3     | Latvia        |
| 4     | Andorra       |
| 1     | Gibraltar     |
| 2     | Liechtenstein |
| 3     | San Marino    |

