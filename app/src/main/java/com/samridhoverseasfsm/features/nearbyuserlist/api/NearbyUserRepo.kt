package com.samridhoverseasfsm.features.nearbyuserlist.api

import com.samridhoverseasfsm.app.Pref
import com.samridhoverseasfsm.features.nearbyuserlist.model.NearbyUserResponseModel
import com.samridhoverseasfsm.features.newcollection.model.NewCollectionListResponseModel
import com.samridhoverseasfsm.features.newcollection.newcollectionlistapi.NewCollectionListApi
import io.reactivex.Observable

class NearbyUserRepo(val apiService: NearbyUserApi) {
    fun nearbyUserList(): Observable<NearbyUserResponseModel> {
        return apiService.getNearbyUserList(Pref.session_token!!, Pref.user_id!!)
    }
}