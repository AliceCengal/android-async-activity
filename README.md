Android AsyncActivity
=====================
*A small addon to mate android.app.Activity with scala.util.Future*

An easy way to execute an asynchronous job in Scala is to wrap the procedure in a `scala.util.Future`, and then do 
monadic transformation on the returned Future object.

```scala
val f: Future[DomainObject] = Future {
  val jsonResponse = doNetworkCall();
  new DomainObject(jsonResponse)
}

f.map(_.divisionId)
  .onComplete {
    case Success(id) => displayId(id)
    case Failure(ex) => displayError(ex)
  }
```

This approach would be really helpful in Android programming where fetching data from the server is done all the 
time. The problem is that the `onComplete` callback is not guaranteed to be run in the same thread that it is 
defined, which is a major problem if you want to update the UI based on the result of the async job.

The trait `AsyncActivity` has an implicit conversion that adds methods to Future objects with `ForUi` suffix.

```scala
def onCompleteForUi[U](f: (Try[T]) ⇒ U)(implicit executor: ExecutionContext): Unit

def onSuccessForUi[U](pf: PartialFunction[T, U])(implicit executor: ExecutionContext): Unit

def onFailureForUi[U](pf: PartialFunction[Throwable, U])(implicit executor: ExecutionContext): Unit
```

These methods are equivalent to the normal ones defined on scala.util.Future, except that they are guaranteed to run 
the callback on the UI thread. Life is good.

Credits to [Sung-Ho Lee](https://github.com/pocorall) for his blog post [here](http://blog.scaloid.org/2013/11/using-scalaconcurrentfuture-in-android.html)
