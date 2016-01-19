package com.dwolla.testutils.matchers

import org.specs2.matcher.{Expectable, MatchResult, Matcher}

import util.Try

trait AdditionalSeqMatchers
  extends ContainSliceMatcher
    with StartWithMatcher
    with EndWithMatcher

trait ContainSliceMatcher {
  def containSlice[T](expected: T*): ContainSlice[T] = new ContainSlice[T](expected: _*)

  class ContainSlice[T](expected: T*) extends Matcher[Seq[T]] {
    override def apply[S <: Seq[T]](t: Expectable[S]): MatchResult[S] = {
      val prettySlice = s"( ${expected.mkString(", ")} )"
      result(
        test = t.value.containsSlice(expected),
        okMessage = s"${t.description} contains the slice $prettySlice",
        koMessage = s"${t.description} does not contain the slice $prettySlice",
        value = t
      )
    }
  }

}

trait StartWithMatcher {
  def startWith[T](expected: T): StartWith[T] = new StartWith[T](expected)

  class StartWith[T](expected: T) extends Matcher[Seq[T]] {
    override def apply[S <: Seq[T]](t: Expectable[S]): MatchResult[S] = result(
      test = t.value.headOption.exists(_ == expected),
      okMessage = s"""${t.description} starts with "$expected"""",
      koMessage = s"""${t.description} does not start with "$expected"""",
      value = t
    )
  }

}

trait EndWithMatcher {
  def endWith[T](expected: T): EndWith[T] = new EndWith[T](expected)

  class EndWith[T](expected: T) extends Matcher[Seq[T]] {
    override def apply[S <: Seq[T]](t: Expectable[S]): MatchResult[S] = result(
      test = Try {
        t.value.last == expected
      }.getOrElse(false),
      okMessage = s"""${t.description} ends with "$expected"""",
      koMessage = if (t.value.isEmpty)
        s"""The provided (but empty) sequence does not end with "$expected""""
      else
        s"""${t.description} does not end with "$expected"""",
      value = t
    )
  }

}
