package ar.edu.unq.eperdemic.services.runner

import ar.edu.unq.eperdemic.services.runner.SessionFactoryProvider.Companion.instance
import org.neo4j.driver.Driver
import org.neo4j.driver.Session


object TransactionNeo4j: Transaction {
    private var transaction : org.neo4j.driver.Transaction? =null
    private var session: Session? = null

    val currentSession: Session
        get() {
            if (session == null) {
                throw RuntimeException("No hay ninguna session en el contexto")
            }
            return session!!
        }


    override fun start() {
        session=DriverNeo4j().driver.session()
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


