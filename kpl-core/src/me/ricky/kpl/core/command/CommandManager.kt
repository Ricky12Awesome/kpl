package me.ricky.kpl.core.command

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.mojang.brigadier.Command
import com.mojang.brigadier.LiteralMessage
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import me.lucko.commodore.Commodore
import me.lucko.commodore.CommodoreProvider
import me.ricky.kpl.core.util.ErrorMessage
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import kotlin.reflect.KClass
import kotlin.reflect.full.safeCast

typealias CommandResponse<S> = Either<CommandContext<S>, ErrorMessage>

typealias MinecraftCommandNode<S> = com.mojang.brigadier.tree.CommandNode<S>
typealias MinecraftCommand<S> = com.mojang.brigadier.Command<S>

object PlayerContextCreator : ContextCreator<Player> by genericContextCreator()
object ConsoleContextCreator : ContextCreator<ConsoleCommandSender> by genericContextCreator()
object CommonContextCreator : ContextCreator<CommandSender> by genericContextCreator()

inline fun <reified C : CommandContext<S>, reified S : CommandSender> genericContextCreator(): GenericContextCreator<C, S> {
  return GenericContextCreator(S::class, C::class)
}

interface ContextCreator<S : CommandSender> {
  fun create(commodore: Commodore, context: CommandContext<Any>): CommandResponse<S>
}

class GenericContextCreator<C : CommandContext<S>, S : CommandSender>(
  private val senderClass: KClass<S>,
  private val contextClass: KClass<C>
) : ContextCreator<S> {
  override fun create(commodore: Commodore, context: CommandContext<Any>): CommandResponse<S> {
    val bukkitSender = commodore.getBukkitSender(context.source)
    val sender = senderClass.safeCast(bukkitSender)
      ?: return ErrorMessage("&e${bukkitSender::class.simpleName}&c is not typeof &e${senderClass.simpleName}").right()

    val contextWithSender = context.copyFor(sender)

    return contextClass.safeCast(contextWithSender)?.left()
      ?: ErrorMessage("&e${context::class.simpleName}&c is not typeof &e${contextClass.simpleName}").right()
  }
}

data class CommodoreContextCreator<S : CommandSender>(
  private val commodore: Commodore,
  private val source: ContextCreator<S>
) {
  infix fun senderOf(context: CommandContext<Any>) = commodore.getBukkitSender(context)
    ?: error("Couldn't get sender from context")

  infix fun create(context: CommandContext<Any>): CommandResponse<S> {
    return source.create(commodore, context)
  }

  inline fun handle(context: CommandContext<Any>, execute: CommandContext<S>.() -> Unit) {
    when (val response = create(context)) {
      is Either.Left -> execute(response.a)
      is Either.Right -> throw SimpleCommandExceptionType(LiteralMessage(response.b.colored)).create()
    }
  }

  infix fun create(execute: CommandContext<S>.() -> Unit): Command<Any> = Command {
    when (val response = create(it)) {
      is Either.Left -> execute(response.a)
      is Either.Right -> response.b.sendTo(senderOf(it))
    }

    0
  }
}

class CommandManager(private val plugin: Plugin) {
  private lateinit var commodore: Commodore

  fun <S : CommandSender> create(creator: ContextCreator<S>) = CommodoreContextCreator(commodore, creator)

  fun enable() {
    if (!CommodoreProvider.isSupported()) {
      plugin.logger.severe("Server must be 1.13 or higher to use this plugin.")
      return plugin.pluginLoader.disablePlugin(plugin)
    }

    commodore = CommodoreProvider.getCommodore(plugin)
  }

  fun register(command: MinecraftCommandNode<Any>) {
    commodore.dispatcher.root.addChild(command)
  }

}
