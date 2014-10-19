package com.cengallut.asyncpackage

import android.os.{AsyncTask, Handler, Looper}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

trait AsyncPackage {

  implicit val exec = ExecutionContext.fromExecutor(
    AsyncTask.THREAD_POOL_EXECUTOR)

  lazy val mainThread = new Handler(Looper.getMainLooper)

  implicit def future2uiFuture[T](f: Future[T]): HandlerFuture[T] =
    new HandlerFuture[T](f, mainThread)

}

class HandlerFuture[T](future: Future[T], h: Handler) {

  def onCompleteForUi[U](f: (Try[T]) ⇒ U)
                        (implicit executor: ExecutionContext): Unit = {
    future.onComplete { result =>
      h.post(new Runnable {
        override def run(): Unit = f(result)
      })
    }
  }

  def onSuccessForUi[U](pf: PartialFunction[T, U])
                       (implicit executor: ExecutionContext): Unit = {
    future.onSuccess {
      case result => h.post(new Runnable {
        override def run(): Unit = pf(result)
      })
    }
  }

  def onFailureForUi[U](pf: PartialFunction[Throwable, U])
                       (implicit executor: ExecutionContext): Unit = {
    future.onFailure {
      case failure => h.post(new Runnable {
        override def run(): Unit = pf(failure)
      })
    }
  }

  def andThenForUi[U](pf: PartialFunction[Try[T], U])
                     (implicit executor: ExecutionContext): Future[T] = {
    future.andThen {
      case result: Try[T] => h.post(new Runnable() {
        override def run(): Unit = pf(result)
      })
    }
  }

}