package com.example.rachapro.domain

data class StreakResult(
    val current: Int,
    val best: Int
)

object StreakCalculator {

    fun calculate(
        completedDays: List<Long>,
        todayEpochDay: Long
    ): StreakResult {

        val validDays =
            completedDays
                .filter { day ->
                    day <= todayEpochDay
                }
                .distinct()
                .sorted()

        if (validDays.isEmpty()) {
            return StreakResult(
                current = 0,
                best = 0
            )
        }

        var bestStreak = 1
        var runningStreak = 1

        for (index in 1 until validDays.size) {

            if (
                validDays[index] ==
                validDays[index - 1] + 1
            ) {
                runningStreak++
            } else {
                runningStreak = 1
            }

            bestStreak = maxOf(bestStreak, runningStreak)
        }

        val validDaysSet = validDays.toSet()

        val streakEndDay =
            when {
                todayEpochDay in validDaysSet ->
                    todayEpochDay

                todayEpochDay - 1 in validDaysSet ->
                    todayEpochDay - 1

                else ->
                    return StreakResult(
                        current = 0,
                        best = bestStreak
                    )
            }

        var currentStreak = 0
        var dayToCheck = streakEndDay

        while (dayToCheck in validDaysSet) {
            currentStreak++
            dayToCheck--
        }

        return StreakResult(
            current = currentStreak,
            best = bestStreak
        )
    }
}
