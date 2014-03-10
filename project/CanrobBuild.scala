import sbt._
import Keys._
import sbt.Package.ManifestAttributes

import com.typesafe.sbteclipse.plugin.EclipsePlugin._

import scala.scalajs.sbtplugin._
import ScalaJSPlugin._
import ScalaJSKeys._

object CanrobBuild extends Build {

  object D {
    val scalaVersion = "2.10.3"
    val doctusVersion = "1.0.1"
  }

  object S {

    lazy val baseSettings =
      Defaults.defaultSettings ++
        Seq(
          organization := "net.entelijan",
          organizationHomepage := Some(url("http://entelijan.net/")),
          libraryDependencies += "org.scalatest" %% "scalatest" % "2.0" % "test",
          resolvers += "entelijan" at "http://entelijan.net/artifactory/repo/",
          EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource,
          EclipseKeys.withSource := true)

    lazy val defaultSettings =
      baseSettings ++
        Seq(
          version := "1.0-SNAPSHOT")

    lazy val coreSettings =
      defaultSettings ++
        scalaJSSettings ++
        Seq(
          libraryDependencies += "net.entelijan" %% "doctus-core" % D.doctusVersion,
          fork := true)

    lazy val swingSettings =
      defaultSettings ++
        Seq(
          libraryDependencies += "org.scala-lang" % "scala-swing" % D.scalaVersion,
          libraryDependencies += "net.entelijan" %% "doctus-swing" % D.doctusVersion)

    lazy val scalajsSettings =
      defaultSettings ++
        scalaJSSettings ++
        Seq(
          libraryDependencies += "org.scala-lang.modules.scalajs" %% "scalajs-dom" % "0.2",
          libraryDependencies += "org.scala-lang.modules.scalajs" %% "scalajs-jquery" % "0.2",
          libraryDependencies += "net.entelijan" %% "doctus-scalajs" % D.doctusVersion,
          unmanagedSources in (Compile, packageJS) += baseDirectory.value / "js" / "startup.js")

  }

  lazy val root = Project(
    id = "canrob-root",
    base = file("."),
    settings = S.defaultSettings)
    .aggregate(core, swing, scalajs)

  lazy val core = Project(
    id = "canrob-core",
    base = file("core"),
    settings = S.coreSettings)

  lazy val swing = Project(
    id = "canrob-swing",
    base = file("swing"),
    settings = S.swingSettings)
    .dependsOn(core)

  lazy val scalajs = Project(
    id = "canrob-scalajs",
    base = file("scalajs"),
    settings = S.scalajsSettings)
    .dependsOn(core)

}
