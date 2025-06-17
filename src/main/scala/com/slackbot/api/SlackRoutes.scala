package com.slackbot.api

import cats.effect.IO
import cats.implicits._
import com.slackbot.api.SlackEndPoints._
import com.slackbot.client.SlackClient
import com.slackbot.models._
import com.slackbot.models.SlackModels.given
import io.circe.parser._
import org.http4s.HttpRoutes
import sttp.tapir.server.http4s.Http4sServerInterpreter
import java.util.UUID
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

class SlackRoutes(slackClient: SlackClient) {

  private val interpreter = Http4sServerInterpreter[IO]()

  
  private def sendMessage(request: SendMessageRequest): IO[Either[String, SendMessageResponse]] = {
    val messageId = request.messageId.getOrElse(UUID.randomUUID().toString)
    val interactiveMessage = slackClient.createInteractiveMessage(
      channel = request.channel,
      text = request.message,
      messageId = messageId
    )

    slackClient.sendMessage(interactiveMessage)
      .map(_ => Right(SendMessageResponse(success = true, messageId = messageId)))
      .handleError(error => Left(s"Failed to send message: ${error.getMessage}"))
  }
  
  private def handleInteractive(payload: String): IO[Either[String, InteractiveResponse]] = {
    IO {
      for {
        decodedPayload <- Either.catchNonFatal(URLDecoder.decode(payload, StandardCharsets.UTF_8.toString))
        parsedJson <- parse(decodedPayload)
        slackPayload <- parsedJson.as[SlackInteractivePayload]
      } yield {
        val action = slackPayload.actions.headOption
        val actionType = action.map(_.action_id).getOrElse("unknown")
        val user = slackPayload.user.username
        val messageValue = action.map(_.value).getOrElse("unknown")

        val responseMessage = actionType match {
          case "accept_action" => s"✅ Request $messageValue was accepted by $user"
          case "decline_action" => s"❌ Request $messageValue was declined by $user"
          case _ => s"Unknown action $actionType by $user"
        }

        InteractiveResponse(
          message = responseMessage,
          action = actionType,
          user = user
        )
      }
    }.attempt.map {
      case Right(result) => result.left.map(_.getMessage)
      case Left(error) => Left(s"Failed to parse interactive payload: ${error.getMessage}")
    }
  }
  private def healthCheck(): IO[Either[String, String]] = {
    IO.pure(Right("OK - Slack Bot is running"))
  }
  
  val routes: HttpRoutes[IO] = List(
    interpreter.toRoutes(sendMessageEndpoint.serverLogic(sendMessage)),
    interpreter.toRoutes(interactiveCallbackEndpoint.serverLogic(handleInteractive)),
    interpreter.toRoutes(healthEndpoint.serverLogic(_ => healthCheck()))
  ).reduce(_ <+> _)
}