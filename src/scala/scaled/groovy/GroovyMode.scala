//
// Scaled Groovy Mode - a Scaled major mode for editing Groovy code
// http://github.com/scaled/scala-mode/blob/master/LICENSE

package scaled.groovy

import scaled._
import scaled.code.{CodeConfig, Commenter}
import scaled.grammar.{Grammar, GrammarConfig, GrammarCodeMode}
import scaled.util.Paragrapher

object GroovyConfig extends Config.Defs {
  import CodeConfig._
  import GrammarConfig._

  // map TextMate grammar scopes to Scaled style definitions
  val effacers = List(
    effacer("comment.line", commentStyle),
    effacer("comment.block", docStyle),
    effacer("constant", constantStyle),
    effacer("invalid", invalidStyle),
    effacer("keyword", keywordStyle),
    effacer("string", stringStyle),

    effacer("entity.name.package", moduleStyle),
    effacer("entity.name.class", typeStyle),
    effacer("entity.name.type.class", typeStyle),
    effacer("entity.other.inherited-class", typeStyle),
    effacer("entity.name.function", functionStyle),
    effacer("entity.name.val-declaration", variableStyle),

    // effacer("meta.definition.method.groovy", functionStyle),
    effacer("meta.method.groovy", functionStyle),

    effacer("storage.modifier.import", moduleStyle),
    effacer("storage.modifier", keywordStyle),
    effacer("storage.type.annotation", preprocessorStyle),
    effacer("storage.type.def", keywordStyle),
    effacer("storage.type", typeStyle),

    effacer("variable.import", typeStyle),
    effacer("variable.language", constantStyle),
    effacer("variable.parameter", variableStyle)
  )

  // map TextMate grammar scopes to Scaled syntax definitions
  val syntaxers = List(
    syntaxer("comment.line", Syntax.LineComment),
    syntaxer("comment.block", Syntax.DocComment),
    syntaxer("constant", Syntax.OtherLiteral),
    syntaxer("string.quoted.double", Syntax.StringLiteral)
  )

  val grammars = resource(Seq("HTML.ndf", "JavaDoc.ndf", "Groovy.ndf"))(Grammar.parseNDFs)
}

@Major(name="groovy",
       tags=Array("code", "project", "groovy"),
       pats=Array(".*\\.groovy", ".*\\.gradle"),
       ints=Array("groovy"),
       desc="A major editing mode for the Groovy language.")
class GroovyMode (env :Env) extends GrammarCodeMode(env) {
  import CodeConfig._
  import scaled.util.Chars._

  override def configDefs = GroovyConfig :: super.configDefs

  override def grammars = GroovyConfig.grammars.get
  override def effacers = GroovyConfig.effacers
  override def syntaxers = GroovyConfig.syntaxers

  override protected def createIndenter = new GroovyIndenter(config)

  override val commenter = new Commenter() {
    val atCmdM = Matcher.regexp("@[a-z]+")

    override def linePrefix  = "//"
    override def blockOpen = "/*"
    override def blockPrefix = "*"
    override def blockClose = "*/"
    override def docOpen   = "/**"

    override def mkParagrapher (syn :Syntax, buf :Buffer) = new CommentParagrapher(syn, buf) {
      private def isAtCmdLine (line :LineV) = line.matches(atCmdM, commentStart(line))
      // don't extend paragraph upwards if the current top is an @cmd
      override def canPrepend (row :Int) =
        super.canPrepend(row) && !isAtCmdLine(line(row+1))
      // don't extend paragraph downwards if the new line is at an @cmd
      override def canAppend (row :Int) =
        super.canAppend(row) && !isAtCmdLine(line(row))
    }
  }

  // TODO: more things!
}
