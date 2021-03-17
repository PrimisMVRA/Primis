package com.example.hackaton.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v4.app.Fragment
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.content.SharedPreferences
import android.os.storage.StorageManager
import android.support.design.widget.TextInputEditText
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import com.example.hackaton.R
import java.lang.StringBuilder
import java.security.KeyStore
import java.security.Signature
import java.util.*

class SettingsFragment : Fragment() {
    private val alias = "KEYEKEY"
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var textEditCardNum: TextInputEditText
    private lateinit var textEditCardHolder: TextInputEditText
    private lateinit var textEditCardValid: TextInputEditText
    private lateinit var textEditCardCvv: TextInputEditText
    private lateinit var switchRequest: Switch
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        settingsViewModel =
                ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(SettingsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_settings, container, false)
        textEditCardNum = root.findViewById(R.id.setsEditTextCardNum)
        textEditCardNum.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                var str = s.toString()
                if (str.length == 4 || str.length == 9 || str.length == 14){
                    textEditCardNum.setText(StringBuilder(str).append(' ').toString())
                }
                if (str.length < 19){
                    textEditCardNum.setError(getString(R.string.app_err_card_num),resources.getDrawable(R.drawable.ic_baseline_error_outline_24, context!!.theme))
                }
            }
        })

        /*(val ks : KeyStore = KeyStore.getInstance("HackatonStorage").apply { load(null) }
        val aliases : Enumeration<String> = ks.aliases()
        val entry: KeyStore.PrivateKeyEntry = ks.getEntry(alias,null) as KeyStore.PrivateKeyEntry
        val signature : ByteArray = Signature.getInstance("SHA256withECDSA").run{
            initVerify(entry.certificate)
            update(data)
            verify(signature)
        }*/
        textEditCardHolder = root.findViewById(R.id.setsEditTextCardHolder)
        textEditCardValid = root.findViewById(R.id.setsEditTextCardValidThru)
        textEditCardCvv = root.findViewById(R.id.setsEditTextCardCvv)

        switchRequest = root.findViewById(R.id.setsSwRequest)
        switchRequest.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                switchRequest.setTextColor(resources.getColor(R.color.switch_color_on, context!!.theme))
            } else{
                switchRequest.setTextColor(resources.getColor(R.color.switch_color_off, context!!.theme))
            }
        }

        val sharedPrefs = activity?.getPreferences(Context.MODE_PRIVATE)
        tryLoadPrefsToInput(sharedPrefs)

        val buttonSave : Button = root.findViewById(R.id.sets_b_save)
        buttonSave.setOnClickListener {
            sharedPrefs?.apply {
                this.edit()
                        .putString("CNum", textEditCardNum.text.toString())
                        .putString("CHolder", textEditCardHolder.text.toString())
                        .putString("CValid", textEditCardValid.text.toString())
                        .putString("CCvv", textEditCardCvv.text.toString())
                        .putInt("isReq", if (switchRequest.isChecked) 1 else 0)
                        .apply()
            }

        }
        val buttonCancel : Button=root.findViewById(R.id.sets_b_cancel)
        buttonCancel.setOnClickListener {
            tryLoadPrefsToInput(sharedPrefs)
        }

        /*settingsViewModel.text.observe(viewLifecycleOwner, Observer {
            textEditCardNum.setText(it)
        })*/
        return root
    }

    private fun tryLoadPrefsToInput(prefs : SharedPreferences?){
        prefs?.apply {
                textEditCardNum.setText(this.getString("CNum", ""))
                textEditCardHolder.setText(this.getString("CHolder", ""))
                textEditCardValid.setText(this.getString("CValid", ""))
                textEditCardCvv.setText(this.getString("CCvv", ""))
                switchRequest.isChecked = this.getInt("isReq",0) == 1
        }
    }
}