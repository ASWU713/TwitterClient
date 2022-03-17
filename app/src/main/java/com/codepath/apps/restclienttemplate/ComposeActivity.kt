  package com.codepath.apps.restclienttemplate

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

  class ComposeActivity : AppCompatActivity() {

    lateinit var etCompose: EditText
    lateinit var btnTweet: Button
    lateinit var tvCharCount: TextView

    lateinit var client: TwitterClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        etCompose = findViewById(R.id.etTweetCompose)
        btnTweet = findViewById(R.id.btnTweet)
        tvCharCount = findViewById(R.id.tvChars)

        client = TwitterApplication.getRestClient(this)

        etCompose.addTextChangedListener{
            val remainderChar = 280 - etCompose.text.length
            tvCharCount.text = remainderChar.toString()
            if(remainderChar < 0){
                tvCharCount.setTextColor(Color.RED)
            }
        }

        //Handle user click on tweet button
        btnTweet.setOnClickListener{
            val tweetContent = etCompose.text.toString()

            //1. Make sure content isn't empty
            if(tweetContent.isEmpty()){
                Toast.makeText(this, "Tweet is empty!", Toast.LENGTH_LONG ).show()
                //look into displaying snackbar
            } else{
                //2. Make sure tweet is under character count
                if(tweetContent.length > 280){
                    Toast.makeText(this, "Tweet too long",Toast.LENGTH_LONG).show()
                } else{
                    client.publishTweet(tweetContent, object: JsonHttpResponseHandler(){
                        override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                            Log.i(TAG, "Successfully published tweet!")
                            //Send tweet back to TimelineActivity to show

                            val tweet = Tweet.fromJson(json.jsonObject)

                            val intent = Intent()
                            intent.putExtra("tweet", tweet)
                            setResult(RESULT_OK, intent)
                            finish()
                        }

                        override fun onFailure(
                            statusCode: Int,
                            headers: Headers?,
                            response: String?,
                            throwable: Throwable?
                        ) {
                            Log.e(TAG, "failed to publish tweet", throwable)
                        }
                    })
                }
            }
        }
    }
      companion object{
          val TAG = "ComposeActivity"
      }
}