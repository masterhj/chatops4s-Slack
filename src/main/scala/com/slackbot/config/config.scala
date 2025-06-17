package com.slackbot.config

import pureconfig._
case class SlackConfig(
                        botToken: String,
                        signingSecret: String
                      ) derives ConfigReader

case class ServerConfig(
                         host: String,
                         port: Int
                       ) derives ConfigReader

case class AppConfig(
                      server: ServerConfig,
                      slack: SlackConfig
                    ) derives ConfigReader

object Config {
  def load: AppConfig = {
    ConfigSource.default.loadOrThrow[AppConfig]
  }
}