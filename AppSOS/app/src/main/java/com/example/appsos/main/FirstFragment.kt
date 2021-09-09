package com.example.appsos.main

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.appsos.Dialog.DialogAddNumberPhone
import com.example.appsos.R
import com.example.appsos.dataObject.MyDatabase
import com.example.appsos.dataObject.address
import com.example.appsos.dataObject.listMessage
import com.example.appsos.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private val namePrefer = "FirstAccess"

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private lateinit var swipeUpLinear : LinearLayout
    private lateinit var dialog : DialogAddNumberPhone
    private var checkFirstAccess : Boolean? = null
    //mục đích khai báo db là để query listMessage ngay lần đầu tiên vào app
    private lateinit var db : MyDatabase
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        db = MyDatabase(requireContext())
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }
    //Lần đầu đăng nhập thì sẽ xuất hiện mục hướng dẫn đầu tiên
    private fun intentTutorial() {
        getStatus()
        if(checkFirstAccess == true) {
            findNavController().navigate(R.id.action_FirstFragment_to_tutorial)
        }
        status()
    }
    private fun getStatus() {
        val preference = this.requireActivity().getSharedPreferences(namePrefer, Context.MODE_PRIVATE)
        checkFirstAccess = preference.getBoolean("firstAccess",true)
    }
    private fun status() {
        val preference = this.requireActivity().getSharedPreferences(namePrefer, Context.MODE_PRIVATE)
        val editor = preference.edit()
        editor.putBoolean("firstAccess",false)
        // kích nút "Xong" ở Hướng Dẫn sẽ trở về home khi firstAccess2 == true, nếu firstAccess2 == false thì quay lại menu
        //check = true -> firstAccess = false vs firstAccess2 = check, quay lại fragment1 thì check = false -> firstAccess2 = check
        editor.putBoolean("firstAccess2",checkFirstAccess!!)
        editor.apply()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeUpLinear = view.findViewById(R.id.swipe_up)
        init()
        sOS()
        showLocation()
        binding.apply {
            intentTutorial()
            binding.imgSwipeUp.setOnClickListener {
                reInit()
                val move = AnimationUtils.loadAnimation(context, R.anim.keolen)
                swipeUpLinear.startAnimation(move)
                val countDownTimer = object : CountDownTimer(1000, 10) {
                    override fun onTick(l: Long) {}
                    override fun onFinish() {
                        findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
                    }
                }.start()
                com.example.appsos.dataObject.check = false
            }
            if(com.example.appsos.dataObject.check) {
                reInit()
                val move = AnimationUtils.loadAnimation(context, R.anim.keoxuong)
                swipeUpLinear.startAnimation(move)
                val countDownTimer = object : CountDownTimer(1000, 1000) {
                    override fun onTick(l: Long) {}
                    override fun onFinish() {
                        init()
                    }
                }.start()
                com.example.appsos.dataObject.check = false
            }
        }
    }
    private fun sOS() {
        binding.btnSOS.setOnClickListener {
            if(listMessage.isNotEmpty())
               findNavController().navigate(R.id.action_FirstFragment_to_checkCancelSend)
            else {
                dialog = DialogAddNumberPhone("Danh Sách Nhận Tin KHẨN CẤP Rỗng, Vui Lòng Vào Mục Thêm SĐT Để Tạo Danh Sách Nhận Tin KHẨN CẤP")
                dialog.show(requireFragmentManager(),"123")
            }

        }
    }

    override fun onResume() {
        super.onResume()
        showLocation()
    }
    private fun showLocation() {
        if(address.size < 1)
            binding.txtShow.text = getString(R.string.noConnectInternet)
        else {
            //Loại "Tôi Đang Gặp Nạn: "
                binding.txtShow.text = ""
            for (i in 1 until address.size)
                binding.txtShow.append(address[i])
        }
    }
    private fun init() {
        db.query()
        Glide.with(this).load(R.drawable.len).into(binding.imgSwipeUp)
        binding.imgSwipeUp.visibility = View.VISIBLE
        swipeUpLinear.visibility = View.INVISIBLE
    }
    private fun reInit(){
        binding.imgSwipeUp.visibility = View.INVISIBLE
        swipeUpLinear.visibility = View.VISIBLE
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}