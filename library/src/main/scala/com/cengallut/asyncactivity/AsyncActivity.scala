package com.cengallut.asyncactivity

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

import android.app.Activity
import android.os.AsyncTask

trait AsyncActivity extends Activity {

  implicit val exec = ExecutionContext.fromExecutor(
    AsyncTask.THREAD_POOL_EXECUTOR)

  implicit def future2uifuture[T](f: Future[T]): UiFuture[T] =
    new UiFuture[T](f, this)

}

class UiFuture[T](future: Future[T], activity: Activity) {

  def onCompleteForUi[U](f: (Try[T]) ⇒ U)
                        (implicit executor: ExecutionContext): Unit = {
    future.onComplete { result =>
      activity.runOnUiThread(new Runnable {
        override def run(): Unit = { f(result) }
      })
    }
  }

  def onSuccessForUi[U](pf: PartialFunction[T, U])
                       (implicit executor: ExecutionContext): Unit = {
    future.onSuccess {
      case result => activity.runOnUiThread(new Runnable {
        override def run(): Unit = pf(result)
      })
    }
  }

  def onFailureForUi[U](pf: PartialFunction[Throwable, U])
                       (implicit executor: ExecutionContext): Unit = {
    future.onFailure {
      case failure => activity.runOnUiThread(new Runnable {
        override def run(): Unit = pf(failure)
      })
    }
  }

  def andThenForUi[U](pf: PartialFunction[Try[T], U])
                     (implicit executor: ExecutionContext): Future[T] = {
    future.andThen {
      case result: Try[T] =>
        activity.runOnUiThread(new Runnable() {
          override def run(): Unit = pf(result)
        })
    }
  }

}















