package org.questions.snippet

import scala.collection.JavaConversions._
import _root_.net.liftweb.http._
import S._
import _root_.net.liftweb.util._
import Helpers._
import _root_.scala.xml._
import org.questions.model.{User, Talk}

/**
 * User: chris
 * Date: 1/15/11
 * Time: 7:40 PM
 */

object AddTalk {
  def render = {
    var speakerName = ""
    var title = ""

    def process() = {
      S.notice("got speaker name " + speakerName)
      S.notice("got title " + title)

      val talk : Talk = Talk.create.speakerName(speakerName).title(title).user(User.currentUser)
      talk.save()

   }
    "name=speakerName" #> SHtml.onSubmit(speakerName=_) &
            "name=title" #> SHtml.onSubmit(title=_) &
            "type=submit" #> SHtml.onSubmitUnit(process)
  }

}