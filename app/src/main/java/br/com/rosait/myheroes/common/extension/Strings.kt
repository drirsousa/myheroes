package br.com.rosait.myheroes.common.extension

fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }