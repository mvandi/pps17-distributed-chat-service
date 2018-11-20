package it.unibo.dcs.commons

import java.text.SimpleDateFormat
import java.util.Date

import com.google.gson.GsonBuilder

import scala.language.implicitConversions

package object dataaccess {

  private val mySqlPattern = "yyyy-MM-dd'T'HH:mm:ss'Z'"
  private val mySqlFormat = new SimpleDateFormat(mySqlPattern)

  def gsonConfiguration(builder: GsonBuilder): Unit = builder.setDateFormat(mySqlPattern)

  object Implicits {

    implicit def dateToString(date: Date): String = mySqlFormat.format(date)

    implicit def stringToDate(date: String): Date = mySqlFormat.parse(date)

    implicit def booleanToString(value: Boolean): String = if (value) "1" else "0"

    implicit def stringToBoolean(value: String): Boolean = value match {
      case "1" => true
      case "0" => false
      case _ => throw new IllegalArgumentException(s"illegal boolean value: $value")
    }

  }

}
