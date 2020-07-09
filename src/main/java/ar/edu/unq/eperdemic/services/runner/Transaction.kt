package ar.edu.unq.eperdemic.services.runner

interface Transaction {
    fun hasSession(): Boolean
    fun start()
    fun commit()
    fun rollback()
}