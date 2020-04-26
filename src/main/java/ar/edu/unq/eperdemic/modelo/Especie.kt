package ar.edu.unq.eperdemic.modelo

class Especie(var patogeno: Patogeno,
              var nombre: String,
              var paisDeOrigen: String) {

    fun factorContagioAnimal(): Int = patogeno.factorContagioAnimal()

    fun factorContagioInsecto(): Int = patogeno.factorContagioInsecto()

    fun factorContagioHumano(): Int = patogeno.factorContagioHumano()


}