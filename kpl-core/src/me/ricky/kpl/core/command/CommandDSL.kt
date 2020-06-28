package me.ricky.kpl.core.command

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
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

  fun <T> argument(name: String, type: CustomArgumentType<T>) {
    arguments += RequiredArgumentBuilder
      .argument<Any, T>(name, type.type)
      .suggests { ctx, builder ->
        type.listSuggestions(ctx, builder)
      }
  }

  fun dynamicArgument(
    name: String,
    type: StringArgumentType = StringArgumentType.word(),
    list: CommandContext<S>.() -> Iterable<String>
  ) {
    argument(name, DynamicArgumentType(creator, list, type))
  }

  fun onDynamicArgument(
    name: String,
    type: StringArgumentType = StringArgumentType.word(),
    list: CommandContext<S>.() -> Iterable<String>,
    executor: CommandContext<S>.() -> Unit
  ) {
    onArgument(name, DynamicArgumentType(creator, list, type), executor)
  }

  fun <T> onArgument(name: String, type: ArgumentType<T>, executor: CommandContext<S>.() -> Unit) {
    arguments += RequiredArgumentBuilder
      .argument<Any, T>(name, type)
      .executes(creator create executor)
  }

  fun <T> onArgument(name: String, type: CustomArgumentType<T>, executor: CommandContext<S>.() -> Unit) {
    arguments += RequiredArgumentBuilder
      .argument<Any, T>(name, type.type)
      .suggests { ctx, builder ->
        type.listSuggestions(ctx, builder)
      }
      .executes(creator create executor)
  }

  fun onCommand(name: String, dsl: CommandDSL<S>.() -> Unit) {
    arguments += CommandDSL(name, creator).apply(dsl).build()
  }

  fun executes(executor: CommandContext<S>.() -> Unit) {
    this.executor = executor
  }

  fun build(): LiteralArgumentBuilder<Any> {
    val required = arguments.filter { it is RequiredArgumentBuilder<*, *> }
    val literal = arguments.filter { it is LiteralArgumentBuilder<*> }

    with(required.lastOrNull() ?: builder) {
      executes(creator create executor)
    }

    for (i in required.lastIndex - 1 downTo 0) {
      required[i].then(required[i + 1])
    }

    required.firstOrNull()?.let {
      builder.then(it)
    }

    literal.forEach {
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