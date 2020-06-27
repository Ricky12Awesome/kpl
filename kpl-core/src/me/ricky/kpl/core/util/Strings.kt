package me.ricky.kpl.core.util

import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

class ErrorMessage(val message: String) {
  inline val colored inline get() = message.colorize()

  fun sendTo(target: CommandSender) {
    target.sendColoredMessage(message)
  }
}

fun color(text: String): String = ChatColor.translateAlternateColorCodes('&', text)

fun String.colorize(): String = color(this)

fun color(texts: List<String>): List<String> = texts.map(String::colorize)

fun List<String>.colorize(): List<String> = color(this)

fun color(vararg texts: String): Array<out String> = texts.map(String::colorize).toTypedArray()

fun Array<out String>.colorize(): Array<out String> = color(*this)

fun CommandSender.sendColoredMessage(vararg message: String) {
  sendMessage(color(*message))
}

fun CommandSender.sendColoredMessage(message: String) {
  sendMessage(color(message))
}