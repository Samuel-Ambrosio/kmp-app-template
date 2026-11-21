package com.samuelav.data.sourcesImpl.local.di

import com.samuelav.data.sourcesImpl.local.BuildKonfig
import com.samuelav.data.sourcesImpl.local.createDataStore
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File

internal actual val platformLocalModule: Module = module {
    single { createDataStore { File(storageDirectory(), DATA_STORE_FILE_NAME).absolutePath } }
    // single { Room.databaseBuilder<AppRoomDatabase>(name = File(storageDirectory(), ROOM_DATABASE_FILE_NAME).absolutePath).buildAppRoomDatabase() }
}

private fun storageDirectory(): File =
    File(System.getProperty("user.home"), ".${BuildKonfig.APPLICATION_ID}").apply { mkdirs() }
