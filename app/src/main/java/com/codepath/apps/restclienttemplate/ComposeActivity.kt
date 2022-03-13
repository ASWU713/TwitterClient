  package com.codepath.apps.restclienttemplate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

  class ComposeActivity : AppCompatActivity() {

    lateinit var etCompose: EditText
    lateinit var btnTweet: Button

    lateinit var client: TwitterClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        etCompose = findViewById(R.id.etTweetCompose)
        btnTweet = findViewById(R.id.btnTweet)

        client = TwitterApplication.getRestClient(this)

        //Handle user click on tweet button
        btnTweet.setOnClickListener{
            val tweetContent = etCompose.text.toString()

            //1. Make sure content isn't empty
            if(tweetContent.isEmpty()){
                Toast.makeText(this, "Tweet is empty!", Toast.LENGTH_LONG ).show()
                //look into displaying snackbar
            } else{
                //2. Make sure tweet is under character count
                if(tweetContent.length > 140){
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