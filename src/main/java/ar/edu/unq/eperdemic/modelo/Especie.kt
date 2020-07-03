package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.modelo.exception.EspecieNoCumpleRequisitosParaMutarException
import com.fasterxml.jackson.annotation.JsonManagedReference
import org.hibernate.annotations.Cascade
import javax.persistence.*

@Entity
class Especie() {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    var id : Int? = null
    @ManyToOne()
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JsonManagedReference
    lateinit var patogeno: Patogeno
    lateinit var nombre: String
    lateinit var paisDeOrigen: String

    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.PERSIST])
    @JoinTable(
        name = "Especies_Mutadas",
        joinColumns = [JoinColumn(name = "especie_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "mutacion_id", referencedColumnName = "id")]
    )
    var mutaciones : MutableSet<Mutacion> = mutableSetOf()

    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.PERSIST])
    @JoinTable(
        name = "Especies_Desbloqueadoras",
        joinColumns = [JoinColumn(name = "especie_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "mutacion_id", referencedColumnName = "id")]
    )
    var mutacionesDesbloqueadas : MutableSet<Mutacion> = mutableSetOf()

    var cantidadInfectadosParaADN = 0

    fun agregarInfectadoParaADN() {
        cantidadInfectadosParaADN++
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
        return cantidadInfectadosParaADN.div(5)
    }

    fun agregarMutacion(unaMutacion : Mutacion) {
        this.mutaciones.add(unaMutacion)
    }

    fun desbloquearMutaciones(mutaciones : Set<Mutacion>) {
        this.mutacionesDesbloqueadas.addAll(mutaciones)
    }

    fun fueMutadaEn(mutacion: Mutacion) : Boolean {
        return this.mutaciones.any { (it.id ?: it) == (mutacion.id ?: mutacion) }
    }

    fun tieneDesbloqueadaLaMutacion(mutacion : Mutacion) : Boolean {
        return this.mutacionesDesbloqueadas.any { (it.id ?: it) == (mutacion.id ?: mutacion) }
    }

    private fun puedeMutarEn(unaMutacion: Mutacion) : Boolean {
        return this.cantidadDeADN() >= unaMutacion.adnNecesario
                && this.tieneDesbloqueadaLaMutacion(unaMutacion)
                && unaMutacion.validaMutacionesNecesarias(this)
    }

    private fun descontarAdn(cantADN: Int) {
        this.cantidadInfectadosParaADN -= cantADN * 5
    }

    fun mutar(unaMutacion : Mutacion) : Boolean {
        val puedeMutar = this.puedeMutarEn(unaMutacion)
        if (!puedeMutar) {
            throw EspecieNoCumpleRequisitosParaMutarException(this, unaMutacion)
        }

        this.agregarMutacion(unaMutacion)
        this.descontarAdn(unaMutacion.adnNecesario)
        this.desbloquearMutaciones(unaMutacion.mutacionesDesbloqueables)
        unaMutacion.mutarAtributoDeEspecie(this)
        return puedeMutar
    }
}