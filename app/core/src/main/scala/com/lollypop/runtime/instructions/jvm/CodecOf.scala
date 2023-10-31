package com.lollypop.runtime.instructions.jvm

import com.lollypop.language.HelpDoc.{CATEGORY_JVM_REFLECTION, PARADIGM_FUNCTIONAL}
import com.lollypop.language.models.Expression
import com.lollypop.runtime.datatypes.Inferences
import com.lollypop.runtime.instructions.expressions.{RuntimeExpression, StringExpression}
import com.lollypop.runtime.instructions.functions.{FunctionCallParserE1, ScalarFunctionCall}
import com.lollypop.runtime.{LollypopVM, Scope}

/**
 * CodecOf() function - returns the CODEC (encoder/decoder) of an expression
 * @param expression the expression being tested
 * @example {{{
 *  val b = "HELLO".getBytes()
 *  codecOf(b) // VarBinary(5)
 * }}}
 */
case class CodecOf(expression: Expression) extends ScalarFunctionCall with RuntimeExpression with StringExpression {
  override def evaluate()(implicit scope: Scope): String = {
    val value = LollypopVM.execute(scope, expression)._3
    Inferences.fromValue(value).toSQL
  }
}

object CodecOf extends FunctionCallParserE1(
  name = "codecOf",
  category = CATEGORY_JVM_REFLECTION,
  paradigm = PARADIGM_FUNCTIONAL,
  description =
    """|Returns the CODEC (encoder/decoder) of an expression.
       |""".stripMargin,
  example =
    """|val counter = 5
       |codecOf(counter)
       |""".stripMargin)