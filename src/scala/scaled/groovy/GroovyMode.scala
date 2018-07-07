//
// Scaled Groovy Mode - a Scaled major mode for editing Groovy code
// http://github.com/scaled/scala-mode/blob/master/LICENSE

package scaled.groovy

import scaled._
import scaled.code.{Commenter, BlockIndenter}
import scaled.grammar.GrammarCodeMode

@Major(name="groovy",
       tags=Array("code", "project", "groovy"),
       pats=Array(".*\\.groovy", ".*\\.gradle"),
       ints=Array("groovy"),
       desc="A major editing mode for the Groovy language.")
class GroovyMode (env :Env) extends GrammarCodeMode(env) {

  override def langScope = "source.groovy"

  override protected def createIndenter = new BlockIndenter(config, Std.seq(
    // bump extends/implements in two indentation levels
    BlockIndenter.adjustIndentWhenMatchStart(Matcher.regexp("(extends|implements)\\b"), 2),
    // align changed method calls under their dot
    new BlockIndenter.AlignUnderDotRule(),
    // handle javadoc and block comments
    new BlockIndenter.BlockCommentRule(),
    // handle indenting switch statements properly
    new BlockIndenter.SwitchRule(),
    // handle continued statements, with some special sauce for : after case
    new BlockIndenter.CLikeContStmtRule()
  ))

  override val commenter = new Commenter() {
    override def linePrefix  = "//"
    override def blockOpen = "/*"
    override def blockPrefix = "*"
    override def blockClose = "*/"
    override def docOpen   = "/**"
  }

  // TODO: more things!
}
