package com.upstox.android.foody.ui.fragments.instructions

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.upstox.android.foody.databinding.FragmentInstructionsBinding
import com.upstox.android.foody.models.Result
import com.upstox.android.foody.util.Constants.Companion.RECIPE_RESULT_KEY


class InstructionsFragment : Fragment() {

    private var _binding: FragmentInstructionsBinding? =  null
    private val binding get() = _binding!!

  override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentInstructionsBinding.inflate(inflater, container, false)

      val args = arguments
      val myBundle: Result? = args?.getParcelable(RECIPE_RESULT_KEY)

      //So if you won't assign this webView client and try to click any link in your instruction webView
      //you will be redirected to default web browser for handling URLs
      binding.instructionsWebView.webViewClient = object : WebViewClient() {
          override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
              Log.d("xyz","should override url called1")
              view!!.loadUrl(url!!)
              return true
          }

          override fun shouldOverrideUrlLoading(
              view: WebView?,
              request: WebResourceRequest?
          ): Boolean {
              Log.d("xyz","should override url called2")
              view!!.loadUrl(request!!.url.toString())
              return true
          }
      }

      //enabling javaScript to navigate to websites that uses javaScript
      binding.instructionsWebView.settings.javaScriptEnabled = true

      val websiteUrl:String= myBundle!!.sourceUrl

      binding.instructionsWebView.loadUrl(websiteUrl)

      return binding.root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding =null
    }

}