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
    var defensaContraMicroorganismos : Int = 0
    var letalidad : Int = 0

    @Column(nullable = false)
    lateinit var tipo : String

    fun crearEspecie(nombreEspecie: String, paisDeOrigen: String, cantidadInfectadosADN: Int = 0, mutacionesDesbloqueadas: MutableSet<Mutacion> = mutableSetOf()) : Especie{
        cantidadDeEspecies++
        val especie = Especie()
        especie.paisDeOrigen = paisDeOrigen
        especie.patogeno = this
        especie.nombre = nombreEspecie
        especie.cantidadInfectadosParaADN = cantidadInfectadosADN
        especie.mutacionesDesbloqueadas = mutacionesDesbloqueadas
        return Especie()
    }

    fun factorContagioAnimal(): Int = factorContagioAnimal

    fun factorContagioInsecto(): Int = factorContagioInsecto

    fun factorContagioHumano(): Int = factorContagioHumano

    fun defensaContraMicroorganismos(): Int = defensaContraMicroorganismos

    fun letalidad(): Int = letalidad
}