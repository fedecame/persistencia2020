package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.modelo.exception.EspecieNoCumpleRequisitosParaMutarException
import ar.edu.unq.eperdemic.modelo.tipoMutacion.TipoMutacion
import ar.edu.unq.eperdemic.modelo.tipoMutacion.transformer.TipoMutacionConverter
import javax.persistence.*

@Entity
class Mutacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null
    var adnNecesario : Int = 0

    @Column(nullable = false)
    @Convert(converter = TipoMutacionConverter::class)
    lateinit var tipo : TipoMutacion

    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    var mutacionesNecesarias : MutableSet<Mutacion> = mutableSetOf()

    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    var mutacionesDesbloqueables : MutableSet<Mutacion> = mutableSetOf()

    private fun validaMutacionDeEspecie(especie : Especie) : Boolean {
        //TODO Pensar como sacar la validacion y simplemente DELEGAR (para q sea mas objetoso)
        return especie.cantidadDeADN() >= adnNecesario
                && especie.puedeMutarEn(this.id!!)
                && mutacionesNecesarias.all { mutacion -> especie.mutoEn(mutacion.id!!) }
    }

    fun mutar(unaEspecie : Especie) {
        //TODO Sacar este if, reemplazar por DELEGACION
        if (!this.validaMutacionDeEspecie(unaEspecie)) {
            throw EspecieNoCumpleRequisitosParaMutarException(unaEspecie.id.toString(), this.id!!.toString())
        }

        unaEspecie.agregarMutacion(this)
        unaEspecie.desbloquearMutaciones(this.mutacionesDesbloqueables)
        this.tipo.mutarAtributoDeEspecie(unaEspecie)
    }
}


//Hay q agregar:
// *cantidad de ADN necesario para mutar/ya esta
// *mutaciones previas necesarias para mutar/ver con fede
// *incremento al valor especifico de la especie
// *implementar en la especie un mensaje q aumente el valor elegido(ver q opcion)