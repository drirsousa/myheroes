package br.com.rosait.marvelcharacters.common.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "item")
class ItemEntity(@PrimaryKey(autoGenerate = false) var id : Long,
                 @ColumnInfo(name = "name") var name : String,
                 @ColumnInfo(name = "description") var description : String,
                 @ColumnInfo(name = "thumbnail") var thumbnail : String
) : Serializable

{

    constructor() : this(0, "", "", "")

}