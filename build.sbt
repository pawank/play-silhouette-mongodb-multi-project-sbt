import sbt.Keys._

import scalariform.formatter.preferences._

Common.appSettings(messagesFilesFrom = Seq("common", "admin", "web"))


lazy val common = (project in file("modules/common")).enablePlugins(PlayScala)

lazy val admin = (project in file("modules/admin")).enablePlugins(PlayScala).dependsOn(common)

lazy val web = (project in file("modules/web")).enablePlugins(PlayScala).dependsOn(common)

lazy val root = (project in file(".")).enablePlugins(PlayScala).aggregate(common, admin, web).dependsOn(common, admin, web)


libraryDependencies ++= Common.commonDependencies

//scalariformSettings
fork in run := true

//********************************************************
// Scalariform settings
//********************************************************

defaultScalariformSettings

  ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(FormatXml, false)
  .setPreference(DoubleIndentClassDeclaration, false)
.setPreference(PreserveDanglingCloseParenthesis, true)
