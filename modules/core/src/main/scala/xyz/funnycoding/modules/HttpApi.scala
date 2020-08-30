package xyz.funnycoding.modules

import cats.effect.{ Concurrent, Sync, Timer }
import org.http4s.server.middleware._

import scala.concurrent.duration._
import org.http4s._
import org.http4s.implicits._
import xyz.funnycoding.http.routes.BooksRoute

object HttpApi {
  def make[F[_]: Concurrent: Timer](algebras: Algebras[F]): F[HttpApi[F]] = Sync[F].delay {
    new HttpApi(algebras)
  }
}

class HttpApi[F[_]: Timer: Concurrent](algebras: Algebras[F]) {
  private val booksRoute = new BooksRoute[F](algebras.books).routes

  private val middleware: HttpRoutes[F] => HttpRoutes[F] = {
    { http: HttpRoutes[F] =>
      AutoSlash(http)
    } andThen { http: HttpRoutes[F] =>
      CORS(http, CORS.DefaultCORSConfig)
    } andThen { http: HttpRoutes[F] =>
      Timeout(60.seconds)(http)
    }
  }

  private val loggers: HttpApp[F] => HttpApp[F] = {
    { http: HttpApp[F] =>
      RequestLogger.httpApp(logHeaders = true, logBody = true)(http)
    } andThen { http: HttpApp[F] =>
      ResponseLogger.httpApp(logHeaders = true, logBody = true)(http)
    }
  }

  val httpApp: HttpApp[F] = loggers(middleware(booksRoute).orNotFound)
}