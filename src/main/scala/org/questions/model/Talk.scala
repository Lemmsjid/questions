package org.questions.model

import net.liftweb.mapper._

/**
 * User: chris
 * Date: 1/15/11
 * Time: 1:24 PM
 */

class Talk extends LongKeyedMapper[Talk] with OneToMany[Long,Talk] with CreatedUpdated
{
  def getSingleton = Talk
  def primaryKeyField = id
  object id extends MappedLongIndex(this)
  object speakerName extends MappedString(this,140) {
    override def dbIndexed_? = true
  }
  object title extends MappedString(this, 140)
  object abs extends MappedText(this)
  object questions extends MappedOneToMany(Question,Question.talk,OrderBy(Question.votes,Descending))
}

object Talk extends Talk with LongKeyedMetaMapper[Talk]
