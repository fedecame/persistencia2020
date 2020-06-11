package ar.edu.unq.eperdemic.services.runner


object TransactionRunner{
    var transactions: MutableList<Transaction> = mutableListOf(TransactionHibernate, TransactionNeo4j)

    private fun addIf(transaction: Transaction): TransactionRunner {
        if (!this.isThere(transaction)) {
            transactions.add(transaction)
        }
        return this
    }

    private fun isThere(transaction: Transaction): Boolean = transactions.any { it.javaClass.name == transaction.javaClass.name }
    private fun forAll(bloque: (Transaction) -> Unit) {
        transactions.forEach(bloque)
    }
    private fun start() {
        this.forAll { it.start() }
    }
    private fun commit() {
        this.forAll { it.commit() }
    }
    private fun rollback() {
        this.forAll { it.rollback() }
    }
    fun clear() {
        transactions = mutableListOf()
    }

    fun addHibernate(): TransactionRunner = this.addIf(TransactionHibernate)

    fun addNeo4j() : TransactionRunner = this.addIf(TransactionNeo4j)

    fun <T> runTrx(bloque: () -> T): T {
        try {
            this.start()
            val resultado = bloque()
            this.commit()
            return resultado
        } catch (e: RuntimeException) {
            this.rollback()
            throw e
        }
    }
}

     /*
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

