package ar.edu.unq.eperdemic.modelo

import javax.persistence.Id
import javax.persistence.ManyToOne

class Medico() {
    @Id
   lateinit var nombre :String;
    @ManyToOne()
    lateinit var mutableList: MutableList<Antidoto>
}