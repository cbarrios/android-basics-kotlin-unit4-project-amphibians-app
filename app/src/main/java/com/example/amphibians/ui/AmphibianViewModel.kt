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
package com.example.amphibians.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.amphibians.network.Amphibian
import com.example.amphibians.network.AmphibianApi
import com.example.amphibians.ui.AmphibianApiStatus.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class AmphibianApiStatus { LOADING, ERROR, DONE }

class AmphibianViewModel : ViewModel() {

    // TODO: Create properties to represent MutableLiveData and LiveData for the API status
    private val _status = MutableLiveData<AmphibianApiStatus>()
    val status: LiveData<AmphibianApiStatus> get() = _status

    // TODO: Create properties to represent MutableLiveData and LiveData for a list of amphibian objects
    private val _amphibians = MutableLiveData<List<Amphibian>>()
    val amphibians: LiveData<List<Amphibian>> get() = _amphibians

    // TODO: Create properties to represent MutableLiveData and LiveData for a single amphibian object.
    //  This will be used to display the details of an amphibian when a list item is clicked
    private val _amphibian = MutableLiveData<Amphibian>()
    val amphibian: LiveData<Amphibian> get() = _amphibian

    // TODO: Create a function that gets a list of amphibians from the api service and sets the
    //  status via a Coroutine
    fun getAmphibianList() {
        viewModelScope.launch {
            try {
                setStatus(LOADING)
                val response = getAmphibians()
                if (response.isSuccessful) {
                    setAmphibians(response.body()!!)
                    setStatus(DONE)
                } else {
                    Log.d("VM", "Server error: ${response.code()}")
                    error()
                }
            } catch (e: Exception) {
                Log.d("VM", e.message.toString())
                error()
            }
        }
    }

    // Setters
    private fun setStatus(status: AmphibianApiStatus) {
        _status.value = status
    }

    private fun setAmphibians(amphibians: List<Amphibian>) {
        _amphibians.value = amphibians
    }

    private fun setAmphibian(amphibian: Amphibian) {
        _amphibian.value = amphibian
    }

    // Do it the right way
    private suspend fun getAmphibians() = withContext(Dispatchers.IO) {
        AmphibianApi.retrofitService.getAmphibians()
    }

    // On error
    private fun error() {
        setAmphibians(emptyList())
        setStatus(ERROR)
    }

    fun onAmphibianClicked(amphibian: Amphibian) {
        // TODO: Set the amphibian object
        setAmphibian(amphibian)
    }

}
