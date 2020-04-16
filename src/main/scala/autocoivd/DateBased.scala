package autocoivd

class DateBased() {
  var data: Map[String, Int] = Map()

  def loadRowsCases(rows: List[NYTData]): Unit = {
    data = Map()
    rows.foreach { row =>
      increment(row.county, row.cases)
    }
  }

  def increment(k: String, v: Int): Unit = {
    val nextval = v + data.getOrElse(k, 0)
    data = data + (k -> nextval)
  }

  def loadRowsDeaths(rows: List[NYTData]): Unit = {
    data = Map()
    rows.foreach { row =>
      increment(row.county, row.deaths)
    }
  }

  override def toString(): String = {
    data.collect { case (k, v) =>
      s"$k $v"
    }.mkString("\n")
  }
}
