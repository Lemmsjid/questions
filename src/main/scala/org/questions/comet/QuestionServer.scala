package org.questions.comet

import net.liftweb._
import common.Box
import http._
import actor._
import mapper.By
import util.Helpers._
import js._
import JsCmds._
import JE._
import js.jquery.JqJsCmds.{AppendHtml, FadeOut, Hide, FadeIn}
import scala.xml.{Text, NodeSeq}
import org.questions.model.{Question, Talk}

/**
 * User: chris
 * Date: 1/15/11
 * Time: 12:00 PM
 */

final case class Remove(guid: Long)

object QuestionServer extends LiftActor with ListenerManager {

  def createUpdate = {}

  override protected def lowPriority = {
    case (id: Long, question: String) => {
      val talk = Talk.findByKey(id).get
      val q = Question.create.text(question).talk(talk)
      q.save
      updateListeners(q)
    }
  }
}

class QuestionSession extends CometActor with CometListener {
  private var msgs: Vector[Question] = Vector()


  def getQuestions(): List[Question] = Question.findAll(By(Question.talk, 1l))

  def registerWith = QuestionServer

  override def lowPriority = {
    case (Remove(guid), v: Vector[Question]) => {
      partialUpdate(
        FadeOut(guid.toString, TimeSpan(0), TimeSpan(500)) &
                After(TimeSpan(500), Replace(guid.toString, NodeSeq.Empty)))
    }

    case (m: Question) => {
      partialUpdate(
        AppendHtml("ul_dude", doLine(m)(("li ^^" #> "^^")(defaultXml))) &
                Hide(m.id.get.toString) & FadeIn(m.id.get.toString, TimeSpan(0), TimeSpan(500)))
    }
    case v: Vector[Question] => msgs = v; reRender()
  }

  def render = {
    notice("rendering")
    val qs = getQuestions()
    if(qs.length == 0) {
      <p>Sorry, no questions</p>
    }else {
      "ul [id]" #> "ul_dude" & "li" #> qs.map(doLine)
    }
    <lift:form>
      {SHtml.text("", s => {
      QuestionServer ! (S.param("id").get.toLong,s)
      SetValById("chat_box", "")
    }, "id" -> "chat_box")}
        <input type="submit" value="Chat"/>
    </lift:form>

  }


  private def doLine(m: Question)(node: NodeSeq) = {
    ("li [id]" #> m.id & // set GUID
            // set body
            "li *" #> (Text(m.text + " ") ++
                    SHtml.ajaxButton("delete", () => {
                      QuestionServer ! Remove(m.id.get)
                      Noop
                    })))
    (node)
  }

  override def fixedRender = {
    <lift:form>
      {SHtml.text("", s => {
      QuestionServer ! (S.param("id").get.toLong,s)
      SetValById("chat_box", "")
    }, "id" -> "chat_box")}
        <input type="submit" value="Chat"/>
    </lift:form>
  }
}