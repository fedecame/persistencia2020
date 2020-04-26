package ar.edu.unq.eperdemic.modelo

class Vector( var id: Int?,
              var nombreDeLocacionActual: String) {
    private var estadoActual: EstadoVector = Sano()
    private lateinit var tipo: TipoVector
    private val especies = mutableListOf<Especie>()

    fun contagiar(vectoresAContagiar: List<Vector>) {
        estadoActual.contagiar(vectoresAContagiar)
    }

    fun agregarEspecie(unaEspecie: Especie){
        especies.add(unaEspecie)
    }

    fun especies() = especies

    fun tipo() = tipo

    fun contagiarsePor(vectorQueContagia: Vector) {
        tipo.contagiamePor(vectorQueContagia.especies(), vectorQueContagia.tipo())
    }

    fun infectar(especie: Especie) {
        estadoActual.infectarse(this)
        this.agregarEspecie(especie)
    }

    fun cambiarEstado(estado:EstadoVector) {
        this.estadoActual=estado;
    }

}