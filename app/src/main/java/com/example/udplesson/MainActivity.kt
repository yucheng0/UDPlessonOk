package com.example.udplesson


import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.net.DatagramPacket


class MainActivity : AppCompatActivity(), View.OnClickListener {
    val TAG: String = "myTag"
    val LOCAL_PORT: Int = 8009 // 12348 //8009
    val DEST_PORT: Int = 8008
    lateinit var mRecvBtn: Button
    lateinit var mCloseBtn: Button
    lateinit var mScrollView: ScrollView
    lateinit var mLogTx: TextView
    var isClosed: Boolean = false
    lateinit var mUDPBroadCaster: UDPBroadcaster

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mUDPBroadCaster = UDPBroadcaster(this)
    initView()
     initEvent()
    }


    private fun initEvent() {
        mRecvBtn.setOnClickListener(this)
        mCloseBtn.setOnClickListener(this)
    }

    private fun initView() {
      mRecvBtn = findViewById(R.id.btn_receive) as Button
     mCloseBtn = findViewById(R.id.btn_close) as Button
        mScrollView = findViewById(R.id.scrollView) as ScrollView
       mLogTx = findViewById(R.id.log) as TextView

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_receive -> recvUDPBroadcast()
            R.id.btn_close -> cancelRecv()
        }
    }

    private fun cancelRecv() {
        isClosed = true
    }

    private fun recvUDPBroadcast() {
        isClosed = false
        mUDPBroadCaster.open(LOCAL_PORT, DEST_PORT)     //設定socket 用
        var buffer: ByteArray = kotlin.ByteArray(1024)  //定義一定抓取的容量大小
        val packet = DatagramPacket(buffer, buffer.size)     // 定義容器packet 資料抓到後放在buffer內
        println ("packet = $packet")

        Thread(Runnable {
            while (!isClosed) {
                try {
                    Thread.sleep(500) //500ms延时
                } catch (e: Exception) {
                    println ("slfdsflsdlfsdflsdfsdfdsfsdf")
                    e.printStackTrace()
                }
                println ("500ms到")
               val a = mUDPBroadCaster.recvPacket(buffer) //接收广播 (一直在等值出現呢?) 卡死在這
               println (a)
               val data: String = String(packet.data)
              addLog("$TAG data: $data")
               addLog("$TAG addr: ${packet.address}")
             addLog("$TAG port: ${packet.port}")
            }
            mUDPBroadCaster.close() //退出接收广播
        }).start()              //再啟動一次
    }

    private fun addLog(log: String) {
        var mLog: String = log
        if (mLog.endsWith("\n").not()) {
            mLog += "\n"
        }
        mScrollView.post(Runnable {
            mLogTx.append(mLog)
            mScrollView.fullScroll(ScrollView.FOCUS_DOWN)
        })
    }
}