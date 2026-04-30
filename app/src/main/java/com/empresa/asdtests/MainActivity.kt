package com.empresa.asdtests


import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.empresa.asdtests.databinding.ActivityMainBinding
import com.empresa.asdtests.model.Pregunta
import com.empresa.asdtests.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import java.util.regex.Matcher
import java.util.regex.Pattern
import com.google.firebase.firestore.QueryDocumentSnapshot

import com.google.firebase.firestore.QuerySnapshot

import androidx.annotation.NonNull

import com.google.android.gms.tasks.OnCompleteListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    //binding
    private lateinit var binding : ActivityMainBinding

    //firebase auth
    private lateinit var auth: FirebaseAuth

//    //firebase database
//    val database = Firebase.database
//    val dbReferenceUsuarios = database.getReference("usuarios")

    //firestore
    private val db = FirebaseFirestore.getInstance()


    //otras variables
    private lateinit var listaUsuarios: ArrayList<Usuario>
    private lateinit var role: String




    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        //setContentView(R.layout.activity_main)
        setContentView(binding.root)

        //inicializacion firebase
        Firebase.initialize(this)
        auth = Firebase.auth

        role = "Unknown"

        auth.signOut()

        binding.progressBarMainActivity.visibility = View.GONE






//        binding.btnCorutina.setOnClickListener {
//
//            Log.e("FG", "Undió boton")
//            Toast.makeText(this, "Aca undió", Toast.LENGTH_SHORT).show()
//
//            CoroutineScope(Dispatchers.IO).launch{
//                probarCorutina()
//            }
//
//        }





        val currentUser = auth.currentUser
        if(currentUser != null){

            Log.e("FG", "El usuario con uId: " + currentUser?.uid.toString() + "tiene el role: " + role)
            Toast.makeText(this, "Botón presionado currentuser", Toast.LENGTH_SHORT).show()

            db.collection("users").document(currentUser?.uid.toString()).get().addOnSuccessListener {
                role = (it.get("role") as String?).toString()
                Log.e("FG", "Ingreso a buscar en DB funcion login.  Role: " + role)
                verActivityUsuarioLogueado(role)
            }



        }else{


            binding.btnSignIn.setOnClickListener {

                Log.e("FB", "Botón Sign In presionado")
                Toast.makeText(this, "Botón presionado", Toast.LENGTH_SHORT).show()

                if(binding.etUsername.text.toString().length < 1 ){
                    binding.etUsername.setError("Este campo no puede ser vácio")
                }
                if(binding.etPassword.text.toString().length < 1){
                    binding.etPassword.setError("Este campo no puede ser vácio")
                }

                if(validarCampo(binding.etUsername, "email")) {

                    binding.progressBarMainActivity.visibility = View.VISIBLE

                    //login
                    login(binding.etUsername.text.toString(), binding.etPassword.text.toString())



//                    val docRef = db.collection("users").document(currentUser?.uid.toString())
//                    docRef.addSnapshotListener { snapshot, e ->
//                        if (e != null) {
//                            Log.e(TAG, "Listen failed.", e)
//                            return@addSnapshotListener
//                        }
//
//                        if (snapshot != null && snapshot.exists()) {
//                            Log.e(TAG, "Current data: ${snapshot.data}")
//
//                            role = (snapshot.get("role") as String?).toString()
//
//
//                        } else {
//                            Log.e(TAG, "Current data: null")
//                        }
//                    }
//
//                    CoroutineScope(Dispatchers.IO).launch{
//                        probarCorutina()
//                    }
//                    Log.e("FG", "Rol desde db: "+role)




                }else{
                    binding.etUsername.setError("No has ingresado un correo válido")
                }


            }






            binding.btnCreateAccount.setOnClickListener {

                verActivityCreateAccount()

            }


//            binding.btnCargarRole.setOnClickListener{
//                val currentUser = auth.currentUser
//                db.collection("users").document(currentUser?.uid.toString()).get().addOnSuccessListener {
//                    role = (it.get("role") as String?).toString()
//                    Log.e("FG", "Ingreso a buscar en DB funcion login.  Role: " + role)
//                }
//
//
//                Log.e("FG", "el role obtenido desde el boton es: " + role)
//                binding.tvRole.text = role
//
//            }


//            binding.btnCargarActivity.setOnClickListener{
//
//                verActivityUsuarioLogueado(role);
//
//            }




        }



    }

//    suspend fun probarCorutina() {
//        return withContext(Dispatchers.IO){
//
//            for(i in 1 .. 10000 ){
//
//                println("probando: $i")
//
//            }
//
//
//        }
//
//
//
//
//    }


    //Otras funciones
//
//
//    fun postItem(item: Item) {
//        launch {
//            val token = preparePost()
//            val post = submitPost(token, item)
//            processPost(post)
//        }
//    }
//
//    suspend fun preparePost(): Token {
//        // makes a request and suspends the coroutine
//        return suspendCoroutine { /* ... */ }
//    }
//
//
//





    private fun login(email: String, password: String){

        Log.e("FG", "entró a loguearse")
        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.e("FB", "Login exitoso")
                    Log.d("LOG TAG", "signInWithEmail:success")
                    val user = auth.currentUser

                    db.collection("users").document(user?.uid.toString()).get().addOnSuccessListener {
                        role = (it.get("role") as String?).toString()
                        Log.e("FG", "Ingreso a buscar en DB funcion login.  Role: " + role)
                        verActivityUsuarioLogueado(role);
                    }.addOnFailureListener {
                        Log.e("FB", "Error obteniendo role: " + it.message)
                    }





                } else {
                    binding.progressBarMainActivity.visibility = View.GONE
                    // If sign in fails, display a message to the user.
                    Log.e("FB", "Error login: " + task.exception?.message)
                    Log.w("LOG TAG", "signInWithEmail:failure", task.exception)
//                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    Toast.makeText(baseContext, task.exception?.message,
                        Toast.LENGTH_LONG).show()
                    //updateUI(null)
                }
            }



    }


    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            //var role = roleUsuarioLogueado(currentUser?.uid.toString())

//            db.collection("users").document(user?.uid.toString()).get().addOnSuccessListener {
//                role = (it.get("role") as String?).toString()
//                Log.e("FG", "Ingreso a buscar en DB funcion login.  Role: " + role)
//                verActivityUsuarioLogueado(role);
//            }

        }
    }




    private fun verActivityUsuarioLogueado(role: String) {

        val intent = Intent(this, ActivityPantallaPrincipal::class.java)
        intent.putExtra("role", role)
        this.startActivity(intent)
        finish()
    }

    private fun verActivityCreateAccount(){
        val intent = Intent(this, ActivityCreateAccount::class.java)
        this.startActivity(intent)
    }


    private fun validarCampo (campo: EditText, tipo: String): Boolean {
        var pattern: Pattern
        var matcher: Matcher
        var patron: String
        when (tipo){
            "nombre" -> patron = "^[a-zA-Z]{3,}$"
            "email" -> patron = Patterns.EMAIL_ADDRESS.toString()
            "clave" -> patron = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&.])[A-Za-z\\d@$!%*#?&.]{8,}$"
            "telefono" -> patron = "^[0-9]{7,15}$"
            else -> patron = "^{1,}$"
        }

        pattern = Pattern.compile(patron)

        matcher = pattern.matcher(campo.text.toString())
        return matcher.matches();
    }







//    private fun obtenerRol(userId: String):String{
//        var rol: String
//        rol = "Unknown"
//
//        Log.e("FG", "el uid recibido es: "+userId)
//
//        db.collection("users").document(userId).get().addOnSuccessListener {
//            Log.e("FG", "Ingreso a buscar en DB")
//            rol = (it.get("role") as String?).toString()
//        }
//
//        return rol
//
//    }




}