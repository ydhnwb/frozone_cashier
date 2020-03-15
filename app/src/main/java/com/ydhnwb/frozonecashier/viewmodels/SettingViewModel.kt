package com.ydhnwb.frozonecashier.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ydhnwb.frozonecashier.models.Branch
import com.ydhnwb.frozonecashier.utils.SingleLiveEvent
import com.ydhnwb.frozonecashier.webservices.JustApi
import com.ydhnwb.frozonecashier.webservices.WrappedListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingViewModel : ViewModel(){
    private var state : SingleLiveEvent<SettingState> = SingleLiveEvent()
    private var branches = MutableLiveData<List<Branch>>()
    private var api = JustApi.instance()

    fun fetchBranch(){
        state.value = SettingState.IsLoading(true)
        api.getAllBranch().enqueue(object : Callback<WrappedListResponse<Branch>> {
            override fun onFailure(call: Call<WrappedListResponse<Branch>>, t: Throwable) {
                println(t.message)
                state.value = SettingState.IsLoading(false)
                state.value = SettingState.ShowToast(t.message.toString())
            }

            override fun onResponse(call: Call<WrappedListResponse<Branch>>, response: Response<WrappedListResponse<Branch>>) {
                if(response.isSuccessful){
                    val body = response.body() as WrappedListResponse<Branch>
                    if(body.status!!){
                        branches.postValue(body.data)
                    }else{
                        state.value = SettingState.ShowToast("Tidak dapat mengambil data cabang")
                    }
                }else{
                    state.value = SettingState.ShowToast("Kesalahan saat mengambil data cabang")
                }
                state.value = SettingState.IsLoading(false)
            }
        })
    }

    fun updateBranchName(branchName: String) { state.value = SettingState.SelectedBranch(branchName) }
    fun listenToUIState() = state
    fun listenToBranches() = branches
}

sealed class SettingState{
    data class IsLoading(var state : Boolean) : SettingState()
    data class ShowToast(var message : String) : SettingState()
    data class SelectedBranch(var branchName : String) : SettingState()
    object Reset : SettingState()
}