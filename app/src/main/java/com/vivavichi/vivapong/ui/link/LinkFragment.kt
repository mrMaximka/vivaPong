package com.vivavichi.vivapong.ui.link

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.*
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.vivavichi.vivapong.R
import com.vivavichi.vivapong.databinding.FragmentLinkBinding
import com.vivavichi.vivapong.ui.viva.VivaMenuActivity

class LinkFragment : Fragment(), LinkContract.View {

    companion object {
        fun newInstance() = LinkFragment()
        const val INPUT_FILE_REQUEST_CODE = 1
    }

    private var mFilePathCallback: ValueCallback<Array<Uri>>? = null
    private var _binding: FragmentLinkBinding? = null
    private val binding get() = _binding!!
    private lateinit var presenter: LinkContract.Presenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLinkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = LinkPresenter(this)
        if (presenter.checkTheInternet(requireContext())){
            presenter.tryToLoadData(requireContext())
            initBack()
        }
        else{
            initInternetError()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
            super.onActivityResult(requestCode, resultCode, data)
            return
        }

        var results: Array<Uri>? = null

        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (data != null) {
                val dataString: String? = data.dataString
                if (dataString != null) {
                    results = arrayOf(Uri.parse(dataString))
                }
            }
        }
        mFilePathCallback!!.onReceiveValue(results)
        mFilePathCallback = null
        return
    }

    private fun initInternetError() {
        requireActivity().window?.setBackgroundDrawableResource(R.color.white)
        binding.webView.visibility = View.GONE
        binding.refreshLayout.visibility = View.VISIBLE
        binding.refreshLayout.setOnRefreshListener {
            if (presenter.checkTheInternet(requireContext())){
                binding.webView.visibility = View.VISIBLE
                binding.refreshLayout.visibility = View.GONE
                presenter.tryToLoadData(requireContext())
                initBack()
            }
            binding.refreshLayout.isRefreshing = false
        }
    }

    private fun initBack() {
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    if (binding.webView.canGoBack()) {
                        binding.webView.goBack()
                        isEnabled = true
                        initBack()
                    } else {
                        if (isEnabled) {
                            isEnabled = false
                            requireActivity().onBackPressed()
                        }
                    }
                }
            })
    }

    override fun loadViva() {
        val intent = Intent(requireContext(), VivaMenuActivity::class.java)
        requireActivity().startActivity(intent)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun loadLink(link: String) {
        binding.webView.settings.domStorageEnabled = true
        binding.webView.settings.javaScriptEnabled = true
        var isLoading = false
        var isStart = false

        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                isLoading = true
                view.loadUrl(url)
                return true
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                if (!isLoading && url != null) {
                    view?.loadUrl(url)
                    isLoading = true
                }

                isStart = true
            }
            override fun onPageFinished(view: WebView, url: String) {
                if (isStart){
                    binding.webView.visibility = View.VISIBLE
                    requireActivity().window?.setBackgroundDrawableResource(R.color.white)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        requireActivity().window?.insetsController?.hide(WindowInsets.Type.statusBars())
                    } else {
                        requireActivity().window?.setFlags(
                            WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN
                        )
                    }
                }
            }

        }

        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView, filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                if (mFilePathCallback != null) {
                    mFilePathCallback!!.onReceiveValue(null)
                }
                mFilePathCallback = filePathCallback

                val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
                contentSelectionIntent.type = "image/*"
                val chooserIntent = Intent(Intent.ACTION_CHOOSER)
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Выберите изображение")
                startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE)
                return true
            }
        }

        binding.webView.setDownloadListener { url, _, contentDisposition, _, _ ->
            var filename: String = contentDisposition.toString()
            if (contentDisposition != null) {
                var startIndex = contentDisposition.indexOf("filename=") + 9
                if (contentDisposition.contains("filename=")) {
                    startIndex =
                        contentDisposition.indexOf("filename=") + 9
                } else if (contentDisposition.contains("filename*=")) {
                    startIndex =
                        contentDisposition.indexOf("filename*=") + 10
                }
                var endIndex = contentDisposition.length
                if (contentDisposition[contentDisposition.length - 1] == '"') {
                    endIndex = contentDisposition.length - 1
                }
                filename = contentDisposition.substring(startIndex, endIndex)
                if (filename[0] == '"') {
                    startIndex = 1
                    endIndex = filename.length
                    filename = filename.substring(startIndex, endIndex)
                }

                if (filename.contains('"')) {
                    startIndex = filename.lastIndexOf('"')
                    endIndex = filename.length
                    filename = filename.substring(startIndex, endIndex)
                }
            }
            val request = DownloadManager.Request(Uri.parse(url))
            request.allowScanningByMediaScanner()
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename)
            val dm = requireActivity().getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
            Toast.makeText(
                requireContext(),
                "Downloading File",
                Toast.LENGTH_LONG
            ).show()
        }

        if (link.isNotEmpty()) {
            binding.webView.loadUrl(link)
        }
    }
}