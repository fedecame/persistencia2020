package ar.edu.unq.eperdemic.utility

abstract class Delivery<Any> (values : List<Any>) {

    private var things : MutableMap<String, Any>

    init {
        things = mutableMapOf()
        values.forEach {  this.add(it) }
    }

    open fun add(aValue: Any) {
        //Como consigo el nombre de una clase Any?
        val name = ""
        // //this.format(aValue!!::class.java.canonicalName as String)
        this.ifConditionThrow(this.isInTheList(name), this.myAddException(name), this.myAddBlock(name, aValue))
    }

    abstract fun myAddException(word : String): Exception

    abstract fun myGetException(word : String): Exception

    fun myAddBlock(key : String, value : Any) = { things.put(key, value) }
    fun myGetBlock(key : String) = { things.get(this.format(key)) }

    fun get(key: String): Any? {
        return this.ifConditionThrow(!this.isInTheList(key), this.myGetException(key), this.myGetBlock(key))
    }

    private fun isInTheList(name: String) = things.keys.map{this.format(it)}.contains(this.format(name))

    private fun format(word : String) = word.toLowerCase()

    private fun <T> ifConditionThrow(condition: Boolean, e: Exception, bloque: () -> T): T {
        if (condition) {
            throw e
        }
        return bloque()
    }
}