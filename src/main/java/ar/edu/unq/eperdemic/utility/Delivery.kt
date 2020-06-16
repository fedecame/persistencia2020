package ar.edu.unq.eperdemic.utility

abstract class Delivery<T> (values : List<Any>) {

    protected var things : MutableMap<String, T> = mutableMapOf()

    init {
        values.forEach {  this.add(it) }
    }

    fun add(aValue: Any) {
        val y : T = aValue as T
        val name = this.format(aValue.javaClass!!.simpleName)
        this.ifConditionThrow(this.isInTheList(name), this.myAddException(name), this.myAddBlock(name, aValue))
    }

    abstract fun myAddException(word : String): Exception

    abstract fun myGetException(word : String): Exception

    fun myAddBlock(key : String, value : T) = { things.put(key, value) }
    fun myGetBlock(key : String) = { things.get(this.format(key)) }

    fun get(key: String): T? {
        return this.ifConditionThrow(!this.isInTheList(key), this.myGetException(key), this.myGetBlock(key))
    }

    private fun isInTheList(name: String) = things.keys.map{this.format(it)}.contains(this.format(name))

    private fun format(word : String) = word.toLowerCase()

    private fun <A> ifConditionThrow(condition: Boolean, e: Exception, bloque: () -> A): A {
        if (condition) {
            throw e
        }
        return bloque()
    }
}