---
layout: docsplus 
title: "Football teams"
---

# Football teams

#### Рассмотрим пример: 
Сколько человек в каждой футбольной сборной имеют более одного гражданства и что это за гражданства?

За основу возьмем данные с сайта [transfermarkt.com](https://www.transfermarkt.com/):
- [рейтинг сборных](https://www.transfermarkt.com/statistik/weltrangliste/statistik)
- [страница сборной страны](https://www.transfermarkt.com/belgien/startseite/verein/3382)
- [страница игрока](https://www.transfermarkt.com/dedryck-boyata/profil/spieler/88262)
- На странице игрока есть поле **Citizenship**, которое предоставляет необходимую информацию

### Составление списка сборных

1) Согласно [доке](https://www.scalatest.org/plus/selenium) переход на заданную страницу осуществляется так: 
```
val rankingListPage = new RankingListPage()
go to rankingListPage
```
`RankingListPage` должен реализовывать `trait org.scalatestplus.selenium.Page`

2) Далее нужно дождаться окончания загрузки страницы. Возьмем кнопку **Compact** и дождемся, когда она станет видима.
Локатор кнопки будет таким: 
   
`val compactQuery: Query = xpath("//div[.='Compact']")`
   
Ожидание видимости элемента осуществляется так (`timeout` можно задать в конфиге, `query` - заданный элемент):
`new WebDriverWait(driver, Duration.ofSeconds(timeout)).until(ExpectedConditions.visibilityOfElementLocated(query.by))`

3) Нам нужно перейти на закладку **Compact**, но нет гарантии, что она активна 
(стартовая страница может быть изменена на ту, где по умолчанию открыта другая закладка).
   


   rankingListPage.clickCompact()
   val urls = scala.collection.mutable.ArrayBuffer.empty[Country]
   while (rankingListPage.nextPageQuery.isPresent) {
   urls ++= rankingListPage.countriesList().toBuffer
   rankingListPage.clickNextPage()
   }
   urls ++= rankingListPage.countriesList().toBuffer

   urls.length should be > 200
   log.info(urls.toString())

