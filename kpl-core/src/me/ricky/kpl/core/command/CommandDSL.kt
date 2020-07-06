package me.ricky.kpl.core.command

import dev.jorel.commandapi.CommandPermission
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.executors.CommandExecutor
import dev.jorel.commandapi.executors.IExecutorNormal
import org.bukkit.command.BlockCommandSender
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.command.ProxiedCommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

class CommandDSL(override val name: String) : Command {
  inline val asCommand inline get() = this as Command

  override val aliases: MutableList<String> = mutableListOf()
  override val arguments: MutableMap<String, Argument> = mutableMapOf()
  override var permission: CommandPermission = CommandPermission.NONE
  override var executor: IExecutorNormal<*> = asCommand.executor {
    sender.sendMessage("No Executor Specified")
  }

  inline fun executor(crossinline executes: ContextCommandExecutor<CommandSender>) {
    executor = asCommand.executor(executes)
  }

  inline fun playerExecutor(crossinline executes: ContextCommandExecutor<Player>) {
    executor = asCommand.playerExecutor(executes)
  }

  inline fun consoleExecutor(crossinline executes: ContextCommandExecutor<ConsoleCommandSender>) {
    executor = asCommand.consoleExecutor(executes)
  }

  inline fun entityExecutor(crossinline executes: ContextCommandExecutor<Entity>) {
    executor = asCommand.entityExecutor(executes)
  }

  inline fun proxyExecutor(crossinline executes: ContextCommandExecutor<ProxiedCommandSender>) {
    executor = asCommand.proxyExecutor(executes)
  }

  inline fun commandBlockExecutor(crossinline executes: ContextCommandExecutor<BlockCommandSender>) {
    executor = asCommand.commandBlockExecutor(executes)
  }

}

inline fun command(name: String, dsl: CommandDSL.() -> Unit): Command {
  return CommandDSL(name).apply(dsl)
}

inline fun CommandManager.command(name: String, dsl: CommandDSL.() -> Unit) {
  addCommand(CommandDSL(name).apply(dsl))
}