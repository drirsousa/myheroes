package br.com.rosait.marvelcharacters.common.model

import java.io.Serializable

data class ItemCharacters (
    var id: Long,
    var name: String,
    var description: String?,
    var thumbnail: ThumbnailItem
) : Serializable