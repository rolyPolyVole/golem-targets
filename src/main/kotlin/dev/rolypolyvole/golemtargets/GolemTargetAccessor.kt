package dev.rolypolyvole.golemtargets

import net.minecraft.world.SimpleContainer

interface GolemTargetAccessor {
    @Suppress("FunctionName")
    fun `golemTargets$getContainer`(): SimpleContainer
}
