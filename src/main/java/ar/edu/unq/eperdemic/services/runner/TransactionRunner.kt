package ar.edu.unq.eperdemic.services.runner


import org.hibernate.Session

object TransactionRunner {
    private var transactions:List<Transaction> = listOf(TransactionHibernate())

    fun <T> runTrx(bloque: ()->T): T {
        try{
           transactions.forEach { it.start() }
           val result = bloque()
           transactions.forEach { it.commit() }
           return result
        } catch (exception:Throwable){
           transactions.forEach { it.rollback() }
           throw exception
        }
    }

     /*
    fun <T> runTrx(bloque: ()->T): T {
        session = SessionFactoryProvider.instance.createSession()
        session.use {
            val tx =  session!!.beginTransaction()
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
        session = null
    }
    */

}