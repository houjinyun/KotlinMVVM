package com.hjy.template.imagepicker

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

object TakePhotoHelper {

    fun init(activity: FragmentActivity): TakePhotoMediator {
        return TakePhotoMediator(activity)
    }

    fun init(fragment: Fragment): TakePhotoMediator {
        return TakePhotoMediator(fragment)
    }

}