package ar.edu.unq.eperdemic.modelo

import javax.persistence.*

@Entity
class Especie() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null
    lateinit var patogeno: Patogeno
    var nombre: String = ""
    var paisDeOrigen: String = ""
    var cantidadInfectados = 0

    fun agregarInfectado() {
        cantidadInfectados++
    }

    /*
    fun factorContagioAnimal(): Int = patogeno.factorContagioAnimal()

    fun factorContagioInsecto(): Int = patogeno.factorContagioInsecto()

    fun factorContagioHumano(): Int = patogeno.factorContagioHumano()

    fun cantidadDeADN() {
        cantidadInfectados.div(5)
    }
     */
}