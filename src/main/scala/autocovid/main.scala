package autocoivd

import java.io._
import scala.util.Try

// date,county,state,fips,cases,deaths
case class NYTData(
  date: String,
  county: String,
  state: String,
  fips: String,
  cases: Int,
  deaths: Int
) {
  def toCsvOutRow: CsvOutRow = CsvOutRow(date, cases, deaths)
}

case class CsvOutRow(
  date: String,
  cases: Int,
  deaths: Int
) {
  def add(i: CsvOutRow): CsvOutRow = {
    CsvOutRow(i.date, i.cases + cases, i.deaths + deaths)
  }

  def toCsvString: String = s"$date,$cases,$deaths\n"
}

object NYTData {
  def fromString(input: String): Option[NYTData] = {
    val parts = input.split(",")
    if (parts.length != 6) {
      None
    } else {
      val tryRow = for {
        icase <- Try(parts(4).toInt)
        ideath <- Try(parts(5).toInt)
        row = NYTData(parts(0).trim, parts(1).trim, parts(2).trim, parts(3).trim, icase, ideath)
      } yield row
      tryRow.toOption
    }
  }
}

object Main {
  val allData: List[NYTData] = getAllData

  def getAllData: List[NYTData] = {
    val bufferedSource = io.Source.fromResource("us-counties.csv")
    bufferedSource
      .getLines()
      .map { l => NYTData.fromString(l) }
      .filterNot(_.isEmpty)
      .map(_.get)
      .toList
  }

  val bayAreaCounties: Seq[String] = Seq(
    "alameda",
    "contra costa",
    "san francisco",
    "santa clara",
    "marin",
    "napa",
    "san mateo",
    "solano",
    "sonoma"
  )

  def buildDataFromCsv(rows: List[NYTData], counties: Seq[String]): Map[String, CsvOutRow] = {
    spacer()
    var data: Map[String, CsvOutRow] = Map()
    val myrows = rows.filter { row => counties.contains(row.county.toLowerCase) }
    myrows.foreach { row =>
      val existing = data.get(row.date)
      val next: CsvOutRow = existing.map(row.toCsvOutRow.add(_)).getOrElse(row.toCsvOutRow)
      data = data + (row.date -> next)
    }
    data
  }

  def table(data: Map[String, CsvOutRow]): Unit = {
    data.collect { case (date, row) =>
      row
    }.toList.sortBy(_.date).foreach { row =>
      println(s"${row.date}\t${row.cases}\t${row.deaths}")
    }
  }

  def writeCsvFile(filename: String, data: Map[String, CsvOutRow]): Unit = {
    val sorted = data.values.toList.sortBy(_.date)
    val bw = new BufferedWriter(new FileWriter(new File(filename)))
    bw.write("Date,Cases,Deaths\n")
    for (line <- sorted) {
      bw.write(line.toCsvString)
    }
    bw.close()
  }

  val alamedaCounties: Seq[String] = Seq("alameda")

  def main(args: Array[String]): Unit = {
    spacer()
    println("Hey sbt!")
    println(s"number of rows: ${allData.length}")
    val alameda = buildDataFromCsv(allData, alamedaCounties)
    println(s"Alameda records: ${alameda.size}")
    table(alameda)
    writeCsvFile("alameda.csv", alameda)

    val bayArea = buildDataFromCsv(allData, bayAreaCounties)
    println(s"Bay area records: ${bayArea.size}")
    table(bayArea)
    writeCsvFile("bay-area.csv", bayArea)

    spacer()
  }

  def spacer(): Unit = {
    println("\n=======\n")
  }
}
