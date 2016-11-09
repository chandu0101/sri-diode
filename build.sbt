

enablePlugins(ScalaJSPlugin)

name := "sri-diode"

version := "2016.11.0-SNAPSHOT"

organization := "com.github.chandu0101"

scalaVersion := "2.11.8"

crossScalaVersions := Seq("2.11.8","2.12.0")


val scalatestVersion = "3.0.0"

val sriVersion = "0.6.0-SNAPSHOT"

val diodeVersion = "1.1.0"

libraryDependencies += "com.github.chandu0101" %%% "sri-core" % sriVersion

libraryDependencies += "me.chrons" %%% "diode" % diodeVersion

relativeSourceMaps := true

scalacOptions += "-deprecation"

scalacOptions += "-feature"

//======================== Publication Settings =========================\\

homepage := Some(url("https://github.com/chandu0101/sri-diode"))
licenses +=("Apache-2.0", url("http://opensource.org/licenses/Apache-2.0"))

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

pomExtra :=
  <scm>
    <connection>scm:git:github.com:chandu0101/sri-diode</connection>
    <developerConnection>scm:git:git@github.com:chandu0101/scalajs-diode.git</developerConnection>
    <url>github.com:chandu0101/sri-diode.git</url>
  </scm>
    <developers>
      <developer>
        <id>chandu0101</id>
        <name>Chandra Sekhar Kode</name>
      </developer>
    </developers>


//================ Testing settings =====================\\
libraryDependencies += "org.scalatest" %%% "scalatest" % scalatestVersion % Test

scalaJSStage in Global := FastOptStage

requiresDOM := true

jsDependencies += RuntimeDOM

jsEnv in Test := new PhantomJS2Env(scalaJSPhantomJSClassLoader.value, addArgs = Seq("--web-security=no"))
//jsEnv in Test := NodeJSEnv().value
//postLinkJSEnv in Test := NodeJSEnv().value
