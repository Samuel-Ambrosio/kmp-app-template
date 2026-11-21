package com.samuelav.data.sourcesImpl.local.di

import org.koin.core.module.Module
import org.koin.dsl.module

public val localModule: Module = module {
    includes(platformLocalModule)
}

internal expect val platformLocalModule: Module

internal const val DATA_STORE_FILE_NAME = "data-store.preferences_pb"
internal const val ROOM_DATABASE_FILE_NAME = "RoomDatabase.db"
