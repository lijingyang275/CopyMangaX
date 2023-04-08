package com.crow.module_user.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.crow.base.R.id.app_main_fcv
import com.crow.base.app.appContext
import com.crow.base.current_project.BaseStrings
import com.crow.base.current_project.BaseUser
import com.crow.base.tools.coroutine.FlowBus
import com.crow.base.tools.extensions.*
import com.crow.base.ui.fragment.BaseMviBottomSheetDF
import com.crow.module_user.R
import com.crow.module_user.databinding.UserFragmentBinding
import com.crow.module_user.ui.adapter.UserRvAdapter
import com.crow.module_user.ui.viewmodel.UserViewModel
import com.google.android.material.R.id.design_bottom_sheet
import com.google.gson.annotations.Until
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import com.crow.base.R as baseR

/*************************
 * @Machine: RedmiBook Pro 15 Win11
 * @Path: module_user/src/main/kotlin/com/crow/module_user/ui/fragment
 * @Time: 2023/3/18 21:20
 * @Author: CrowForKotlin
 * @Description: UserRepository
 * @formatter:on
 **************************/

class UserBottomSheetFragment() : BaseMviBottomSheetDF<UserFragmentBinding>() {

    companion object { val TAG = UserBottomSheetFragment::class.java.simpleName }

    constructor(hideOnFade: (Fragment) -> Unit, navigateAbout: () -> Unit) : this() {
        mHideOnFade = hideOnFade
        mNavigateAbout = navigateAbout
    }

    // 用戶 VM
    private val mUserVM by sharedViewModel<UserViewModel>()

    private var mHideOnFade: ((Fragment) -> Unit)? = null
    private var mNavigateAbout: (() -> Unit)? = null

    // 用户适配器数据
    private val mAdapterData = mutableListOf (
        R.drawable.user_ic_usr_24dp to appContext.getString(R.string.user_login),
        R.drawable.user_ic_reg_24dp to appContext.getString(R.string.user_reg),
        R.drawable.user_ic_history_24dp to appContext.getString(R.string.user_browsing_history),
        baseR.drawable.base_ic_download_24dp to appContext.getString(R.string.user_download),
        R.drawable.user_ic_about_24dp to appContext.getString(R.string.user_about),
        R.drawable.user_ic_update_24dp to appContext.getString(R.string.user_check_update)
    )

    // 用户适配器
    private lateinit var mUserRvAdapter: UserRvAdapter

    override fun getViewBinding(inflater: LayoutInflater) = UserFragmentBinding.inflate(inflater)

    override fun onStart() {
        super.onStart()

        // 设置BottomSheet的 高度
        dialog?.findViewById<View>(design_bottom_sheet)?.layoutParams!!.height = ViewGroup.LayoutParams.MATCH_PARENT
    }

    override fun initView(bundle: Bundle?) {

        mUserRvAdapter = UserRvAdapter(mAdapterData) { pos, content ->

            // 根据 位置 做对应的逻辑处理
            dismissAllowingStateLoss()
            if (pos !in 2..3 && pos != 5) mHideOnFade?.invoke(this)
            when (pos) {
                // 登录 ＆ 个人信息
                0 -> {
                    if (content == getString(R.string.user_info)) parentFragmentManager.navigateByAddWithBackStack(app_main_fcv, UserUpdateInfoFragment.newInstance(), "UserUpdateInfoFragment") { it.withFadeAnimation() }
                    else parentFragmentManager.navigateByAddWithBackStack(app_main_fcv, UserLoginFragment.newInstance(), "UserLoginFragment") { it.withFadeAnimation() }
                }
                1 -> parentFragmentManager.navigateByAddWithBackStack(app_main_fcv, UserRegFragment.newInstance(), "UserRegFragment") { it.withFadeAnimation() }
                2 -> toast("还在开发中...")
                3 -> toast("还在开发中...")
                4 -> mNavigateAbout?.invoke()
                5 -> FlowBus.with<Unit>(BaseStrings.Key.CHECK_UPDATE).post(lifecycleScope, Unit)
            }
        }

        // 设置 适配器
        mBinding.userRv.adapter = mUserRvAdapter
    }

    override fun initObserver() {

        // 用户信息 收集
        mUserVM.userInfo.onCollect(this) {

            // 初始化 Icon链接 设置用户名 退出可见 修改适配器数据
            mUserVM.doLoadIcon(mContext, false) { resource ->  mBinding.userIcon.setImageDrawable(resource) }

            // 数据空 则退出
            if (it == null) return@onCollect

            // 设置昵称
            mBinding.userName.text = getString(R.string.user_nickname, it.mNickname)

            // 退出按钮可见
            mBinding.userExit.visibility = View.VISIBLE

            // 移除适配器首位数据 默认是 登录
            mAdapterData.removeFirst()

            // 索引0插入数据
            mAdapterData.add(0, R.drawable.user_ic_usr_24dp to getString(R.string.user_info))
        }

        // 点击 头像事件
        mBinding.userIcon.clickGap { _, _ ->

            // 点击头像 并 深链接跳转
            dismissAllowingStateLoss()

            parentFragmentManager.hide(this, "ContainerFragmentByUserBottom")

            // 导航至头像Fragment Token不为空则跳转
            parentFragmentManager.navigateByAddWithBackStack(app_main_fcv, UserIconFragment.newInstance().also { it.arguments = bundleOf("iconUrl" to if (BaseUser.CURRENT_USER_TOKEN.isNotEmpty()) mUserVM.mIconUrl else null) }, "UserIconFragment")
        }

        // 点击 退出事件
        mBinding.userExit.clickGap { _, _ ->

            // 发送事件清除用户数据
            FlowBus.with<Unit>(BaseStrings.Key.EXIT_USER).post(lifecycleScope, Unit)

            // SnackBar提示
            mBinding.root.showSnackBar(getString(R.string.user_exit_sucess))

            // 关闭当前界面
            dismissAllowingStateLoss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mHideOnFade = null
        mNavigateAbout = null
    }
}