package com.app.gong4

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavDirections
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.gong4.DTO.QnaItem
import com.app.gong4.DTO.ResponseQnaListBody
import com.app.gong4.DTO.UserInfo
import com.app.gong4.api.RequestServer
import com.app.gong4.databinding.FragmentMyPageBinding
import com.app.gong4.databinding.FragmentMyPageQnaBinding
import com.app.gong4.util.CommonService
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyPageQnaFragment : Fragment() {
    private lateinit var binding: FragmentMyPageQnaBinding

    private val args by navArgs<MyPageQnaFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPageQnaBinding.inflate(inflater, container, false)
        val mainActivity = activity as MainActivity
        mainActivity.hideToolbar(false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getMyPageInfo(args.userInfo)

        getQnaList()
    }

    override fun onStop() {
        super.onStop()
        val mainActivity = activity as MainActivity
        mainActivity.hideToolbar(true)
    }

    fun getMyPageInfo(info: UserInfo){
        val imgPath = CommonService().getImageGlide(info.imgPath)
        Glide.with(requireContext()).load(imgPath).into(binding.profileImageview)

        val studyHour = info.totalStudyTime.substring(0,2)
        val studyMinute = info.totalStudyTime.substring(3,5)
        binding.profileNameTextview.text = info.nickname
        binding.profileTimeTextview.text = String.format(resources.getString(R.string.mypage_study_time),studyHour,studyMinute)
        binding.profileLevelTextview.text = String.format(resources.getString(R.string.mypage_study_level),info.level)
        binding.profilePercentageTextview.text = String.format(resources.getString(R.string.mypage_study_percentage),info.percentage.toInt())

    }

    fun getQnaList(){
        RequestServer.qnaService.getMyQnaList().enqueue(object : Callback<ResponseQnaListBody>{
            override fun onResponse(
                call: Call<ResponseQnaListBody>,
                response: Response<ResponseQnaListBody>
            ) {
                val qnaList = response.body()!!.data.questionList
                setAdapter(qnaList)
            }

            override fun onFailure(call: Call<ResponseQnaListBody>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    fun setAdapter(list: List<QnaItem>) {
        val adapter = QnaListApdater(list as ArrayList<QnaItem>, object : onMoveAdapterListener {
            override fun onMoveQnaDetail(id: Int): NavDirections {
                return MyPageQnaFragmentDirections.actionMyPageQnaFragmentToGroupQnaDetailFragment(id)
            }
        })
        binding.qnaRecylcerview.adapter = adapter
        binding.qnaRecylcerview.layoutManager = LinearLayoutManager(context)
        adapter.notifyDataSetChanged()
        binding.qnaRecylcerview.setHasFixedSize(true)
    }

}