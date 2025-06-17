package com.slackbot

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import com.comcast.ip4s._
import com.slackbot.api.SlackRoutes
import com.slackbot.client.SlackClient
import com.slackbot.config.Config
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.middleware.{CORS, Logger}
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.server.http4s.Http4sServerInterpreter

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] = {
    for {

      config <- IO.pure(Config.load)

      slackClient = new SlackClient(config.slack.botToken)
      slackRoutes = new SlackRoutes(slackClient)

      swaggerEndpoints = SwaggerInterpreter()
        .fromEndpoints[IO](
          List(
            com.slackbot.api.SlackEndPoints.sendMessageEndpoint,
            com.slackbot.api.SlackEndPoints.interactiveCallbackEndpoint,
            com.slackbot.api.SlackEndPoints.healthEndpoint
          ),
          "Slack Bot API",
          "1.0.0"
        )

      swaggerRoutes = Http4sServerInterpreter[IO]().toRoutes(swaggerEndpoints)
      allRoutes = slackRoutes.routes <+> swaggerRoutes

      finalRoutes = Logger.httpRoutes(true, true)(CORS.policy.withAllowOriginAll(allRoutes))

      _ <- EmberServerBuilder
        .default[IO]
        .withHost(Host.fromString(config.server.host).getOrElse(ipv4"0.0.0.0"))
        .withPort(Port.fromInt(config.server.port).getOrElse(port"8080"))
        .withHttpApp(finalRoutes.orNotFound)
        .build
        .use { server =>
          IO.println(s"Server started at ${server.address}") *>
            IO.println(s"Swagger UI available at: http://${config.server.host}:${config.server.port}/docs") *>
            IO.println("Press ENTER to stop the server...") *>
            IO.readLine *>
            IO.println("Stopping the server...")
        }
    } yield ExitCode.Success
  }
}