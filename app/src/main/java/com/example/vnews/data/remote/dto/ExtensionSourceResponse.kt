package com.example.vnews.data.remote.dto


import com.example.vnews.data.local.entity.ExtensionEntity
import kotlinx.serialization.Serializable


@Serializable
data class RssSource(
    val name: String,
    val icon: String,
    val source: String
)

fun RssSource.toExtensionEntity(): ExtensionEntity {
    return ExtensionEntity(
        name = this.name,
        icon = this.icon,
        source = this.source
    )
}
