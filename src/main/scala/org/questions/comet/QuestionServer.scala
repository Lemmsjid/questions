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
import org.questions.model.{Question, Talk}
import scala.collection._

/**
 * User: chris
 * Date: 1/15/11
 * Time: 12:00 PM
 */

final case class Remove(guid:Long)

object QuestionServer extends LiftActor with ListenerManager {
  private var talks = new mutable.HashMap[Long,mutable.Set[Question]] with mutable.MultiMap[Long,Question]


  def createUpdate = talks

  override protected def lowPriority = {
    case  (id:Long,question:String) =>{
      val talk = Talk.findByKey(id).get
      val q = Question.create.text(question).talk(talk)
      q.save
      talks.add(id,q)
      updateListeners(q -> talks)
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
        FadeOut(guid.toString,TimeSpan(0),TimeSpan(500)) &
        After(TimeSpan(500),Replace(guid.toString, NodeSeq.Empty)))
    }

    case (m: Question, v: Vector[Question]) => {
      msgs = v
      partialUpdate(
        AppendHtml("ul_dude", doLine(m)(("li ^^" #> "^^")(defaultXml))) &
        Hide(m.id.get.toString) & FadeIn(m.id.get.toString, TimeSpan(0),TimeSpan(500)))
    }

    case v: Vector[Question] => msgs = v; reRender()
  }

  def render = "ul [id]" #> "ul_dude" & "li" #> msgs.map(doLine)

  private def doLine(m: Question)(node: NodeSeq) = {
    ("li [id]" #> m.id & // set GUID
     // set body
     "li *" #> (Text(m.text+" ") ++
                SHtml.ajaxButton("delete", () => {
                  QuestionServer ! Remove(m.id.get)
                  Noop
                })))(node)
  }

  override def fixedRender = {
    <lift:form>
    {
      SHtml.text("", s => {
        QuestionServer ! (S.param("id").get.toLong,s)
        SetValById("chat_box", "")
      }, "id" -> "chat_box")
    }
    <input type="submit" value="Chat"/>
    </lift:form>
  }
}