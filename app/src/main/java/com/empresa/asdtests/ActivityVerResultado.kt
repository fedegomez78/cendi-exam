package com.empresa.asdtests

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.empresa.asdtests.databinding.ActivityVerResultadoBinding
import com.empresa.asdtests.model.Test
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase



class ActivityVerResultado : AppCompatActivity() {

    private lateinit var binding : ActivityVerResultadoBinding

    private lateinit var listTestActual: ArrayList<Test>
    private lateinit var listTestsAcumuladoUsuario: ArrayList<Test>

    //firebase auth
    private lateinit var auth: FirebaseAuth

    val database = Firebase.database
    val dbReferencePreguntasTest = database.getReference("tests")


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityVerResultadoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        val currentUser = auth.currentUser


        //soportar la barra de menu toolbar
        setSupportActionBar(binding.toolbarMyToolbar)


        var testId: String
        testId = ""

        var userId: String
        userId = ""

        testId = intent.getStringExtra("testId").toString()
        //userId = intent.getStringExtra("userId").toString()

        userId = currentUser?.uid.toString()

        binding.tvIdTest.text = testId
        binding.tvUserId.text = userId

        listTestActual = ArrayList()

        verTestActual(testId)

        listTestsAcumuladoUsuario = ArrayList()
        verTestsUsuarioAcumulado(userId)



    }//fin oncreate


    ///////////////////////////////

    private fun verTestActual(testId: String) {
        var correctas = 0
        var totalPreguntas = 0
        var resultado = 0.0
        val testItemListener = object : ValueEventListener {
            override fun onDataChange(datasnapshot: DataSnapshot) {

                for (dstest in datasnapshot.children){
                    var test = Test( "", "", "", "", "","", 0)

                    //objeto MAP
                    val mapTest : Map<String, Any> = dstest.value as HashMap<String, Any>

                    test.id = mapTest.get("id").toString()
                    test.userId = mapTest.get("userId").toString()
                    test.testId = mapTest.get("testId").toString()
                    test.preguntaId = mapTest.get("preguntaId").toString()
                    test.preguntaTexto = mapTest.get("preguntaTexto").toString()
                    test.respuesta = mapTest.get("respuesta").toString().toInt()

                    //agregamos a la lista global de preguntas
                    listTestActual.add(test)
//                    Log.e("FG","test actual: " + test)

                    totalPreguntas++
                    correctas = correctas + test.respuesta

                }

                if(totalPreguntas>0) {
                    resultado = (correctas / totalPreguntas).toDouble()
                }else {
                    resultado = 0.0
                }
                binding.edtResultadoActual.setText(correctas.toString() + " / " + totalPreguntas.toString())



            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FB", "Error: " + error.message)
            }

        }

        val dbReferenceFiltrado = database.getReference("tests").orderByChild("testId").equalTo(testId)
        dbReferenceFiltrado.addValueEventListener(testItemListener)



    }




    private fun verTestsUsuarioAcumulado(userId: String) {

        var cantPreguntas = 0
        var cantPreguntasCorrectas = 0
        var resultado = 0.0
        var cantidadTests = 0

        val testItemListener = object : ValueEventListener {
            override fun onDataChange(datasnapshot: DataSnapshot) {


                for (dstest in datasnapshot.children){

                    var test = Test( "", "", "", "", "", "",0)

                    //objeto MAP
                    val mapTest : Map<String, Any> = dstest.value as HashMap<String, Any>

                    test.id = mapTest.get("id").toString()
                    test.userId = mapTest.get("userId").toString()
                    test.testId = mapTest.get("testId").toString()
                    test.preguntaId = mapTest.get("preguntaId").toString()
                    test.preguntaArea = mapTest.get("preguntaArea").toString()
                    test.preguntaTexto = mapTest.get("preguntaTexto").toString()
                    test.respuesta = mapTest.get("respuesta").toString().toInt()

                    //agregamos a la lista global de preguntas
                    listTestsAcumuladoUsuario.add(test)

                    //Log.e("FG","test acumulado: " + test)


                    cantPreguntas++
                    cantPreguntasCorrectas = cantPreguntasCorrectas + test.respuesta

                }

                binding.edtCantidadTestsRealizados.setText((listTestsAcumuladoUsuario.size/5).toString())
                binding.edtCantidadTotalPreguntas.setText(cantPreguntas.toString())
                binding.edtCantidadTotalPreguntasCorrectas.setText(cantPreguntasCorrectas.toString())

                if(cantPreguntas>0) {
                    resultado = (cantPreguntasCorrectas.toDouble() / cantPreguntas.toDouble())*100
                }else{
                    resultado = 0.0
                }
                binding.edtResultadoAcumulado.setText(resultado.toString() + "%")




            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FB", "Error: " + error.message)
            }

        }

        val dbReferenceFiltrado = database.getReference("tests").orderByChild("userId").equalTo(userId)
        dbReferenceFiltrado.addValueEventListener(testItemListener)


//        cantidadTests = listTestsAcumuladoUsuario.size/5
//        //cantidadTests = cantidadTests/5
//        binding.edtCantidadTestsRealizados.setText(cantidadTests.toString())


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


    ////////////////////////////////




}