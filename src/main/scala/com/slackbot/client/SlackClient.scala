package com.slackbot.client

import cats.effect.IO
import com.slackbot.models._
import com.slackbot.models.SlackModels.given
import io.circe.syntax._
import io.circe.Json
import sttp.client3._
import sttp.client3.circe._
import sttp.client3.http4s.Http4sBackend

class SlackClient(botToken: String) {

  private val backendResource = Http4sBackend.usingDefaultEmberClientBuilder[IO]()

  def sendMessage(message: SlackMessage): IO[Unit] = {
    backendResource.use { implicit backend =>
      val request = basicRequest
        .post(uri"https://slack.com/api/chat.postMessage")
        .header("Authorization", s"Bearer $botToken")
        .header("Content-Type", "application/json")
        .body(message.asJson)
        .response(asJson[Json])

      request.send(backend).flatMap { response =>
        response.body match {
          case Right(json) =>
            //as i am working with json later with different
            val okField = json.hcursor.get[Boolean]("ok")
            okField match {
              case Right(true) => IO.println("Message sent successfully")
              case Right(false) =>
                val errorMsg = json.hcursor.get[String]("error").getOrElse("Unknown error")
                IO.raiseError(new RuntimeException(s"Slack API error: $errorMsg"))
              case Left(_) => IO.println("Message sent (status unclear)")
            }
          case Left(error) => IO.raiseError(new RuntimeException(s"Failed to send message: $error"))
        }
      }
    }
  }

  def createInteractiveMessage(channel: String, text: String, messageId: String = "approval_request"): SlackMessage = {
    val blocks = List(
      SlackBlock(
        `type` = "section",
        text = Some(SlackText(`type` = "mrkdwn", text = text))
      ),
      SlackBlock(
        `type` = "actions",
        elements = Some(List(
          SlackElement(
            `type` = "button",
            text = Some(SlackText(`type` = "plain_text", text = "Accept", emoji = Some(true))),
            action_id = Some("accept_action"),
            value = Some(messageId),
            style = Some("primary")
          ),
          SlackElement(
            `type` = "button",
            text = Some(SlackText(`type` = "plain_text", text = "Decline", emoji = Some(true))),
            action_id = Some("decline_action"),
            value = Some(messageId),
            style = Some("danger")
          )
        ))
      )
    )

    SlackMessage(
      channel = channel,
      text = text,
      blocks = Some(blocks)
    )
  }
}