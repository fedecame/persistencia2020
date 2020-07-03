package ar.edu.unq.eperdemic.services.runner

import org.hibernate.Session

class TransactionHibernate: Transaction{


    private var transaction = ThreadLocal<org.hibernate.Transaction>()

    companion object {
        private val CONTEXTO = ThreadLocal<Session>()


        val currentSession: Session
            get() {
                return CONTEXTO.get() ?: throw RuntimeException("No hay ninguna session en el contexto")
            }
    }

    override fun start() {
        if (noHaySessionAbierta()) {
            val session = HibernateSessionFactoryProvider.instance.createSession()
            CONTEXTO.set(session)
            transaction.set(session?.beginTransaction())
        }
    }

    private fun noHaySessionAbierta() = CONTEXTO.get() == null

    override fun commit() {
        transaction.get()?.commit()
        close()
    }

    override fun rollback() {
        transaction.get()?.rollback()
        close()
    }

    fun close() {
        CONTEXTO.get()?.close()
        transaction.set(null)
        CONTEXTO.set(null)
    }
}