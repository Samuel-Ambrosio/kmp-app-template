package com.samuelav.data.sourcesImpl.local.di

import org.koin.core.module.Module
import org.koin.dsl.module

// Room and DataStore have no browser storage: local data sources on the web are in-memory implementations
internal actual val platformLocalModule: Module = module { }
