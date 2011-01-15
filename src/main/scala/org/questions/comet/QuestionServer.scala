package org.questions.comet

import net.liftweb._
import http._
import actor._
import util.Helpers._
import js._
import JsCmds._
import JE._
import js.jquery.JqJsCmds.{AppendHtml, FadeOut, Hide, FadeIn}
import java.util.Date
import scala.xml.{Text, NodeSeq}

/**
 * User: chris
 * Date: 1/15/11
 * Time: 12:00 PM
 */

final case class Question(question:String,when:Date = new Date(),guid:String = nextFuncName)
final case class Remove(guid:String)

object QuestionServer extends LiftActor with ListenerManager {
  private var questions = Vector(Question("Welcome"))

  def createUpdate = questions

  override protected def lowPriority = {
    case s: String =>{
      val m = Question(s)
      questions :+= m
      updateListeners(m -> questions)
    }
    case r @ Remove(guid) => {
      questions = questions.filterNot(_.guid == guid)
      updateListeners(r -> questions)
    }
  }
}

class QuestionSession extends CometActor with CometListener {
  private var msgs: Vector[Question] = Vector()

  def registerWith = QuestionServer

  override def lowPriority = {
    case (Remove(guid), v: Vector[Question]) => {
      msgs = v
      partialUpdate(
        FadeOut(guid,TimeSpan(0),TimeSpan(500)) &
        After(TimeSpan(500),Replace(guid, NodeSeq.Empty)))
    }

    case (m: Question, v: Vector[Question]) => {
      msgs = v
      partialUpdate(
        AppendHtml("ul_dude", doLine(m)(("li ^^" #> "^^")(defaultXml))) &
        Hide(m.guid) & FadeIn(m.guid, TimeSpan(0),TimeSpan(500)))
    }

    case v: Vector[Question] => msgs = v; reRender()
  }

  def render = "ul [id]" #> "ul_dude" & "li" #> msgs.map(doLine)

  private def doLine(m: Question)(node: NodeSeq) = {
    ("li [id]" #> m.guid & // set GUID
     // set body
     "li *" #> (Text(m.question+" ") ++
                SHtml.ajaxButton("delete", () => {
                  QuestionServer ! Remove(m.guid)
                  Noop
                })))(node)
  }

  override def fixedRender = {
    <lift:form>
    {
      SHtml.text("", s => {
        QuestionServer ! s
        SetValById("chat_box", "")
      }, "id" -> "chat_box")
    }
    <input type="submit" value="Chat"/>
    </lift:form>
  }
}