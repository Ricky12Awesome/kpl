package me.ricky.kpl.core.command

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import org.bukkit.command.CommandSender

class CommandDSL<S : CommandSender>(
  private val name: String,
  private val creator: CommodoreContextCreator<S>
) {
  private val builder = LiteralArgumentBuilder.literal<Any>(name)
  private var executor: CommandContext<S>.() -> Unit = {}
  val arguments = mutableListOf<ArgumentBuilder<Any, *>>()

  fun <T> argument(name: String, type: ArgumentType<T>) {
    arguments += RequiredArgumentBuilder.argument<Any, T>(name, type)
  }

  fun dynamicArgument(name: String) {

  }

  fun <T> onArgument(name: String, type: ArgumentType<T>, executor: CommandContext<S>.() -> Unit) {
    arguments += RequiredArgumentBuilder
      .argument<Any, T>(name, type)
      .executes(creator create executor)
  }

  fun onCommand(name: String, dsl: CommandDSL<S>.() -> Unit) {
    arguments += CommandDSL(name, creator).apply(dsl).build()
  }

  fun executes(executor: CommandContext<S>.() -> Unit) {
    this.executor = executor
  }

  fun build(): LiteralArgumentBuilder<Any> {
    with(arguments.lastOrNull() ?: builder) {
      executes(creator create executor)
    }

    for (i in arguments.lastIndex - 1 downTo 0) {
      arguments[i].then(arguments[i + 1])
    }

    arguments.firstOrNull()?.let {
      builder.then(it)
    }

    return builder
  }
}

inline fun <S : CommandSender> CommandManager.command(
  name: String,
  creator: ContextCreator<S>,
  dsl: CommandDSL<S>.() -> Unit
) {
  val command = CommandDSL(name, create(creator)).apply(dsl)

  register(command.build().build())
}