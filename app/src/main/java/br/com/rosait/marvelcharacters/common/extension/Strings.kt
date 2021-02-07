package br.com.rosait.marvelcharacters.common.extension

fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }