package ar.edu.unq.eperdemic.modelo

import javax.persistence.*

@Entity
class Vector() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Int? = null

   // @Column(nullable = false, length = 500)
   // @ManyToOne(cascade = CascadeType.ALL)
    // var ubicacion : Ubicacion? = null

/*
    //private lateinit var tipo: TipoVector
    //private var estadoActual: EstadoVector = Sano()

    //Inicialmente esto estaba en el constructor, junto al ID, pero el constructor no tiene que recibir parametros
    //private var nombreDeLocacionActual: String?

    //private val especies = mutableListOf<Especie>()

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
        estadoActual.infectarse(this)
        this.agregarEspecie(especie)
    }

    fun cambiarEstado(estado:EstadoVector) {
        this.estadoActual=estado;
    }
*/
}