package hipsum

object Parse {
  import scala.util.parsing.json._
  def apply(str: String) =
    JSON.parseFull(str).map {
      _.asInstanceOf[Map[String, Any]]("text").toString
    }
}

trait Requests {
  import dispatch._
  val Host = :/("hipsterjesus.com") / "api"
  def asText = As.string.andThen { Parse.apply }
}

object Text extends Requests {
  import scala.util.control.Exception.allCatch
  import dispatch._
  def apply(paras: Int = 4,
            flav: Flavor = Latin,
            html: Boolean = false): Promise[Either[String, String]] =
    for(either <-
        Http(Host <<? Map(
          "paras" -> math.min(paras, 99).toString,
          "type" -> flav.param
        ) > asText ).either
     ) yield {
       either.left.map(error).right.flatMap {
         _.map { txt =>
           Right(if(html) txt else txt.replaceAll("<p>","")
              .replaceAll("""</p>""", "\r\n"))
         }.getOrElse(Left("Invalid json"))
       }
    }

  private def error(t: Throwable) =
    "Sigh... : %s" format t.getMessage
}
