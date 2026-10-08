package com.example.rachapro.experiment.spike03

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

object Spike03CsvWriter {

    fun writeRun(
        outputDirectory: Path,
        metadata: Spike03RunMetadata,
        clockOverheadRecords: List<Spike03ClockOverheadRecord>,
        observationRecords: List<Spike03ObservationRecord>
    ) {
        require(
            !Files.exists(outputDirectory)
        ) {
            "El directorio de evidencia ya existe y no será sobrescrito: $outputDirectory"
        }

        Files.createDirectories(outputDirectory)

        writeMetadata(
            outputDirectory = outputDirectory,
            metadata = metadata
        )

        writeClockOverhead(
            outputDirectory = outputDirectory,
            records = clockOverheadRecords
        )

        writeObservations(
            outputDirectory = outputDirectory,
            records = observationRecords
        )
    }

    private fun writeMetadata(
        outputDirectory: Path,
        metadata: Spike03RunMetadata
    ) {
        val file =
            outputDirectory.resolve("metadata.csv")

        val lines = listOf(
            "runId,processId,jvmStartTimeEpochMs,runStartTimeEpochMs,javaVersion,javaVendor,osName,osVersion,referenceDate,referenceEpochDay,warmupsPerVolume,observationsPerVolume,clockOverheadObservations,medianClockOverheadNs,functionalFailCount,runValid",
            listOf(
                metadata.runId,
                metadata.processId,
                metadata.jvmStartTimeEpochMs,
                metadata.runStartTimeEpochMs,
                metadata.javaVersion,
                metadata.javaVendor,
                metadata.osName,
                metadata.osVersion,
                metadata.referenceDate,
                metadata.referenceEpochDay,
                metadata.warmupsPerVolume,
                metadata.observationsPerVolume,
                metadata.clockOverheadObservations,
                metadata.medianClockOverheadNs,
                metadata.functionalFailCount,
                metadata.runValid
            ).joinToString(",") {
                csvEscape(it.toString())
            }
        )

        writeLines(
            file = file,
            lines = lines
        )
    }

    private fun writeClockOverhead(
        outputDirectory: Path,
        records: List<Spike03ClockOverheadRecord>
    ) {
        val file =
            outputDirectory.resolve("clock-overhead.csv")

        val lines = mutableListOf(
            "runId,observation,clockOverheadNs"
        )

        records.forEach { record ->
            lines += listOf(
                record.runId,
                record.observation,
                record.clockOverheadNs
            ).joinToString(",") {
                csvEscape(it.toString())
            }
        }

        writeLines(
            file = file,
            lines = lines
        )
    }

    private fun writeObservations(
        outputDirectory: Path,
        records: List<Spike03ObservationRecord>
    ) {
        val file =
            outputDirectory.resolve("observations.csv")

        val lines = mutableListOf(
            "runId,volume,observation,elapsedNs,current,best,functionalResult"
        )

        records.forEach { record ->
            lines += listOf(
                record.runId,
                record.volume,
                record.observation,
                record.elapsedNs,
                record.current,
                record.best,
                record.functionalResult.name
            ).joinToString(",") {
                csvEscape(it.toString())
            }
        }

        writeLines(
            file = file,
            lines = lines
        )
    }

    private fun writeLines(
        file: Path,
        lines: List<String>
    ) {
        Files.write(
            file,
            lines,
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE_NEW,
            StandardOpenOption.WRITE
        )
    }

    private fun csvEscape(
        value: String
    ): String {
        val requiresQuotes =
            value.contains(",") ||
                value.contains("\"") ||
                value.contains("\n") ||
                value.contains("\r")

        if (!requiresQuotes) {
            return value
        }

        return "\"" +
            value.replace(
                "\"",
                "\"\""
            ) +
            "\""
    }
}
