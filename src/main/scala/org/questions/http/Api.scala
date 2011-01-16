package org.questions.http

import net.liftweb.json.Extraction._
import org.questions.model.Talk
import net.liftweb.http.rest.RestHelper
import net.liftweb.json.JsonAST.JValue

/**
 * User: chris
 * Date: 1/15/11
 * Time: 5:29 PM
 */

trait Convertable {
  def toJson: JValue
}


object Api extends RestHelper {


  case class TalkJson(speaker: String, title: String)

  def talkToJson(talk: Talk): TalkJson = {
    TalkJson(talk.speakerName, talk.title)
  }

  serve {
    case "api" :: "talks" :: _ JsonGet _ => decompose(TalkJson("Chris Bissell", "Lambs"))
    case "api" :: "talk" :: id :: _ JsonGet _ => decompose(talkToJson(Talk.find(id.toLong).get))

//    serveJx {
////      case Post("api" :: "talk" :: "add" :: _, _) => addTalk
//    }


    //    case Req("api" :: "talks" :: "add" :: PostRequest) =>

  }
}