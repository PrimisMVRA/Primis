package com.example.hackaton.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.MediaSyncEvent
import android.os.Build
import android.os.IBinder
import android.telephony.TelephonyManager
import android.util.Log
import kotlin.concurrent.thread

private const val ACTION_IN = "android.intent.action.PHONE_STATE"
private const val ACTION_OUT = "android.intent.action.NEW_OUTGOING_CALL"
private const val SAMPLE_RATE = 8000
class ListenerService : Service(){

    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val internalBufSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, channelConfig, audioFormat) * 4
    private lateinit var recorder : AudioRecord
    private var isRecording : Boolean = false

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        //Add info to log
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isRecording = false
        val filter = IntentFilter()
        filter.addAction(ACTION_OUT)
        filter.addAction(ACTION_IN)
        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            AudioRecord(MediaRecorder.AudioSource.VOICE_PERFORMANCE,
                    SAMPLE_RATE, channelConfig, audioFormat, internalBufSize)
        } else{
            AudioRecord(MediaRecorder.AudioSource.VOICE_UPLINK,
                    SAMPLE_RATE, channelConfig, audioFormat, internalBufSize)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    inner class CallBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            var state = intent?.extras?.getString(TelephonyManager.EXTRA_STATE)
            if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                isRecording = true;
                recorder.startRecording()
                thread (start = true){
                    try {
                        var buffer = ShortArray(internalBufSize)
                        while (isRecording){
                            recorder.read(buffer, 0, buffer.size)
                            //Отправлять на анализатор тут
                        }
                    }
                    catch (tr : Throwable){
                        Log.e("Record Starting Exception",if (tr.message != null) tr.message!! else "")
                    }
                }
            }
            else if(state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                if (isRecording){
                    recorder.stop()
                    isRecording = false;
                }
            }
        }
    }
}