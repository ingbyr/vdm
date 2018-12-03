package com.ingbyr.vdm.dao

import com.ingbyr.vdm.engines.utils.EngineType
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.transactions.transaction

object EngineInfoTable : IntIdTable() {
    val name = varchar("name", 255)
    val execPath = varchar("execPath", 255)
    var localVersion = varchar("localVersion", 255)
    val remoteVersionUrl = varchar("remoteVersionUrl", 255)
}

class EngineInfo(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EngineInfo>(EngineInfoTable)

    var name by EngineInfoTable.name
    var execPath by EngineInfoTable.execPath
    var localVersion by EngineInfoTable.localVersion
    var remoteVersionUrl by EngineInfoTable.remoteVersionUrl
}

fun searchEngineInfo(engineType: EngineType): EngineInfo {
    return when (engineType) {
        EngineType.ANNIE ->{
            transaction {
                EngineInfo.find { EngineInfoTable.name eq "annie" }.first()
            }
        }
        EngineType.YOUTUBE_DL -> {
            transaction {
                EngineInfo.find { EngineInfoTable.name eq "youtube-dl" }.first()
            }
        }
        else -> {
            throw NoSuchElementException("not found info of $engineType")
        }
    }
}
