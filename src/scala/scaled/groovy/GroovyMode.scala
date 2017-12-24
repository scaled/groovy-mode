//
// Scaled Groovy Mode - a Scaled major mode for editing Groovy code
// http://github.com/scaled/scala-mode/blob/master/LICENSE

package scaled.groovy

import scaled._
import scaled.code.Commenter
import scaled.grammar.GrammarCodeMode

@Major(name="groovy",
       tags=Array("code", "project", "groovy"),
       pats=Array(".*\\.groovy", ".*\\.gradle"),
       ints=Array("groovy"),
       desc="A major editing mode for the Groovy language.")
class GroovyMode (env :Env) extends GrammarCodeMode(env) {

  override def langScope = "source.groovy"

  override protected def createIndenter = new GroovyIndenter(config)

  override val commenter = new Commenter() {
    override def linePrefix  = "//"
    override def blockOpen = "/*"
    override def blockPrefix = "*"
    override def blockClose = "*/"
    override def docOpen   = "/**"
  }

  // TODO: more things!
}
