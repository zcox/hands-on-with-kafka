package utils

import scala.util.Random

object Utils {
  def newRandomName(length: Int = 3): String = Random.alphanumeric take length mkString
}