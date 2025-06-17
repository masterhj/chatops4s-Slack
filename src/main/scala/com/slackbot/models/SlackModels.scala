package com.slackbot.models

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.*


case class SlackUser(
                      id: String,
                      username: String,
                      name: String
                    )

case class SlackTeam(
                      id: String,
                      domain: String
                    )

case class SlackChannel(
                         id: String,
                         name: String
                       )

case class SlackAction(
                        `type`: String,
                        action_id: String,
                        block_id: String,
                        text: SlackText,
                        value: String,
                        action_ts: String
                      )

case class SlackText(
                      `type`: String,
                      text: String,
                      emoji: Option[Boolean] = None
                    )

case class SlackInteractivePayload(
                                    `type`: String,
                                    user: SlackUser,
                                    api_app_id: String,
                                    token: String,
                                    container: Map[String, String],
                                    trigger_id: String,
                                    team: SlackTeam,
                                    channel: SlackChannel,
                                    response_url: String,
                                    actions: List[SlackAction]
                                  )


case class SlackBlock(
                       `type`: String,
                       text: Option[SlackText] = None,
                       elements: Option[List[SlackElement]] = None,
                       accessory: Option[SlackElement] = None
                     )

case class SlackElement(
                         `type`: String,
                         action_id: Option[String] = None,
                         text: Option[SlackText] = None,
                         value: Option[String] = None,
                         style: Option[String] = None
                       )

case class SlackMessage(
                         channel: String,
                         text: String,
                         blocks: Option[List[SlackBlock]] = None
                       )

case class SlackResponse(
                          response_type: String = "in_channel",
                          text: String,
                          blocks: Option[List[SlackBlock]] = None
                        )


object SlackModels {
  implicit val slackTextDecoder: Decoder[SlackText] = deriveDecoder
  implicit val slackTextEncoder: Encoder[SlackText] = deriveEncoder

  implicit val slackUserDecoder: Decoder[SlackUser] = deriveDecoder
  implicit val slackUserEncoder: Encoder[SlackUser] = deriveEncoder

  implicit val slackTeamDecoder: Decoder[SlackTeam] = deriveDecoder
  implicit val slackTeamEncoder: Encoder[SlackTeam] = deriveEncoder

  implicit val slackChannelDecoder: Decoder[SlackChannel] = deriveDecoder
  implicit val slackChannelEncoder: Encoder[SlackChannel] = deriveEncoder

  implicit val slackActionDecoder: Decoder[SlackAction] = deriveDecoder
  implicit val slackActionEncoder: Encoder[SlackAction] = deriveEncoder

  implicit val slackInteractivePayloadDecoder: Decoder[SlackInteractivePayload] = deriveDecoder
  implicit val slackInteractivePayloadEncoder: Encoder[SlackInteractivePayload] = deriveEncoder

  implicit val slackElementDecoder: Decoder[SlackElement] = deriveDecoder
  implicit val slackElementEncoder: Encoder[SlackElement] = deriveEncoder

  implicit val slackBlockDecoder: Decoder[SlackBlock] = deriveDecoder
  implicit val slackBlockEncoder: Encoder[SlackBlock] = deriveEncoder

  implicit val slackMessageDecoder: Decoder[SlackMessage] = deriveDecoder
  implicit val slackMessageEncoder: Encoder[SlackMessage] = deriveEncoder

  implicit val slackResponseDecoder: Decoder[SlackResponse] = deriveDecoder
  implicit val slackResponseEncoder: Encoder[SlackResponse] = deriveEncoder
}
