package com.slackbot.api

import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.*
import io.circe.generic.auto.*

object SlackEndPoints {
  
  case class SendMessageRequest(
                                 channel: String,
                                 message: String,
                                 messageId: Option[String] = None
                               )

  case class SendMessageResponse(
                                  success: Boolean,
                                  messageId: String
                                )
  
  case class InteractiveResponse(
                                  message: String,
                                  action: String,
                                  user: String
                                )

  val sendMessageEndpoint: PublicEndpoint[SendMessageRequest, String, SendMessageResponse, Any] =
    endpoint.post
      .in("slack" / "send")
      .in(jsonBody[SendMessageRequest])
      .out(jsonBody[SendMessageResponse])
      .errorOut(stringBody)
      .name("Send Interactive Message")
      .description("Send a message with Accept/Decline buttons to Slack")

  val interactiveCallbackEndpoint: PublicEndpoint[String, String, InteractiveResponse, Any] =
    endpoint.post
      .in("slack" / "interactive")
      .in(stringBody)
      .out(jsonBody[InteractiveResponse])
      .errorOut(stringBody)
      .name("Handle Interactive Callback")
      .description("Handle button clicks from Slack interactive messages")

  val healthEndpoint: PublicEndpoint[Unit, String, String, Any] =
    endpoint.get
      .in("health")
      .out(stringBody)
      .errorOut(stringBody)
      .name("Health Check")
      .description("Health check endpoint")
}