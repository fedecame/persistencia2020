package ar.edu.unq.eperdemic.modelo

import javax.persistence.*

@Entity
class Especie() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null



    /*
    //Esto estaba en el constructor:
    var patogeno: Patogeno,
    var nombre: String,
    var paisDeOrigen: String

    private var cantidadInfectados = 0

    fun factorContagioAnimal(): Int = patogeno.factorContagioAnimal()

    fun factorContagioInsecto(): Int = patogeno.factorContagioInsecto()

    fun factorContagioHumano(): Int = patogeno.factorContagioHumano()

    fun agregarInfectado() {
        cantidadInfectados++
    }

    fun cantidadDeADN() {
        cantidadInfectados.div(5)
    }

     */
}