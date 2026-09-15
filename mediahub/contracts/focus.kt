package com.mediahub.contracts

data class MediaHubFocusSpec(
    val scale: Float = 1.04f,
    val ringWidthDp: Float = 2f,
    val animationMillis: Int = 120
) {
    init {
        require(scale in 1.0f..1.10f)
        require(ringWidthDp in 1f..6f)
        require(animationMillis in 60..250)
    }
}
