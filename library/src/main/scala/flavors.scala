package hipsum

sealed trait Flavor {
  def param: String
  def arg: String
  def unapply(f: String): Option[Flavor] =
    if(arg.equalsIgnoreCase(f)) Some(this) else None
}

case object Latin extends Flavor {
  val param = "hipster-latin"
  val arg = "latin"
}

case object Neat extends Flavor {
  val param = "hipster-centric"
  val arg = "neat"
}
