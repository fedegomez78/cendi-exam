package com.empresa.asdtests

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.empresa.asdtests.databinding.ActivityRealizarTestBinding
import com.empresa.asdtests.model.Pregunta
import com.empresa.asdtests.model.Test
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.random.Random

class ActivityRealizarTest : AppCompatActivity() {

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var binding : ActivityRealizarTestBinding
    private lateinit var listaPreguntas: ArrayList<Pregunta>
    private lateinit var listaPreguntasTest: ArrayList<Pregunta>
    private lateinit var listaTest: ArrayList<Test>
    private lateinit var preguntaAdapter : ArrayAdapter<Pregunta>

    private lateinit var testPreguntaAdapter : ArrayAdapter<Test>


    private lateinit var userId: String

    var cantidadPreguntasPorTest: Int = 5

    val testId: String = UUID.randomUUID().toString()


    val database = Firebase.database
    val dbReferencePreguntas = database.getReference("preguntas")
    val dbReferencePreguntasTest = database.getReference("tests")



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRealizarTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getStringExtra("userId").toString()

        Firebase.initialize(this)

        binding.tvTestUniqueId.text = testId


        //soportar la barra de menu toolbar
        setSupportActionBar(binding.toolbarMyToolbar)



        listaPreguntas = ArrayList<Pregunta>()

        verListadoPreguntas()

        binding.lvPreguntasTest.setOnItemClickListener { parent, view, position, id ->
            var test = listaTest[position]

            var pregunta = obtenerPregunta(test.preguntaId)

            val args = Bundle ()
            args.putString("testUniqueId", test.id)
            args.putString("testId", test.testId)
            args.putString("userId", test.userId)
            args.putString("preguntaId", test.preguntaId)
            args.putString("preguntaArea", pregunta.area)
            args.putString("preguntaTexto", pregunta.pretexto)
            args.putString("preguntaOpcion1", pregunta.opcion1)
            args.putString("preguntaOpcion2", pregunta.opcion2)
            args.putString("preguntaRespuesta", pregunta.respuesta)



            //Toast.makeText(this, "${test}", Toast.LENGTH_SHORT ).show()

            binding.lvPreguntasTest.visibility = View.GONE
            supportFragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .replace( R.id.fragmentContainerResolverTest, FragmentResponderPregunta::class.java, args, "ResponderPreguntas" )
                .commit()


        }


        binding.btnVerResultadoTest.setOnClickListener {
            val intent = Intent(this, ActivityVerResultado::class.java)
            intent.putExtra("testId", testId)
            intent.putExtra("userId", userId)

            this.startActivity(intent)
        }


    }//fin oncreate

    private fun obtenerPregunta(preguntaId: String): Pregunta {

        Log.e("FG", "preguntaId: " + preguntaId)

//        for (pregunta in listaPreguntas){
//
//        }

        var pregunta: Pregunta? = listaPreguntas.find { it.id == preguntaId }
        //assertEquals(theFirstBatman, "Michael Keaton")

        if (pregunta != null) {
            Log.e("FG", "OK preguntaId: "+preguntaId +" desde lista: " + pregunta.pretexto)
        }else{
            pregunta = Pregunta ("pregid1", "area1", "pretexto1", "opcion1-1", "opcion2 ", "respuesta1")
            Log.e("FG", "preguntaId: " + pregunta.pretexto)
        }

        return pregunta

    }


    //funciones

    private fun verListadoPreguntas() {
        val preguntaItemListener = object : ValueEventListener {
            override fun onDataChange(datasnapshot: DataSnapshot) {

                for (preg in datasnapshot.children){
                    var pregunta = Pregunta( "", "", "", "", "", "")

                    //objeto MAP
                    val mapPregunta : Map<String, Any> = preg.value as HashMap<String, Any>

                    pregunta.id = mapPregunta.get("id").toString()
                    pregunta.area = mapPregunta.get("area").toString()
                    pregunta.pretexto = mapPregunta.get("pretexto").toString()
                    pregunta.opcion1 = mapPregunta.get("opcion1").toString()
                    pregunta.opcion2 = mapPregunta.get("opcion2").toString()
                    pregunta.respuesta = mapPregunta.get("respuesta").toString()
                    //agregamos a la lista global de preguntas
                    listaPreguntas.add(pregunta)

                }



                listaTest = generarTest ( listaPreguntas, userId, testId )


//                var pruebaListaTest: ArrayList<Test>
//                pruebaListaTest = ArrayList()
//
//                var pruebaTest = Test ("id1", "testId1", "userId1", "preguntaId1","pre-texto1", "respuesta1")
//                pruebaListaTest.add(pruebaTest)
//                pruebaTest = Test ("id2", "testId2", "userId2", "preguntaId2","pre-texto2",  "respuesta2")
//                pruebaListaTest.add(pruebaTest)
//
//                Log.e("FG", "listado test: "+pruebaListaTest)


                testPreguntaAdapter = TestPreguntasAdapter(this@ActivityRealizarTest, listaTest)
                binding.lvPreguntasTest.adapter = testPreguntaAdapter



            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FB", "Error: " + error.message)
            }

        }

        dbReferencePreguntas.addValueEventListener(preguntaItemListener)



    }



    fun generarTest(lista: ArrayList<Pregunta>, userId: String, testId: String): ArrayList<Test> {

        var listaPreguntasTests: ArrayList<Pregunta>
        listaPreguntasTests = ArrayList<Pregunta>()

        var listaTest: ArrayList<Test>
        listaTest = ArrayList<Test>()


        var i: Int = 0
        while (i < cantidadPreguntasPorTest ){

            val randomIndex = Random.nextInt(lista.size)
            Log.e("FG", "valor random: " + lista[randomIndex].pretexto)

            if(lista[randomIndex] !in listaPreguntasTests) {
                listaPreguntasTests.add(lista[randomIndex])
                i++
            }else{
                Log.e("FG", "La pregunta ya estaba repetida")
            }

        }


        //recorremos la lista  y la vamos insertando en la base de datos



        for(preguntaTest in listaPreguntasTests) {
            var test = Test (
                UUID.randomUUID().toString(),
                testId,
                userId,
                preguntaTest.id,
                preguntaTest.area,
                preguntaTest.pretexto,
                0
            )

            dbReferencePreguntasTest.child(test.id).setValue(test)
            listaTest.add(test)

        }


        return listaTest

    }



    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.mnCerrarSesion -> {
            Toast.makeText(this, "Cerrar", Toast.LENGTH_SHORT).show()
            cerrarSesion()
            true
        }

        R.id.mnVerResultados -> {
            verResultados()
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }

    }

    private fun verResultados() {
        val intent = Intent(this, ActivityVerResultado::class.java)
        this.startActivity(intent)
    }

    fun cerrarSesion(){
        auth.signOut()
        val intent = Intent(this, MainActivity::class.java)
        this.startActivity(intent)
    }






}