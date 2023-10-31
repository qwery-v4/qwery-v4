package com.lollypop.runtime.instructions.expressions

import com.lollypop.language.models.{ArrayExpression, Expression, Literal}
import com.lollypop.runtime.datatypes.{ArrayType, DataType, Inferences}
import com.lollypop.runtime.plastics.Tuples.seqToArray
import com.lollypop.runtime.{LollypopVM, Scope}

/**
 * Represents an array literal
 * @param value the values within the array
 */
case class ArrayLiteral(value: List[Expression]) extends Literal with ArrayExpression with RuntimeExpression {
  private lazy val _type = ArrayType(Inferences.resolveType(value.map(Inferences.inferType)), capacity = Some(value.size))

  override def evaluate()(implicit scope: Scope): Any = seqToArray(values = value.map(LollypopVM.execute(scope, _)._3))

  override def returnType: DataType = _type

  override def toSQL: String = value.map(_.toSQL).mkString("[", ", ", "]")

}

object ArrayLiteral {
  def apply(values: Expression*): ArrayLiteral = {
    new ArrayLiteral(values.toList)
  }
}