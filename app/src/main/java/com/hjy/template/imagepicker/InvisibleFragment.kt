package com.hjy.template.imagepicker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.File
import java.util.*

/**
 * 隐藏的 Fragment 用来处理 ActivityResult 回调的问题
 */
class InvisibleFragment: Fragment() {

    private var outputListener: ((TakePhotoMediator.Output) -> Unit)? = null

    private val takePhotoLauncher = registerForActivityResult(TakePicture()) {
        outputListener?.run {
            invoke(it)
        }
    }

    private val openGalleryLauncher = registerForActivityResult(OpenGallery()) {
        outputListener?.run {
            invoke(it)
        }
    }

    fun setOutputListener(listener: (TakePhotoMediator.Output) -> Unit) {
        outputListener = listener
    }

    /**
     * 打开系统摄像头进行拍照
     */
    fun openCamera() {
        try {
            val imageFile = createImageFile()
            val path = imageFile.absolutePath
            val uri = FileProvider.getUriForFile(
                requireContext(), requireContext().applicationContext.packageName + ".provider",
                imageFile)
            takePhotoLauncher.launch(Input(uri, path))
        } catch (e: Exception) {
            e.printStackTrace()
            outputListener?.let {
                it.invoke(TakePhotoMediator.Output.error(e))
            }
        }
    }

    /**
     * 打开相册
     */
    fun openGallery() {
        openGalleryLauncher.launch("")
    }

    private fun createImageFile(): File {
        val imageFileName = "image-" + UUID.randomUUID().toString()
        val path = File(getTmpDir(requireContext()))
        if (!path.exists()) {
            path.mkdirs()
        }
        return File(path, "${imageFileName}.jpg")
    }

    private fun getTmpDir(context: Context): String {
        return context.externalCacheDir.toString() + "/camera_photo"
    }

    internal class Input(val uri: Uri, val imageFilePath: String)


    /**
     * 打开系统相机进行拍照
     */
    internal class TakePicture : ActivityResultContract<Input, TakePhotoMediator.Output>() {

        private lateinit var input: Input

        override fun createIntent(context: Context, input: Input): Intent {
            this.input = input
            return Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, input.uri)
        }

        override fun getSynchronousResult(
            context: Context,
            input: Input
        ): SynchronousResult<TakePhotoMediator.Output>? {
            return null
        }

        override fun parseResult(resultCode: Int, intent: Intent?): TakePhotoMediator.Output {
            if (resultCode == Activity.RESULT_OK) {
                if (this::input.isInitialized) {
                    val file = File(input.imageFilePath)
                    if (!file.exists()) {
                        return TakePhotoMediator.Output.error(null)
                    }
                    return TakePhotoMediator.Output.success(input.imageFilePath)
                } else {
                    return TakePhotoMediator.Output.error(null)
                }
            } else {
                return TakePhotoMediator.Output.cancel()
            }
        }
    }

    internal class OpenGallery : ActivityResultContract<String, TakePhotoMediator.Output>() {

        private lateinit var context: Context

        override fun createIntent(context: Context, input: String): Intent {
            this.context = context
            val intent = Intent(Intent.ACTION_GET_CONTENT).also {
                it.setType("image/*")
                it.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            }
            return intent
        }

        override fun getSynchronousResult(
            context: Context,
            input: String
        ): SynchronousResult<TakePhotoMediator.Output>? {
            return null
        }

        override fun parseResult(resultCode: Int, intent: Intent?): TakePhotoMediator.Output {
            if (resultCode == Activity.RESULT_OK) {
                intent ?: return TakePhotoMediator.Output.error(null)
                val uri = intent.data
                uri ?: return TakePhotoMediator.Output.error(null)
                if (!this::context.isInitialized) {
                    return TakePhotoMediator.Output.error(null)
                }
                val path = RealPathUtil.getRealPathFromURI(this.context, uri)
                path?.let {
                    return TakePhotoMediator.Output.success(it)
                }
                return TakePhotoMediator.Output.error(null)
            } else {
                return TakePhotoMediator.Output.cancel()
            }
        }
    }

}