package com.example.rachapro.experiment.spike03

import com.example.rachapro.domain.StreakCalculator
import com.example.rachapro.domain.StreakResult
import org.junit.Assert.assertFalse
import org.junit.Assume.assumeTrue
import org.junit.Test
import java.nio.file.Path
import java.nio.file.Paths

class Spike03HarnessTest {

    companion object {

        private const val WARMUPS_PER_VOLUME = 50
        private const val OBSERVATIONS_PER_VOLUME = 100
        private const val CLOCK_OVERHEAD_OBSERVATIONS = 10000

        private const val ENV_FORMAL = "SPIKE03_FORMAL"
        private const val ENV_RUN = "SPIKE03_RUN"
        private const val ENV_OUTPUT_DIR = "SPIKE03_OUTPUT_DIR"

        private val RUN_1_ORDER =
            listOf(
                30,
                365,
                1000,
                2500,
                5000,
                10000
            )

        private val RUN_2_ORDER =
            listOf(
                10000,
                5000,
                2500,
                1000,
                365,
                30
            )

        private val RUN_3_ORDER =
            RUN_1_ORDER
    }

    @Test
    fun executeSelectedFormalRun() {
        val formalMode =
            System.getenv(ENV_FORMAL)

        assumeTrue(
            "SPIKE-03 no está habilitado para ejecución formal.",
            formalMode.equals(
                "true",
                ignoreCase = true
            )
        )

        val runId =
            readRunId()

        val outputDirectory =
            readOutputDirectory()

        val volumeOrder =
            volumeOrderFor(
                runId = runId
            )

        val processId =
            readCurrentProcessId()

        val jvmStartTimeEpochMs =
            readJvmStartTimeEpochMs()

        val runStartTimeEpochMs =
            System.currentTimeMillis()

        /*
         * Los datasets se generan de forma determinista
         * antes de medir el overhead del reloj.
         *
         * Esto respeta el orden preregistrado:
         *
         * inicio JVM
         * -> generación determinista de datasets
         * -> medición de overhead
         * -> warm-up
         * -> mediciones formales
         */
        val datasets =
            volumeOrder.associateWith { volume ->
                Spike03DatasetFactory.create(
                    n = volume
                )
            }

        /*
         * La medición del overhead ocurre después
         * de generar completamente los datasets
         * y antes del warm-up.
         *
         * clockT0 y clockT1 son independientes
         * de T0 y T1 de la operación principal.
         */
        val clockOverheadRecords =
            measureClockOverhead(
                runId = runId
            )

        val medianClockOverheadNs =
            median(
                clockOverheadRecords.map {
                    it.clockOverheadNs
                }
            )

        /*
         * Warm-up.
         *
         * No participa en estadísticas formales.
         */
        volumeOrder.forEach { volume ->
            val dataset =
                datasets.getValue(volume)

            repeat(WARMUPS_PER_VOLUME) {
                measureCurrentRoute(
                    dataset = dataset
                )
            }
        }

        val observations =
            mutableListOf<Spike03ObservationRecord>()

        volumeOrder.forEach { volume ->
            val dataset =
                datasets.getValue(volume)

            repeat(OBSERVATIONS_PER_VOLUME) { zeroBasedObservation ->
                val observationNumber =
                    zeroBasedObservation + 1

                val measured =
                    measureCurrentRoute(
                        dataset = dataset
                    )

                /*
                 * La validación funcional ocurre
                 * después de T1.
                 */
                val functionalResult =
                    if (
                        measured.result.current == volume &&
                        measured.result.best == volume
                    ) {
                        Spike03FunctionalResult.PASS
                    } else {
                        Spike03FunctionalResult.FAIL
                    }

                /*
                 * El registro en memoria ocurre
                 * después de T1.
                 */
                observations +=
                    Spike03ObservationRecord(
                        runId = runId,
                        volume = volume,
                        observation = observationNumber,
                        elapsedNs = measured.elapsedNs,
                        current = measured.result.current,
                        best = measured.result.best,
                        functionalResult = functionalResult
                    )
            }
        }

        val functionalFailCount =
            observations.count {
                it.functionalResult ==
                        Spike03FunctionalResult.FAIL
            }

        val runValid =
            functionalFailCount == 0

        val metadata =
            Spike03RunMetadata(
                runId = runId,
                processId = processId,
                jvmStartTimeEpochMs = jvmStartTimeEpochMs,
                runStartTimeEpochMs = runStartTimeEpochMs,
                javaVersion =
                    requireNotNull(
                        System.getProperty("java.version")
                    ) {
                        "No fue posible obtener java.version."
                    },
                javaVendor =
                    requireNotNull(
                        System.getProperty("java.vendor")
                    ) {
                        "No fue posible obtener java.vendor."
                    },
                osName =
                    requireNotNull(
                        System.getProperty("os.name")
                    ) {
                        "No fue posible obtener os.name."
                    },
                osVersion =
                    requireNotNull(
                        System.getProperty("os.version")
                    ) {
                        "No fue posible obtener os.version."
                    },
                referenceDate =
                    Spike03DatasetFactory
                        .referenceDate
                        .toString(),
                referenceEpochDay =
                    Spike03DatasetFactory
                        .referenceEpochDay,
                warmupsPerVolume =
                    WARMUPS_PER_VOLUME,
                observationsPerVolume =
                    OBSERVATIONS_PER_VOLUME,
                clockOverheadObservations =
                    CLOCK_OVERHEAD_OBSERVATIONS,
                medianClockOverheadNs =
                    medianClockOverheadNs,
                functionalFailCount =
                    functionalFailCount,
                runValid =
                    runValid
            )

        /*
         * La escritura de evidencia ocurre
         * después de finalizar todas las
         * mediciones T0-T1.
         */
        Spike03CsvWriter.writeRun(
            outputDirectory = outputDirectory,
            metadata = metadata,
            clockOverheadRecords = clockOverheadRecords,
            observationRecords = observations
        )

        /*
         * Si existió cualquier FUNCTIONAL_FAIL,
         * la evidencia ya quedó preservada antes
         * de marcar la corrida como inválida.
         */
        assertFalse(
            "SPIKE-03 corrida $runId inválida: se detectaron $functionalFailCount FUNCTIONAL_FAIL.",
            functionalFailCount > 0
        )
    }

    private fun readCurrentProcessId(): Long {
        val processHandleClass =
            Class.forName(
                "java.lang.ProcessHandle"
            )

        val currentProcess =
            processHandleClass
                .getMethod("current")
                .invoke(null)

        val pidValue =
            processHandleClass
                .getMethod("pid")
                .invoke(currentProcess)

        require(pidValue is Long) {
            "No fue posible obtener el PID actual como Long."
        }

        return pidValue
    }

    private fun readJvmStartTimeEpochMs(): Long {
        val managementFactoryClass =
            Class.forName(
                "java.lang.management.ManagementFactory"
            )

        val runtimeMxBean =
            managementFactoryClass
                .getMethod("getRuntimeMXBean")
                .invoke(null)

        requireNotNull(runtimeMxBean) {
            "No fue posible obtener RuntimeMXBean."
        }

        val runtimeMxBeanClass =
            Class.forName(
                "java.lang.management.RuntimeMXBean"
            )

        val startTimeValue =
            runtimeMxBeanClass
                .getMethod("getStartTime")
                .invoke(runtimeMxBean)

        require(startTimeValue is Long) {
            "No fue posible obtener el inicio de la JVM como Long."
        }

        return startTimeValue
    }

    private fun readRunId(): Int {
        val raw =
            System.getenv(ENV_RUN)
                ?: error(
                    "Falta la variable de entorno $ENV_RUN."
                )

        val runId =
            raw.toIntOrNull()
                ?: error(
                    "$ENV_RUN debe ser 1, 2 o 3."
                )

        require(runId in 1..3) {
            "$ENV_RUN debe ser 1, 2 o 3. Valor recibido: $runId"
        }

        return runId
    }

    private fun readOutputDirectory(): Path {
        val raw =
            System.getenv(ENV_OUTPUT_DIR)
                ?: error(
                    "Falta la variable de entorno $ENV_OUTPUT_DIR."
                )

        require(raw.isNotBlank()) {
            "$ENV_OUTPUT_DIR no puede estar vacío."
        }

        return Paths.get(raw)
            .toAbsolutePath()
            .normalize()
    }

    private fun volumeOrderFor(
        runId: Int
    ): List<Int> {
        return when (runId) {
            1 -> RUN_1_ORDER
            2 -> RUN_2_ORDER
            3 -> RUN_3_ORDER

            else -> error(
                "Corrida no soportada: $runId"
            )
        }
    }

    private fun measureClockOverhead(
        runId: Int
    ): List<Spike03ClockOverheadRecord> {
        val records =
            ArrayList<Spike03ClockOverheadRecord>(
                CLOCK_OVERHEAD_OBSERVATIONS
            )

        repeat(CLOCK_OVERHEAD_OBSERVATIONS) { index ->
            val clockT0 =
                System.nanoTime()

            val clockT1 =
                System.nanoTime()

            val clockOverheadNs =
                clockT1 - clockT0

            records +=
                Spike03ClockOverheadRecord(
                    runId = runId,
                    observation = index + 1,
                    clockOverheadNs = clockOverheadNs
                )
        }

        return records
    }

    /**
     * Única frontera temporal de la operación
     * experimental principal.
     *
     * Dentro de T0-T1 se ejecuta exclusivamente:
     *
     * activityDays + pomodoroDays
     * -> distinct()
     * -> sorted()
     * -> StreakCalculator.calculate(...)
     * -> StreakResult
     */
    private fun measureCurrentRoute(
        dataset: Spike03Dataset
    ): Spike03MeasuredResult {

        /*
         * T0.
         *
         * Las listas ya existen completamente
         * antes de este punto.
         */
        val t0 =
            System.nanoTime()

        val validDays =
            (dataset.activityDays + dataset.pomodoroDays)
                .distinct()
                .sorted()

        val result =
            StreakCalculator.calculate(
                completedDays = validDays,
                todayEpochDay = dataset.referenceEpochDay
            )

        /*
         * T1.
         *
         * Se captura inmediatamente después
         * del retorno de calculate(...).
         */
        val t1 =
            System.nanoTime()

        return Spike03MeasuredResult(
            elapsedNs = t1 - t0,
            result = result
        )
    }

    private fun median(
        values: List<Long>
    ): Double {
        require(values.isNotEmpty()) {
            "No se puede calcular la mediana de una colección vacía."
        }

        val sorted =
            values.sorted()

        val middle =
            sorted.size / 2

        return if (
            sorted.size % 2 == 0
        ) {
            (
                    sorted[middle - 1].toDouble() +
                            sorted[middle].toDouble()
                    ) / 2.0
        } else {
            sorted[middle].toDouble()
        }
    }

    private data class Spike03MeasuredResult(
        val elapsedNs: Long,
        val result: StreakResult
    )
}