package zio.dynamodb.examples.dynamodblocal

import zio.dynamodb.DynamoDBQuery.put
import zio.dynamodb._
import zio.dynamodb.examples.dynamodblocal.DynamoDB._
import zio.dynamodb.examples.model.Student._
import zio.dynamodb.examples.model._
import zio.stream.ZStream
import zio.{ Console, ZIOAppDefault }

/**
 * An equivalent app to [[StudentJavaSdkExample]] but using `zio-dynamodb` - note the reduction in boiler plate code!
 */
object StudentZioDynamoDbExample extends ZIOAppDefault {

  private val program = for {
    _ <- batchWriteFromStream(ZStream(avi, adam)) { student =>
           put("student", student)
         }.runDrain
    _ <- put("student", avi.copy(payment = Payment.CreditCard)).execute
    _ <- batchReadFromStream("student", ZStream(avi, adam))(s => primaryKey(s.email, s.subject))
           .tap(errorOrStudent => Console.printLine(s"student=$errorOrStudent"))
           .runDrain
  } yield ()

  override def run = program.provide(dynamoDBExecutorLayer, studentTableLayer)
}
