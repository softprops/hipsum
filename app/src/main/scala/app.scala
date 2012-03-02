package hipsum

object Cli {
  import java.io.{ File, InputStream, OutputStream,
                  FileReader, FileWriter, StringReader, PrintWriter }
  import scala.util.control.Exception.allCatch
  import dispatch._

  val Help = """
  |NAME
  |    hipsum - Do you need some text for your website or whatever? *sigh* Okay...
  | 
  |SYNPOSIS
  |    hipsum [-p|--paras <n>] [-f|--flav <flavor>] [-m|--markup <mu>] [-h|--help]
  |
  |DESCRIPTION
  |    An ipsum text generator for hipsters
  |
  |OPTIONS
  |    -p, --paras
  |        indicates number of paragrams (integer 0-99)
  |
  |    -f, --flav
  |        flavor if hipsterism (latin or neat)
  |
  |    -m, --markup
  |       format (text or html)   
  |
  |    -h, --help
  |        Show this help content
  |
  | hipsum 0.1.0""".stripMargin

  case class Arguments(
    paras: Int = 4,
    flavor: Flavor = Latin,
    html: Boolean = false,
    help: Boolean = false
  )

  def main(args: Array[String]) {
    System.exit(run(args))
  }
  
  private def onError(e: String) = {
    Console.err.println(e)
    1
  }

  private def onSuccess(txt: String) = {
    println(txt)
    0
  }

  private def help = {
    println(Help)
    0
  }

  private def argumented(args: Seq[String]) = {
    (((Right(Arguments()): Either[Int, Arguments])) /: args.grouped(2))((a, e) => {
      e match {
        case Seq("-p" | "--paras", p) =>
          a.right.map(_.copy(paras = allCatch.opt { p.toInt }.getOrElse(4)))
        case Seq("-f" | "--flav", f) =>
          f match {
            case Neat(flv) => a.right.map(_.copy(flavor = flv))
            case Latin(flv) => a.right.map(_.copy(flavor = flv))
            case _ => Left(1)
          }
        case Seq("-m" | "--markup", f) =>
          f match {
            case "html" => a.right.map(_.copy(html = true))
            case "text" => a.right.map(_.copy(html = false))
            case _ => Left(1)
          }
        case _ =>
          a.right.map(_.copy(help = true))
      }
    }).fold({ s => s }, {
      _ match {
        case Arguments(p, f, html, false) =>
          Text(p, f, html)().fold(
             e => onError("hipsum is too cool for you right now %s" format e),
            t => onSuccess(t)
          )
        case _ =>
          help
      }
    })
  }

  def run(args: Array[String]): Int =
    argumented(args)
}

class App extends xsbti.AppMain {
  def run(config: xsbti.AppConfiguration) =
    try { new Exit(Cli.run(config.arguments)) }
    finally { dispatch.Http.shutdown() }
}
class Exit(val code: Int) extends xsbti.Exit
