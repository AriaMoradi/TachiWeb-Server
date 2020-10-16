package eu.kanade.tachiyomi.network

import com.gargoylesoftware.htmlunit.BrowserVersion
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlPage
import okhttp3.*
import uy.kohesive.injekt.injectLazy
import java.io.IOException

class CloudflareInterceptor : Interceptor {
    private val network: NetworkHelper by injectLazy()

    private val serverCheck = arrayOf("cloudflare-nginx", "cloudflare")

    @Synchronized
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        // Check if Cloudflare anti-bot is on
        if (response.code == 503 && response.header("Server") in serverCheck) {
            return try {
                chain.proceed(resolveChallenge(response))
            } catch (e: Exception) {
                // Because OkHttp's enqueue only handles IOExceptions, wrap the exception so that
                // we don't crash the entire app
                throw IOException(e)
            }
        }

        return response
    }

    private fun resolveChallenge(response: Response): Request {
        val browserVersion = BrowserVersion.BrowserVersionBuilder(BrowserVersion.BEST_SUPPORTED)
                .setUserAgent(response.request.header("User-Agent") ?: BrowserVersion.BEST_SUPPORTED.userAgent)
                .build()
        val convertedCookies = WebClient(browserVersion).use { webClient ->
            webClient.options.isThrowExceptionOnFailingStatusCode = false
            webClient.options.isThrowExceptionOnScriptError = false
            webClient.getPage<HtmlPage>(response.request.url.toString())
            webClient.waitForBackgroundJavaScript(10000)
            // Challenge solved, process cookies
            webClient.cookieManager.cookies.filter {
                // Only include Cloudflare cookies
                it.name.startsWith("__cf") || it.name.startsWith("cf_")
            }.map {
                // Convert cookies -> OkHttp format
                Cookie.Builder()
                        .domain(it.domain.removePrefix("."))
                        .expiresAt(it.expires.time)
                        .name(it.name)
                        .path(it.path)
                        .value(it.value).apply {
                            if (it.isHttpOnly) httpOnly()
                            if (it.isSecure) secure()
                        }.build()
            }
        }

        // Copy cookies to cookie store
        convertedCookies.forEach {
            network.cookies.addAll(
                    HttpUrl.Builder()
                            .scheme("http")
                            .host(it.domain)
                            .build(),
                    listOf(it)
            )
        }
        // Merge new and existing cookies for this request
        // Find the cookies that we need to merge into this request
        val convertedForThisRequest = convertedCookies.filter {
            it.matches(response.request.url)
        }
        // Extract cookies from current request
        val existingCookies = Cookie.parseAll(
            response.request.url,
            response.request.headers
        )
        // Filter out existing values of cookies that we are about to merge in
        val filteredExisting = existingCookies.filter { existing ->
            convertedForThisRequest.none { converted -> converted.name == existing.name }
        }
        val newCookies = filteredExisting + convertedForThisRequest
        return response.request.newBuilder()
                .header("Cookie", newCookies.map { it.toString() }.joinToString("; "))
                .build()
    }
}
