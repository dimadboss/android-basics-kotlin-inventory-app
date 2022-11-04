/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.inventory

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.inventory.data.EncSharedPreferences
import com.example.inventory.data.Preferences
import com.example.inventory.databinding.FragmentPreferencesBinding

/**
 * Fragment to add or update an item in the Inventory database.
 */
class PreferencesFragment : Fragment() {
    private val viewModel: InventoryViewModel by activityViewModels {
        InventoryViewModelFactory(
            (activity?.application as InventoryApplication).database.itemDao()
        )
    }
    lateinit var item: Preferences

    private lateinit var encSharedPreferences: EncSharedPreferences

    private fun bind(p: Preferences) {
        binding.apply {
            defaultProviderEmail.setText(p.defaultProviderEmail, TextView.BufferType.SPANNABLE)
            defaultProviderName.setText(p.defaultProviderName, TextView.BufferType.SPANNABLE)
            defaultProviderPhone.setText(p.defaultProviderPhone, TextView.BufferType.SPANNABLE)

            hideSensitiveDataSwitch.isChecked = p.hideSensitiveData
            preventSharingSwitch.isChecked = p.preventSharing
            useDefaultValuesSwitch.isChecked = p.useDefaultValues

            savePreferencesAction.setOnClickListener { savePreferences() }
        }
    }

    // Binding object instance corresponding to the fragment_add_item.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment
    private var _binding: FragmentPreferencesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        encSharedPreferences = viewModel.encSharedPreferences
        encSharedPreferences.initEncryptedSharedPreferences(requireContext())
        _binding = FragmentPreferencesBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun getErrString(e: Int?): String? {
        return if (e == null) null else getString(e)
    }

    private fun isEntryValid(): Boolean {
        val emailError = viewModel.checkEmailValid(binding.defaultProviderEmail.text.toString())
        binding.defaultProviderEmail.error = getErrString(emailError)

        val phoneError = viewModel.checkPhoneValid(binding.defaultProviderPhone.text.toString())
        binding.defaultProviderPhone.error = getErrString(phoneError)

        val nameError = viewModel.checkNameValid(binding.defaultProviderName.text.toString())
        binding.defaultProviderName.error = getErrString(nameError)

        return emailError == null && phoneError == null && nameError == null
    }

    private fun savePreferences() {
        if (!isEntryValid()) {
            return
        }

        item = Preferences(
            binding.defaultProviderEmail.text.toString(),
            binding.defaultProviderPhone.text.toString(),
            binding.defaultProviderName.text.toString(),
            binding.hideSensitiveDataSwitch.isChecked,
            binding.useDefaultValuesSwitch.isChecked,
            binding.preventSharingSwitch.isChecked,
        )

        encSharedPreferences.setPreferences(
            item
        )
    }


    /**
     * Called before fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        // Hide keyboard.
        val inputMethodManager =
            requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        item = encSharedPreferences.getPreferences()
        bind(item)
    }


}
