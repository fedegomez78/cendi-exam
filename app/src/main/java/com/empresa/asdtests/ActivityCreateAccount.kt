package com.empresa.asdtests

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import com.empresa.asdtests.databinding.ActivityCreateAccountBinding
import com.empresa.asdtests.model.Pregunta
import com.empresa.asdtests.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class ActivityCreateAccount : AppCompatActivity() {

    private lateinit var binding : ActivityCreateAccountBinding
    private lateinit var auth: FirebaseAuth

    private val db = FirebaseFirestore.getInstance()
//    val database = Firebase.database
//    val dbReferenceUsuarios = database.getReference("usuarios")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        //setContentView(R.layout.activity_create_account)
        setContentView(binding.root)

        //inicializar firebase firestore
        Firebase.initialize(this)

        auth = Firebase.auth

        binding.btnCrearCuenta.setOnClickListener {

            registrarUsuario()


        }

        binding.btnPoliticaPrivacidad.setOnClickListener {
            verPoliticas()
        }


    }


    private fun registrarUsuario(){
        val email = binding.etEmail.text.toString()
        val pass = binding.etClave.text.toString()



/////////////////validacion de campos/////////////
        var flagValidform: Boolean = false

        if (!binding.cbAceptaTerminos.isChecked){
            Toast.makeText(this, "Debe aceptar terminos y condiciones para poder ingresar", Toast.LENGTH_SHORT).show()
            binding.cbAceptaTerminos.setError("Debe aceptar terminos y condiciones para poder ingresar")
            flagValidform = false
        }else{
            flagValidform = true
        }

        if(!validarCampo(binding.etNombre, "nombre")){
            binding.etNombre.setError("Este campo debe tener más de 3 caracteres y unicamente alfabeticos")
            flagValidform = false
        }
        if(!validarCampo(binding.etApellido, "nombre")){
            binding.etApellido.setError("Este campo debe tener más de 3 caracteres y unicamente alfabeticos")
            flagValidform = false
        }
        if(!validarCampo(binding.etNumTelefono, "telefono")){
            binding.etNumTelefono.setError("Este campo debe tener un número telefonico (mínimo 7 y máximo 15 caracteres númericos)")
            flagValidform = false
        }
        if(!validarCampo(binding.etEmail, "email")){
            binding.etEmail.setError("Este campo debe tener un correo electronico de tipo nombre@dominio")
            flagValidform = false
        }
        if(!validarCampo(binding.etClave, "clave")){
            binding.etClave.setError("La clave debe contener mínimo 8 caracteres alfanumericos y contener al menos: una mayuscula, una minuscula y un caracter especial de estos:  @\$!%*#?&. ")
            flagValidform = false
        }

        //si todo esta validado se procede a insertar a la base de datos y a crear usuario en FireBase
        if(flagValidform) {
            //todo bien (form validado)
            Log.e("FB", "Formulario válido, creando usuario...")

            Toast.makeText(
                this,
                "TODO OK... Insertando usuario en la base de datos",
                Toast.LENGTH_LONG
            ).show()

            ///////////autenticacion con Firebase//////////////////
            auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser

                        Log.e("FB", "user ID: " + user?.uid.toString())

                        guardarDatosUsuario(user?.uid.toString())
                        verActivityUsuarioLogueado()
                    } else {
                        Log.e("FB", "Error al crear usuario: " + task.exception?.message)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }





        }







    }

    private fun guardarDatosUsuario(userId: String) {

        var rolUsuario: String

        if(binding.cbIsAdmin.isChecked){
            rolUsuario = "Admin"
        }else{
            rolUsuario = "User"
        }


        db.collection("users").document(userId).set(
            hashMapOf(
                "email" to binding.etEmail.text.toString(),
                "name" to binding.etNombre.text.toString(),
                "lastname" to binding.etApellido.text.toString(),
                "phone" to binding.etNumTelefono.text.toString(),
                "role" to rolUsuario
                )
        )



    }

    private fun verActivityUsuarioLogueado() {
        val intent = Intent(this, ActivityPantallaPrincipal::class.java)
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

    private fun verPoliticas(){

        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"))
        startActivity(browserIntent)
    }


}