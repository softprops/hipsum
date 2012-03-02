object Build extends sbt.Build {
  import sbt._
  import sbt.Keys._

  lazy val root = Project(
    "root", file("."),
    settings = Defaults.defaultSettings ++ conscript.Harness.conscriptSettings) aggregate(lib, app)
  lazy val lib = Project("hipsum", file("library"), settings = commonSettings)
  lazy val app = Project("app", file("app"), settings = commonSettings) dependsOn(lib)

  def commonSettings: Seq[Setting[_]] = Defaults.defaultSettings ++ Seq(
    organization := "me.lessis",
    version := "0.1.0-SNAPSHOT",
    homepage := Some(url("https://github.com/softprops/hipsum")),
    publishMavenStyle := true,
    publishTo <<= version { v =>
      val nexus = "https://oss.sonatype.org/"
      if (v.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "content/repositories/snapshots") 
      else Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    publishArtifact in Test := false,
    licenses <<= (version)(v => Seq(("MIT" -> url("https://github.com/softprops/hipsum/tree/%s/LICENSE".format(v))))),
    pomExtra := (
      <scm>
        <url>git@github.com:softprops/hipsum.git</url>
        <connection>scm:git:git@github.com:softprops/hipsum.git</connection>
      </scm>
      <developers>
        <developer>
          <id>softprops</id>
          <name>Doug Tangren</name>
        <url>https://github.com/softprops</url>
      </developer>
      </developers>)
  )
}
