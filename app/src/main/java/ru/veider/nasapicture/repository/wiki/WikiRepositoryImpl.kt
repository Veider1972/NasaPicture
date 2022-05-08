package ru.veider.nasapicture.repository.wiki

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.w3c.dom.Document
import org.xml.sax.InputSource
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import ru.veider.nasapicture.const.TAG
import ru.veider.nasapicture.const.WIKI_WORDS_COUNT
import java.io.IOException
import java.io.StringReader
import java.lang.Exception
import javax.xml.parsers.DocumentBuilderFactory

class WikiRepositoryImpl : WikiRepository {

    private var baseUrl = "https://en.wikipedia.org/"

    private val wikiApi = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(ScalarsConverterFactory.create())
        .client(createOkHttpClient(WikiInterceptor()))
        .build()
        .create(WikiAPI::class.java)

    private fun createOkHttpClient(interceptor: Interceptor): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder().apply {
            addInterceptor(interceptor)
            addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        }
        return okHttpClient.build()
    }

    private inner class WikiInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            Log.d(TAG, "Wiki: " + chain.request().toString() + "\n")
            return chain.proceed(chain.request())
        }
    }

    override suspend fun wikiSearch(word: String): WikiResponse {
        val str = wikiApi.getPicture(WIKI_WORDS_COUNT, word)
        return getResponse(str)
    }


    private fun getResponse(word: String): WikiResponse {
        val xmlDoc: Document =
                DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .parse(InputSource(StringReader(word)))
        xmlDoc.documentElement.normalize()
        val items = WikiResponse()
        items.query = xmlDoc.getElementsByTagName("Query").item(0).firstChild.nodeValue
        with(xmlDoc.getElementsByTagName("Item")) {
            for (i in 0 until this.length) {
                val node = this.item(i)
                try {
                    items.title.add(node.childNodes.item(0).firstChild.nodeValue)
                } catch (e: Exception) {
                    items.title.add("")
                }
                try {
                    items.url.add(node.childNodes.item(1).firstChild.nodeValue)
                } catch (e: Exception) {
                    items.url.add("")
                }
                try {
                    items.image.add(node.childNodes.item(2).attributes.getNamedItem("source").nodeValue)
                } catch (e: Exception) {
                    items.image.add("")
                }
            }
        }
        return items
    }
}