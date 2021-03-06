package com.zxn.mvvm.viewmodel

import android.os.Bundle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import com.trello.rxlifecycle2.LifecycleProvider
import com.zxn.mvvm.event.EventLiveData
import com.zxn.mvvm.event.SingleLiveEvent
import com.zxn.mvvm.ext.getNewInstance
import com.zxn.mvvm.model.IBaseModel
import com.zxn.mvvm.model.http.LoadingModel
import com.zxn.mvvm.view.ILoadingView
import com.zxn.mvvm.view.IToastView
import java.lang.ref.WeakReference

/**
 *  Created by zxn on 2020/11/5.
 */
abstract class BaseViewModel<M : IBaseModel<*>> : ViewModel(), LifecycleObserver, ILoadingView, IToastView {

    /**
     * 发出,发射数据.
     * @param isLoading true:加载中,false:加载结束.
     * @param showError 错误藐视
     * @param   successData 发给UI的数据.
     */
    fun <T> emitLoading(
            isLoading: Boolean = false,
            showError: String? = null,
            successData: T? = null,
            isRefresh: Boolean = false,
            showEnd: Boolean = false,
    ): LoadingModel<T?> = LoadingModel(isLoading, showError, successData, isRefresh = isRefresh, showEnd = showEnd)

    /**
     * 内置封装好的可通知Activity/fragment 显示隐藏加载框 因为需要跟网络请求显示隐藏loading配套.
     */
    inner class UiLoadingChange {
        //显示加载框
        val showDialog by lazy { EventLiveData<String>() }

        //隐藏
        val dismissDialog by lazy { EventLiveData<Boolean>() }

    }

    val loadingChange: UiLoadingChange by lazy { UiLoadingChange() }

    private var lifecycle: WeakReference<LifecycleProvider<*>>? = null

    @JvmField
    protected var mModel: M? = null

    init {
        mModel = getNewInstance(this, 0)
    }

    override var cancelable: Boolean = false

    /**
     * 显示loading
     */
    override fun showLoading(msg: String?) {
        val params = HashMap<String, Any?>()
        params[ParameterField.MSG] = msg
        params[ParameterField.IS_CANCLE] = cancelable
        getUC().getShowLoadingEvent().postValue(params)
    }

    override fun showLoading(msgResId: Int) {
        val params = HashMap<String, Any?>()
        params[ParameterField.MSG] = msgResId
        params[ParameterField.IS_CANCLE] = cancelable
        getUC().getShowLoadingEvent().postValue(params)
    }

    /**
     * 隐藏loading
     */
    override fun closeLoading() {
        getUC().getHideLoadingEvent().call()
    }

    /**
     * 显示TOAST
     */
    override fun showToast(msg: String) {
        getUC().getShowToastEvent().postValue(msg)
    }

    override fun showToast(msg: Int) {
        getUC().getShowToastEvent().postValue(msg)
    }

    fun injectLifecycleProvider(lifecycle: LifecycleProvider<*>) {
        this.lifecycle = WeakReference(lifecycle)
    }

    fun getLifecycleProvider(): LifecycleProvider<*>? {
        return lifecycle?.get()
    }

    private var uc: UIChangeLiveData? = null

    fun getUC(): UIChangeLiveData {
        if (uc == null) {
            uc = UIChangeLiveData()
        }
        return uc!!
    }


    /**
     * 跳转页面
     *
     * @param clz 所跳转的目的Activity类
     */
    fun startActivity(clz: Class<*>) {
        startActivity(clz, null)
    }

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    fun startActivity(clz: Class<*>, bundle: Bundle?) {
        val params = HashMap<String, Any>()
        params[ParameterField.CLASS] = clz
        if (bundle != null) {
            params[ParameterField.BUNDLE] = bundle
        }
        getUC().getStartActivityEvent().postValue(params)
    }

    /**
     * 关闭界面
     */
    fun finishUI() {
        getUC().getFinishEvent().call()
    }

    /**
     * 返回上一层
     */
    fun onBackPressed() {
        getUC().getOnBackPressedEvent().call()
    }

    /**
     * ViewModel与View的契约UI回调事件
     */
    class UIChangeLiveData {
        private var showLoadingEvent: SingleLiveEvent<Map<String, Any?>>? = null
        private var hideLoadingEvent: SingleLiveEvent<Void>? = null
        private var showToastEvent: SingleLiveEvent<Any>? = null
        private var startActivityEvent: SingleLiveEvent<Map<String, Any>>? = null
        private var finishEvent: SingleLiveEvent<Void>? = null
        private var onBackPressedEvent: SingleLiveEvent<Void>? = null

        fun getShowLoadingEvent(): SingleLiveEvent<Map<String, Any?>> {
            if (showLoadingEvent == null) {
                showLoadingEvent = SingleLiveEvent()
            }
            return showLoadingEvent!!
        }

        fun getHideLoadingEvent(): SingleLiveEvent<Void> {
            if (hideLoadingEvent == null) {
                hideLoadingEvent = SingleLiveEvent()
            }
            return hideLoadingEvent!!
        }

        fun getShowToastEvent(): SingleLiveEvent<Any> {
            if (showToastEvent == null) {
                showToastEvent = SingleLiveEvent()
            }
            return showToastEvent!!
        }

        fun getStartActivityEvent(): SingleLiveEvent<Map<String, Any>> {
            if (startActivityEvent == null) {
                startActivityEvent = SingleLiveEvent()
            }
            return startActivityEvent!!
        }


        fun getFinishEvent(): SingleLiveEvent<Void> {
            if (finishEvent == null) {
                finishEvent = SingleLiveEvent()
            }
            return finishEvent!!
        }


        fun getOnBackPressedEvent(): SingleLiveEvent<Void> {
            if (onBackPressedEvent == null) {
                onBackPressedEvent = SingleLiveEvent()
            }
            return onBackPressedEvent!!
        }
    }

    object ParameterField {
        var CLASS = "CLASS"
        var CANONICAL_NAME = "CANONICAL_NAME"
        var BUNDLE = "BUNDLE"
        var MSG = "MSG"
        var IS_CANCLE = "IS_CANCLE"
    }

    override fun onLoading(isLoading: Boolean) {
        if (isLoading) {
            showLoading()
        } else {
            closeLoading()
        }
    }
}










