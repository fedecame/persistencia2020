package ar.edu.unq.eperdemic.services.runner

object TransactionRunner{
    var transactions: MutableList<Transaction> = mutableListOf()

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
        forAll { it.start() }
    }
    private fun commit() {
        forAll { it.commit() }
    }
    private fun rollback() {
        forAll { it.rollback() }
    }
    fun clear() {
        transactions = mutableListOf()
    }

    fun addHibernate(): TransactionRunner = this.addIf(TransactionHibernate)

    fun addNeo4j() : TransactionRunner = this.addIf(TransactionNeo4j)

    fun <T> runTrx(bloque: () -> T): T {
        if (transactions.any { it.hasSession() }) {
            return bloque()
        } else {
            try {
                this.start()
                val resultado = bloque()
                this.commit()
                return resultado
            } catch (e: RuntimeException) {
                this.rollback()
                throw e
            } finally {
                this.clear()
            }
        }
    }
}