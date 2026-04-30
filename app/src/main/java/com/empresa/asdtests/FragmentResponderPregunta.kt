package com.empresa.asdtests

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.empresa.asdtests.databinding.FragmentResponderPreguntaBinding
import com.empresa.asdtests.model.Pregunta
import com.empresa.asdtests.model.Test
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import java.util.*


class FragmentResponderPregunta : Fragment() {

    private var _binding: FragmentResponderPreguntaBinding? = null
    private val binding get() = _binding!!

    val database = Firebase.database
    val dbReferencePreguntasTest = database.getReference("tests")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        _binding = FragmentResponderPreguntaBinding.inflate(inflater, container, false)

        //se inicializa (si estamos en una actividad el contexto es this
        Firebase.initialize(requireActivity())


        val testUniqueId =  requireArguments().getString("testUniqueId")
        val testId =  requireArguments().getString("testId")
        val userId =  requireArguments().getString("userId")
        val preguntaId =  requireArguments().getString("preguntaId")
        val preguntaArea =  requireArguments().getString("preguntaArea")
        val preguntaTexto =  requireArguments().getString("preguntaTexto")
        val preguntaOpcion1 =  requireArguments().getString("preguntaOpcion1")
        val preguntaOpcion2 =  requireArguments().getString("preguntaOpcion2")
        val preguntaRespuesta =  requireArguments().getString("preguntaRespuesta")



        binding.edtTestUniqueId.setText(testUniqueId)
        binding.edtTestId.setText(testId)
        binding.edtUserId.setText(userId)
        binding.edtPreguntaId.setText(preguntaId)
        binding.tvArea.setText(preguntaArea)
        binding.edtTestPreguntaTexto.setText(preguntaTexto)
        binding.tvOpcion1.setText(preguntaOpcion1)
        binding.tvOpcion2.setText(preguntaOpcion2)
        binding.tvRespuesta.setText(preguntaRespuesta)


        binding.btnEnviar.setOnClickListener {
            responderPregunta()

        }


        binding.chk1.setOnClickListener {
            if(binding.chk1.isChecked) {
                binding.chk2.setEnabled(false)
                binding.chk3.setEnabled(false)
            }else
            {
                binding.chk2.setEnabled(true)
                binding.chk3.setEnabled(true)
            }
        }

        binding.chk2.setOnClickListener {
            if(binding.chk2.isChecked) {
                binding.chk1.setEnabled(false)
                binding.chk3.setEnabled(false)
            }else
            {
                binding.chk1.setEnabled(true)
                binding.chk3.setEnabled(true)
            }
        }


        binding.chk3.setOnClickListener {
            if(binding.chk3.isChecked) {
                binding.chk1.setEnabled(false)
                binding.chk2.setEnabled(false)
            }else
            {
                binding.chk1.setEnabled(true)
                binding.chk2.setEnabled(true)
            }
        }



        binding.btnCancelar.setOnClickListener {
            salir()
        }


        return binding.root


    }

//funciones
        private fun responderPregunta() {

            var respuestaCorrecta = 0
            if((!binding.chk1.isChecked and !binding.chk2.isChecked and !binding.chk3.isChecked)){

                Toast.makeText(requireContext(), "Error. Selecciona una respuesta", Toast.LENGTH_SHORT).show()
                binding.chk1.setError("Error debes escoger una y solo una respuesta")
                binding.chk2.setError("Error debes escoger una y solo una respuesta")
                binding.chk3.setError("Error debes escoger una y solo una respuesta")

            }else {

                if (binding.chk1.isChecked) {
                    Log.e("FG", "la respuesta es INcorrecta")
                    //salir()
                }

                if (binding.chk2.isChecked) {
                    Log.e("FG", "la respuesta es correcta")
                    respuestaCorrecta = 1
                //salir()
                }

                //actualizar test
                var test = Test(
                    binding.edtTestUniqueId.text.toString(),
                    binding.edtTestId.text.toString(),
                    binding.edtUserId.text.toString(),
                    binding.edtPreguntaId.text.toString(),
                    binding.edtTestPreguntaArea.text.toString(),
                    binding.edtTestPreguntaTexto.text.toString(),
                respuestaCorrecta
                )

                dbReferencePreguntasTest.child(test.id).setValue(test)

                Log.e("FG", "actualizar test " + test)
                salir()

            }


    }





    private fun salir(){
        val lvPreguntasTest = activity?.findViewById<ListView>( R.id.lvPreguntasTest )
        lvPreguntasTest?.visibility = View.VISIBLE

        activity?.supportFragmentManager?.beginTransaction()
            ?.remove( this )
            ?.commit()
    }
//fin funciones









}