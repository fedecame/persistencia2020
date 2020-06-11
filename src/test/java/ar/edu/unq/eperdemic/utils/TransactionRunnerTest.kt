package ar.edu.unq.eperdemic.utils

import ar.edu.unq.eperdemic.services.runner.TransactionHibernate
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class TransactionRunnerTest {

/*    @Test
    fun alEmpezarElTRNoTieneTransaction(){
        Assert.assertEquals(0, TransactionRunner.transactions.size)
    }

    @Test
    fun addHibernateAgregaUnaInstanciaDeTransactionHibernate(){
        val l0 = TransactionRunner.transactions.size
        Assert.assertEquals(0, l0)
        TransactionRunner.addHibernate()
        val l1 = TransactionRunner.transactions.size
        Assert.assertEquals(1, l1)
        Assert.assertTrue(TransactionRunner.transactions.all{it is TransactionHibernate })
    }

    @Test
    fun addHibernateNoAgregaRepetidosAlSerRecibidoMasDeUnaVez(){
        val l0 = TransactionRunner.transactions.size
        Assert.assertEquals(0, l0)
        TransactionRunner.addHibernate()
        val l1 = TransactionRunner.transactions.size
        Assert.assertEquals(1, l1)
        TransactionRunner.addHibernate()
        val l2 = TransactionRunner.transactions.size
        Assert.assertEquals(l2, l1)
    }
*//*
    @Test
    fun addNeo4jAgregaUnaInstanciaDeTransactionNeo4j(){
        val l0 = TransactionRunner.transactions.size
        Assert.assertEquals(0, l0)
     //   tr.addNeo4j()
        val l1 = TransactionRunner.transactions.size
        Assert.assertEquals(1, l1)
   //     Assert.assertTrue(TransactionRunner.transactions.all{it is TransactionNeo4j })
    }

    @Test
    fun addNeo4jNoAgregaRepetidosAlSerRecibidoMasDeUnaVez(){
        val l0 = TransactionRunner.transactions.size
        Assert.assertEquals(0, l0)
    //    tr.addNeo4j()
        val l1 = TransactionRunner.transactions.size
        Assert.assertEquals(1, l1)
        tr.addNeo4j()
        val l2 = TransactionRunner.transactions.size
        Assert.assertEquals(l2, l1)
    }
*//*
    @Test
    fun AlAgregarMuchasVecesRepetidosTieneSolamente2Elementos(){
        TransactionRunner.clear()
        val l0 = TransactionRunner.transactions.size
        Assert.assertEquals(0, l0)
        TransactionRunner.addHibernate()
        TransactionRunner.addHibernate()
        TransactionRunner.addHibernate()
        TransactionRunner.addHibernate()
        TransactionRunner.addHibernate()
        TransactionRunner.addHibernate()
        TransactionRunner.addHibernate()
        TransactionRunner.addHibernate()
        TransactionRunner.addHibernate()
        val l1 = TransactionRunner.transactions.size
        Assert.assertEquals(1, l1)
        val l2 = TransactionRunner.transactions.size
        Assert.assertEquals(l2, l1)
    }

    @Before
    @After
    fun clear(){
        TransactionRunner.clear()
    }*/
}

