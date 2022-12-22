package com.app.gong4

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.gong4.DTO.QnaItem
import com.app.gong4.DTO.ResponseQnaListBody
import com.app.gong4.api.RequestServer
import com.app.gong4.databinding.FragmentGroupQnaListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

class GroupQnaListFragment : Fragment() {
    private lateinit var binding: FragmentGroupQnaListBinding
    private val args by navArgs<GroupQnaListFragmentArgs>()
    private var list : ArrayList<QnaItem> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGroupQnaListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getQnaList(args.pid)
    }

    private fun getQnaList(groupUID:Int){
        RequestServer.qnaService.getQnaList(groupUID).enqueue(object :
            Callback<ResponseQnaListBody> {
            override fun onResponse(
                call: Call<ResponseQnaListBody>,
                response: Response<ResponseQnaListBody>
            ) {
                list.clear()
                Log.d("list-item",response.body()!!.data!!.questionList.toString())
                list.addAll(response.body()!!.data!!.questionList)
                setAdapter(list)
            }

            override fun onFailure(call: Call<ResponseQnaListBody>, t: Throwable) {

            }

        })
    }

    fun setAdapter(list: List<QnaItem>) {
        val adapter = QnaListApdater(list as ArrayList<QnaItem>, object : onMoveAdapterListener {
            override fun onMoveQnaDetail(id: Int): NavDirections {
                return GroupQnaListFragmentDirections.actionGroupQnaListFragmentToGroupQnaDetailFragment(id)
            }
        })
        binding.peopleRecyclerView.addItemDecoration(DividerItemDecoration(context,1))
        binding.peopleRecyclerView.adapter = adapter
        binding.peopleRecyclerView.layoutManager = LinearLayoutManager(context)
        adapter.notifyDataSetChanged()
        binding.peopleRecyclerView.setHasFixedSize(true)
    }

}
class QnaListApdater(val dataSet: ArrayList<QnaItem>,val listener: onMoveAdapterListener)
    : RecyclerView.Adapter<QnaListApdater.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val qna_title_textView: TextView
        val qna_content_textView: TextView
        val qna_date_textView: TextView
        val qna_status_textView: TextView

        init {
            qna_title_textView = view.findViewById(R.id.title_textview)
            qna_content_textView = view.findViewById(R.id.content_textview)
            qna_date_textView = view.findViewById(R.id.date_textview)
            qna_status_textView = view.findViewById(R.id.status_textview)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.qna_list_item, parent, false)

        return ViewHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.qna_title_textView.text = dataSet[position].title
        holder.qna_content_textView.text = dataSet[position].content
        holder.qna_date_textView.text = convertTimestampToDate(dataSet[position].createdAt)
        holder.qna_status_textView.text = dataSet[position].answerStatus

        if(dataSet[position].answerStatus == "응답 완료"){
            holder.qna_status_textView.setTextColor(ContextCompat.getColor(holder.qna_status_textView.context,R.color.green_03_main))
        }else{
            holder.qna_status_textView.setTextColor(ContextCompat.getColor(holder.qna_status_textView.context,R.color.black03))
        }
        holder.itemView.setOnClickListener { view ->
            val id = dataSet[position].questionUID
            view.findNavController().navigate(listener.onMoveQnaDetail(id))
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    private fun convertTimestampToDate(time: Long): String {
        val sdf = SimpleDateFormat("yy.MM.dd")
        val date = sdf.format(time).toString()
        return date
    }
}

