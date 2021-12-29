package at.petrak.hex.api

import at.petrak.hex.common.casting.CastException
import at.petrak.hex.common.casting.CastingContext
import at.petrak.hex.common.casting.SpellDatum
import net.minecraft.world.phys.Vec3

/**
 * Manipulates the stack in some way, usually by popping some number of values off the stack
 * and pushing one new value.
 * For a more "traditional" pop arguments, push return experience, see
 * [SimpleOperator][at.petrak.hex.common.casting.operators.SimpleOperator]
 *
 * Implementors MUST NOT mutate the context.
 */
interface SpellOperator {
    val manaCost: Int
        get() = 0

    fun modifyStack(stack: MutableList<SpellDatum<*>>, ctx: CastingContext)

    companion object {
        // I see why vzakii did this: you can't raycast out to infinity!
        const val MAX_DISTANCE: Double = 32.0

        @JvmStatic
        fun raycastEnd(origin: Vec3, look: Vec3): Vec3 =
            origin.add(look.normalize().scale(MAX_DISTANCE))

        /**
         * Try to get a value of the given type.
         */
        @JvmStatic
        inline fun <reified T : Any> List<SpellDatum<*>>.getChecked(idx: Int): T =
            this[idx].tryGet()

        /**
         * Check if the value at the given index is OK. Will throw an error otherwise.
         */
        @JvmStatic
        inline fun <reified T : Any> List<SpellDatum<*>>.assertChecked(idx: Int) {
            this.getChecked<T>(idx)
        }

        /**
         * Make sure the vector is in range of the player.
         */
        @JvmStatic
        fun assertVecInRange(vec: Vec3, ctx: CastingContext) {
            if (vec.distanceToSqr(ctx.caster.position()) > MAX_DISTANCE * MAX_DISTANCE)
                throw CastException(CastException.Reason.TOO_FAR, vec)
        }

        @JvmStatic
        fun spellListOf(vararg vs: Any): List<SpellDatum<*>> {
            val out = ArrayList<SpellDatum<*>>(vs.size)
            for (v in vs) {
                out.add(SpellDatum.make(v))
            }
            return out
        }

        @JvmStatic
        fun makeConstantOp(x: SpellDatum<*>): SpellOperator = object : SimpleOperator {
            override val argc: Int
                get() = 0

            override fun execute(args: List<SpellDatum<*>>, ctx: CastingContext): List<SpellDatum<*>> =
                spellListOf(x)
        }
    }
}