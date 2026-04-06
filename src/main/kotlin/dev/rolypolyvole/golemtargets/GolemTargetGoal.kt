package dev.rolypolyvole.golemtargets

import net.minecraft.core.component.DataComponents
import net.minecraft.world.entity.ai.goal.target.TargetGoal
import net.minecraft.world.entity.animal.golem.AbstractGolem
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Items

class GolemTargetGoal(private val golem: AbstractGolem) : TargetGoal(golem, false) {

    override fun canUse(): Boolean {
        val container = (golem as GolemTargetAccessor).`golemTargets$getContainer`()
        val targetNames = mutableSetOf<String>()

        for (i in 0 until container.containerSize) {
            val stack = container.getItem(i)
            if (!stack.isEmpty && stack.`is`(Items.PAPER) && stack.has(DataComponents.CUSTOM_NAME)) {
                targetNames.add(stack.get(DataComponents.CUSTOM_NAME)!!.string)
            }
        }

        if (targetNames.isEmpty()) return false

        targetMob = golem.level().players()
            .filter { it.name.string in targetNames && !it.isSpectator && !it.isCreative }
            .minByOrNull { it.distanceToSqr(golem) }

        return targetMob != null
    }

    override fun canContinueToUse(): Boolean {
        val target = targetMob ?: return false
        if (!target.isAlive) return false
        if (target is Player && (target.isSpectator || target.isCreative)) return false

        val container = (golem as GolemTargetAccessor).`golemTargets$getContainer`()
        val targetName = target.name.string
        for (i in 0 until container.containerSize) {
            val stack = container.getItem(i)
            if (!stack.isEmpty && stack.`is`(Items.PAPER) && stack.has(DataComponents.CUSTOM_NAME)) {
                if (stack.get(DataComponents.CUSTOM_NAME)!!.string == targetName) {
                    return true
                }
            }
        }
        return false
    }

    override fun start() {
        mob.target = targetMob
    }

    override fun tick() {
        mob.target = targetMob
    }

    override fun stop() {
        targetMob = null
        mob.target = null
    }
}
