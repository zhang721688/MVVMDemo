package com.wanandroid.ui.profile

import android.content.Intent
import android.util.Log
import com.wanandroid.App
import com.wanandroid.R
import com.wanandroid.base.MyBaseFragment
import com.wanandroid.customui.builddialog.BuildDialog
import com.wanandroid.customui.builddialog.BuildDialogListener
import com.wanandroid.customui.commondialog.CommonDialog
import com.wanandroid.customui.commondialog.CommonDialogListener
import com.wanandroid.ui.login.MyLoginActivity
import com.wanandroid.ui.readhistory.ReadHistoryActivity
import com.wanandroid.ui.tab.ShellsActivity
import com.wanandroid.util.SharedPreferencesData
import com.zxn.mvvm.ext.jumpInTo
import kotlinx.android.synthetic.main.fragment_profile.*

/**
 *
 */
@Suppress("DEPRECATED_IDENTITY_EQUALS")
class ProfileFragment : MyBaseFragment() {

//    override fun getLayoutResId() = R.layout.fragment_profile

    override fun initView() {
        setOnClickListener()
//        login.isEnabled = !SharedPreferencesData.isLogin
        if (SharedPreferencesData.isLogin) {
            loginRegister.text = SharedPreferencesData.name
        } else {
            loginRegister.text = resources.getString(R.string.loginRegister)
        }
    }

    override fun initData() {

    }

    private fun setOnClickListener() {
        login.setOnClickListener {
//            val intent = Intent(App.getContext(), RegisterLoginActivity::class.java)
//            startActivityForResult(intent, 1) // requestCode -> 666
            MyLoginActivity.jumpTo(context!!)
        }
        header.setOnClickListener {
            Log.d("ProfileFragment", "header")
        }
        readHistory.setOnClickListener {
            Log.d("ProfileFragment", "readHistory")
            val intent = Intent(App.getContext(), ReadHistoryActivity::class.java)
            startActivity(intent)
//            MyReadHistoryActivity.jumpTo(it.context)
        }
        clearCache.setOnClickListener {
            Log.d("ProfileFragment", "clearCache")
            activity?.let {
                val mDialog =
                    CommonDialog(it)
                mDialog.setViewText("标题", "", "取消", "确定")
                mDialog.setDialogOnClickListener(object :
                    CommonDialogListener {
                    override fun cancelOnClickListener() {
                        mDialog.dismiss()
                    }

                    override fun confirmOnClickListener() {
                    }

                })
                mDialog.show();
            }

        }
        aboutAuthor.setOnClickListener {
            var dialog: BuildDialog? = null
            activity?.let {
                dialog = BuildDialog.Builder(it)
                    .setTitleText("标题")
                    .setContentText("内容")
                    .setCancelText("取消")
                    .setConfirmText("确定")
                    .setDialogOnClickListener(object : BuildDialogListener {
                        override fun cancelOnClickListener() {
                            dialog?.hide()
                        }

                        override fun confirmOnClickListener() {

                        }

                    })
                    .create()

                dialog?.show()
            }
        }
        systemSetting.setOnClickListener {
            jumpInTo<ShellsActivity>(mContext)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode === 2) {
            if (requestCode === 1) {
                loginRegister.text = SharedPreferencesData.name
//                login.isEnabled = !SharedPreferencesData.isLogin
            }
        }
    }

    override val layoutResId: Int
        get() = R.layout.fragment_profile
}

