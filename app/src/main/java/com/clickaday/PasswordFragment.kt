package com.clickaday

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment


class PasswordFragment : Fragment() {

    lateinit var passwordTxt: TextView
    private lateinit var backgroundView: View
    private lateinit var deleteAll: Button
    private lateinit var deleteOne: ImageButton
    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var button4: Button
    private lateinit var button5: Button
    private lateinit var button6: Button
    private lateinit var button7: Button
    private lateinit var button8: Button
    private lateinit var button9: Button
    private lateinit var button0: Button
    private lateinit var buttonCancel: Button
    private lateinit var buttonOK: Button

    private var isToSave: Boolean = false

    private val maskUpdateDelayMillis = 400L //ms

    private val updateTextRunnable = Runnable {
        val maskedText = passwordTxt.text.toString().replace(Regex("[0-9]"), "⬤")
        passwordTxt.text = maskedText
    }

    companion object {
        const val TAG_FRAG = "TAG_PASSWORD_FRAGMENT"
        const val TAG_BOOL = "IS_TO_SAVE"
        const val MAX_PASSWORD_LENGTH = 20
        const val MIN_PASSWORD_LENGTH = 4
        const val RESULT_KEY = "RESULT_KEY_PASSWORD"
        const val RESPOND_VAL_KEY = "RESPOND_VAL_KEY_PASSWORD"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.password_enter_layout, container, false)
        isToSave = arguments?.getBoolean(TAG_BOOL) ?: false

        initializeView(view)
        initializeListener()

        return view
    }

    private fun initializeView(v: View) {
        //initialise view
        passwordTxt = v.findViewById(R.id.textViewPassword)
        backgroundView = v.findViewById(R.id.img_background_password)
        deleteAll = v.findViewById(R.id.delete_all_number_password)
        deleteOne = v.findViewById(R.id.delete_number_password)
        button1 = v.findViewById(R.id.button1)
        button2 = v.findViewById(R.id.button2)
        button3 = v.findViewById(R.id.button3)
        button4 = v.findViewById(R.id.button4)
        button5 = v.findViewById(R.id.button5)
        button6 = v.findViewById(R.id.button6)
        button7 = v.findViewById(R.id.button7)
        button8 = v.findViewById(R.id.button8)
        button9 = v.findViewById(R.id.button9)
        button0 = v.findViewById(R.id.button0)
        buttonCancel = v.findViewById(R.id.cancel_btn_password)
        buttonOK = v.findViewById(R.id.ok_btn_password)
    }

    private fun initializeListener() {
        val scaleAnimation =
            AnimationUtils.loadAnimation(requireContext(), R.anim.password_keyboard_clic)

        button1.setOnClickListener {
            addToTextView(1)
            button1.startAnimation(scaleAnimation)
        }
        button2.setOnClickListener {
            addToTextView(2)
            button2.startAnimation(scaleAnimation)
        }
        button3.setOnClickListener {
            addToTextView(3)
            button3.startAnimation(scaleAnimation)
        }
        button4.setOnClickListener {
            addToTextView(4)
            button4.startAnimation(scaleAnimation)
        }
        button5.setOnClickListener {
            addToTextView(5)
            button5.startAnimation(scaleAnimation)
        }
        button6.setOnClickListener {
            addToTextView(6)
            button6.startAnimation(scaleAnimation)
        }
        button7.setOnClickListener {
            addToTextView(7)
            button7.startAnimation(scaleAnimation)
        }
        button8.setOnClickListener {
            addToTextView(8)
            button8.startAnimation(scaleAnimation)
        }
        button9.setOnClickListener {
            addToTextView(9)
            button9.startAnimation(scaleAnimation)
        }
        button0.setOnClickListener {
            addToTextView(0)
            button0.startAnimation(scaleAnimation)
        }
        deleteAll.setOnClickListener {
            passwordTxt.text = ""
            deleteAll.startAnimation(scaleAnimation)
        }

        deleteOne.setOnClickListener {
            var txt = passwordTxt.text.toString()
            if (txt.isNotEmpty()) {
                txt = txt.substring(0, txt.length - 1)
            }
            passwordTxt.text = txt
            deleteOne.startAnimation(scaleAnimation)
        }

        buttonCancel.setOnClickListener {
            returnVal(false)
        }
        buttonOK.setOnClickListener {
            val enteredPassword = passwordTxt.text.toString()
            if (isToSave) {
                if ((enteredPassword.length >= MIN_PASSWORD_LENGTH) and (enteredPassword.length <= MAX_PASSWORD_LENGTH)) {
                    //save password
                    PreferencesTools.savePrefStrCrypt(
                        requireContext(),
                        enteredPassword,
                        PreferencesTools.PREF_PASSWORD_VALUE_CRYPT
                    )
                    //save is password
                    PreferencesTools.savePrefBool(
                        requireContext(),
                        true,
                        PreferencesTools.PREF_PASSWORD
                    )
                    Toast.makeText(requireContext(), "Password saved", Toast.LENGTH_SHORT).show()
                    returnVal(true)

                } else {
                    //save is password
                    PreferencesTools.savePrefBool(
                        requireContext(),
                        false,
                        PreferencesTools.PREF_PASSWORD
                    )
                    Toast.makeText(
                        requireContext(),
                        "Password not saved : Invalid lenght",
                        Toast.LENGTH_SHORT
                    ).show()
                    returnVal(false)
                }

            } else {
                //verify if the password entered is the good one

                if (enteredPassword == PreferencesTools.getPrefStrCrypt(
                        requireContext(),
                        PreferencesTools.PREF_PASSWORD_VALUE_CRYPT
                    )
                ) {
                    //return that was a good value
                    returnVal(true)

                } else {
                    //return that was a bad value
                    Toast.makeText(
                        requireContext(),
                        "Invalid Password",
                        Toast.LENGTH_SHORT
                    ).show()
                    returnVal(false)
                }
            }

        }
        backgroundView.setOnClickListener {
            returnVal(false)
        }


        // Set a TextWatcher to update the text with masked characters
        passwordTxt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Remove any previously scheduled updates
                passwordTxt.removeCallbacks(updateTextRunnable)

                // Schedule a delayed update with the masked characters
                passwordTxt.postDelayed(updateTextRunnable, maskUpdateDelayMillis)
            }


            override fun afterTextChanged(s: Editable?) {
                // No action needed
            }
        })
    }

    private fun closeFrag() {
        USRTools.closeFragmentWithTag(TAG_FRAG, requireActivity().supportFragmentManager)
    }

    private fun returnVal(value: Boolean) {
        val bundle = Bundle().apply { putBoolean(RESPOND_VAL_KEY, value) }
        requireActivity().supportFragmentManager.setFragmentResult(RESULT_KEY, bundle)
        closeFrag()
    }


    private fun addToTextView(number: Int) {
        val currentText = passwordTxt.text.toString()

        if (currentText.length < MAX_PASSWORD_LENGTH) {
            passwordTxt.text = if (currentText.isNotEmpty()) {
                StringBuilder(currentText).append(number).toString()
            } else {
                number.toString()
            }
        } else {
            Toast.makeText(requireContext(), "Too long", Toast.LENGTH_SHORT).show()
        }
    }

}

