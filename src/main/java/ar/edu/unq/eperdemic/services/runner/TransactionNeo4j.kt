package ar.edu.unq.eperdemic.services.runner

import ar.edu.unq.eperdemic.services.runner.SessionFactoryProvider.Companion.instance
import org.neo4j.driver.Driver
import org.neo4j.driver.Session


object TransactionNeo4j {

    var transaction : org.neo4j.driver.Transaction? =null
    var driverNeo4j=DriverNeo4j()
    private var session: Session? = null


    val currentSession: Session
        get() {
            if (TransactionNeo4j.session == null) {
                throw RuntimeException("No hay ninguna session en el contexto")
            }
            return TransactionNeo4j.session!!
        }


    fun start() {

        TransactionNeo4j.session=DriverNeo4j().driver.session()
        transaction= TransactionNeo4j.session!!.beginTransaction()

    }

     fun commit() {
        transaction?.commit()


    }

     fun rollback() {
        transaction?.rollback()
    }

    fun <T> runTrx(bloque: ()->T): T {
        TransactionNeo4j.start()
        TransactionNeo4j.session.use {
            val tx =  TransactionNeo4j.session!!.beginTransaction()

            try {
                //codigo de negocio
                val resultado = bloque()
                tx!!.commit()
                return resultado
            } catch (e: RuntimeException) {
                tx.rollback()
                throw e
            }
        }
        TransactionNeo4j.session = null
    }

    }


