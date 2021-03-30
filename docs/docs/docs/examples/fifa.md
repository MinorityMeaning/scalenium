---
layout: docsplus 
title: "Football teams"
realization: test/scala/com/github/artemkorsakov/examples/tests/FootballTeamsSpec.scala
---

### Task: 
Сколько человек в каждой футбольной сборной имеют более одного гражданства?

За основу возьмем данные с сайта [transfermarkt.com](https://www.transfermarkt.com/):
- [рейтинг сборных](https://www.transfermarkt.com/statistik/weltrangliste/statistik)
- [страница сборной страны](https://www.transfermarkt.com/belgien/startseite/verein/3382)
- [страница игрока](https://www.transfermarkt.com/dedryck-boyata/profil/spieler/88262)
- На странице игрока есть поле **Citizenship**, которое предоставляет необходимую информацию

### List of football/soccer teams

Переход на заданную страницу, если она реализует trait `org.scalatestplus.selenium.Page`, может осуществляться так: 

```scala
import org.scalatestplus.selenium.Page
import org.scalatestplus.selenium.WebBrowser._
import org.openqa.selenium.WebDriver

implicit def webDriver: WebDriver = ??? /* from container */

class RankingListPage(implicit val webDriver: WebDriver) extends Page {
   val url = "https://www.transfermarkt.com/statistik/weltrangliste/statistik"
}

val rankingListPage = new RankingListPage()
go to rankingListPage
```

После перехода прежде чем работать со страницей необходимо дождаться окончания отрисовки её элементов. 
Будем ориентироваться на кнопку **Compact** и дождемся, когда она станет видима. 

![Compact button](https://raw.githubusercontent.com/artemkorsakov/scalenium/master/docs/src/main/resources/microsite/img/examples/fifa/fifa_ranking_list.png)

Xpath локатор кнопки будет таким:
```scala
import org.scalatestplus.selenium.WebBrowser._

val compactTab: Query = xpath("//div[.='Compact']")
```
   
Ожидание видимости элемента осуществляется так (`timeout` можно задать в конфиге, `query` - заданный элемент):
```scala
import org.openqa.selenium._
import org.openqa.selenium.support.ui.WebDriverWait
import org.openqa.selenium.support.ui.ExpectedConditions
import org.scalatestplus.selenium.WebBrowser._
import java.time.Duration

def waitVisible(query: Query, timeout: Int)(implicit webDriver: WebDriver): WebElement =
    new WebDriverWait(webDriver, Duration.ofSeconds(timeout)).until(ExpectedConditions.visibilityOfElementLocated(query.by))
```

#### Рассмотрим переход на закладку **Compact**.

Переход на закладку будет состоять из следующих шагов:
- Проверяем, активна ли закладка (активная закладка в данном случае в атрибуте `class` содержит "active").
- Если да, то ничего не делаем — переход осуществлен.
- Если нет, то кликаем на закладку и ждём, когда закладка станет активна

Проверить, что элемент `query: Query` содержит заданное значение в атрибуте `class` можно так:
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
    if (!compactTab.doesClassContain("active")) {
      clickOn(compactTab)
      val _ = compactTab.waitClassContain("active")
    }
```

Теперь, когда мы находимся на закладке **Compact** можно начать вычисление списка всех сборных. 
Для этого нужно последовательно обойти все страницы рейтинга и с каждой считать список.
   
Логика будет такой:
- Проверяем, достигли ли последней страницы
- Если нет, считываем список со страницы, переходим на следующую и возвращаемся на пункт выше
- Если дошли до последней страницы, то считываем список с неё

![Next button](https://raw.githubusercontent.com/artemkorsakov/scalenium/master/docs/src/main/resources/microsite/img/examples/fifa/next_button.png)

Для того, чтобы проверить, достигли ли мы последней страницы, достаточно проверить, есть ли кнопка перехода 
на следующую страницу (см. скрин выше, css локатор `li.naechste-seite > a`):
```scala
val nextPageLink: Query = cssSelector("li.naechste-seite > a")

def isPresent: Boolean = find(nextPageLink).isDefined
```

Для того, чтобы считать список стран, необходимо найти все элементы с xpath локатором `//table/tbody//a[count(*)=0]`
и у каждого элемента считать **text** и значение атрибута **href**
(или `//table/tbody/tr[td[.='CONMEBOL']]//a[count(*)=0]` - если интересна только одна конфедерация, 
например, самая маленькая - CONMEBOL(Южная Америка)):

```scala
val itemLink: Query = xpath("//table/tbody//a[count(*)=0]")

def items(): Seq[(String, Option[String])] =
  findAll(itemLink).map(el => (el.text.trim, el.attribute("href"))).toSeq
```

Для перехода на следующую страницу мало кликнуть на кнопку `nextPageLink`, необходимо ещё дождаться, когда этот 
переход произойдет. 

Чтобы удостовериться, что мы перешли на следующую страницу, мы можем считать номер текущей страницы 
(css локатор `li.selected > a`), а после клика на `nextPageLink` дождаться, когда номер текущей страницы станет на 1 больше:

```scala
val selectedPageLink: Query = cssSelector("li.selected > a")

def clickNextPage(): Unit = {
  val nextPage = find(selectedPageLink).map(_.text).get().toInt + 1
  clickOn(nextPageLink)
  val _ = webDriverWait(driver).until(ExpectedConditions.textToBe(selectedPageLink.by, nextPage.toString))
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

Эта задача ещё более простая, потому что страница сборной страны содержит практически все те же необходимые нам элементы,
что и страница рейтинга сборных. Изменения будут касаться только элемента `itemLink` (ссылка на игрока).

![Player list](https://raw.githubusercontent.com/artemkorsakov/scalenium/master/docs/src/main/resources/microsite/img/examples/fifa/players_list.png)

Ссылка на страницу игрока - это xpath локатор `//table/tbody//span[@class='hide-for-small']/a[count(*)=0]`:
```scala
val itemLink: Query = xpath("//table/tbody//span[@class='hide-for-small']/a[count(*)=0]")
```

### Player's citizenship

Осталось только определить гражданство игрока сборной, пользуясь его страницей, найденной на предыдущем этапе.

Для начала нужно перейти на закладку **Profile**.

![Player list](https://raw.githubusercontent.com/artemkorsakov/scalenium/master/docs/src/main/resources/microsite/img/examples/fifa/cityzenship.png)

Мы уже переходили на закладку на предыдущих страницах.
Здесь будет то же самое, за исключением одного нюанса: если раньше у активной закладки менялся атрибут  `class`,
то теперь этот атрибут меняется не у самой ссылки, а у её родителя. Кстати, в этот раз немецкие разработчики сайта
значение атрибута `class` активного элемента не стали переводить на английский, а оставили на немецком - "aktiv":

![Aktiv](https://raw.githubusercontent.com/artemkorsakov/scalenium/master/docs/src/main/resources/microsite/img/examples/fifa/aktiv.png)

Создадим два элемента: ссылку и её родителя, а затем определим переход на закладку так:

```scala
val profileTab: Query  = xpath("//li[@id='profile']")
val profileLink: Query = xpath("//li[@id='profile']/a")

def clickProfile(): Unit = 
  if (!profileTab.doesClassContain("aktiv")) {
    clickOn(profileLink)
    val _ = profileTab.waitClassContain("aktiv")
  }
```

Теперь осталось только определить гражданство. Для этого возьмем элемент `img` из строки **Citizenship:** 
и считаем её атрибут "title":

```scala
val citizenshipImg: Query = xpath("//th[.='Citizenship:']/following-sibling::td/img")

def citizenship(): Seq[String] = findAll(citizenshipImg).flatMap(_.attribute("title")).toSeq
```

Вот и все, все страницы заполнены, осталось только написать автотест и тогда получим следующий результат.


### Results (for Russia, Ukraine and Belarus)

| Country name | % | Foreigners   |
| ------------ |:------------:|:------------:|
| Russia | 11% (3/28) | (Brazil (1) -> (Mário Fernandes), Kyrgyzstan (1) -> (Ilzat Akhmetov), Germany (1) -> (Roman Neustädter)) |
| Ukraine | 9% (3/33) | (Brazil (2) -> (Marlos, Júnior Moraes), Hungary (1) -> (Igor Kharatin)) |
| Belarus | 4% (1/25) | (Cameroon (1) -> (Maks Ebong)) |

<br>В наших сборных только 3 натурализованных игрока (и все из Бразилии). Остальные родились в СССР.


### Results (for CONMEBOL)

| Country name | % | Foreigners   |
| ------------ |:------------:|:------------:|
| Brazil | 36% (9/25) | (Spain (3) -> (Casemiro, Bruno Guimarães, Vinícius Júnior), Italy (1) -> (Alex Telles), France (1) -> (Thiago Silva), Portugal (4) -> (Ederson, Marquinhos, Allan, Lucas Paquetá)) |
| Argentina | 57% (13/23) | (Spain (2) -> (Gonzalo Montiel, Lionel Messi), Italy (11) -> (Lucas Martínez Quarta, Wálter Kannemann, Nicolás Tagliafico, Guido Rodríguez, Rodrigo de Paul, Giovani Lo Celso, Nicolás Domínguez, Ángel Di María, Joaquín Correa, Papu Gómez, Lucas Alario)) |
| Uruguay | 51,5% (18/35) | (Spain (7) -> (José María Giménez, Sebastián Coates, Diego Godín, Agustín Oliveros, Damián Suárez, Lucas Torreira, Federico Valverde), Paraguay (1) -> (Rodrigo Muñoz), Italy (10) -> (Fernando Muslera, Martín Campaña, Sergio Rochet, Matías Viña, Franco Pizzichillo, Nahitan Nández, Matías Vecino, Giorgian de Arrascaeta, Diego Rossi, Cristhian Stuani)) |
| Colombia | 22% (6/27) | (Spain (4) -> (Jeison Murillo, Johan Mojica, James Rodríguez, Luis Suárez), Argentina (1) -> (Frank Fabra), England (1) -> (Steven Alzate)) |
| Chile | 21% (5/24) | (Haiti (1) -> (Jean Beausejour), Spain (3) -> (Claudio Bravo, Gary Medel, Fabián Orellana), Italy (1) -> (Luis Jiménez)) |
| Peru | 33% (12/36) | (Venezuela (1) -> (Carlos Ascues), Spain (3) -> (Alexander Callens, Cristian Benavente, Sergio Peña), Uruguay (1) -> (Gabriel Costa), Italy (2) -> (Luis Abram, Gianluca Lapadula), Netherlands (1) -> (Renato Tapia), Switzerland (1) -> (Jean-Pierre Rhyner), Portugal (1) -> (André Carrillo), Croatia (1) -> (Raúl Ruidíaz), Lebanon (1) -> (Matías Succar)) |
| Venezuela | 25% (7/28) | (Spain (4) -> (Roberto Rosales, Juanpi Añor, Darwin Machís, Fernando Aristeguieta), Switzerland (1) -> (Rolf Feltscher), England (1) -> (Luis Del Pino Mago), Colombia (1) -> (Jan Hurtado)) |
| Paraguay | 21% (7/33) | (Spain (1) -> (Antonio Sanabria), Argentina (4) -> (Santiago Arzamendia, Gastón Giménez, Andrés Cubas, Raúl Bobadilla), Italy (2) -> (Antony Silva, Iván Piris)) |
| Ecuador | 12% (4/33) | (Spain (3) -> (Erick Ferigra, Pervis Estupiñán, Leonardo Campana), Argentina (1) -> (Hernán Galíndez)) |
| Bolivia | 25% (7/28) | (United States (2) -> (Adrián Jusino, Antonio Bustamante), Spain (1) -> (Jaume Cuéllar), Argentina (1) -> (Carlos Lampe), Brazil (1) -> (Marcelo Moreno), Switzerland (1) -> (Boris Cespedes), Portugal (1) -> (Erwin Sánchez)) |

<br>А вот в Южной Америке людей с двойным гражданством довольно много.
Впрочем, это неудивительно: в чемпионатах ЕС жесткий лимит на легионеров (в заявке только 3 игрока с гражданством не ЕС),
поэтому южноамериканцам, чтобы попасть в Европу, приходится либо пытаться получить гражданство бывшей митрополии 
(Бразилия -> Португалия, остальные -> Испания), либо искать среди своих предков итальянцев.
Второе не так сложно, как кажется. Во время Второй Мировой войны Южная Америка хоть и была на бумаге нейтральной, 
по факту разделилась на два лагеря: Бразилия -> союзники, Аргентина + Уругвай -> фашисты.
Поэтому неудивительно, что после 1945 года многие итальянцы в поисках лучшей жизни иммигрировали 
из разрушенной фашисткой Италии в симпатизировавшим ей Аргентине и Уругваю.
Поэтому современному аргентинцу или уругвайцу получить гражданство Италии не сложнее, чем человеку по фамилии 
Зильберман - гражданство Израиля - кто-нибудь среди предков нужной национальности да найдётся!
