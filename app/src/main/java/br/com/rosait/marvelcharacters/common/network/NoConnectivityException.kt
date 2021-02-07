package br.com.rosait.marvelcharacters.common.network

import java.io.IOException

class NoConnectivityException : IOException() {

    override val message: String
        get() = "Falha na conexão, verifique se está conectado à internet"
}