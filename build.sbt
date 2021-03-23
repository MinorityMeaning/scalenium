import Dependencies.Version._
import microsites._
import sbtcatalysts.CatalystsKeys.docsMappingsAPIDir

addCommandAlias("com", "all compile test:compile")
addCommandAlias("fmt", "all scalafmtSbt scalafmtAll")
addCommandAlias("fmtCheck", "all scalafmtSbtCheck scalafmtCheckAll")
addCommandAlias("stl", "all scalastyle test:scalastyle")

val release_version = "0.1.0"
val badge =
  "[![Maven Central](https://img.shields.io/maven-central/v/com.github.artemkorsakov/scalenium-core_2.13.svg?label=Maven%20Central&color=success)](https://search.maven.org/search?q=g:%22com.github.artemkorsakov%22%20AND%20a:%22scalenium-core_2.13%22)"

val apache2 = "Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.html")
val gh = GitHubSettings(
  org = "artemkorsakov",
  proj = "scalenium",
  publishOrg = "artemkorsakov",
  license = apache2
)

val github = "https://github.com/artemkorsakov"
val mainDev =
  Developer(
    "Artem Korsakov",
    "@artemkorsakov",
    "artemkorsakov@mail.ru",
    new java.net.URL(github)
  )

val devs = List(Developer)

lazy val scalenium = Project(id = "scalenium", base = file("core"))
  .configs(IntegrationTest)
  .settings(
    scalaVersion := Scala213,
    inConfig(IntegrationTest)(Defaults.testSettings ++ org.scalafmt.sbt.ScalafmtPlugin.scalafmtConfigSettings),
    crossScalaVersions := Vector(Scala213, Scala212),
    libraryDependencies ++= Dependencies.scalenium.value
  )

lazy val examples = Project(id = "scalenium-examples", base = file("examples"))
  .dependsOn(scalenium)
  .configs(IntegrationTest)
  .settings(
    scalaVersion := Scala213,
    inConfig(IntegrationTest)(Defaults.testSettings ++ org.scalafmt.sbt.ScalafmtPlugin.scalafmtConfigSettings),
    crossScalaVersions := Vector(Scala213, Scala212),
    libraryDependencies ++= Dependencies.scalenium.value
  )

/** Docs - Generates and publishes the scaladoc API documents and the project web site using sbt-microsite.
  * https://47degrees.github.io/sbt-microsites/docs/settings/
  */
lazy val docs = Project(id = "scalenium-docs", base = file("docs"))
  .enablePlugins(MicrositesPlugin)
  .enablePlugins(ScalaUnidocPlugin)
  .settings(
    crossScalaVersions := Vector(Scala213, Scala212),
    micrositeName := "Scalenium",
    micrositeDescription := "Selenium on Scala examples.",
    micrositeUrl := "https://artemkorsakov.github.io",
    micrositeBaseUrl := "/scalenium",
    micrositeDocumentationUrl := "/scalenium/docs",
    micrositeDocumentationLabelDescription := "Documentation",
    micrositeAuthor := "Artem Korsakov",
    micrositeGithubOwner := "artemkorsakov",
    micrositeGithubRepo := "scalenium",
    micrositeTheme := "pattern",
    micrositeEditButton := Some(
      MicrositeEditButton("Improve this Page", "/edit/master/docs/docs/{{ page.path }}")
    ),
    micrositeGithubToken := sys.env.get("GITHUB_TOKEN"),
    micrositePushSiteWith := GitHub4s,
    micrositeGitterChannel := false,
    micrositePalette := Map(
      "brand-primary"   -> "#5B5988",
      "brand-secondary" -> "#292E53",
      "brand-tertiary"  -> "#222749",
      "gray-dark"       -> "#49494B",
      "gray"            -> "#7B7B7E",
      "gray-light"      -> "#E5E5E6",
      "gray-lighter"    -> "#F4F3F4",
      "white-color"     -> "#FFFFFF"
    ),
    apiURL := Some(url(s"${micrositeUrl.value}${micrositeBaseUrl.value}/api/")),
    autoAPIMappings := true,
    unidocProjectFilter in (ScalaUnidoc, unidoc) := inProjects(scalenium),
    docsMappingsAPIDir := "api",
    addMappingsToSiteDir(mappings in (ScalaUnidoc, packageDoc), docsMappingsAPIDir),
    scalacOptions in (ScalaUnidoc, unidoc) ~= { _.filter(_ != "-Xlint:-unused,_") },
    mdocVariables := Map(
      "VERSION"        -> release_version,
      "SCALA_VERSIONS" -> "2.13, 2.12",
      "MAVEN_BADGE"    -> badge,
      "DESCRIPTION"    -> micrositeDescription.value,
      "EMAIL"          -> s"mailto:${mainDev.email}",
      "GITHUB"         -> github,
      "ISSUES"         -> s"$github/scalenium/issues",
      "PULLS"          -> s"$github/scalenium/pulls",
      "WEBSITE"        -> s"${micrositeUrl.value}${micrositeBaseUrl.value}",
      "DOC_SITE"       -> s"${micrositeUrl.value}${micrositeDocumentationUrl.value}",
      "API_SITE"       -> apiURL.value.get.toString
    )
  )

lazy val root = Project(id = "scalenium-all", base = file("."))
  .aggregate(scalenium, examples)
  .enablePlugins(GitBranchPrompt)
  .settings(
    crossScalaVersions := Nil,
    publish / skip := true,
    scalacOptions ++= Seq(
      "-deprecation",
      "-encoding",
      "UTF-8",
      "-language:experimental.macros",
      "-feature",
      "-unchecked",
      "-Xfatal-warnings",
      "-Ywarn-numeric-widen",
      "-Ywarn-value-discard"
    )
  )
