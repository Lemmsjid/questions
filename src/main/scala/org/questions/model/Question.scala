package org.questions.model

import _root_.net.liftweb.mapper._

class Question extends LongKeyedMapper[Question] with CreatedUpdated {

  def getSingleton = Question

  def primaryKeyField = id

  object id extends MappedLongIndex(this)
  object text extends MappedString(this, 160)
  object talk extends LongMappedMapper(this, Talk)
  object votes extends MappedInt(this)
}

object Question extends Question with LongKeyedMetaMapper[Question] {

}