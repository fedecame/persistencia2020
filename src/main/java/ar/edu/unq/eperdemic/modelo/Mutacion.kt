package ar.edu.unq.eperdemic.modelo

import javax.persistence.*

@Entity
class Mutacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Int? = null
    var adn : Int = 0


    fun mutar(unaEspecie : Especie){
        //implementa fede
    }


}


//Hay q agregar:
// *cantidad de ADN necesario para mutar/ya esta
// *mutaciones previas necesarias para mutar/ver con fede
// *incremento al valor especifico de la especie
// *implementar en la especie un mensaje q aumente el valor elegido(ver q opcion)