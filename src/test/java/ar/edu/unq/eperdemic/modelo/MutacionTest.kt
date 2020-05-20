package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.modelo.tipoMutacion.*
import org.junit.Before
import org.junit.Test

class MutacionTest {
    lateinit var mutacion: Mutacion
    lateinit var mutacion1: Mutacion
    lateinit var mutacion2: Mutacion
    lateinit var mutacion3: Mutacion
    lateinit var mutacion4: Mutacion
    lateinit var mutacion5: Mutacion
    lateinit var patogeno: Patogeno
    lateinit var especie: Especie

    @Before
    fun setUp(){
        mutacion = Mutacion()
        mutacion1 = Mutacion()
        mutacion2 = Mutacion()
        mutacion3 = Mutacion()
        mutacion4 = Mutacion()
        mutacion5 = Mutacion()

        mutacion.adnNecesario = 3
        mutacion.tipo = MutacionLetalidad()
        mutacion.mutacionesNecesarias.addAll(mutableListOf(mutacion1))
        mutacion.mutacionesDesbloqueables.addAll(mutableListOf(mutacion3, mutacion5))

        mutacion1.adnNecesario = 1
        mutacion1.tipo = MutacionFactorContagioInsecto()
        mutacion1.mutacionesDesbloqueables.add(mutacion)

        mutacion2.adnNecesario = 2
        mutacion2.tipo = MutacionFactorContagioAnimal()
        mutacion2.mutacionesNecesarias.add(mutacion1)
        mutacion2.mutacionesDesbloqueables.add(mutacion)

        mutacion3.adnNecesario = 1
        mutacion3.tipo = MutacionFactorContagioAnimal()
        mutacion3.mutacionesNecesarias.addAll(mutableListOf(mutacion1, mutacion, mutacion2))
        mutacion3.mutacionesDesbloqueables.addAll(mutableListOf(mutacion4, mutacion5))

        mutacion4.adnNecesario = 2
        mutacion4.tipo = MutacionFactorContagioHumano()
        mutacion4.mutacionesNecesarias.addAll(mutableListOf(mutacion1, mutacion2, mutacion, mutacion3))
        mutacion4.mutacionesDesbloqueables.add(mutacion5)

        mutacion5.adnNecesario = 4
        mutacion5.tipo = MutacionDefensaMicroorganismos()
        mutacion5.mutacionesNecesarias.addAll(mutableListOf(mutacion1, mutacion2, mutacion, mutacion3, mutacion4))

        patogeno = Patogeno()
        patogeno.cantidadDeEspecies = 1
        patogeno.tipo = "Virus"
        patogeno.factorContagioHumano = 80
        patogeno.factorContagioInsecto = 15
        patogeno.factorContagioAnimal = 50
        patogeno.defensaContraMicroorganismos = 30
        patogeno.letalidad = 5

        especie = Especie()
        especie.nombre = "Corona"
        especie.paisDeOrigen = "China"
        especie.mutacionesDesbloqueadas.add(mutacion1)
        especie.cantidadInfectadosParaADN = 18
        especie.patogeno = patogeno
    }


}