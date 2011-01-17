package org.questions.snippet

import _root_.net.liftweb.http._
import _root_.net.liftweb.util._
import Helpers._
import org.questions.model.{Question, Talk}

/**
 * User: chris
 * Date: 1/16/11
 * Time: 3:20 PM
 */

object AddQuestion {

  private object talkId extends RequestVar(S.param("id").get)
  private object text extends RequestVar("")
  private object questionerName extends RequestVar("")

  def render = {
    "name=text" #> SHtml.textElem(text) &
    "name=questionerName" #> SHtml.textElem(questionerName) &
    "name=talkId" #> SHtml.textElem(talkId) &
    "type=submit" #> SHtml.onSubmitUnit(process)
  }

  def process() = {

    S.notice("Got id param: " + talkId.is)
    S.notice("Got text: " + text)
    S.notice("Got questioner name: " + questionerName)
    val talk : Talk = Talk.findByKey(talkId.get.toString.toLong).get
    val question : Question= Question.create.text(text).talk(talk).questionerName(questionerName)
    question.save
    S.redirectTo("/talks/talk?id=" + talkId.is)


    //
    //      val talk : Talk = Talk.create.speakerName(speakerName).title(title).user(User.currentUser)
    //      talk.save()

  }

}