package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.estado.EstadoVector
import ar.edu.unq.eperdemic.estado.Infectado
import ar.edu.unq.eperdemic.estado.Sano
import ar.edu.unq.eperdemic.estado.transformer.EstadoConverter
import ar.edu.unq.eperdemic.tipo.TipoVector
import ar.edu.unq.eperdemic.tipo.transformer.TipoConverter
import javax.persistence.*
import kotlin.jvm.Transient

@Entity
class Vector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    var id : Long? = null


    @ManyToMany(cascade=[CascadeType.ALL], fetch=FetchType.EAGER)
    var especies : MutableSet<Especie> = mutableSetOf()

    @Column(nullable = false)
    @Convert(converter = TipoConverter::class)
    lateinit var tipo : TipoVector

    @Column(nullable = false)
    @Convert(converter = EstadoConverter::class)
    lateinit var estado : EstadoVector

    @ManyToOne()
    var ubicacion: Ubicacion? =null


    init{
        this.recuperarse()
    }

    fun recuperarse(){
        this.cambiarEstado(Sano())
    }

    fun infectarse(){
        this.cambiarEstado(Infectado())
    }

    private fun cambiarEstado(unEstado: EstadoVector) {
        estado = unEstado
    }

    fun agregarEspecie(unaEspecie: Especie){
        especies.add(unaEspecie)
    }
    fun setearNombreUbicacion(nombre: String){

        ubicacion= Ubicacion()
        ubicacion!!.setearNombre(nombre)
    }

}


/*
    //Falta la ubicacion de Nelson aca
    //Esto es lo que teniamos por defecto
    //@Column(nullable = false, length = 500)
    //@ManyToOne(cascade = [CascadeType.ALL])
    //var ubicacion : Ubicacion? = null

    fun contagiarsePor(vectorQueContagia: Vector) {
        tipo.contagiamePor(vectorQueContagia.especies(), vectorQueContagia.tipo())
    }

//Necesitamos que el vector sepa contagiar a uno
    fun contagiar(vectoresAContagiar: List<Vector>) {
        estadoActual.contagiar(vectoresAContagiar)
    }

    fun infectar(especie: Especie) {
        estado.infectarse(this)
        this.agregarEspecie(especie)
    }
*/
