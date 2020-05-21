package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.modelo.tipoMutacion.TipoMutacion
import ar.edu.unq.eperdemic.modelo.tipoMutacion.transformer.TipoMutacionConverter
import javax.persistence.*

@Entity
class Mutacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    var id : Long? = null
    var adnNecesario : Int = 0

    @Column(nullable = false)
    @Convert(converter = TipoMutacionConverter::class)
    lateinit var tipo : TipoMutacion

    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.PERSIST])
    @JoinTable(
    name = "Mutaciones_Necesarias",
    joinColumns = [
        JoinColumn(name= "mutacionPadre_id", referencedColumnName = "id")
    ], inverseJoinColumns = [
        JoinColumn(name= "mutacionNecesaria_id", referencedColumnName = "id")
    ])
    var mutacionesNecesarias : MutableSet<Mutacion> = mutableSetOf()

    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.PERSIST])
    @JoinTable(
    name = "Mutaciones_Desbloqueables",
    joinColumns = [
        JoinColumn(name= "mutacionPadre_id", referencedColumnName = "id")
    ], inverseJoinColumns = [
        JoinColumn(name= "mutacionDesbloqueable_id", referencedColumnName = "id")
    ])
    var mutacionesDesbloqueables : MutableSet<Mutacion> = mutableSetOf()

    fun mutarAtributoDeEspecie(unaEspecie: Especie) {
        this.tipo.mutarAtributoDeEspecie(unaEspecie)
    }

    fun validaMutacionesNecesarias(unaEspecie: Especie) : Boolean {
        return mutacionesNecesarias.all { mutacion -> unaEspecie.fueMutadaEn(mutacion) }
    }
}


//Hay q agregar:
// *cantidad de ADN necesario para mutar/ya esta
// *mutaciones previas necesarias para mutar/ver con fede
// *incremento al valor especifico de la especie
// *implementar en la especie un mensaje q aumente el valor elegido(ver q opcion)