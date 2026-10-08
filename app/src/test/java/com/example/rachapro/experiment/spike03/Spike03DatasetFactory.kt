package com.example.rachapro.experiment.spike03

import java.time.LocalDate

data class Spike03Dataset(
    val n: Int,
    val referenceEpochDay: Long,
    val activityDays: List<Long>,
    val pomodoroDays: List<Long>
)

object Spike03DatasetFactory {

    val volumes: List<Int> = listOf(
        30,
        365,
        1000,
        2500,
        5000,
        10000
    )

    val referenceDate: LocalDate =
        LocalDate.of(2026, 10, 7)

    val referenceEpochDay: Long =
        referenceDate.toEpochDay()

    fun create(n: Int): Spike03Dataset {
        require(n in volumes) {
            "Volumen no preregistrado para SPIKE-03: $n"
        }

        val activityDays = mutableListOf<Long>()
        val pomodoroDays = mutableListOf<Long>()

        val firstEpochDay =
            referenceEpochDay - (n - 1L)

        repeat(n) { index ->
            val epochDay =
                firstEpochDay + index

            when (index % 5) {
                0 -> {
                    activityDays += epochDay
                    pomodoroDays += epochDay
                }

                1,
                2 -> {
                    activityDays += epochDay
                }

                3,
                4 -> {
                    pomodoroDays += epochDay
                }
            }
        }

        validateDataset(
            n = n,
            activityDays = activityDays,
            pomodoroDays = pomodoroDays
        )

        return Spike03Dataset(
            n = n,
            referenceEpochDay = referenceEpochDay,
            activityDays = activityDays.toList(),
            pomodoroDays = pomodoroDays.toList()
        )
    }

    private fun validateDataset(
        n: Int,
        activityDays: List<Long>,
        pomodoroDays: List<Long>
    ) {
        require(activityDays == activityDays.sorted()) {
            "activityDays no está en orden ascendente para N=$n"
        }

        require(pomodoroDays == pomodoroDays.sorted()) {
            "pomodoroDays no está en orden ascendente para N=$n"
        }

        val union =
            (activityDays + pomodoroDays)
                .distinct()
                .sorted()

        require(union.size == n) {
            "La unión lógica debe contener exactamente $n días distintos, pero contiene ${union.size}"
        }

        val expectedFirst =
            referenceEpochDay - (n - 1L)

        require(union.first() == expectedFirst) {
            "El primer día no coincide con el esperado para N=$n"
        }

        require(union.last() == referenceEpochDay) {
            "El último día debe coincidir con referenceEpochDay"
        }

        for (index in 1 until union.size) {
            require(
                union[index] ==
                    union[index - 1] + 1
            ) {
                "Los días deben ser consecutivos para N=$n"
            }
        }
    }
}
