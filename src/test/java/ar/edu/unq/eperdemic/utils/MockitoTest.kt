package ar.edu.unq.eperdemic.utils
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito


class MockitoTest {


    @Test
    fun test1()  {
        val especieMock = Mockito.mock(Especie::class.java)
        Mockito.`when`(especieMock.id).thenReturn(42)
        val patogenoMock = Mockito.mock(Patogeno::class.java)
        Mockito.`when`(patogenoMock.crearEspecie("sarasa", "sarasa")).thenReturn(especieMock)
       Assert.assertEquals(42, patogenoMock.crearEspecie("sarasa", "sarasa").id)
    }
}