package com.samuelav.data.sourcesImpl.local

/*import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.samuelav.data.sourcesImpl.local.converters.Converters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(
    entities = [],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
@ConstructedBy(AppRoomDatabaseConstructor::class)
internal abstract class AppRoomDatabase : RoomDatabase() {
    //abstract fun getDao(): Dao
}

// Room generates the actual implementations for every target with KSP
@Suppress("KotlinNoActualForExpect")
internal expect object AppRoomDatabaseConstructor : RoomDatabaseConstructor<AppRoomDatabase> {
    override fun initialize(): AppRoomDatabase
}

internal fun RoomDatabase.Builder<AppRoomDatabase>.buildAppRoomDatabase(): AppRoomDatabase =
    setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()*/
