package com.samridhoverseasfsm.features.dashboard.presentation.api.dayStartEnd

import com.samridhoverseasfsm.features.stockCompetetorStock.api.AddCompStockApi
import com.samridhoverseasfsm.features.stockCompetetorStock.api.AddCompStockRepository

object DayStartEndRepoProvider {
    fun dayStartRepositiry(): DayStartEndRepository {
        return DayStartEndRepository(DayStartEndApi.create())
    }

}