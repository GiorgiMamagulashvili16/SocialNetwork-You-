package com.example.you.ui.signupfragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.you.databinding.RegistrationFragmentBinding
import com.example.you.util.Resource

class Registration : Fragment() {

    private lateinit  var  viewModel: RegistrationViewModel
    private var _binding: RegistrationFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = RegistrationFragmentBinding.inflate(layoutInflater, container, false)
        init()
        return binding.root
    }

    private fun init(){
        viewModel = RegistrationViewModel()
        binding.Register.setOnClickListener{
            saveAccount()
        }

        binding.PickedPicture.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 456)
        }
        observe()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        binding.PickedPicture.setImageURI(data?.data)
    }

    private fun saveAccount(){
        val email = binding.Email.text.toString()
        val pass = binding.Password.text.toString()
        val repeatPass = binding.repeatPassword.text.toString()
        val userName = binding.UserName.text.toString()
        val status = "NONE"
        if(email.isEmpty() && pass.isEmpty() && repeatPass.isEmpty() && userName.isEmpty()){
            Toast.makeText(requireContext(), "გთხოვთ შეავსეთ ყველა ველი", Toast.LENGTH_SHORT).show()
        }else{
            if(pass == repeatPass){
            viewModel.register(
                email,
                pass,
                userName,
                status
            )
            Toast.makeText(requireContext(), viewModel.registerResponse.toString(), Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(requireContext(), "შეყვანილი პაროლი არ ემთხვევა წინას", Toast.LENGTH_SHORT).show()
            }
        }


    }
    private fun observe() {
        viewModel.registerResponse.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    Log.d("RESPONSE", "${it.data}")
                }
                is Resource.Error -> {
                    Log.d("RESPONSE ", "${it.errorMessage}")
                }
            }
        })
    }
}