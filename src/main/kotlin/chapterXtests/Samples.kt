package chapterXtests

import io.reactivex.Observable
import java.lang.Thread.sleep

fun main() {

    retrieveMockDataFromNetworkWithMappers()


    sleep(5000)
}

/**
 * Mock function to sample getting data from ui, mapping data. We observer
 * how this function reacts to exceptions in data retrieval and mapping response to another
 *
 */
private fun retrieveMockDataFromNetworkWithMappers() {
    getResponse()
        .map {
            mapResponse(it)
        }
        .onErrorResumeNext { _: Throwable ->
//            Observable.just(listOf())
            Observable.error(UnsupportedOperationException())
        }
        .map {
            mapResponse2(it)
        }
//        .onErrorResumeNext { _: Throwable ->
//            Observable.just(listOf("Second onErrorResumeNext() created new list"))
//        }
        .subscribe(
            {
                println("onNext() $it")
            },
            {
                println("onError() $it")
            })
}


fun getResponse(): Observable<List<String>> {
    return Observable.fromCallable {
        throw  RuntimeException("Network Exception occurred")
        getResult()
    }
}

private fun getResult(): List<String> {
    sleep(1000)
    return listOf("Initial response1", "Initial response2")
}

private fun mapResponse(rawResponse: List<String>): List<String> {
   return rawResponse.map {
        "👍 Mapped $it"
    }
//    throw RuntimeException("First Mapper Failed")
}

private fun mapResponse2(rawResponse: List<String>): List<String> {
    return rawResponse.map {
        "🤝 Mapped again $rawResponse"
    }

//    throw RuntimeException("Second Mapper Failed")
}
