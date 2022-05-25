@file:JvmName("HexUtils")
package at.petrak.hexcasting.api.utils

import at.petrak.hexcasting.api.spell.SpellDatum
import at.petrak.hexcasting.api.spell.math.HexCoord
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.LongArrayTag
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

const val TAU = Math.PI * 2.0
const val SQRT_3 = 1.7320508f

fun Vec3.serializeToNBT(): LongArrayTag =
    LongArrayTag(longArrayOf(this.x.toRawBits(), this.y.toRawBits(), this.z.toRawBits()))

fun vecFromNBT(tag: LongArray): Vec3 = if (tag.size != 3) Vec3.ZERO else
    Vec3(
        Double.fromBits(tag[0]),
        Double.fromBits(tag[1]),
        Double.fromBits(tag[2])
    )

fun Vec2.serializeToNBT(): LongArrayTag =
    LongArrayTag(longArrayOf(this.x.toDouble().toRawBits(), this.y.toDouble().toRawBits()))

fun vec2FromNBT(tag: LongArray): Vec2 = if (tag.size != 2) Vec2.ZERO else
    Vec2(
        Double.fromBits(tag[0]).toFloat(),
        Double.fromBits(tag[1]).toFloat(),
    )

fun otherHand(hand: InteractionHand) =
    if (hand == InteractionHand.MAIN_HAND) InteractionHand.OFF_HAND else InteractionHand.MAIN_HAND

fun fixNAN(x: Double): Double = if (x.isFinite()) x else 0.0

fun findCenter(points: List<Vec2>): Vec2 {
    var minX = Float.POSITIVE_INFINITY
    var minY = Float.POSITIVE_INFINITY
    var maxX = Float.NEGATIVE_INFINITY
    var maxY = Float.NEGATIVE_INFINITY

    for (pos in points) {
        minX = min(minX, pos.x)
        minY = min(minY, pos.y)
        maxX = max(maxX, pos.x)
        maxY = max(maxY, pos.y)
    }
    return Vec2(
        (minX + maxX) / 2f,
        (minY + maxY) / 2f
    )
}

fun coordToPx(coord: HexCoord, size: Float, offset: Vec2): Vec2 =
    Vec2(
        SQRT_3 * coord.q.toFloat() + SQRT_3 / 2.0f * coord.r.toFloat(),
        1.5f * coord.r.toFloat()
    ).scale(size).add(offset)

fun pxToCoord(px: Vec2, size: Float, offset: Vec2): HexCoord {
    val offsetted = px.add(offset.negated())
    var qf = (SQRT_3 / 3.0f * offsetted.x - 0.33333f * offsetted.y) / size
    var rf = (0.66666f * offsetted.y) / size

    val q = qf.roundToInt()
    val r = rf.roundToInt()
    qf -= q
    rf -= r
    return if (q.absoluteValue >= r.absoluteValue)
        HexCoord(q + (qf + 0.5f * rf).roundToInt(), r)
    else
        HexCoord(q, r + (rf + 0.5 * qf).roundToInt())
}

fun Iterable<SpellDatum<*>>.serializeToNBT(): ListTag {
    val tag = ListTag()
    for (elt in this)
        tag.add(elt.serializeToNBT())
    return tag
}

// Copy the impl from forge
fun ItemStack.serializeToNBT(): CompoundTag {
    val out = CompoundTag()
    this.save(out)
    return out
}
