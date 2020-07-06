package me.ricky.kpl.core.command

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandPermission
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.executors.*
import me.ricky.kpl.core.KPlugin
import me.ricky.kpl.core.util.Manager
import org.bukkit.block.CommandBlock
import org.bukkit.command.BlockCommandSender
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.command.ProxiedCommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import kotlin.reflect.KProperty

typealias ContextCommandExecutor<S> = CommandContext<S>.() -> Unit

class Arguments(args: Map<String, Any?>) : Map<String, Any?> by args {
  override val values: List<Any?> = args.values.toList()

  inline infix fun <reified T> at(index: Int): T {
    return values[index] as T
  }

  inline operator fun <reified T> getValue(any: Any?, property: KProperty<*>): T {
    return this[property.name] as T
  }
}

fun Command.argumentsFrom(args: Array<out Any?>): Arguments {
  return Arguments(buildMap {
    arguments.keys.forEachIndexed { index, name ->
      this[name] = args[index]
    }
  })
}

inline fun Command.executor(
  crossinline executes: ContextCommandExecutor<CommandSender>
) = CommandExecutor { sender, args ->
  CommandContext(sender, argumentsFrom(args)).executes()
}

inline fun Command.playerExecutor(
  crossinline executes: ContextCommandExecutor<Player>
) = PlayerCommandExecutor { sender, args ->
  CommandContext(sender, argumentsFrom(args)).executes()
}

inline fun Command.consoleExecutor(
  crossinline executes: ContextCommandExecutor<ConsoleCommandSender>
) = ConsoleCommandExecutor { sender, args ->
  CommandContext(sender, argumentsFrom(args)).executes()
}

inline fun Command.entityExecutor(
  crossinline executes: ContextCommandExecutor<Entity>
) = EntityCommandExecutor { sender, args ->
  CommandContext(sender, argumentsFrom(args)).executes()
}

inline fun Command.proxyExecutor(
  crossinline executes: ContextCommandExecutor<ProxiedCommandSender>
) = ProxyCommandExecutor { sender, args ->
  CommandContext(sender, argumentsFrom(args)).executes()
}

inline fun Command.commandBlockExecutor(
  crossinline executes: ContextCommandExecutor<BlockCommandSender>
) = CommandBlockCommandExecutor { sender, args ->
  CommandContext(sender, argumentsFrom(args)).executes()
}

class CommandContext<S : CommandSender>(
  val sender: S,
  val arguments: Arguments
)

interface Command {
  val name: String
  val aliases: List<String>
  val arguments: Map<String, Argument>
  val permission: CommandPermission
  val executor: IExecutorNormal<*>
}

class CommandManager : Manager {
  val commands = mutableListOf<CommandAPICommand>()

  fun addCommand(command: Command) {
    commands += CommandAPICommand(command.name).apply {
      withArguments(LinkedHashMap(command.arguments))
      withAliases(*command.aliases.toTypedArray())
      withPermission(command.permission)

      when (val executor = command.executor) {
        is CommandExecutor -> executes(executor)
        is PlayerCommandExecutor -> executesPlayer(executor)
        is ConsoleCommandExecutor -> executesConsole(executor)
        is EntityCommandExecutor -> executesEntity(executor)
        is ProxyCommandExecutor -> executesProxy(executor)
        is CommandBlockCommandExecutor-> executesCommandBlock(executor)
        else -> error("Unsupported Executor, valid executors: [Player, Console, Entity, Proxy, CommandBlock]CommandExecutor")
      }
    }
  }

  override fun enable(source: KPlugin) {
    if (CommandAPI.canRegister()) {
      commands.forEach(CommandAPICommand::register)
    } else {
      source.logger.warning("Cannot register commands.")
    }
  }
}
