Global / excludeLintKeys += logManager
Global / excludeLintKeys += scalaJSUseMainModuleInitializer
Global / excludeLintKeys += scalaJSLinkerConfig

inThisBuild(
  List(
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    organization      := "com.indoorvivants",
    organizationName  := "Anton Sviridov",
    homepage := Some(
      url("https://github.com/indoorvivants/opaque-newtypes")
    ),
    startYear := Some(2022),
    licenses := List(
      "Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")
    ),
    developers := List(
      Developer(
        "keynmol",
        "Anton Sviridov",
        "velvetbaldmime@protonmail.com",
        url("https://blog.indoorvivants.com")
      )
    )
  )
)

val Versions = new {
  val Scala3        = "3.3.3"
  val munit         = "1.0.0"
  val scalaVersions = Seq(Scala3)
}

// https://github.com/cb372/sbt-explicit-dependencies/issues/27
lazy val disableDependencyChecks = Seq(
  unusedCompileDependenciesTest     := {},
  missinglinkCheck                  := {},
  undeclaredCompileDependenciesTest := {}
)

lazy val munitSettings = Seq(
  libraryDependencies += {
    "org.scalameta" %%% "munit" % Versions.munit % Test
  }
)

lazy val root = project
  .in(file("."))
  .aggregate(`opaque-newtypes`.projectRefs*)
  .aggregate(docs.projectRefs*)
  .settings(noPublish)

lazy val `opaque-newtypes` = projectMatrix
  .in(file("modules/core"))
  .defaultAxes(defaults*)
  .settings(
    moduleName := "opaque-newtypes",
    Test / scalacOptions ~= filterConsoleScalacOptions
  )
  .settings(munitSettings)
  .jvmPlatform(Versions.scalaVersions)
  .jsPlatform(Versions.scalaVersions, disableDependencyChecks)
  .nativePlatform(Versions.scalaVersions, disableDependencyChecks)
  .settings(
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule))
  )

lazy val docs = projectMatrix
  .in(file("myproject-docs"))
  .dependsOn(`opaque-newtypes`)
  .defaultAxes(defaults*)
  .settings(
    mdocVariables := Map(
      "VERSION" -> version.value
    )
  )
  .settings(disableDependencyChecks)
  .jvmPlatform(Versions.scalaVersions)
  .enablePlugins(MdocPlugin)
  .settings(noPublish)

val noPublish = Seq(
  publish / skip      := true,
  publishLocal / skip := true
)

val defaults =
  Seq(VirtualAxis.scalaABIVersion(Versions.Scala3), VirtualAxis.jvm)

val scalafixRules = Seq(
  "OrganizeImports",
  "DisableSyntax",
  "LeakingImplicitClassVal",
  "NoValInForComprehension"
).mkString(" ")

val CICommands = Seq(
  "clean",
  "compile",
  "test",
  "scalafmtCheckAll",
  "scalafmtSbtCheck",
  s"scalafix --check $scalafixRules",
  "headerCheck",
  "undeclaredCompileDependenciesTest",
  "unusedCompileDependenciesTest",
  "missinglinkCheck"
).mkString(";")

val PrepareCICommands = Seq(
  s"scalafix --rules $scalafixRules",
  "scalafmtAll",
  "scalafmtSbt",
  "headerCreate",
  "undeclaredCompileDependenciesTest"
).mkString(";")

addCommandAlias("ci", CICommands)

addCommandAlias("preCI", PrepareCICommands)
