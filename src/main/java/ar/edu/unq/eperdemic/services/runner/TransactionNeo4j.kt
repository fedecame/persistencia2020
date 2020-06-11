package ar.edu.unq.eperdemic.services.runner

import ar.edu.unq.eperdemic.services.runner.SessionFactoryProvider.Companion.instance
import org.neo4j.driver.Driver
import org.neo4j.driver.Session


class TransactionNeo4j {

    var transaction : org.neo4j.driver.Transaction? =null
    var driverNeo4j=DriverNeo4j()



    companion object{
        var session: Session? =null
        val currentSesion : Session
            get(){
                if(session==null){
                    throw RuntimeException("No hay ninguna session en el contexto")
                }

                return session!!
            }

    }


     fun start() {
        session=driverNeo4j.driver.session()
        transaction= session!!.beginTransaction()

    }

     fun commit() {
        transaction?.commit()
        session?.close()


    }

     fun rollback() {
        transaction?.rollback()
        session?.close()
    }


}

