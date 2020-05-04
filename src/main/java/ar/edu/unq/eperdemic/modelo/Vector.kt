package ar.edu.unq.eperdemic.modelo

import javax.persistence.*

@Entity
class Vector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    var id : Long? = null

    //Falta la ubicacion de Nelson aca
    //Esto es lo que teniamos por defecto
    //@Column(nullable = false, length = 500)
    //@ManyToOne(cascade = [CascadeType.ALL])
    //var ubicacion : Ubicacion? = null


    @Column(nullable = false)
    var estado = EstadoDeVector.Sano.name.toString()

    @OneToMany()
    private val especies : MutableSet<Especie> = mutableSetOf()

    @Transient
    var estadoDelivery = EstadoDelivery()


    @Column(nullable = false)
    @Convert(converter = TipoConverter::class)
    lateinit var tipo : TipoVector

    private fun estado() : EstadoVector = estadoDelivery.estado(estado)!!


    fun recuperarse(){
        this.cambiarEstado(EstadoDeVector.Sano)
    }

    fun infectarse(){
        this.cambiarEstado(EstadoDeVector.Infectado)
    }

    private fun cambiarEstado(unEstado: EstadoDeVector) {
        estado = unEstado.name
    }

    fun agregarEspecie(unaEspecie: Especie){
        especies.add(unaEspecie)
    }
}


/*

    fun contagiarsePor(vectorQueContagia: Vector) {
        tipo.contagiamePor(vectorQueContagia.especies(), vectorQueContagia.tipo())
    }

    fun contagiar(vectoresAContagiar: List<Vector>) {
        estadoActual.contagiar(vectoresAContagiar)
    }


    fun especies() = especies

    fun tipo() = tipo

    fun infectar(especie: Especie) {
        estado.infectarse(this)
        this.agregarEspecie(especie)
    }


*/
