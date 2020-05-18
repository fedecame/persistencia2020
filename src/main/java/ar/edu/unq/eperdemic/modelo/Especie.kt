package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.modelo.exception.EspecieNoCumpleRequisitosParaMutarException
import javax.persistence.*

@Entity
class Especie() {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    var id : Int? = null
    @ManyToOne(cascade=[CascadeType.ALL])
    lateinit var patogeno: Patogeno
    lateinit var nombre: String
    lateinit var paisDeOrigen: String
    @ManyToMany(fetch = FetchType.EAGER, cascade=[CascadeType.ALL])
    var mutaciones : MutableSet<Mutacion> = mutableSetOf()
    @ManyToMany(fetch = FetchType.EAGER, cascade=[CascadeType.ALL])
    var mutacionesDesbloqueadas : MutableSet<Mutacion> = mutableSetOf()
    var cantidadInfectados = 0

    fun agregarInfectado() {
        cantidadInfectados++
    }

    fun factorContagioAnimal(): Int = patogeno.factorContagioAnimal()

    fun factorContagioInsecto(): Int = patogeno.factorContagioInsecto()

    fun factorContagioHumano(): Int = patogeno.factorContagioHumano()

    fun defensaContraMicroorganismos(): Int = patogeno.defensaContraMicroorganismos()

    fun letalidad(): Int = patogeno.letalidad()

    fun aumentarFactorContagioAnimal() {
        patogeno.factorContagioAnimal++
    }

    fun aumentarfactorContagioInsecto() {
        patogeno.factorContagioInsecto++
    }

    fun aumentarfactorContagioHumano() {
        patogeno.factorContagioHumano++
    }

    fun aumentarDefensaContraMicroorganismos() {
        patogeno.defensaContraMicroorganismos++
    }

    fun aumentarLetalidad() {
        patogeno.letalidad++
    }

    fun cantidadDeADN() : Int {
        return cantidadInfectados.div(5)
    }

    fun agregarMutacion(unaMutacion : Mutacion) {
        this.mutaciones.add(unaMutacion)
    }

    fun desbloquearMutaciones(mutaciones : Set<Mutacion>) {
        this.mutacionesDesbloqueadas.addAll(mutaciones)
    }

    fun mutoEn(mutacionId : Long) : Boolean {
        return this.mutaciones.any { it.id == mutacionId }
    }

    fun tieneDesbloqueadaLaMutacion(mutacionId : Long) : Boolean {
        return this.mutacionesDesbloqueadas.any { it.id == mutacionId }
    }

    private fun puedeMutarEn(unaMutacion: Mutacion) : Boolean {
        return this.cantidadDeADN() >= unaMutacion.adnNecesario
                && this.tieneDesbloqueadaLaMutacion(unaMutacion.id!!)
                && unaMutacion.validaMutacionesNecesarias(this)
    }

    fun mutar(unaMutacion : Mutacion) {
        if (!this.puedeMutarEn(unaMutacion)) {
            throw EspecieNoCumpleRequisitosParaMutarException(this.id.toString(), unaMutacion.id.toString())
        }

        this.agregarMutacion(unaMutacion)
        this.desbloquearMutaciones(unaMutacion.mutacionesDesbloqueables)
        unaMutacion.mutarAtributoDeEspecie(this)
    }
}