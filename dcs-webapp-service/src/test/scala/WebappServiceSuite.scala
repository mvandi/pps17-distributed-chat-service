import io.vertx.ext.unit.TestSuite
import io.vertx.lang.scala.ScalaVerticle.nameForVerticle
import io.vertx.scala.core.Vertx
import it.unibo.dcs.service.webapp.verticles.WebAppVerticle
import org.scalamock.scalatest.MockFactory
import org.scalatest.FlatSpec

import scala.language.postfixOps

class WebappServiceSuite extends FlatSpec with MockFactory {

  val testSuite: TestSuite = TestSuite.create("WebappService Test Suite")
  testSuite.beforeEach(_ =>
    Vertx vertx() deployVerticle nameForVerticle[WebAppVerticle]
  ).afterEach(_ => {
    Vertx vertx() undeploy Vertx.vertx().deploymentIDs().head
  })

  "WebAppService" should "fail if registration request has an empty body" in {
    val test = testSuite.test("empty registration fails", context => {
      val async1 = context.async()
      val client = Vertx vertx() createHttpClient
      val req = client.post(8080, "localhost", "/api/register")
      req.exceptionHandler((err) => context.fail(err.getMessage))
      req.handler((resp) => {
        context.assertEquals(500, resp.statusCode)
        async1.complete()
      })
      async1.awaitSuccess()
    })
  }
}
