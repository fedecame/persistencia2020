package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.estado.EstadoVector
import ar.edu.unq.eperdemic.estado.Infectado
import ar.edu.unq.eperdemic.estado.Sano
import ar.edu.unq.eperdemic.estado.transformer.EstadoConverter
import ar.edu.unq.eperdemic.tipo.TipoVector
import ar.edu.unq.eperdemic.tipo.transformer.TipoConverter
import org.hibernate.annotations.Cascade
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    var ubicacion: Ubicacion? =null

    init{
        this.recuperarse()
    }

    fun recuperarse(){
        this.cambiarEstado(Sano())
    }

    fun infectarse(especie: Especie){
        if (especies.find { it.id == especie.id } == null) {
            this.cambiarEstado(Infectado())
            this.agregarEspecie(especie)
            tipo.agregarInfectado(especie)
        }
    }

    private fun cambiarEstado(unEstado: EstadoVector) {
        estado = unEstado
    }

    fun agregarEspecie(unaEspecie: Especie){
        especies.add(unaEspecie)
    }

    fun contagiarsePor(vectorQueContagia: Vector) {
        tipo.contagiamePor(this, vectorQueContagia.tipo, vectorQueContagia.especies.toList())
    }

    fun contagiar(vectoresAContagiar: List<Vector>) {
        estado.contagiar(this, vectoresAContagiar)
    }
}