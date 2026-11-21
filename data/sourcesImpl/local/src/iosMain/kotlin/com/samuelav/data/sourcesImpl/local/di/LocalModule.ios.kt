package com.samuelav.data.sourcesImpl.local.di

import com.samuelav.data.sourcesImpl.local.createDataStore
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

internal actual val platformLocalModule: Module = module {
    single { createDataStore { "${documentDirectory()}/$DATA_STORE_FILE_NAME" } }
    // single { Room.databaseBuilder<AppRoomDatabase>(name = "${documentDirectory()}/$ROOM_DATABASE_FILE_NAME").buildAppRoomDatabase() }
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String =
    requireNotNull(
        NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = true,
            error = null,
        )?.path
    )
