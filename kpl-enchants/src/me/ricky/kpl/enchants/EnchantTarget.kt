package me.ricky.kpl.enchants

import org.bukkit.Material
import org.bukkit.Material.*
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.inventory.ItemStack

data class EnchantTarget(val source: List<EnchantmentTarget>, val types: List<Material>) {
  constructor(source: EnchantmentTarget) : this(listOf(source), emptyList())
  constructor(types: List<Material>) : this(emptyList(), types)
  constructor(type: Material) : this(listOf(type))
  constructor(vararg types: Material) : this(types.toList())
  constructor(vararg targets: EnchantTarget) : this(targets.flatMap { it.source }, targets.flatMap { it.types })

  infix operator fun contains(type: Material): Boolean {
    return source.any { it.includes(type) } || if (this == none) true else type in types
  }

  infix operator fun contains(item: ItemStack?) = contains(item?.type ?: AIR)

  companion object {
    // Weapons & Tools
    val sword = EnchantTarget(EnchantmentTarget.WEAPON)
    val axe = EnchantTarget(WOODEN_AXE, STONE_AXE, IRON_AXE, GOLDEN_AXE, DIAMOND_AXE)
    val pickaxe = EnchantTarget(WOODEN_PICKAXE, STONE_PICKAXE, IRON_PICKAXE, GOLDEN_PICKAXE, DIAMOND_PICKAXE)
    val shovel = EnchantTarget(WOODEN_SHOVEL, STONE_SHOVEL, IRON_SHOVEL, GOLDEN_SHOVEL, DIAMOND_SHOVEL)
    val hoe = EnchantTarget(WOODEN_HOE, STONE_HOE, IRON_HOE, GOLDEN_HOE, DIAMOND_HOE)
    val weapon = EnchantTarget(sword, axe)
    val tool = EnchantTarget(EnchantmentTarget.TOOL)

    // Armor
    val helmet = EnchantTarget(EnchantmentTarget.ARMOR_HEAD)
    val chestplate = EnchantTarget(EnchantmentTarget.ARMOR_TORSO)
    val leggings = EnchantTarget(EnchantmentTarget.ARMOR_LEGS)
    val boots = EnchantTarget(EnchantmentTarget.ARMOR_FEET)
    val armor = EnchantTarget(EnchantmentTarget.ARMOR)

    // Other
    val bow = EnchantTarget(EnchantmentTarget.BOW)
    val crossbow = EnchantTarget(EnchantmentTarget.CROSSBOW)
    val fishingRod = EnchantTarget(EnchantmentTarget.FISHING_ROD)
    val trident = EnchantTarget(EnchantmentTarget.TRIDENT)
    val breakable = EnchantTarget(EnchantmentTarget.BREAKABLE)
    val wearable = EnchantTarget(EnchantmentTarget.WEARABLE)
    val all = EnchantTarget(EnchantmentTarget.ALL)
    val none = EnchantTarget(AIR)
  }
}