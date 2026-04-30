package com.empresa.asdtests

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.empresa.asdtests.databinding.FragmentEditarPreguntaBinding
import com.empresa.asdtests.model.Pregunta
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize


class FragmentEditarPregunta : Fragment() {

    private var _binding: FragmentEditarPreguntaBinding? = null
    private val binding get() = _binding!!

    val database = Firebase.database
    val dbReferencePreguntas = database.getReference("preguntas")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        _binding = FragmentEditarPreguntaBinding.inflate(inflater, container, false)

        //se inicializa (si estamos en una actividad el contexto es this
        Firebase.initialize(requireActivity())

        val id =  requireArguments().getString("id")
        val area =  requireArguments().getString("area")
        val pretexto =  requireArguments().getString("pretexto")
        val opcion1 =  requireArguments().getString("opcion1")
        val opcion2 =  requireArguments().getString("opcion2")
        val respuesta =  requireArguments().getString("respuesta")

        binding.edtId.setText(id)
        binding.edtArea.setText(area)
        binding.edtPreguntaTexto.setText(pretexto)
        binding.edtOpcion1.setText(opcion1)
        binding.edtOpcion2.setText(opcion2)
        binding.edtRespuesta.setText(respuesta)


        binding.btnActualizar.setOnClickListener {
            actualizarPregunta()

        }


        binding.btnCancelar.setOnClickListener {
            salir()
        }


        binding.btnEliminar.setOnClickListener {
            eliminarPregunta(binding.edtId.text.toString())
        }


        return binding.root



    }

//funciones
//private fun eliminarPregunta(idPregunta: Int) {
//    CoroutineScope( Dispatchers.IO ).launch {
//        val database = context?.let { ASDTestsDB.getDataBase(it) }
//        val pelicula = Pregunta(idPregunta, "", "", "", "")
//        database?.preguntaDAO()?.eliminar(pelicula)
//    }
//    salir()

private fun eliminarPregunta(idPregunta: String) {

    dbReferencePreguntas.child(idPregunta).removeValue()
    salir()

}

    private fun actualizarPregunta() {

        var pregunta : Pregunta = Pregunta (
            binding.edtId.text.toString(),
            binding.edtArea.text.toString(),
            binding.edtPreguntaTexto.text.toString(),
            binding.edtOpcion1.text.toString(),
            binding.edtOpcion2.text.toString(),
            binding.edtRespuesta.text.toString())

        dbReferencePreguntas.child(pregunta.id).setValue(pregunta)

        salir()

    }



    private fun salir(){
        val lvPreguntas = activity?.findViewById<ListView>( R.id.lvPreguntas )
        lvPreguntas?.visibility = View.VISIBLE

        activity?.supportFragmentManager?.beginTransaction()
            ?.remove( this )
            ?.commit()
    }
//fin funciones    
    
    
    
    
    
    
    
    
    
}