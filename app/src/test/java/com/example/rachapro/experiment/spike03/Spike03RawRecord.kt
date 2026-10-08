package com.example.rachapro.experiment.spike03

enum class Spike03FunctionalResult {
    PASS,
    FAIL
}

data class Spike03ObservationRecord(
    val runId: Int,
    val volume: Int,
    val observation: Int,
    val elapsedNs: Long,
    val current: Int,
    val best: Int,
    val functionalResult: Spike03FunctionalResult
)

data class Spike03ClockOverheadRecord(
    val runId: Int,
    val observation: Int,
    val clockOverheadNs: Long
)

data class Spike03RunMetadata(
    val runId: Int,
    val processId: Long,
    val jvmStartTimeEpochMs: Long,
    val runStartTimeEpochMs: Long,
    val javaVersion: String,
    val javaVendor: String,
    val osName: String,
    val osVersion: String,
    val referenceDate: String,
    val referenceEpochDay: Long,
    val warmupsPerVolume: Int,
    val observationsPerVolume: Int,
    val clockOverheadObservations: Int,
    val medianClockOverheadNs: Double,
    val functionalFailCount: Int,
    val runValid: Boolean
)
