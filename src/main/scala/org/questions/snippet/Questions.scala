package org.questions.snippet

import scala.collection.JavaConversions._
import _root_.net.liftweb.http._
import S._
import _root_.net.liftweb.util._
import Helpers._
import _root_.scala.xml._
import org.questions.model.Question
import net.liftweb.mapper.By
import net.liftweb.common.Full
import net.liftweb.util.BindHelpers._


/**
 * User: chris
 * Date: 1/16/11
 * Time: 4:42 PM
 */

class Questions {
  def show(xhtml:NodeSeq) : NodeSeq = {
    S.param("id") match {
      case Full(id) => {
        notice("Found talk " + id.toString)
        val questions : List[Question] = Question.findAll(By(Question.talk,id.toLong))
        notice("Found questions: " + questions.length)

        ("#questions" #> {
          "li" #> questions.map(q=>
            "span" #> q.text
          )
        }).apply(xhtml)
      }
      case _ => {
        <p>No questions</p>
      }
    }
  }
}