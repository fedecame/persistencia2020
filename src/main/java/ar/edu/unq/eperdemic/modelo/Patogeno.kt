package ar.edu.unq.eperdemic.modelo

import java.io.Serializable
import javax.persistence.*

@Entity
class Patogeno : Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Int? = null
    var cantidadDeEspecies : Int = 0
    var factorContagioAnimal : Int  = 0
    var factorContagioInsecto :Int  = 0
    var factorContagioHumano : Int  = 0

    @Column(nullable = false)
    lateinit var tipo : String

    fun crearEspecie(nombreEspecie: String, paisDeOrigen: String) : Especie{
        cantidadDeEspecies++
        val especie = Especie()
        especie.paisDeOrigen = paisDeOrigen
        especie.patogeno = this
        especie.nombre = nombreEspecie
        return Especie()
    }

    fun factorContagioAnimal(): Int = factorContagioAnimal

    fun factorContagioInsecto(): Int = factorContagioInsecto

    fun factorContagioHumano(): Int = factorContagioHumano
}