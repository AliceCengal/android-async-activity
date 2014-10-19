package com.cengallut.asyncsample.samplepackage

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import com.cengallut.asyncsample.{ServerCalls, R}

import scala.util.{Failure, Success}

class PackageMain extends Activity {

  override def onCreate(saved: Bundle): Unit = {
    super.onCreate(saved)
    setContentView(R.layout.activity_main)

    val tv = findViewById(R.id.tv1).asInstanceOf[TextView]

    ServerCalls.doCall.onCompleteForUi {
      case Success(result) => tv.setText(s"Success: $result")
      case Failure(ex)     => tv.setText(s"Failure: $ex")
    }
  }

}