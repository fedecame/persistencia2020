package ar.edu.unq.eperdemic.modelo

import java.io.Serializable
import javax.persistence.*

@Entity
class Ubicacion() : Serializable {
    @Id
    lateinit var nombreUbicacion: String

    @OneToMany(mappedBy = "ubicacion", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var vectores: MutableSet<Vector> = HashSet()

    fun setearNombre(nombre:String){
        nombreUbicacion=nombre
    }
    fun alojarVector(vector:Vector){
        vectores.add(vector)
    }

}