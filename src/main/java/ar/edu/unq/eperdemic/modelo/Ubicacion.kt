package ar.edu.unq.eperdemic.modelo

import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable
import javax.persistence.*

@Entity
class Ubicacion() : Serializable {
    @Id
    lateinit var nombreUbicacion: String

    @OneToMany(mappedBy = "ubicacion", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JsonIgnore
    var vectores: MutableSet<Vector> = HashSet()

    fun agregarVector(vector : Vector){
        vectores.add(vector)
    }
}