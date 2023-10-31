package com.lollypop.runtime.instructions.invocables

import com.lollypop.language.HelpDoc.{CATEGORY_ASYNC_REACTIVE, PARADIGM_REACTIVE}
import com.lollypop.language._
import com.lollypop.language.models.{Expression, Instruction}
import com.lollypop.runtime.instructions.expressions.RuntimeExpression.RichExpression
import com.lollypop.runtime.{LollypopVM, Scope}
import com.lollypop.util.OptionHelper.OptionEnrichment
import lollypop.io.IOCost

import java.util.{Timer, TimerTask}

/**
 * after instruction
 * @param delay       the [[Expression delay interval]] of execution
 * @param instruction the [[Instruction command(s)]] to execute
 * @example {{{ after '2 seconds' { delete from @@entries where attachID is null } }}}
 */
case class After(delay: Expression, instruction: Instruction) extends RuntimeInvokable {
  private val timer = new Timer()

  override def execute()(implicit scope: Scope): (Scope, IOCost, Any) = {
    timer.schedule(new TimerTask {
      override def run(): Unit = LollypopVM.execute(scope, instruction)
    }, (delay.asInterval || dieExpectedInterval()).toMillis)
    (scope, IOCost.empty, timer)
  }

  override def toSQL: String = s"after ${delay.toSQL} ${instruction.toSQL}"

}

object After extends InvokableParser {

  override def help: List[HelpDoc] = List(HelpDoc(
    name = "after",
    category = CATEGORY_ASYNC_REACTIVE,
    paradigm = PARADIGM_REACTIVE,
    syntax = templateCard,
    description = "Schedules a one-time execution of command(s) after a specific delay period",
    example =
      """|var ticker = 5
         |after Interval('100 millis') { ticker += 3 }
         |import "java.lang.Thread"
         |Thread.sleep(Long(250))
         |ticker is 8
         |""".stripMargin
  ))

  override def parseInvokable(ts: TokenStream)(implicit compiler: SQLCompiler): After = {
    val params = SQLTemplateParams(ts, templateCard)
    After(delay = params.expressions("delay"), instruction = params.instructions("command"))
  }

  val templateCard: String = "after %e:delay %N:command"

  override def understands(stream: TokenStream)(implicit compiler: SQLCompiler): Boolean = stream is "after"

}