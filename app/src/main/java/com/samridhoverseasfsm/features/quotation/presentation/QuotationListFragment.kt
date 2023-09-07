package com.samridhoverseasfsm.features.quotation.presentation

import android.content.Context
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import timber.log.Timber

import com.pnikosis.materialishprogress.ProgressWheel
import com.samridhoverseasfsm.R
import com.samridhoverseasfsm.app.AppDatabase
import com.samridhoverseasfsm.app.NetworkConstant
import com.samridhoverseasfsm.app.Pref
import com.samridhoverseasfsm.app.domain.*
import com.samridhoverseasfsm.app.types.FragType
import com.samridhoverseasfsm.app.uiaction.IntentActionable
import com.samridhoverseasfsm.app.utils.AppUtils
import com.samridhoverseasfsm.base.BaseResponse
import com.samridhoverseasfsm.base.presentation.BaseActivity
import com.samridhoverseasfsm.base.presentation.BaseFragment
import com.samridhoverseasfsm.features.addshop.api.AddShopRepositoryProvider
import com.samridhoverseasfsm.features.addshop.api.assignToPPList.AssignToPPListRepoProvider
import com.samridhoverseasfsm.features.addshop.api.assignedToDDList.AssignToDDListRepoProvider
import com.samridhoverseasfsm.features.addshop.api.typeList.TypeListRepoProvider
import com.samridhoverseasfsm.features.addshop.model.AddShopRequestData
import com.samridhoverseasfsm.features.addshop.model.AddShopResponse
import com.samridhoverseasfsm.features.addshop.model.AssignedToShopListResponseModel
import com.samridhoverseasfsm.features.addshop.model.assigntoddlist.AssignToDDListResponseModel
import com.samridhoverseasfsm.features.addshop.model.assigntopplist.AssignToPPListResponseModel
import com.samridhoverseasfsm.features.dashboard.presentation.DashboardActivity
import com.samridhoverseasfsm.features.location.model.ShopDurationRequest
import com.samridhoverseasfsm.features.location.model.ShopDurationRequestData
import com.samridhoverseasfsm.features.location.shopdurationapi.ShopDurationRepositoryProvider
import com.samridhoverseasfsm.features.quotation.api.QuotationRepoProvider
import com.samridhoverseasfsm.features.quotation.model.AddQuotInputModel
import com.samridhoverseasfsm.features.quotation.model.QuotationListResponseModel
import com.samridhoverseasfsm.widgets.AppCustomTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Created by Saikat on 15-Jun-20.
 */
// 1.0 QuotationListFragment AppV 4.0.6 saheli 12-01-2023 multiple contact Data added on Api called
class QuotationListFragment : BaseFragment() {

    private lateinit var mContext: Context

    private lateinit var rv_quot_list: RecyclerView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var fab: FloatingActionButton
    private lateinit var tv_no_data_available: AppCustomTextView
    private lateinit var rl_quot_main: RelativeLayout
    private lateinit var tv_quot_count: AppCustomTextView

    private var shopId = ""

    companion object {

        fun getInstance(shopId: Any?): QuotationListFragment {
            val fragment = QuotationListFragment()

            val bundle = Bundle()
            bundle.putString("shop_id", shopId.toString())
            fragment.arguments = bundle

            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

        if (!TextUtils.isEmpty(arguments?.getString("shop_id")))
            shopId = arguments?.getString("shop_id")!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_quot, container, false)

        initView(view)

        return view
    }

    private fun initView(view: View) {
        view.apply {
            rv_quot_list = findViewById(R.id.rv_quot_list)
            progress_wheel = findViewById(R.id.progress_wheel)
            fab = findViewById(R.id.fab)
            tv_no_data_available = findViewById(R.id.tv_no_data_available)
            rl_quot_main = findViewById(R.id.rl_quot_main)
            tv_quot_count = findViewById(R.id.tv_quot_count)
        }

        rv_quot_list.layoutManager = LinearLayoutManager(mContext)
        progress_wheel.stopSpinning()

        if (!TextUtils.isEmpty(shopId))
            fab.visibility = View.VISIBLE
        else
            fab.visibility = View.GONE


        rl_quot_main.setOnClickListener(null)
        fab.setOnClickListener {
            if (!Pref.isAddAttendence)
                (mContext as DashboardActivity).checkToShowAddAttendanceAlert()
            else
                (mContext as DashboardActivity).loadFragment(FragType.AddQuotationFragment, true, shopId)
        }


        val list = AppDatabase.getDBInstance()?.quotDao()?.getAll()
        if (list == null || list.isEmpty())
            geQuotApi()
        else
            initAdapter()
    }

    private fun geQuotApi() {

        if (!AppUtils.isOnline(mContext)) {
            tv_no_data_available.visibility = View.VISIBLE
            return
        }

        progress_wheel.spin()
        val repository = QuotationRepoProvider.provideBSListRepository()
        BaseActivity.compositeDisposable.add(
                repository.getQuotList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as QuotationListResponseModel
                            Timber.d("GET QUOT DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            if (response.status == NetworkConstant.SUCCESS) {

                                if (response.quot_list != null && response.quot_list!!.isNotEmpty()) {

                                    doAsync {

                                        response.quot_list?.forEach {
                                            val quotEntity = QuotationEntity()
                                            AppDatabase.getDBInstance()?.quotDao()?.insert(quotEntity.apply {
                                                quo_id = it.quo_id
                                                quo_no = it.quo_no

                                                date = it.date

                                                hypothecation = if (!TextUtils.isEmpty(it.hypothecation))
                                                    it.hypothecation
                                                else
                                                    ""
                                                account_no = if (!TextUtils.isEmpty(it.account_no))
                                                    it.account_no
                                                else
                                                    ""

                                                model_id = it.model_id
                                                bs_id = it.bs_id

                                                gearbox = if (!TextUtils.isEmpty(it.gearbox))
                                                    it.gearbox
                                                else
                                                    ""

                                                number1 = if (!TextUtils.isEmpty(it.number1))
                                                    it.number1
                                                else
                                                    ""

                                                value1 = if (!TextUtils.isEmpty(it.value1))
                                                    it.value1
                                                else
                                                    ""

                                                value2 = if (!TextUtils.isEmpty(it.value2))
                                                    it.value2
                                                else
                                                    ""

                                                tyres1 = if (!TextUtils.isEmpty(it.tyres1))
                                                    it.tyres1
                                                else
                                                    ""

                                                number2 = if (!TextUtils.isEmpty(it.number2))
                                                    it.number2
                                                else
                                                    ""

                                                value3 = if (!TextUtils.isEmpty(it.value3))
                                                    it.value3
                                                else
                                                    ""

                                                value4 = if (!TextUtils.isEmpty(it.value4))
                                                    it.value4
                                                else
                                                    ""

                                                tyres2 = if (!TextUtils.isEmpty(it.tyres2))
                                                    it.tyres2
                                                else
                                                    ""

                                                amount = if (!TextUtils.isEmpty(it.amount))
                                                    it.amount
                                                else
                                                    ""

                                                discount = if (!TextUtils.isEmpty(it.discount))
                                                    it.discount
                                                else
                                                    ""

                                                cgst = if (!TextUtils.isEmpty(it.cgst))
                                                    it.cgst
                                                else
                                                    ""

                                                sgst = if (!TextUtils.isEmpty(it.sgst))
                                                    it.sgst
                                                else
                                                    ""

                                                tcs = if (!TextUtils.isEmpty(it.tcs))
                                                    it.tcs
                                                else
                                                    ""

                                                insurance = if (!TextUtils.isEmpty(it.insurance))
                                                    it.insurance
                                                else
                                                    ""

                                                net_amount = if (!TextUtils.isEmpty(it.net_amount))
                                                    it.net_amount
                                                else
                                                    ""

                                                remarks = if (!TextUtils.isEmpty(it.remarks))
                                                    it.remarks
                                                else
                                                    ""

                                                shop_id = it.shop_id
                                                isUploaded = true
                                                isEditUpdated = -1
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            initAdapter()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    tv_no_data_available.visibility = View.VISIBLE
                                }

                            } else {
                                progress_wheel.stopSpinning()
                                tv_no_data_available.visibility = View.VISIBLE
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            Timber.d("GET QUOT DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            tv_no_data_available.visibility = View.VISIBLE
                        })
        )
    }

    private fun initAdapter() {
        tv_quot_count.visibility = View.VISIBLE

        var list: ArrayList<QuotationEntity>? = null

        list = if (!TextUtils.isEmpty(shopId))
            AppDatabase.getDBInstance()?.quotDao()?.getSingleShopQuotation(shopId) as ArrayList<QuotationEntity>
        else
            AppDatabase.getDBInstance()?.quotDao()?.getAll() as ArrayList<QuotationEntity>

        if (list != null && list.isNotEmpty()) {
            tv_no_data_available.visibility = View.GONE
            tv_quot_count.text = "Total Quotation(s): " + list?.size
            rv_quot_list.adapter = QuotationAdapter(mContext, list, { quot: QuotationEntity ->

                val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(quot.shop_id)

                if (shop != null) {
                    if (shop.isUploaded) {

                        if (!quot.isUploaded)
                            addQuotApi(quot)
                        else if (quot.isEditUpdated == 0) {
                            editQuotApi(quot)
                        }

                    } else {
                        syncShop(shop, quot)
                    }
                }


            }, { quot: QuotationEntity ->
                (mContext as DashboardActivity).loadFragment(FragType.QuotationDetailsFragment, true, quot.quo_id!!)
            }, { phoneNo: String ->
                IntentActionable.initiatePhoneCall(mContext, phoneNo)
            }, { shop: AddShopDBModelEntity? ->
                shop?.let {
                    (mContext as DashboardActivity).openLocationMap(it, false)
                }
            }, { quot: QuotationEntity ->
                sendMailSms(quot, true)
            }, { quot: QuotationEntity ->
                sendMailSms(quot, false)
            })
        } else {
            tv_quot_count.text = "Total Quotation(s): 0"
            tv_no_data_available.visibility = View.VISIBLE
        }
    }

    private fun sendMailSms(quot: QuotationEntity, isSms: Boolean) {

        Timber.d("==============Send Mail Sms Input Params(Quot.List)====================")
        Timber.d("shop id=======> " + quot.shop_id)
        Timber.d("quot. id=======> " + quot.quo_id)
        Timber.d("isSms=======> " + isSms)
        Timber.d("user_id=======> " + Pref.user_id)
        Timber.d("session_token=======> " + Pref.session_token)
        Timber.d("======================================================================")

        progress_wheel.spin()
        val repository = QuotationRepoProvider.provideBSListRepository()

        BaseActivity.compositeDisposable.add(
                repository.sendQuoSmsMail(quot.quo_id, quot.shop_id, isSms)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BaseResponse
                            Timber.d("Send Mail Sms DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(response.message!!)

                            if (response.status == NetworkConstant.SUCCESS) {

                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            Timber.d("Send Mail Sms DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    private fun addQuotApi(quotEntity: QuotationEntity) {

        Timber.d("==============Sync Add Quot. Input Params(Quot.List)====================")
        Timber.d("shop id=======> " + quotEntity.shop_id)
        Timber.d("quot. date=======> " + quotEntity.date)
        Timber.d("quot. id=======> " + quotEntity.quo_id)
        Timber.d("quot. no=======> " + quotEntity.quo_no)
        Timber.d("hypothecation=======> " + quotEntity.hypothecation)
        Timber.d("account_no=======> " + quotEntity.account_no)
        Timber.d("model_id=======> " + quotEntity.model_id)
        Timber.d("bs_id=======> " + quotEntity.bs_id)
        Timber.d("gearbox=======> " + quotEntity.gearbox)
        Timber.d("number1=======> " + quotEntity.number1)
        Timber.d("value1=======> " + quotEntity.value1)
        Timber.d("value2=======> " + quotEntity.value2)
        Timber.d("tyres1=======> " + quotEntity.tyres1)
        Timber.d("number2=======> " + quotEntity.number2)
        Timber.d("value3=======> " + quotEntity.value3)
        Timber.d("value4=======> " + quotEntity.value4)
        Timber.d("tyres2=======> " + quotEntity.tyres2)
        Timber.d("amount=======> " + quotEntity.amount)
        Timber.d("discount=======> " + quotEntity.discount)
        Timber.d("cgst=======> " + quotEntity.cgst)
        Timber.d("sgst=======> " + quotEntity.sgst)
        Timber.d("tcs=======> " + quotEntity.tcs)
        Timber.d("insurance=======> " + quotEntity.insurance)
        Timber.d("net_amount=======> " + quotEntity.net_amount)
        Timber.d("remarks=======> " + quotEntity.remarks)
        Timber.d("session_token=======> " + Pref.session_token)
        Timber.d("user_id=======> " + Pref.user_id)
        Timber.d("======================================================================")

        progress_wheel.spin()
        val repository = QuotationRepoProvider.provideBSListRepository()

        val addQuot = AddQuotInputModel(Pref.session_token!!, Pref.user_id!!, quotEntity.shop_id!!, quotEntity.quo_id!!,
                quotEntity.quo_no!!, quotEntity.date!!, quotEntity.hypothecation!!, quotEntity.account_no!!, quotEntity.model_id!!,
                quotEntity.bs_id!!, quotEntity.gearbox!!, quotEntity.number1!!, quotEntity.value1!!, quotEntity.value2!!,
                quotEntity.tyres1!!, quotEntity.number2!!, quotEntity.value3!!, quotEntity.value4!!, quotEntity.tyres2!!, quotEntity.amount!!,
                quotEntity.discount!!, quotEntity.cgst!!, quotEntity.sgst!!, quotEntity.tcs!!, quotEntity.insurance!!, quotEntity.net_amount!!,
                quotEntity.remarks!!)

        BaseActivity.compositeDisposable.add(
                repository.addQuot(addQuot)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BaseResponse
                            Timber.d("ADD QUOT. DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            if (response.status == NetworkConstant.SUCCESS) {

                                doAsync {

                                    AppDatabase.getDBInstance()?.quotDao()?.updateIsUploaded(true, quotEntity.quo_id!!)

                                    uiThread {
                                        progress_wheel.stopSpinning()
                                        (mContext as DashboardActivity).showSnackMessage("Quotation synced successfully")
                                        initAdapter()
                                    }
                                }

                            } else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync_quot))
                                initAdapter()
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            Timber.d("ADD QUOT. DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync_quot))
                            initAdapter()
                        })
        )
    }

    private fun editQuotApi(quotEntity: QuotationEntity) {
        Timber.d("==============Sync Edit Quot. Input Params(Quot.List)====================")
        Timber.d("shop id=======> " + quotEntity.shop_id)
        Timber.d("quot. date=======> " + quotEntity.date)
        Timber.d("quot. id=======> " + quotEntity.quo_id)
        Timber.d("quot. no=======> " + quotEntity.quo_no)
        Timber.d("hypothecation=======> " + quotEntity.hypothecation)
        Timber.d("account_no=======> " + quotEntity.account_no)
        Timber.d("model_id=======> " + quotEntity.model_id)
        Timber.d("bs_id=======> " + quotEntity.bs_id)
        Timber.d("gearbox=======> " + quotEntity.gearbox)
        Timber.d("number1=======> " + quotEntity.number1)
        Timber.d("value1=======> " + quotEntity.value1)
        Timber.d("value2=======> " + quotEntity.value2)
        Timber.d("tyres1=======> " + quotEntity.tyres1)
        Timber.d("number2=======> " + quotEntity.number2)
        Timber.d("value3=======> " + quotEntity.value3)
        Timber.d("value4=======> " + quotEntity.value4)
        Timber.d("tyres2=======> " + quotEntity.tyres2)
        Timber.d("amount=======> " + quotEntity.amount)
        Timber.d("discount=======> " + quotEntity.discount)
        Timber.d("cgst=======> " + quotEntity.cgst)
        Timber.d("sgst=======> " + quotEntity.sgst)
        Timber.d("tcs=======> " + quotEntity.tcs)
        Timber.d("insurance=======> " + quotEntity.insurance)
        Timber.d("net_amount=======> " + quotEntity.net_amount)
        Timber.d("remarks=======> " + quotEntity.remarks)
        Timber.d("session_token=======> " + Pref.session_token)
        Timber.d("user_id=======> " + Pref.user_id)
        Timber.d("======================================================================")

        progress_wheel.spin()
        val repository = QuotationRepoProvider.provideBSListRepository()

        val addQuot = AddQuotInputModel(Pref.session_token!!, Pref.user_id!!, quotEntity.shop_id!!, quotEntity.quo_id!!,
                quotEntity.quo_no!!, quotEntity.date!!, quotEntity.hypothecation!!, quotEntity.account_no!!, quotEntity.model_id!!,
                quotEntity.bs_id!!, quotEntity.gearbox!!, quotEntity.number1!!, quotEntity.value1!!, quotEntity.value2!!,
                quotEntity.tyres1!!, quotEntity.number2!!, quotEntity.value3!!, quotEntity.value4!!, quotEntity.tyres2!!, quotEntity.amount!!,
                quotEntity.discount!!, quotEntity.cgst!!, quotEntity.sgst!!, quotEntity.tcs!!, quotEntity.insurance!!, quotEntity.net_amount!!,
                quotEntity.remarks!!)

        BaseActivity.compositeDisposable.add(
                repository.addQuot(addQuot)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BaseResponse
                            Timber.d("EDIT QUOT. DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            if (response.status == NetworkConstant.SUCCESS) {

                                doAsync {

                                    AppDatabase.getDBInstance()?.quotDao()?.updateIsEdit(1, quotEntity.quo_id!!)

                                    uiThread {
                                        progress_wheel.stopSpinning()
                                        (mContext as DashboardActivity).showSnackMessage("Quotation synced successfully")
                                        initAdapter()
                                    }
                                }

                            } else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync_quot))
                                initAdapter()
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            Timber.d("EDIT QUOT. DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync_quot))
                            initAdapter()
                        })
        )
    }

    private fun syncShop(shop: AddShopDBModelEntity?, quot: QuotationEntity) {
        val addShopData = AddShopRequestData()

        shop?.apply {
            addShopData.session_token = Pref.session_token
            addShopData.address = address
            addShopData.owner_contact_no = ownerContactNumber
            addShopData.owner_email = ownerEmailId
            addShopData.owner_name = ownerName
            addShopData.pin_code = pinCode
            addShopData.shop_lat = shopLat.toString()
            addShopData.shop_long = shopLong.toString()
            addShopData.shop_name = shopName.toString()
            addShopData.type = type.toString()
            addShopData.shop_id = shop_id
            addShopData.user_id = Pref.user_id

            if (!TextUtils.isEmpty(dateOfBirth))
                addShopData.dob = AppUtils.changeAttendanceDateFormatToCurrent(dateOfBirth)

            if (!TextUtils.isEmpty(dateOfAniversary))
                addShopData.date_aniversary = AppUtils.changeAttendanceDateFormatToCurrent(dateOfAniversary)

            addShopData.assigned_to_dd_id = assigned_to_dd_id
            addShopData.assigned_to_pp_id = assigned_to_pp_id
            addShopData.added_date = added_date
            addShopData.amount = amount
            addShopData.area_id = area_id
            addShopData.model_id = model_id
            addShopData.primary_app_id = primary_app_id
            addShopData.secondary_app_id = secondary_app_id
            addShopData.lead_id = lead_id
            addShopData.stage_id = stage_id
            addShopData.funnel_stage_id = funnel_stage_id
            addShopData.booking_amount = booking_amount
            addShopData.type_id = type_id

            addShopData.director_name = director_name
            addShopData.key_person_name = person_name
            addShopData.phone_no = person_no

            if (!TextUtils.isEmpty(family_member_dob))
                addShopData.family_member_dob = AppUtils.changeAttendanceDateFormatToCurrent(family_member_dob)

            if (!TextUtils.isEmpty(add_dob))
                addShopData.addtional_dob = AppUtils.changeAttendanceDateFormatToCurrent(add_dob)

            if (!TextUtils.isEmpty(add_doa))
                addShopData.addtional_doa = AppUtils.changeAttendanceDateFormatToCurrent(add_doa)

            addShopData.specialization = specialization
            addShopData.category = category
            addShopData.doc_address = doc_address
            addShopData.doc_pincode = doc_pincode
            addShopData.is_chamber_same_headquarter = chamber_status.toString()
            addShopData.is_chamber_same_headquarter_remarks = remarks
            addShopData.chemist_name = chemist_name
            addShopData.chemist_address = chemist_address
            addShopData.chemist_pincode = chemist_pincode
            addShopData.assistant_contact_no = assistant_no
            addShopData.average_patient_per_day = patient_count
            addShopData.assistant_name = assistant_name

            if (!TextUtils.isEmpty(doc_family_dob))
                addShopData.doc_family_member_dob = AppUtils.changeAttendanceDateFormatToCurrent(doc_family_dob)

            if (!TextUtils.isEmpty(assistant_dob))
                addShopData.assistant_dob = AppUtils.changeAttendanceDateFormatToCurrent(assistant_dob)

            if (!TextUtils.isEmpty(assistant_doa))
                addShopData.assistant_doa = AppUtils.changeAttendanceDateFormatToCurrent(assistant_doa)

            if (!TextUtils.isEmpty(assistant_family_dob))
                addShopData.assistant_family_dob = AppUtils.changeAttendanceDateFormatToCurrent(assistant_family_dob)

            addShopData.entity_id = entity_id
            addShopData.party_status_id = party_status_id
            addShopData.retailer_id = retailer_id
            addShopData.dealer_id = dealer_id
            addShopData.beat_id = beat_id
            addShopData.assigned_to_shop_id = assigned_to_shop_id
            addShopData.actual_address = actual_address

            var uniqKeyObj=AppDatabase.getDBInstance()!!.shopActivityDao().getNewShopActivityKey(shop_id,false)
            addShopData.shop_revisit_uniqKey=uniqKeyObj?.shop_revisit_uniqKey!!

            addShopData.project_name = project_name
            addShopData.landline_number = landline_number
            addShopData.agency_name = agency_name

            addShopData.alternateNoForCustomer = alternateNoForCustomer
            addShopData.whatsappNoForCustomer = whatsappNoForCustomer

            // duplicate shop api call
            addShopData.isShopDuplicate=isShopDuplicate

            addShopData.purpose=purpose

            addShopData.GSTN_Number=gstN_Number
            addShopData.ShopOwner_PAN=shopOwner_PAN




        }
        callAddShopApi(addShopData, shop?.shopImageLocalPath!!, quot, shop.doc_degree!!)
    }

    private fun callAddShopApi(addShop: AddShopRequestData, shop_imgPath: String?, quot: QuotationEntity, doc_degree: String?) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        progress_wheel.spin()


        Timber.d("==============SyncShop Input Params (Quot. List)====================")
        Timber.d("shop id=======> " + addShop.shop_id)
        val index = addShop.shop_id!!.indexOf("_")
        Timber.d("decoded shop id=======> " + addShop.user_id + "_" + AppUtils.getDate(addShop.shop_id!!.substring(index + 1, addShop.shop_id!!.length).toLong()))
        Timber.d("shop added date=======> " + addShop.added_date)
        Timber.d("shop address=======> " + addShop.address)
        Timber.d("assigned to dd id=======> " + addShop.assigned_to_dd_id)
        Timber.d("assigned to pp id=======> " + addShop.assigned_to_pp_id)
        Timber.d("date aniversery=======> " + addShop.date_aniversary)
        Timber.d("dob=======> " + addShop.dob)
        Timber.d("shop owner phn no=======> " + addShop.owner_contact_no)
        Timber.d("shop owner email=======> " + addShop.owner_email)
        Timber.d("shop owner name=======> " + addShop.owner_name)
        Timber.d("shop pincode=======> " + addShop.pin_code)
        Timber.d("session token=======> " + addShop.session_token)
        Timber.d("shop lat=======> " + addShop.shop_lat)
        Timber.d("shop long=======> " + addShop.shop_long)
        Timber.d("shop name=======> " + addShop.shop_name)
        Timber.d("shop type=======> " + addShop.type)
        Timber.d("user id=======> " + addShop.user_id)
        Timber.d("amount=======> " + addShop.amount)
        Timber.d("area id=======> " + addShop.area_id)
        Timber.d("model id=======> " + addShop.model_id)
        Timber.d("primary app id=======> " + addShop.primary_app_id)
        Timber.d("secondary app id=======> " + addShop.secondary_app_id)
        Timber.d("lead id=======> " + addShop.lead_id)
        Timber.d("stage id=======> " + addShop.stage_id)
        Timber.d("funnel stage id=======> " + addShop.funnel_stage_id)
        Timber.d("booking amount=======> " + addShop.booking_amount)
        Timber.d("type id=======> " + addShop.type_id)

        if (shop_imgPath != null)
            Timber.d("shop image path=======> $shop_imgPath")

        Timber.d("director name=======> " + addShop.director_name)
        Timber.d("family member dob=======> " + addShop.family_member_dob)
        Timber.d("key person's name=======> " + addShop.key_person_name)
        Timber.d("phone no=======> " + addShop.phone_no)
        Timber.d("additional dob=======> " + addShop.addtional_dob)
        Timber.d("additional doa=======> " + addShop.addtional_doa)
        Timber.d("family member dob=======> " + addShop.family_member_dob)
        Timber.d("key person's name=======> " + addShop.key_person_name)
        Timber.d("phone no=======> " + addShop.phone_no)
        Timber.d("additional dob=======> " + addShop.addtional_dob)
        Timber.d("additional doa=======> " + addShop.addtional_doa)
        Timber.d("doctor family member dob=======> " + addShop.doc_family_member_dob)
        Timber.d("specialization=======> " + addShop.specialization)
        Timber.d("average patient count per day=======> " + addShop.average_patient_per_day)
        Timber.d("category=======> " + addShop.category)
        Timber.d("doctor address=======> " + addShop.doc_address)
        Timber.d("doctor pincode=======> " + addShop.doc_pincode)
        Timber.d("chambers or hospital under same headquarter=======> " + addShop.is_chamber_same_headquarter)
        Timber.d("chamber related remarks=======> " + addShop.is_chamber_same_headquarter_remarks)
        Timber.d("chemist name=======> " + addShop.chemist_name)
        Timber.d("chemist name=======> " + addShop.chemist_address)
        Timber.d("chemist pincode=======> " + addShop.chemist_pincode)
        Timber.d("assistant name=======> " + addShop.assistant_name)
        Timber.d("assistant contact no=======> " + addShop.assistant_contact_no)
        Timber.d("assistant dob=======> " + addShop.assistant_dob)
        Timber.d("assistant date of anniversary=======> " + addShop.assistant_doa)
        Timber.d("assistant family dob=======> " + addShop.assistant_family_dob)
        Timber.d("entity id=======> " + addShop.entity_id)
        Timber.d("party status id=======> " + addShop.party_status_id)
        Timber.d("retailer id=======> " + addShop.retailer_id)
        Timber.d("dealer id=======> " + addShop.dealer_id)
        Timber.d("beat id=======> " + addShop.beat_id)
        Timber.d("assigned to shop id=======> " + addShop.assigned_to_shop_id)
        Timber.d("actual address=======> " + addShop.actual_address)

        if (doc_degree != null)
            Timber.d("doctor degree image path=======> $doc_degree")
        Timber.d("======================================================================")


        if (TextUtils.isEmpty(shop_imgPath) && TextUtils.isEmpty(doc_degree)) {
            val repository = AddShopRepositoryProvider.provideAddShopWithoutImageRepository()
            BaseActivity.compositeDisposable.add(
                    repository.addShop(addShop)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val addShopResult = result as AddShopResponse
                                Timber.d("syncShopFromShopList : " + ", SHOP: " + addShop.shop_name + ", RESPONSE:" + result.message)
                                if (addShopResult.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)

                                    doAsync {
                                        val resultAs = runLongTask(addShop.shop_id)
                                        uiThread {
                                            if (resultAs == true) {

                                            }

                                        }
                                    }
                                    progress_wheel.stopSpinning()
                                    getAssignedPPListApi(addShop.shop_id, quot)

                                } else if (addShopResult.status == NetworkConstant.DUPLICATE_SHOP_ID) {
                                    Timber.d("DuplicateShop : " + ", SHOP: " + addShop.shop_name)
                                    AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)
                                    progress_wheel.stopSpinning()
                                    //(mContext as DashboardActivity).showSnackMessage(addShopResult.message!!)
                                    if (AppDatabase.getDBInstance()!!.addShopEntryDao().getDuplicateShopData(addShop.owner_contact_no).size > 0) {
                                        AppDatabase.getDBInstance()!!.addShopEntryDao().deleteShopById(addShop.shop_id)
                                        AppDatabase.getDBInstance()!!.shopActivityDao().deleteShopByIdAndDate(addShop.shop_id!!, AppUtils.getCurrentDateForShopActi())
                                    }
                                    doAsync {
                                        val resultAs = runLongTask(addShop.shop_id)
                                        uiThread {
                                            if (resultAs == true) {

                                            }

                                        }
                                    }
                                    getAssignedPPListApi(addShop.shop_id, quot)
                                } else {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
                                }


                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
                                if (error != null)
                                    Timber.d("syncShopFromShopList : " + ", SHOP: " + addShop.shop_name + error.localizedMessage)
                            })
            )
        }
        else {
            val repository = AddShopRepositoryProvider.provideAddShopRepository()
            BaseActivity.compositeDisposable.add(
                    repository.addShopWithImage(addShop, shop_imgPath, doc_degree, mContext)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val addShopResult = result as AddShopResponse
                                Timber.d("syncShopFromShopList : " + ", SHOP: " + addShop.shop_name + ", RESPONSE:" + result.message)
                                if (addShopResult.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)

                                    doAsync {
                                        val resultAs = runLongTask(addShop.shop_id)
                                        uiThread {
                                            if (resultAs == true) {

                                            }

                                        }
                                    }
                                    progress_wheel.stopSpinning()
                                    getAssignedPPListApi(addShop.shop_id, quot)

                                } else if (addShopResult.status == NetworkConstant.DUPLICATE_SHOP_ID) {
                                    Timber.d("DuplicateShop : " + ", SHOP: " + addShop.shop_name)
                                    AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)
                                    progress_wheel.stopSpinning()
                                    //(mContext as DashboardActivity).showSnackMessage(addShopResult.message!!)
                                    if (AppDatabase.getDBInstance()!!.addShopEntryDao().getDuplicateShopData(addShop.owner_contact_no).size > 0) {
                                        AppDatabase.getDBInstance()!!.addShopEntryDao().deleteShopById(addShop.shop_id)
                                        AppDatabase.getDBInstance()!!.shopActivityDao().deleteShopByIdAndDate(addShop.shop_id!!, AppUtils.getCurrentDateForShopActi())
                                    }
                                    doAsync {
                                        val resultAs = runLongTask(addShop.shop_id)
                                        uiThread {
                                            if (resultAs == true) {

                                            }

                                        }
                                    }
                                    getAssignedPPListApi(addShop.shop_id, quot)
                                } else {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
                                }


                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
                                if (error != null)
                                    Timber.d("syncShopFromShopList : " + ", SHOP: " + addShop.shop_name + error.localizedMessage)
                            })
            )
        }
    }

    private fun runLongTask(shop_id: String?): Any {
        val shopActivity = AppDatabase.getDBInstance()!!.shopActivityDao().durationAvailableForShop(shop_id!!, true, false)
        if (shopActivity != null)
            callShopActivitySubmit(shop_id)
        return true
    }

    private var shop_duration = ""
    private fun callShopActivitySubmit(shopId: String) {
        var list = AppDatabase.getDBInstance()!!.shopActivityDao().getShopForDay(shopId, AppUtils.getCurrentDateForShopActi())
        if (list.isEmpty())
            return

        var shopDataList: MutableList<ShopDurationRequestData> = java.util.ArrayList()
        var shopDurationApiReq = ShopDurationRequest()
        shopDurationApiReq.user_id = Pref.user_id
        shopDurationApiReq.session_token = Pref.session_token

        if (!Pref.isMultipleVisitEnable) {
            var shopActivity = list[0]

            var shopDurationData = ShopDurationRequestData()
            shopDurationData.shop_id = shopActivity.shopid
            if (shopActivity.startTimeStamp != "0" && !shopActivity.isDurationCalculated) {
                val totalMinute = AppUtils.getMinuteFromTimeStamp(shopActivity.startTimeStamp, System.currentTimeMillis().toString())
                val duration = AppUtils.getTimeFromTimeSpan(shopActivity.startTimeStamp, System.currentTimeMillis().toString())

                AppDatabase.getDBInstance()!!.shopActivityDao().updateTotalMinuteForDayOfShop(shopActivity.shopid!!, totalMinute, AppUtils.getCurrentDateForShopActi())
                AppDatabase.getDBInstance()!!.shopActivityDao().updateTimeDurationForDayOfShop(shopActivity.shopid!!, duration, AppUtils.getCurrentDateForShopActi())

                shopDurationData.spent_duration = duration
            } else {
                shopDurationData.spent_duration = shopActivity.duration_spent
            }
            shopDurationData.visited_date = shopActivity.visited_date
            shopDurationData.visited_time = shopActivity.visited_date
            if (TextUtils.isEmpty(shopActivity.distance_travelled))
                shopActivity.distance_travelled = "0.0"
            shopDurationData.distance_travelled = shopActivity.distance_travelled
            var sList = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdList(shopDurationData.shop_id)
            if (sList != null && sList.isNotEmpty())
                shopDurationData.total_visit_count = sList[0].totalVisitCount

            if (!TextUtils.isEmpty(shopActivity.feedback))
                shopDurationData.feedback = shopActivity.feedback
            else
                shopDurationData.feedback = ""

            shopDurationData.isFirstShopVisited = shopActivity.isFirstShopVisited
            shopDurationData.distanceFromHomeLoc = shopActivity.distance_from_home_loc
            shopDurationData.next_visit_date = shopActivity.next_visit_date

            if (!TextUtils.isEmpty(shopActivity.early_revisit_reason))
                shopDurationData.early_revisit_reason = shopActivity.early_revisit_reason
            else
                shopDurationData.early_revisit_reason = ""

            shopDurationData.device_model = shopActivity.device_model
            shopDurationData.android_version = shopActivity.android_version
            shopDurationData.battery = shopActivity.battery
            shopDurationData.net_status = shopActivity.net_status
            shopDurationData.net_type = shopActivity.net_type
            shopDurationData.in_time = shopActivity.in_time
            shopDurationData.out_time = shopActivity.out_time
            shopDurationData.start_timestamp = shopActivity.startTimeStamp
            shopDurationData.in_location = shopActivity.in_loc
            shopDurationData.out_location = shopActivity.out_loc

            shopDurationData.shop_revisit_uniqKey=shopActivity.shop_revisit_uniqKey!!

            /*10-12-2021*/
            shopDurationData.updated_by = Pref.user_id
            try {
                shopDurationData.updated_on = shopActivity.updated_on!!
            }
            catch (ex:Exception){
                shopDurationData.updated_on = ""
            }


            if (!TextUtils.isEmpty(shopActivity.pros_id!!))
                shopDurationData.pros_id = shopActivity.pros_id!!
            else
                shopDurationData.pros_id = ""

            if (!TextUtils.isEmpty(shopActivity.agency_name!!))
                shopDurationData.agency_name =shopActivity.agency_name!!
            else
                shopDurationData.agency_name = ""

            if (!TextUtils.isEmpty(shopActivity.approximate_1st_billing_value))
                shopDurationData.approximate_1st_billing_value = shopActivity.approximate_1st_billing_value!!
            else
                shopDurationData.approximate_1st_billing_value = ""

            //New shop Create issue
            shopDurationData.isnewShop = shopActivity.isnewShop

            // 1.0 QuotationListFragment  AppV 4.0.6  multiple contact Data added on Api called
            shopDurationData.multi_contact_name = shopActivity.multi_contact_name
            shopDurationData.multi_contact_number = shopActivity.multi_contact_number

            shopDataList.add(shopDurationData)
        }
        else {
            for (i in list.indices) {
                var shopActivity = list[i]

                var shopDurationData = ShopDurationRequestData()
                shopDurationData.shop_id = shopActivity.shopid
                if (shopActivity.startTimeStamp != "0" && !shopActivity.isDurationCalculated) {
                    val totalMinute = AppUtils.getMinuteFromTimeStamp(shopActivity.startTimeStamp, System.currentTimeMillis().toString())
                    val duration = AppUtils.getTimeFromTimeSpan(shopActivity.startTimeStamp, System.currentTimeMillis().toString())

                    AppDatabase.getDBInstance()!!.shopActivityDao().updateTotalMinuteForDayOfShop(shopActivity.shopid!!, totalMinute, AppUtils.getCurrentDateForShopActi(), shopActivity.startTimeStamp)
                    AppDatabase.getDBInstance()!!.shopActivityDao().updateTimeDurationForDayOfShop(shopActivity.shopid!!, duration, AppUtils.getCurrentDateForShopActi(), shopActivity.startTimeStamp)

                    shopDurationData.spent_duration = duration
                } else {
                    shopDurationData.spent_duration = shopActivity.duration_spent
                }
                shopDurationData.visited_date = shopActivity.visited_date
                shopDurationData.visited_time = shopActivity.visited_date

                if (TextUtils.isEmpty(shopActivity.distance_travelled))
                    shopActivity.distance_travelled = "0.0"

                shopDurationData.distance_travelled = shopActivity.distance_travelled

                var sList = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdList(shopDurationData.shop_id)
                if (sList != null && sList.isNotEmpty())
                    shopDurationData.total_visit_count = sList[0].totalVisitCount

                if (!TextUtils.isEmpty(shopActivity.feedback))
                    shopDurationData.feedback = shopActivity.feedback
                else
                    shopDurationData.feedback = ""

                shopDurationData.isFirstShopVisited = shopActivity.isFirstShopVisited
                shopDurationData.distanceFromHomeLoc = shopActivity.distance_from_home_loc
                shopDurationData.next_visit_date = shopActivity.next_visit_date

                if (!TextUtils.isEmpty(shopActivity.early_revisit_reason))
                    shopDurationData.early_revisit_reason = shopActivity.early_revisit_reason
                else
                    shopDurationData.early_revisit_reason = ""

                shopDurationData.device_model = shopActivity.device_model
                shopDurationData.android_version = shopActivity.android_version
                shopDurationData.battery = shopActivity.battery
                shopDurationData.net_status = shopActivity.net_status
                shopDurationData.net_type = shopActivity.net_type
                shopDurationData.in_time = shopActivity.in_time
                shopDurationData.out_time = shopActivity.out_time
                shopDurationData.start_timestamp = shopActivity.startTimeStamp
                shopDurationData.in_location = shopActivity.in_loc
                shopDurationData.out_location = shopActivity.out_loc

                shopDurationData.shop_revisit_uniqKey=shopActivity.shop_revisit_uniqKey!!




                /*10-12-2021*/
                shopDurationData.updated_by = Pref.user_id
                try {
                    shopDurationData.updated_on = shopActivity.updated_on!!
                }
                catch(ex:Exception){
                    shopDurationData.updated_on = ""
                }

                if (!TextUtils.isEmpty(shopActivity.pros_id!!))
                    shopDurationData.pros_id = shopActivity.pros_id!!
                else
                    shopDurationData.pros_id = ""

                if (!TextUtils.isEmpty(shopActivity.agency_name!!))
                    shopDurationData.agency_name =shopActivity.agency_name!!
                else
                    shopDurationData.agency_name = ""

                if (!TextUtils.isEmpty(shopActivity.approximate_1st_billing_value))
                    shopDurationData.approximate_1st_billing_value = shopActivity.approximate_1st_billing_value!!
                else
                    shopDurationData.approximate_1st_billing_value = ""

                //New shop Create issue
                shopDurationData.isnewShop = shopActivity.isnewShop

                // 1.0 QuotationListFragment  AppV 4.0.6  multiple contact Data added on Api called
                shopDurationData.multi_contact_name = shopActivity.multi_contact_name
                shopDurationData.multi_contact_number = shopActivity.multi_contact_number

                shopDataList.add(shopDurationData)
            }
        }

        if (shopDataList.isEmpty()) {
            return
        }

        shopDurationApiReq.shop_list = shopDataList
        val repository = ShopDurationRepositoryProvider.provideShopDurationRepository()

        BaseActivity.compositeDisposable.add(
                repository.shopDuration(shopDurationApiReq)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            Timber.d("syncShopActivityFromShopList : " + ", SHOP: " + list[0].shop_name + ", RESPONSE:" + result.message)
                            if (result.status == NetworkConstant.SUCCESS) {

                            }

                        }, { error ->
                            error.printStackTrace()
                            if (error != null)
                                Timber.d("syncShopActivityFromShopList : " + ", SHOP: " + list[0].shop_name + error.localizedMessage)
//                                (mContext as DashboardActivity).showSnackMessage("ERROR")
                        })
        )

    }

    private fun getAssignedPPListApi(shop_id: String?, quot: QuotationEntity) {

        val shopActivityList = AppDatabase.getDBInstance()!!.shopActivityDao().getShopForDay(shop_id!!, AppUtils.getCurrentDateForShopActi())

        if (!Pref.isMultipleVisitEnable) {
            if (shopActivityList[0].isVisited && shopActivityList[0].isDurationCalculated) {
                AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shop_id, AppUtils.getCurrentDateForShopActi())
                Timber.d("================sync locally shop visited (quot.list)===============")
            }
        }
        else {
            shopActivityList.forEach {
                if (it.isVisited && it.isDurationCalculated) {
                    AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shop_id, AppUtils.getCurrentDateForShopActi(), it.startTimeStamp)
                    Timber.d("================sync locally shop visited (quot.list)===============")
                }
            }
        }

        val repository = AssignToPPListRepoProvider.provideAssignPPListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.assignToPPList(Pref.profile_state)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as AssignToPPListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.assigned_to_pp_list

                                if (list != null && list.isNotEmpty()) {

                                    doAsync {

                                        val assignPPList = AppDatabase.getDBInstance()?.ppListDao()?.getAll()
                                        if (assignPPList != null)
                                            AppDatabase.getDBInstance()?.ppListDao()?.delete()

                                        for (i in list.indices) {
                                            val assignToPP = AssignToPPEntity()
                                            assignToPP.pp_id = list[i].assigned_to_pp_id
                                            assignToPP.pp_name = list[i].assigned_to_pp_authorizer_name
                                            assignToPP.pp_phn_no = list[i].phn_no
                                            AppDatabase.getDBInstance()?.ppListDao()?.insert(assignToPP)
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            getAssignedDDListApi(shop_id, quot)
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    getAssignedDDListApi(shop_id, quot)
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                getAssignedDDListApi(shop_id, quot)
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            getAssignedDDListApi(shop_id, quot)
                        })
        )
    }

    private fun getAssignedDDListApi(shop_id: String?, quot: QuotationEntity) {
        val repository = AssignToDDListRepoProvider.provideAssignDDListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.assignToDDList(Pref.profile_state)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as AssignToDDListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.assigned_to_dd_list

                                if (list != null && list.isNotEmpty()) {

                                    doAsync {

                                        val assignDDList = AppDatabase.getDBInstance()?.ddListDao()?.getAll()
                                        if (assignDDList != null)
                                            AppDatabase.getDBInstance()?.ddListDao()?.delete()

                                        for (i in list.indices) {
                                            val assignToDD = AssignToDDEntity()
                                            assignToDD.dd_id = list[i].assigned_to_dd_id
                                            assignToDD.dd_name = list[i].assigned_to_dd_authorizer_name
                                            assignToDD.dd_phn_no = list[i].phn_no
                                            assignToDD.pp_id = list[i].assigned_to_pp_id
                                            assignToDD.type_id = list[i].type_id
                                            assignToDD.dd_latitude = list[i].dd_latitude
                                            assignToDD.dd_longitude = list[i].dd_longitude
                                            AppDatabase.getDBInstance()?.ddListDao()?.insert(assignToDD)
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            getAssignedToShopApi(shop_id, quot)
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    getAssignedToShopApi(shop_id, quot)
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                getAssignedToShopApi(shop_id, quot)
                            }

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            getAssignedToShopApi(shop_id, quot)
                        })
        )
    }

    private fun getAssignedToShopApi(shop_id: String?, quot: QuotationEntity) {
        val repository = TypeListRepoProvider.provideTypeListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.assignToShopList(Pref.profile_state)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as AssignedToShopListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.shop_list

                                AppDatabase.getDBInstance()?.assignToShopDao()?.delete()

                                doAsync {
                                    list?.forEach {
                                        val shop = AssignToShopEntity()
                                        AppDatabase.getDBInstance()?.assignToShopDao()?.insert(shop.apply {
                                            assigned_to_shop_id = it.assigned_to_shop_id
                                            name = it.name
                                            phn_no = it.phn_no
                                            type_id = it.type_id
                                        })
                                    }

                                    uiThread {
                                        progress_wheel.stopSpinning()
                                        addQuotApi(quot)
                                    }
                                }
                            }
                            else {
                                progress_wheel.stopSpinning()
                                addQuotApi(quot)
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            addQuotApi(quot)
                        })
        )
    }

    fun updateList() {
        initAdapter()
    }
}