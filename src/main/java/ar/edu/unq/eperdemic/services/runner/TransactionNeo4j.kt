package ar.edu.unq.eperdemic.services.runner

import org.neo4j.driver.Session


class TransactionNeo4j: Transaction {

    var session = ThreadLocal<org.neo4j.driver.Session>()

    companion object {
        private val CONTEXTO = ThreadLocal<org.neo4j.driver.Transaction>()


        val currentTransaction: org.neo4j.driver.Transaction
            get() {
                return CONTEXTO.get() ?: throw RuntimeException("No hay ninguna session en el contexto")

            }
    }

    override fun start() {
        if (noHaySessionAbierta()) {
            session.set(Neo4jSessionFactoryProvider.instance.createSession())
            val transaction = session.get().beginTransaction()
            CONTEXTO.set(transaction)
        }

    }

    private fun noHaySessionAbierta() = CONTEXTO.get() == null


    override fun commit() {
        CONTEXTO.get().commit()
        close()
    }

    override fun rollback() {
        CONTEXTO.get()?.rollback()
        close()
    }

    fun close() {
        CONTEXTO.get()?.close()
        session.get()?.close()
        session.set(null)
        CONTEXTO.set(null)
    }
}


