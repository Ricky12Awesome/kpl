package me.ricky.kpl.core.command

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import org.bukkit.command.CommandSender
import java.util.concurrent.CompletableFuture

interface CustomArgumentType<T> {
  val type: ArgumentType<T>

  fun listSuggestions(
    context: CommandContext<Any>,
    builder: SuggestionsBuilder
  ): CompletableFuture<Suggestions>
}

class DynamicArgumentType<S : CommandSender>(
  private val creator: CommodoreContextCreator<S>,
  private val list: CommandContext<S>.() -> Iterable<String>,
  override val type: StringArgumentType = StringArgumentType.word()
) : CustomArgumentType<String> {
  override fun listSuggestions(
    context: CommandContext<Any>,
    builder: SuggestionsBuilder
  ): CompletableFuture<Suggestions> {
    creator.handle(context) {
      list(this).forEach {
        builder.suggest(it)
      }
    }

    return builder.buildFuture()
  }
}