package filters

import javax.inject.Inject
import akka.stream.Materializer
import play.api.Logger
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}

class LoggingFilter @Inject() (implicit val mat: Materializer, ec: ExecutionContext) extends Filter {

  def apply(nextFilter: RequestHeader => Future[Result])
           (requestHeader: RequestHeader): Future[Result] = {

    val startTime = System.currentTimeMillis

    nextFilter(requestHeader).map { result =>

      val endTime = System.currentTimeMillis
      val requestTime = endTime - startTime

      Logger.info(s"${requestHeader.method} ${requestHeader.uri} took ${requestTime}ms and returned ${result.header.status}")

      result.withHeaders("Request-Time" -> requestTime.toString)
    }
  }
}

//
//import javax.inject.Inject
//import akka.stream.Materializer
//import play.api.mvc.{Result, RequestHeader, Filter}
//import play.api.Logger
//import play.api.routing.Router.Tags
//import scala.concurrent.Future
//import play.api.libs.concurrent.Execution.Implicits.defaultContext
//
//class LoggingFilter @Inject() (implicit val mat: Materializer) extends Filter {
//  def apply(nextFilter: RequestHeader => Future[Result])
//           (requestHeader: RequestHeader): Future[Result] = {
//
//    val startTime = System.currentTimeMillis
//
//    nextFilter(requestHeader).map { result =>
//
//      val action = requestHeader.tags(Tags.RouteController) +
//        "." + requestHeader.tags(Tags.RouteActionMethod)
//      val endTime = System.currentTimeMillis
//      val requestTime = endTime - startTime
//
//      Logger.info(s"${action} took ${requestTime}ms and returned ${result.header.status}")
//
//      result.withHeaders("Request-Time" -> requestTime.toString)
//    }
//  }
//}