//
// Scaled Groovy Mode - a Scaled major mode for editing Groovy code
// http://github.com/scaled/groovy-mode/blob/master/LICENSE

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
    effacer("entity.other.inherited-class", typeStyle),
    effacer("entity.name.function", functionStyle),
    effacer("entity.name.val-declaration", variableStyle),

    effacer("storage.modifier", keywordStyle),
    effacer("storage.type.primitive", typeStyle),

    effacer("variable.package", moduleStyle),
    effacer("variable.import", typeStyle),
    effacer("variable.language", constantStyle),
    // effacer("variable.parameter", variableStyle), // leave params white
    effacer("variable.other.type", variableStyle)
  )

  // map TextMate grammar scopes to Scaled syntax definitions
  val syntaxers = List(
    syntaxer("comment.line", Syntax.LineComment),
    syntaxer("comment.block", Syntax.DocComment),
    syntaxer("constant", Syntax.OtherLiteral),
    syntaxer("string.quoted.triple", Syntax.HereDocLiteral),
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
  import Syntax.{HereDocLiteral => HD}

  override def configDefs = GroovyConfig :: super.configDefs

  override def grammars = GroovyConfig.grammars.get
  override def effacers = GroovyConfig.effacers
  override def syntaxers = GroovyConfig.syntaxers

  override def mkParagrapher (syntax :Syntax) =
    if (syntax != HD) super.mkParagrapher(syntax)
    else new Paragrapher(syntax, buffer) {
      override def isDelim (row :Int) = super.isDelim(row) || {
        val ln = line(row)
        (ln.syntaxAt(0) != HD) || (ln.syntaxAt(ln.length-1) != HD)
      }
    }

  override protected def createIndenter = new GroovyIndenter(buffer, config)

  override protected def canAutoFill (p :Loc) :Boolean =
    super.canAutoFill(p) || (buffer.syntaxNear(p) == HD)

  override val commenter :GroovyCommenter = new GroovyCommenter() {
    // the groovy grammar marks all whitespace leading up to the open doc in comment style, so we
    // have to hack this predicate a bit
    override def inDoc (buffer :BufferV, p :Loc) :Boolean = {
      super.inDoc(buffer, p) && {
        val line = buffer.line(p)
        (line.indexOf(openDocM, p.col) == -1)
      }
    }
  }

  //
  // FNs

  override def electricNewline () {
    // shenanigans to determine whether we should auto-insert the doc prefix (* )
    if (commenter.inDoc(buffer, view.point())) {
      newline()
      view.point() = commenter.insertDocPre(buffer, view.point())
      reindentAtPoint()
    } else super.electricNewline()
  }

  // TODO: more things!
}
