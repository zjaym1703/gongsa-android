package com.app.gong4

import android.text.Editable
import android.text.TextWatcher
import androidx.navigation.findNavController
import com.app.gong4.model.res.ResponseLoginBody
import com.app.gong4.api.RequestServer
import com.app.gong4.databinding.FragmentLoginBinding
import com.app.gong4.model.req.RequestLoginBody
import com.app.gong4.utils.CommonTextWatcher
import com.app.gong4.utils.TokenManager
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern
import javax.inject.Inject

class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    val requestServer = RequestServer

    @Inject
    lateinit var tokenManager: TokenManager

    override fun initView() {
        val mainActivity = activity as MainActivity
        mainActivity.hideBottomNavigationBar(true)

        checkInput()
        goLogin()
        goSignupScreen()
        goFindPasswordScreen()
    }

    override fun onStop() {
        super.onStop()
        val mainActivity = activity as MainActivity
        mainActivity.hideBottomNavigationBar(false)
    }

    //회원가입으로 이동
    fun goSignupScreen(){
        binding.signupTextView.setOnClickListener {
            it.findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }
    }

    //비밀번호 찾기로 이동
    fun goFindPasswordScreen(){
        binding.findPasswordTextView.setOnClickListener {
            it.findNavController().navigate(R.id.action_loginFragment_to_findpasswordFragment)
        }
    }

    // 로그인
    fun goLogin(){
        binding.loginButton.setOnClickListener {
            hideKeyboard(it)
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            requestServer.userService.login(RequestLoginBody(email,password)).enqueue(object :
                Callback<ResponseLoginBody>{
                override fun onResponse(
                    call: Call<ResponseLoginBody>,
                    response: Response<ResponseLoginBody>
                ) {
                    if(response.isSuccessful()){
                        var repos: ResponseLoginBody? = response.body()

                        repos.let { it ->
                            val accessToken = it!!.data.accessToken
                            val refreshToken = it!!.data.refreshToken

                            tokenManager.saveAccessToken(accessToken)
                            tokenManager.saveRefreshToken(refreshToken)
                        }
                        it.findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
                    }else{
                        val error = response.errorBody()!!.string().trimIndent()

                        val result = Gson().fromJson(error, ResponseLoginBody::class.java)
                        resetLoginErrorMsg()
                        showLoginErrorMsg(result.location,result.msg)
                    }
                }

                override fun onFailure(call: Call<ResponseLoginBody>, t: Throwable) {
                    showToastMessage(getString(R.string.server_error_msg))
                }
            }
            )

        }
    }

    fun resetLoginErrorMsg(){
        binding.validPasswordTextView.text = ""
        binding.validEmailTextView.text = ""
    }

    // 로그인 에러메시지 보여주는 함수
    fun showLoginErrorMsg(location : String?, msg:String?){
        if(location == "passwd"){
            binding.validPasswordTextView.text = msg
        }else if(location == "email"){
            binding.validEmailTextView.text = msg
        }
       binding.loginButton.isEnabled = false
    }

    // 입력값 체크
    fun checkInput(){
        binding.emailEditText.addTextChangedListener(CommonTextWatcher(
            onChanged = { _,_,_,_ ->
                binding.validEmailTextView.text = ""
            },
            afterChanged = {
                binding.loginButton.isEnabled = true
            }
        ))
        binding.passwordEditText.addTextChangedListener(CommonTextWatcher(
            onChanged = { _, _, _, _ ->
                binding.validPasswordTextView.text = ""
            },
            afterChanged = {
                binding.loginButton.isEnabled = true
            }
        ))
    }

    fun checkEmail(){
        binding.emailEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(p0.toString()).matches())
                {
                    binding.validEmailTextView.text = String.format(resources.getString(R.string.login_fragment_wrong_answer),"이메일 주소")
                    binding.emailEditText.background = context!!.resources.getDrawable(R.drawable.custom_error_input)
                }
                else
                {
                    binding.validEmailTextView.text = ""
                    binding.emailEditText.background = context!!.resources.getDrawable(R.drawable.custom_input)
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                binding.loginButton.isEnabled = binding.passwordEditText.text.toString() != "" && binding.validEmailTextView.text == ""
            }

        })
    }
    fun checkPassword(){
        binding.passwordEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(!Pattern.matches("^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-zA-Z]).{8,16}$", p0.toString()))
                {
                    binding.validPasswordTextView.text = String.format(resources.getString(R.string.login_fragment_wrong_answer),"비밀번호")
                    binding.passwordEditText.background = context!!.resources.getDrawable(R.drawable.custom_error_input)
                }
                else
                {
                    binding.validPasswordTextView.text = ""
                    binding.passwordEditText.background = context!!.resources.getDrawable(R.drawable.custom_input)

                }
            }

            override fun afterTextChanged(p0: Editable?) {
                binding.loginButton.isEnabled = binding.emailEditText.text.toString() != "" && binding.validPasswordTextView.text == ""
            }

        })
    }
}