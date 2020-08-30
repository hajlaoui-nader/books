package xyz.funnycoding

import org.scalacheck.Arbitrary
import xyz.funnycoding.domain.data._
import xyz.funnycoding.generators._

object arbitrairies {
  implicit val arbBook: Arbitrary[Book]             = Arbitrary(bookGen)
  implicit val arbCreateBook: Arbitrary[CreateBook] = Arbitrary(createBookGen)
}