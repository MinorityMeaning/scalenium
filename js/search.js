// When the user clicks on the search box, we want to toggle the search dropdown
function displayToggleSearch(e) {
  e.preventDefault();
  e.stopPropagation();

  closeDropdownSearch(e);
  
  if (idx === null) {
    console.log("Building search index...");
    prepareIdxAndDocMap();
    console.log("Search index built.");
  }
  const dropdown = document.querySelector("#search-dropdown-content");
  if (dropdown) {
    if (!dropdown.classList.contains("show")) {
      dropdown.classList.add("show");
    }
    document.addEventListener("click", closeDropdownSearch);
    document.addEventListener("keydown", searchOnKeyDown);
    document.addEventListener("keyup", searchOnKeyUp);
  }
}

//We want to prepare the index only after clicking the search bar
var idx = null
const docMap = new Map()

function prepareIdxAndDocMap() {
  const docs = [  
    {
      "title": "About author",
      "url": "/scalenium/about_me.html",
      "content": "About author My name is Artem Korsakov (Артём Корсаков). I am a QA Automation Engineer. I love mathematics and algorithms. Contact with me (Russian or English) github email linkedIn"
    } ,    
    {
      "title": "Changelog",
      "url": "/scalenium/changelog.html",
      "content": "Changelog v0.1.0 Features: Create library Add Football teams example"
    } ,    
    {
      "title": "Examples",
      "url": "/scalenium/docs/examples.html",
      "content": "Examples В этом проекте разбираются примеры использования Selenium на Scala. В интернете довольно мало примеров UI-автоматизации на языке Scala по нескольким причинам: UI-автоматизация - это практически всегда тестирование “черного ящика”, а значит можно выбрать любой язык. Чаще всего выбирают языки из стандартной пятерки Selenium. Scala сложнее других языков, в частности, Java. И в Scala гораздо легче “наступить на грабли”. Очень сложно найти автоматизатора на Scala и уж тем более сложно такого специалиста обучить. Почему в примерах используется Testcontainers? Обычно для прогона Selenium тестов разворачивают Selenium Grid, но у этого способа есть несколько недостатков: Selenium Grid развернут всегда, даже когда он не используется, и в тех проектах, где нет ежеминутной прогонки автотестов (почти во всех случаях), это будет пустая трата ресурсов если один из тестов развалится, то возможны ситуации, когда на Hub подвиснут браузеры и их необходимо будет почистить, чтобы освободить место для новых прогонов. Чаще всего заметить это вовремя не удается и проблема выявляется во время следующего прогона, когда тесты начинают падать чаще всего Selenium Grid развернут на удаленной машине и находится под управлением DevOps-ов, а значит время от времени придется их дергать. DevOps-ы заняты и зачастую им нет дела до проблем автоматизаторов. И как следствие надолго задерживается прогон автотестов Поэтому хочется, чтобы Selenium разворачивался исключительно на время прогона автотестов и все ресурсы чистились сразу после прогона. Testcontainers именно это и делает - разворачивает docker image на время прогона автотестов. Правда возникает проблема с локальным дебагом, но для этого всегда можно использовать отдельный Spec Scala + Selenium examples: Football teams: Сколько человек в каждой футбольной сборной имеют более одного гражданства? Nations league: Самый стремительный взлет в Лиги наций УЕФА?"
    } ,    
    {
      "title": "Football teams",
      "url": "/scalenium/docs/examples/fifa.html",
      "content": "Task: Сколько человек в каждой футбольной сборной имеют более одного гражданства? За основу возьмем данные с сайта transfermarkt.com: рейтинг сборных страница сборной страны страница игрока На странице игрока есть поле Citizenship, которое предоставляет необходимую информацию List of football/soccer teams Переход на заданную страницу, если она реализует trait org.scalatestplus.selenium.Page, может осуществляться так: import org.scalatestplus.selenium.Page import org.scalatestplus.selenium.WebBrowser._ import org.openqa.selenium.WebDriver implicit def webDriver: WebDriver = ??? /* from container */ class RankingListPage(implicit val webDriver: WebDriver) extends Page { val url = \"https://www.transfermarkt.com/statistik/weltrangliste/statistik\" } val rankingListPage = new RankingListPage() go to rankingListPage После перехода прежде чем работать со страницей необходимо дождаться окончания отрисовки её элементов. Будем ориентироваться на кнопку Compact и дождемся, когда она станет видима. Xpath локатор кнопки будет таким: import org.scalatestplus.selenium.WebBrowser._ val compactTab: Query = xpath(\"//div[.='Compact']\") Ожидание видимости элемента осуществляется так (timeout можно задать в конфиге, query - заданный элемент): import org.openqa.selenium._ import org.openqa.selenium.support.ui.WebDriverWait import org.openqa.selenium.support.ui.ExpectedConditions import org.scalatestplus.selenium.WebBrowser._ import java.time.Duration def waitVisible(query: Query, timeout: Int)(implicit webDriver: WebDriver): WebElement = new WebDriverWait(webDriver, Duration.ofSeconds(timeout)).until(ExpectedConditions.visibilityOfElementLocated(query.by)) Рассмотрим переход на закладку Compact. Переход на закладку будет состоять из следующих шагов: Проверяем, активна ли закладка (активная закладка в данном случае в атрибуте class содержит “active”). Если да, то ничего не делаем — переход осуществлен. Если нет, то кликаем на закладку и ждём, когда закладка станет активна Проверить, что элемент query: Query содержит заданное значение в атрибуте class можно так: def doesClassContain(value: String): Boolean = (for { element &lt;- find(query) attribute &lt;- element.attribute(\"class\") } yield attribute.contains(value)).contains(true) Кликнуть на элемент можно так: clickOn(query) Ожидание, когда атрибут class элемента будет содержать заданное значение, можно реализовать так: def waitClassContain(value: String): Boolean = new WebDriverWait(driver, Duration.ofSeconds(timeout)).until(ExpectedConditions.attributeContains(query.by, \"class\", value)) Итого: def clickCompact(): Unit = if (!compactTab.doesClassContain(\"active\")) { clickOn(compactTab) val _ = compactTab.waitClassContain(\"active\") } Теперь, когда мы находимся на закладке Compact можно начать вычисление списка всех сборных. Для этого нужно последовательно обойти все страницы рейтинга и с каждой считать список. Логика будет такой: Проверяем, достигли ли последней страницы Если нет, считываем список со страницы, переходим на следующую и возвращаемся на пункт выше Если дошли до последней страницы, то считываем список с неё Для того, чтобы проверить, достигли ли мы последней страницы, достаточно проверить, есть ли кнопка перехода на следующую страницу (см. скрин выше, css локатор li.naechste-seite &gt; a): val nextPageLink: Query = cssSelector(\"li.naechste-seite &gt; a\") def isPresent: Boolean = find(nextPageLink).isDefined Для того, чтобы считать список стран, необходимо найти все элементы с xpath локатором //table/tbody//a[count(*)=0] и у каждого элемента считать text и значение атрибута href (или //table/tbody/tr[td[.='CONMEBOL']]//a[count(*)=0] - если интересна только одна конфедерация, например, самая маленькая - CONMEBOL(Южная Америка)): val itemLink: Query = xpath(\"//table/tbody//a[count(*)=0]\") def items(): Seq[(String, Option[String])] = findAll(itemLink).map(el =&gt; (el.text.trim, el.attribute(\"href\"))).toSeq Для перехода на следующую страницу мало кликнуть на кнопку nextPageLink, необходимо ещё дождаться, когда этот переход произойдет. Чтобы удостовериться, что мы перешли на следующую страницу, мы можем считать номер текущей страницы (css локатор li.selected &gt; a), а после клика на nextPageLink дождаться, когда номер текущей страницы станет на 1 больше: val selectedPageLink: Query = cssSelector(\"li.selected &gt; a\") def clickNextPage(): Unit = { val nextPage = find(selectedPageLink).map(_.text).get().toInt + 1 clickOn(nextPageLink) val _ = webDriverWait(driver).until(ExpectedConditions.textToBe(selectedPageLink.by, nextPage.toString)) } Соединяем все воедино и получаем следующий список (на 13 марта 2021): Country name Url Belgium link France link Brazil link … … B. Virgin link Anguilla link San Marino link Team player list Теперь, когда у нас есть url страны можно составить список игроков сборной. Эта задача ещё более простая, потому что страница сборной страны содержит практически все те же необходимые нам элементы, что и страница рейтинга сборных. Изменения будут касаться только элемента itemLink (ссылка на игрока). Ссылка на страницу игрока - это xpath локатор //table/tbody//span[@class='hide-for-small']/a[count(*)=0]: val itemLink: Query = xpath(\"//table/tbody//span[@class='hide-for-small']/a[count(*)=0]\") Player’s citizenship Осталось только определить гражданство игрока сборной, пользуясь его страницей, найденной на предыдущем этапе. Для начала нужно перейти на закладку Profile. Мы уже переходили на закладку на предыдущих страницах. Здесь будет то же самое, за исключением одного нюанса: если раньше у активной закладки менялся атрибут class, то теперь этот атрибут меняется не у самой ссылки, а у её родителя. Кстати, в этот раз немецкие разработчики сайта значение атрибута class активного элемента не стали переводить на английский, а оставили на немецком - “aktiv”: Создадим два элемента: ссылку и её родителя, а затем определим переход на закладку так: val profileTab: Query = xpath(\"//li[@id='profile']\") val profileLink: Query = xpath(\"//li[@id='profile']/a\") def clickProfile(): Unit = if (!profileTab.doesClassContain(\"aktiv\")) { clickOn(profileLink) val _ = profileTab.waitClassContain(\"aktiv\") } Теперь осталось только определить гражданство. Для этого возьмем элемент img из строки Citizenship: и считаем её атрибут “title”: val citizenshipImg: Query = xpath(\"//th[.='Citizenship:']/following-sibling::td/img\") def citizenship(): Seq[String] = findAll(citizenshipImg).flatMap(_.attribute(\"title\")).toSeq Вот и все, все страницы заполнены, осталось только написать автотест и тогда получим следующий результат. Results (for Russia, Ukraine and Belarus) Country name % Foreigners Russia 11% (3/28) (Brazil (1) -&gt; (Mário Fernandes), Kyrgyzstan (1) -&gt; (Ilzat Akhmetov), Germany (1) -&gt; (Roman Neustädter)) Ukraine 9% (3/33) (Brazil (2) -&gt; (Marlos, Júnior Moraes), Hungary (1) -&gt; (Igor Kharatin)) Belarus 4% (1/25) (Cameroon (1) -&gt; (Maks Ebong)) В наших сборных только 3 натурализованных игрока (и все из Бразилии). Остальные родились в СССР. Results (for CONMEBOL) Country name % Foreigners Brazil 36% (9/25) (Spain (3) -&gt; (Casemiro, Bruno Guimarães, Vinícius Júnior), Italy (1) -&gt; (Alex Telles), France (1) -&gt; (Thiago Silva), Portugal (4) -&gt; (Ederson, Marquinhos, Allan, Lucas Paquetá)) Argentina 57% (13/23) (Spain (2) -&gt; (Gonzalo Montiel, Lionel Messi), Italy (11) -&gt; (Lucas Martínez Quarta, Wálter Kannemann, Nicolás Tagliafico, Guido Rodríguez, Rodrigo de Paul, Giovani Lo Celso, Nicolás Domínguez, Ángel Di María, Joaquín Correa, Papu Gómez, Lucas Alario)) Uruguay 51,5% (18/35) (Spain (7) -&gt; (José María Giménez, Sebastián Coates, Diego Godín, Agustín Oliveros, Damián Suárez, Lucas Torreira, Federico Valverde), Paraguay (1) -&gt; (Rodrigo Muñoz), Italy (10) -&gt; (Fernando Muslera, Martín Campaña, Sergio Rochet, Matías Viña, Franco Pizzichillo, Nahitan Nández, Matías Vecino, Giorgian de Arrascaeta, Diego Rossi, Cristhian Stuani)) Colombia 22% (6/27) (Spain (4) -&gt; (Jeison Murillo, Johan Mojica, James Rodríguez, Luis Suárez), Argentina (1) -&gt; (Frank Fabra), England (1) -&gt; (Steven Alzate)) Chile 21% (5/24) (Haiti (1) -&gt; (Jean Beausejour), Spain (3) -&gt; (Claudio Bravo, Gary Medel, Fabián Orellana), Italy (1) -&gt; (Luis Jiménez)) Peru 33% (12/36) (Venezuela (1) -&gt; (Carlos Ascues), Spain (3) -&gt; (Alexander Callens, Cristian Benavente, Sergio Peña), Uruguay (1) -&gt; (Gabriel Costa), Italy (2) -&gt; (Luis Abram, Gianluca Lapadula), Netherlands (1) -&gt; (Renato Tapia), Switzerland (1) -&gt; (Jean-Pierre Rhyner), Portugal (1) -&gt; (André Carrillo), Croatia (1) -&gt; (Raúl Ruidíaz), Lebanon (1) -&gt; (Matías Succar)) Venezuela 25% (7/28) (Spain (4) -&gt; (Roberto Rosales, Juanpi Añor, Darwin Machís, Fernando Aristeguieta), Switzerland (1) -&gt; (Rolf Feltscher), England (1) -&gt; (Luis Del Pino Mago), Colombia (1) -&gt; (Jan Hurtado)) Paraguay 21% (7/33) (Spain (1) -&gt; (Antonio Sanabria), Argentina (4) -&gt; (Santiago Arzamendia, Gastón Giménez, Andrés Cubas, Raúl Bobadilla), Italy (2) -&gt; (Antony Silva, Iván Piris)) Ecuador 12% (4/33) (Spain (3) -&gt; (Erick Ferigra, Pervis Estupiñán, Leonardo Campana), Argentina (1) -&gt; (Hernán Galíndez)) Bolivia 25% (7/28) (United States (2) -&gt; (Adrián Jusino, Antonio Bustamante), Spain (1) -&gt; (Jaume Cuéllar), Argentina (1) -&gt; (Carlos Lampe), Brazil (1) -&gt; (Marcelo Moreno), Switzerland (1) -&gt; (Boris Cespedes), Portugal (1) -&gt; (Erwin Sánchez)) А вот в Южной Америке людей с двойным гражданством довольно много. Впрочем, это неудивительно: в чемпионатах ЕС жесткий лимит на легионеров (в заявке только 3 игрока с гражданством не ЕС), поэтому южноамериканцам, чтобы попасть в Европу, приходится либо пытаться получить гражданство бывшей митрополии (Бразилия -&gt; Португалия, остальные -&gt; Испания), либо искать среди своих предков итальянцев. Второе не так сложно, как кажется. Во время Второй Мировой войны Южная Америка хоть и была на бумаге нейтральной, по факту разделилась на два лагеря: Бразилия -&gt; союзники, Аргентина + Уругвай -&gt; фашисты. Поэтому неудивительно, что после 1945 года многие итальянцы в поисках лучшей жизни иммигрировали из разрушенной фашисткой Италии в симпатизировавшим ей Аргентине и Уругваю. Поэтому современному аргентинцу или уругвайцу получить гражданство Италии не сложнее, чем человеку по фамилии Зильберман - гражданство Израиля - кто-нибудь среди предков нужной национальности да найдётся!"
    } ,    
    {
      "title": "Introduction",
      "url": "/scalenium/docs/",
      "content": "Introduction Selenium on Scala examples. Quick Start The library is available in the central repository - It is available on scala 2.13, 2.12. Add the following to your build.sbt libraryDependencies += \"com.github.artemkorsakov\" %% \"scalenium\" % \"0.1.0\" Set configuration Create a reference.conf file and add it as a resource of your application (src/main/resources or src/test/resources (for tests)): selenium { browser = \"chrome\" video-logs = \"\" timeout = 30 } Create test Install docker Create spec extending SeleniumContainerSuite. For example: import com.github.artemkorsakov.containers.SeleniumContainerSuite import com.github.artemkorsakov.query.UpQuery._ import org.scalatest.flatspec.AnyFlatSpec import org.scalatestplus.selenium.WebBrowser._ class SeleniumDriverSpec extends AnyFlatSpec with SeleniumContainerSuite { \"Browser\" should \"show google\" in { go to \"https://www.google.com/\" name(\"q\").waitVisible() } } Run spec Additional methods for Query import com.github.artemkorsakov.query.UpQuery._ val query: Query = ??? // Does the element exist on the page? query.isPresent // Boolean // Waiting for the element to present on the page query.waitPresent() // WebElement // Does the element visible on the page? query.isVisible // Boolean // Waiting for the element to visible on the page query.waitVisible() // WebElement // Wait for the element to become invisible on the page query.waitNotVisible() // Boolean // The text content of the element after whitespace normalization. // For example: if element's text is \"\\n\\t Press \\n \\n enter \\n\\n\" normalizeSpaceText returns \"Press enter\" query.normalizeSpaceText // String // The text content of all elements with the given locator query.allTexts // IndexedSeq[String] // Element attribute query.attribute(name: String) // Option[String] // Placeholder attribute of the element query.placeholder // Option[String] // Does element's attribute \"class\" contain the given value? query.doesClassContain(value: String) // Boolean // Waiting for element's attribute \"class\" to contain the given value query.waitClassContain(value: String) // Boolean // Scroll to element query.scrollToElement() // Object"
    } ,    
    {
      "title": "Home",
      "url": "/scalenium/",
      "content": "Home Overview Selenium on Scala examples. Please send an email, create an issue or a pull request on github for any cases you think are important. In addition, Improve this Page button is available on the documentation pages. Getting Started Add the following to your build.sbt libraryDependencies += \"com.github.artemkorsakov\" %% \"scalenium\" % \"0.1.0\" Use additional methods for Query in the code: import com.github.artemkorsakov.query.UpQuery._ val query: Query = ??? query.waitClassContain(\"active\") Documentation Documentation ScalaDoc"
    } ,    
    {
      "title": "Nations league",
      "url": "/scalenium/docs/examples/nations-league.html",
      "content": "Task: Какой самый стремительный взлет в Лиги наций УЕФА? Т.к. цель этой статьи показать, как пишутся Selenium-автотесты на Scala, а не поставить интригующий вопрос, продержать читателя в неведении, а в конце статьи дать неожиданный ответ, то дальше будут спойлеры. С момента запуска Лиги наций УЕФА прошло целых два розыгрыша и уже можно подвести промежуточные результаты: Сборная России два раза подряд занимала второе место в группе дивизиона B (оба раза блестяще начиная и столь же блестяще-позорно заканчивая) и в третьем розыгрыше предсказуемо выступит в том же дивизионе B - вот это стабильность (со слезами на глазах) Сборная Турции два раза подряд занимала последнее место в своей группе (оба раза в той же самой, где была и сборная России), но в третьем розыгрыше выступит всего лишь одним дивизионом ниже - в дивизионе С А вот и обещанный спойлер: сборная Венгрии в первом розыгрыше заняла второе место в третьем по силе дивизионе С, но в третьем розыгрыше выступит в самом сильном дивизионе А вместе с топ-сборными - вот это феерический взлет. Жаль что он произошел в той же самой группе, где была и сборная России. А как же выступили остальные сборные? После того, как ингрига была напрочь уничтожена, можно заняться автоматизацией. За основу возьмем данные с сайта transfermarkt.com: результаты сборных в Лиге наций Определимся с понятиями В лиге наций УЕФА 4 по силе дивизиона: A (самый сильный), B, C, D. Победитель дивизиона поднимается в более сильный дивизион, занявший последнее место - опускается дивизионом ниже. В первом розыгрыше в сильных дивизионах было только по 3 команды, но после этого УЕФА решила, что топ-матчей должно быть больше и во втором розыгрыше во всех дивизионах, кроме самого слабого (D), стало выступать по 4 команды. Именно по причине необходимой доукомплектации более сильных дивизионов после первого розыгрыша Турция, занявшая последнее место, не вылетела, а Венгрия, занявшая второе - поднялась выше. Считаем результаты первых двух розыгрышей, определим с помощью спортивного принципа состав групп третьего розыгрыша и сформируем статистику выступлений каждой сборной в следующем виде: Россия (B-B-B). Страница дивизиона Лиги Наций УЕФА Для того, чтобы перейти на страницу группы X необходимо выполнить следующее: Перейти на главную страницу сайта transfermarkt.com В верхнем меню нажать на пункт Competitions Во всплывающем меню выбрать пункт “UEFA Nations League X” Xpath локаторы для ссылок меню Competitions и UEFA Nations League X будут следующие: val competitionsLink: Query = xpath(\"//a[normalize-space(.)='Competitions']\") def groupLink(group: String): Query = xpath(s\"//a[normalize-space(.)='UEFA Nations League $group']\") Мы вынуждены использовать функцию normalize-space, потому что ссылка меню Competitions содержит не только слово Competitions, но ещё и кучу пробелов с одним переносом строки: Именно поэтому в локаторе //a[normalize-space(.)='Competitions'] мы ищем элемент с тэгом a, текстовое содержимое которого после нормализации пробелов равно Competitions. Ожидание появления элемента query можно реализовать так: new WebDriverWait(driver, Duration.ofSeconds(timeout)).until(ExpectedConditions.visibilityOfElementLocated(query.by)) в результате вернется видимый элемент WebElement, на который затем можно кликнуть и получится: competitionsLink.waitVisible().click() groupLink(group).waitVisible().click() Конечно, после перехода на страницу группы Лиги Наций нужно подождать, когда эта страница загрузится. Для этого возьмем элемент заглавия страницы с css-локатором div.dataName &gt; h1: и подождем, когда в нём отобразится название группы: val header: Query = cssSelector(\"div.dataName &gt; h1\") new WebDriverWait(driver, Duration.ofSeconds(timeout)).until(ExpectedConditions.textToBe(header.by, s\"UEFA Nations League $group\")) Результаты в дивизионе В правом нижнем углу страницы дивизиона есть результаты выступлений сборных: Строка с результатами сборной будет иметь xpath-локатор //table//tr[td[@class='rechts']]. Всего у нас будет до 16 строк и каждую строку нужно обработать - выудить из строки место сборной (оно необходимо, чтобы определить состав дивизионов в третьем розыгрыше) и, конечно, название страны. К сожалению, Scala библиотека для Selenium довольно бедная и не позволяет искать подэлементы относительно заданного, поэтому воспользуемся Java-библиотекой: val resultRow: Query = xpath(\"//table//tr[td[@class='rechts']]\") import scala.jdk.CollectionConverters._ import org.openqa.selenium._ val webDriver: WebDriver = ??? def results: mutable.Buffer[(Int, String)] = webDriver.findElements(resultRow.by).asScala.map { el =&gt; { val place = el.findElement(By.xpath(\".//td[@class='rechts']\")).getText val name = el.findElement(By.xpath(\".//td[contains(@class, 'hauptlink')]\")).getText (place.toInt, name) } } Здесь происходит следующее: находим все элементы с xpath-локатором //table//tr[td[@class='rechts']] конвертируем java.util.List в scala.collection.mutable.Buffer для каждого такого элемента находим два его дочерних подэлемента: .//td[@class='rechts'] - место в таблице, .//td[contains(@class, 'hauptlink')] - название страны считываем текст из дочерних элементов Получим следующую картину, например, для дивизиона D: Place Country 1 Faroe Islands 2 Malta 3 Latvia 4 Andorra 1 Gibraltar 2 Liechtenstein 3 San Marino Previous results Для того, чтобы получить результаты предыдущего сезона, необходимо в фильтре Filter by season: выбрать значение 18/19 и нажать на кнопку Show: Здесь будет больше действий и проверок: В первую очередь нужно раскрыть список доступных сезонов (нажать на кнопку с css-локатором a.chzn-single &gt; div &gt; b) Затем кликнуть на предыдущий сезон (нажать на ссылку с xpath-локатором //li[.='18/19']) После этого необходимо дождаться, когда скроется список доступных сезонов Затем необходимо нажать на кнопку Show (css-локатор input[value='Show']) И подождать, когда произойдет переход на страницу предыдущего сезона (для этого будем ждать, когда url текущей страницы станет оканчиваться на “saison_id=2018”) val selectSeason: Query = cssSelector(\"a.chzn-single &gt; div &gt; b\") val previousSeason: Query = xpath(\"//li[.='18/19']\") val show: Query = cssSelector(\"input[value='Show']\") def selectPreviousSeason: Boolean = { selectSeason.waitVisible().click() previousSeason.waitVisible().click() new WebDriverWait(driver, Duration.ofSeconds(timeout)).until(invisibilityOfElementLocated(previousSeason.by)) clickOn(show) new WebDriverWait(driver, Duration.ofSeconds(timeout)).until(wd =&gt; wd.getCurrentUrl.endsWith(\"saison_id=2018\")) } Теперь можно собрать все воедино и получим следующее: Переходим на главную страницу сайта Для каждой из групп ‘A’, ‘B’, ‘C’, ‘D’ выполняем: Переходим в заданную группу Считываем результаты второго розыгрыша Сохраняем их Переходим в предыдущий сезон Считываем результаты первого розыгрыша Сохраняем их P.S. Забегая вперед, скажу, что за это время страна “Macedonia” сменила имя на “North Macedonia” - это пришлось учесть. case class Result(number: Int, group: Char, place: Int, country: String) val mainPage = new MainPage go to mainPage val results: ArrayBuffer[Result] = ArrayBuffer.empty[Result] Seq('A', 'B', 'C', 'D').foreach(group =&gt; { val leagueGroupPage = mainPage.goToGroup(group.toString) val groupResult = leagueGroupPage.results groupResult.foreach { case (place, country) =&gt; results += Result(2, group, place, country) } leagueGroupPage.selectPreviousSeason leagueGroupPage.waitLoad(group.toString) val previousSeasonResult = leagueGroupPage.results previousSeasonResult.foreach { case (place, country) =&gt; results += Result(1, group, place, country.replace(\"Macedonia\", \"North Macedonia\")) } }) Теперь осталось только обработать результаты. Результаты у нас в виде коллекции case class Result(number: Int, group: Char, place: Int, country: String), приведем эту коллекцию к коллекции case class ParsedResult(country: String, firstSeason: (Char, Int), secondSeason: (Char, Int), thirdSeason: Char, progress: (Int, Int)), где сезон представлен в виде tuple (дивизион, итоговое место), а прогресс - из двух цифр, обозначающих прогресс по итогам розыгрыша: 1 (повышение в классе) | 0 | -1 (понижение) import scala.collection.mutable.ArrayBuffer case class Result(number: Int = 0, group: Char = 'E', place: Int = 0, country: String = \"\") case class ParsedResult(country: String, firstSeason: (Char, Int), secondSeason: (Char, Int), thirdSeason: Char, progress: (Int, Int)) val results: ArrayBuffer[Result] = ??? val parsedResults = results .groupBy(_.country) .view .mapValues(seq =&gt; { val country: String = seq.head.country val firstRes = seq.find(_.number == 1).getOrElse(Result()) val firstSeason: (Char, Int) = (firstRes.group, firstRes.place) val secondRes = seq.find(_.number == 2).getOrElse(Result()) val secondSeason: (Char, Int) = (secondRes.group, secondRes.place) val thirdSeason: Char = if (secondSeason._2 == 1 &amp;&amp; secondSeason._1 != 'A') (secondSeason._1 - 1).toChar else if (secondSeason._2 == 4 &amp;&amp; secondSeason._1 != 'D') (secondSeason._1 + 1).toChar else secondSeason._1 val progress: (Int, Int) = (firstSeason._1 - secondSeason._1, secondSeason._1 - thirdSeason) ParsedResult(country, firstSeason, secondSeason, thirdSeason, progress) }) .values .groupBy(_.progress) Самые успешные сборные? Разделим полученный результат на группы сборных, объединенных по достигнутому прогрессу. Получим следующий результат: Суперпрогресс (сборные, совершившие прорыв через 2 дивизиона) - 2 Country 1st 2nd 3rd Hungary C(2) B(1) A Armenia D(2) C(1) B Поднялись и закрепились - 13 Country 1st 2nd 3rd Denmark B(1) A(2) A Romania C(2) B(3) B Serbia C(1) B(3) B Scotland C(1) B(2) B Finland C(1) B(2) B Israel C(2) B(3) B Norway C(1) B(2) B Luxembourg D(2) C(2) C Azerbaijan D(2) C(3) C Georgia D(1) C(3) C Belarus D(1) C(2) C Kosovo D(1) C(3) C North Macedonia D(1) C(2) C Поднялись со второй попытки - 8 Country 1st 2nd 3rd Wales B(2) B(1) A Czech Republic B(2) B(1) A Austria B(2) B(1) A Slovenia C(4) C(1) B Albania C(3) C(1) B Montenegro C(3) C(1) B Gibraltar D(3) D(1) C Faroe Islands D(3) D(1) C Поднялись и опустились - 6 Country 1st 2nd 3rd Ukraine B(1) A(4) B Sweden B(1) A(4) B Bosnia B(1) A(4) B Bulgaria C(2) B(4) C Kazakhstan D(2) C(4) D Moldova D(3) C(4) D Стабильная группа - 20 Country 1st 2nd 3rd Poland A(3) A(3) A Spain A(2) A(1) A Italy A(2) A(1) A Netherlands A(1) A(2) A England A(1) A(3) A Croatia A(3) A(3) A Belgium A(2) A(1) A France A(2) A(1) A Switzerland A(1) A(3) A Germany A(3) A(2) A Portugal A(1) A(2) A Russia B(2) B(2) B Ireland B(3) B(3) B Greece C(3) C(2) C Lithuania C(4) C(3) C Latvia D(3) D(3) D Andorra D(4) D(4) D Liechtenstein D(4) D(2) D Malta D(4) D(2) D San Marino D(4) D(3) D Опустились и поднялись - 0 Опустились и остались - 0 Опустились на втором сезоне - 6 Country 1st 2nd 3rd Iceland A(3) A(4) B N. Ireland B(3) B(4) C Slovakia B(3) B(4) C Turkey B(3) B(4) C Estonia C(4) C(4) D Cyprus C(3) C(4) D Супернеудачники, упавшие два раза подряд - 0"
    } ,        
    {
      "title": "Sources",
      "url": "/scalenium/sources.html",
      "content": "List of References Sites and articles ScalaTest + Selenium Books: …"
    }    
  ];

  idx = lunr(function () {
    this.ref("title");
    this.field("content");

    docs.forEach(function (doc) {
      this.add(doc);
    }, this);
  });

  docs.forEach(function (doc) {
    docMap.set(doc.title, doc.url);
  });
}

// The onkeypress handler for search functionality
function searchOnKeyDown(e) {
  const keyCode = e.keyCode;
  const parent = e.target.parentElement;
  const isSearchBar = e.target.id === "search-bar";
  const isSearchResult = parent ? parent.id.startsWith("result-") : false;
  const isSearchBarOrResult = isSearchBar || isSearchResult;

  if (keyCode === 40 && isSearchBarOrResult) {
    // On 'down', try to navigate down the search results
    e.preventDefault();
    e.stopPropagation();
    selectDown(e);
  } else if (keyCode === 38 && isSearchBarOrResult) {
    // On 'up', try to navigate up the search results
    e.preventDefault();
    e.stopPropagation();
    selectUp(e);
  } else if (keyCode === 27 && isSearchBarOrResult) {
    // On 'ESC', close the search dropdown
    e.preventDefault();
    e.stopPropagation();
    closeDropdownSearch(e);
  }
}

// Search is only done on key-up so that the search terms are properly propagated
function searchOnKeyUp(e) {
  // Filter out up, down, esc keys
  const keyCode = e.keyCode;
  const cannotBe = [40, 38, 27];
  const isSearchBar = e.target.id === "search-bar";
  const keyIsNotWrong = !cannotBe.includes(keyCode);
  if (isSearchBar && keyIsNotWrong) {
    // Try to run a search
    runSearch(e);
  }
}

// Move the cursor up the search list
function selectUp(e) {
  if (e.target.parentElement.id.startsWith("result-")) {
    const index = parseInt(e.target.parentElement.id.substring(7));
    if (!isNaN(index) && (index > 0)) {
      const nextIndexStr = "result-" + (index - 1);
      const querySel = "li[id$='" + nextIndexStr + "'";
      const nextResult = document.querySelector(querySel);
      if (nextResult) {
        nextResult.firstChild.focus();
      }
    }
  }
}

// Move the cursor down the search list
function selectDown(e) {
  if (e.target.id === "search-bar") {
    const firstResult = document.querySelector("li[id$='result-0']");
    if (firstResult) {
      firstResult.firstChild.focus();
    }
  } else if (e.target.parentElement.id.startsWith("result-")) {
    const index = parseInt(e.target.parentElement.id.substring(7));
    if (!isNaN(index)) {
      const nextIndexStr = "result-" + (index + 1);
      const querySel = "li[id$='" + nextIndexStr + "'";
      const nextResult = document.querySelector(querySel);
      if (nextResult) {
        nextResult.firstChild.focus();
      }
    }
  }
}

// Search for whatever the user has typed so far
function runSearch(e) {
  if (e.target.value === "") {
    // On empty string, remove all search results
    // Otherwise this may show all results as everything is a "match"
    applySearchResults([]);
  } else {
    const tokens = e.target.value.split(" ");
    const moddedTokens = tokens.map(function (token) {
      // "*" + token + "*"
      return token;
    })
    const searchTerm = moddedTokens.join(" ");
    const searchResults = idx.search(searchTerm);
    const mapResults = searchResults.map(function (result) {
      const resultUrl = docMap.get(result.ref);
      return { name: result.ref, url: resultUrl };
    })

    applySearchResults(mapResults);
  }

}

// After a search, modify the search dropdown to contain the search results
function applySearchResults(results) {
  const dropdown = document.querySelector("div[id$='search-dropdown'] > .dropdown-content.show");
  if (dropdown) {
    //Remove each child
    while (dropdown.firstChild) {
      dropdown.removeChild(dropdown.firstChild);
    }

    //Add each result as an element in the list
    results.forEach(function (result, i) {
      const elem = document.createElement("li");
      elem.setAttribute("class", "dropdown-item");
      elem.setAttribute("id", "result-" + i);

      const elemLink = document.createElement("a");
      elemLink.setAttribute("title", result.name);
      elemLink.setAttribute("href", result.url);
      elemLink.setAttribute("class", "dropdown-item-link");

      const elemLinkText = document.createElement("span");
      elemLinkText.setAttribute("class", "dropdown-item-link-text");
      elemLinkText.innerHTML = result.name;

      elemLink.appendChild(elemLinkText);
      elem.appendChild(elemLink);
      dropdown.appendChild(elem);
    });
  }
}

// Close the dropdown if the user clicks (only) outside of it
function closeDropdownSearch(e) {
  // Check if where we're clicking is the search dropdown
  if (e.target.id !== "search-bar") {
    const dropdown = document.querySelector("div[id$='search-dropdown'] > .dropdown-content.show");
    if (dropdown) {
      dropdown.classList.remove("show");
      document.documentElement.removeEventListener("click", closeDropdownSearch);
    }
  }
}
