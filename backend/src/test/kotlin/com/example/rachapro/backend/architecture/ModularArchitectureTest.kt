package com.example.rachapro.backend.architecture

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices
import kotlin.test.Test

class ModularArchitectureTest {

    private val base = "com.example.rachapro.backend"

    private val classes =
        ClassFileImporter()
            .withImportOption(
                ImportOption.Predefined.DO_NOT_INCLUDE_TESTS
            )
            .importPackages(base)

    @Test
    fun `activities internals are isolated`() {
        noClasses()
            .that()
            .resideOutsideOfPackage("$base.activities..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(
                "$base.activities.activity..",
                "$base.activities.category..",
                "$base.activities.subtask.."
            )
            .check(classes)
    }

    @Test
    fun `identity internals are isolated`() {
        noClasses()
            .that()
            .resideOutsideOfPackage("$base.identity..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(
                "$base.identity.auth..",
                "$base.identity.user..",
                "$base.identity.security.."
            )
            .check(classes)
    }

    @Test
    fun `focus internals are isolated`() {
        noClasses()
            .that()
            .resideOutsideOfPackage("$base.focus..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(
                "$base.focus.pomodoro.."
            )
            .check(classes)
    }

    @Test
    fun `progress internals are isolated`() {
        noClasses()
            .that()
            .resideOutsideOfPackage("$base.progress..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(
                "$base.progress.achievement.."
            )
            .check(classes)
    }

    @Test
    fun `reminders internals are isolated`() {
        noClasses()
            .that()
            .resideOutsideOfPackage("$base.reminders..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(
                "$base.reminders.reminder.."
            )
            .check(classes)
    }

    @Test
    fun `shared does not depend on business modules`() {
        noClasses()
            .that()
            .resideInAPackage("$base.shared..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(
                "$base.activities..",
                "$base.identity..",
                "$base.focus..",
                "$base.progress..",
                "$base.reminders.."
            )
            .check(classes)
    }

    @Test
    fun `shared contains no business repositories`() {
        noClasses()
            .that()
            .resideInAPackage("$base.shared..")
            .should()
            .haveSimpleNameEndingWith("Repository")
            .check(classes)
    }

    @Test
    fun `shared contains no business entities`() {
        noClasses()
            .that()
            .resideInAPackage("$base.shared..")
            .should()
            .haveSimpleNameEndingWith("Entity")
            .check(classes)
    }

    @Test
    fun `modules are free of dependency cycles`() {
        slices()
            .matching("$base.(*)..")
            .should()
            .beFreeOfCycles()
            .check(classes)
    }
}