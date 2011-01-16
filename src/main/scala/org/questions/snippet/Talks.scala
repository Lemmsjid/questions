package org.questions.snippet

import org.questions.model.User
import net.liftweb.common.Full
import xml.{NodeSeq, Text}

// Import Helper's implicits for binding

import net.liftweb.util.BindHelpers._

/**
 * User: chris
 * Date: 1/15/11
 * Time: 7:25 PM
 */

class Talks {
  def summary(xhtml:NodeSeq):NodeSeq = {
    User.currentUser match {
      case Full(user) => {
        user.allTalks match {
          case Nil => Text("No talks yet")
          case talks => {
            ("#talk" #> {
              "li" #> talks.map{ talk =>
                "a [href]" #> ("/talks/talk?id=" + talk.id) &
                "a *" #> talk.title &
                "#speaker" #> talk.speakerName &
                "#date" #> talk.createdAt
              }
            }).apply(xhtml)
          }
        }
      }
      case _ => {
        <p>You don't got shit yet</p>
      }
    }
  }
}