package com.cengallut.asyncsample

import scala.concurrent.{ExecutionContext, Future}
import com.squareup.okhttp.{Request, OkHttpClient}

object ServerCalls {

  private lazy val httpclient = new OkHttpClient

  def doCall(implicit executor: ExecutionContext): Future[String] = {
    Future {
      httpclient
        .newCall(request("http://www.google.com"))
        .execute()
        .body()
        .string()
    }
  }

  private def request(url: String) =
    new Request.Builder().url(url).build()

}
