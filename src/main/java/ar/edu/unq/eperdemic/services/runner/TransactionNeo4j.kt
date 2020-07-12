package ar.edu.unq.eperdemic.services.runner

import org.neo4j.driver.Session

object TransactionNeo4j: Transaction {
    private var transaction : org.neo4j.driver.Transaction? =null
    private var session: Session? = null

    val currentTransaction: org.neo4j.driver.Transaction
        get() {
            if(transaction == null) {
                throw RuntimeException("No hay ninguna transaction en el contexto")
            }
            return transaction!!
        }

    override fun hasSession(): Boolean {
        return session != null
    }

    override fun start() {
        session=Neo4jSessionFactoryProvider.instance.createSession()
        transaction= session!!.beginTransaction()
    }

    override fun commit() {
        transaction?.commit()
        session?.close()
        this.nullVar()
    }

    override fun rollback() {
        transaction?.rollback()
        session?.close()
        this.nullVar()
    }

    private fun nullVar(){
        session = null
        transaction = null
    }
}


