package ar.edu.unq.eperdemic.modelo

import javax.persistence.*

@Entity
class Vector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    var id : Long? = null
    var nombre : String = ""

    @Column(nullable = false)
    var estado = EstadoDelivery.EstadoVectorEnum.Sano.name.toString()

    @OneToMany()
    private val especies : MutableSet<Especie> = mutableSetOf()

    @Transient
    var estadoDelivery = EstadoDelivery()

    private fun estado() : EstadoVector = estadoDelivery.estado(estado)!!

    @Transient
    private val tipo = 2

    fun recuperarse(){
        this.cambiarEstado(EstadoDelivery.EstadoVectorEnum.Sano)
    }

    fun infectarse(){
        this.cambiarEstado(EstadoDelivery.EstadoVectorEnum.Infectado)
    }

    private fun cambiarEstado(unEstado: EstadoDelivery.EstadoVectorEnum) {
        estado = unEstado.name
    }


}

    //private lateinit var tipo: TipoVector? =null
    //@Column(nullable = false, length = 500)
    //@ManyToOne(cascade = CascadeType.ALL)
    //private var estadoActual: EstadoVector = Sano()

    // @Column(nullable = false, length = 500)
   // @ManyToOne(cascade = CascadeType.ALL)
    // var ubicacion : Ubicacion? = null

/*

    //Inicialmente esto estaba en el constructor, junto al ID, pero el constructor no tiene que recibir parametros
    //private var nombreDeLocacionActual: String?


    //Quien agrego esta linea y por que? Paraque se necesita un mapa de ubicaciones?
    //private var mapaDeUbicaciones=MapaDeUbicaciones()
    //private   var ubicacionActual = mapaDeUbicaciones.getUbicacion(nombreDeLocacionActual)




    fun contagiarsePor(vectorQueContagia: Vector) {
        tipo.contagiamePor(vectorQueContagia.especies(), vectorQueContagia.tipo())
    }

    fun getUbicacionActual():Ubicacion?{
        return ubicacionActual
    }

    fun mover(nombreDeUbicacion :String){
        ubicacionActual=mapaDeUbicaciones.getUbicacion(nombreDeUbicacion)
    }

    fun contagiar(vectoresAContagiar: List<Vector>) {
        estadoActual.contagiar(vectoresAContagiar)
    }

    fun agregarEspecie(unaEspecie: Especie){
        especies.add(unaEspecie)
    }

    fun especies() = especies

    fun tipo() = tipo


    fun infectar(especie: Especie) {
        estado.infectarse(this)
        this.agregarEspecie(especie)
    }


*/
