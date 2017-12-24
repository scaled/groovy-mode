//
// Scaled Groovy Mode - a Scaled major mode for editing Groovy code
// http://github.com/scaled/groovy-mode/blob/master/LICENSE

package scaled.groovy

import scaled._
import scaled.code.CodeConfig
import scaled.grammar._

@Plugin(tag="textmate-grammar")
class GroovyGrammarPlugin extends GrammarPlugin {
  import CodeConfig._

  override def grammars = Map("source.groovy" -> "Groovy.ndf")

  override def effacers = List(
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

  override def syntaxers = List(
    syntaxer("comment.line",  Syntax.LineComment),
    syntaxer("comment.block", Syntax.DocComment),
    syntaxer("constant",      Syntax.OtherLiteral),
    syntaxer("string.quoted", Syntax.StringLiteral)
  )
}
