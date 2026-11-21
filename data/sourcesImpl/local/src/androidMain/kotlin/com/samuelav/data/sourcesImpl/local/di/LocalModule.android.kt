package com.samuelav.data.sourcesImpl.local.di

import android.content.Context
import com.samuelav.data.sourcesImpl.local.createDataStore
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val platformLocalModule: Module = module {
    single { createDataStore { get<Context>().filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath } }
    /*single {
        Room.databaseBuilder<AppRoomDatabase>(
            context = get<Context>(),
            name = get<Context>().getDatabasePath(ROOM_DATABASE_FILE_NAME).absolutePath,
        ).buildAppRoomDatabase()
    }*/
}
