package me.ricky.kpl.core.command

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import org.bukkit.command.CommandSender
import java.util.concurrent.CompletableFuture

class DynamicArgument<S : CommandSender>(
  type: StringArgumentType = StringArgumentType.word(),
  private val creator: CommodoreContextCreator<S>,
  private val list: CommandContext<S>.() -> List<String>
) : ArgumentType<String> by type {

  @Suppress("UNCHECKED_CAST")
  override fun <S : Any> listSuggestions(
    context: CommandContext<S>,
    builder: SuggestionsBuilder
  ): CompletableFuture<Suggestions> {
    creator.handle(context as CommandContext<Any>) {
      list(this).forEach {
        builder.suggest(it)
      }
    }
    
    return builder.buildFuture()
  }
}