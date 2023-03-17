package com.crow.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.crow.base.dialog.LoadingAnimDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/*************************
 * @Machine: RedmiBook Pro 15 Win11
 * @Path: lib_base/src/main/java/com/barry/base/fragment
 * @Time: 2022/11/14 21:26
 * @Author: CrowForKotlin
 * @Description: BaseVBBottomSheetDfImpl
 * @formatter:on
 **************************/
abstract class BaseBottomSheetDFImpl : BottomSheetDialogFragment(), IBaseFragment {

    override fun initData() {}

    override fun showLoadingAnim() { LoadingAnimDialog.show(parentFragmentManager) }
    override fun dismissLoadingAnim() { LoadingAnimDialog.dismiss(parentFragmentManager) }

    inline fun dismissLoadingAnim(crossinline animEnd: () -> Unit) {
        LoadingAnimDialog.dismiss(parentFragmentManager) { animEnd()  }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return getView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        initListener()
    }
}