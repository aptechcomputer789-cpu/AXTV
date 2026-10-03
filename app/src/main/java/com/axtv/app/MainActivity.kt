package com.axtv.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.axtv.app.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.net.URI

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: EntryAdapter
    private var currentUrl = BASE_URL

    private val videoExtensions = listOf(
        ".mp4", ".m3u8", ".m4v", ".mov", ".webm", ".ts", ".mkv", ".avi"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = EntryAdapter { entry ->
            when (entry.type) {
                EntryType.FOLDER -> loadDirectory(entry.url)
                EntryType.VIDEO -> openPlayer(entry)
                EntryType.FILE -> Toast.makeText(this, "Unsupported file", Toast.LENGTH_SHORT).show()
            }
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        binding.refreshButton.setOnClickListener { loadDirectory(currentUrl) }
        binding.upButton.setOnClickListener { goUp() }
        binding.packagesButton.setOnClickListener {
            startActivity(Intent(this, PackagesActivity::class.java))
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (currentUrl != BASE_URL) goUp() else finish()
            }
        })

        loadDirectory(BASE_URL)
    }

    private fun loadDirectory(url: String) {
        currentUrl = normalizeFolderUrl(url)
        binding.serverText.text = currentUrl
        binding.progressBar.visibility = android.view.View.VISIBLE
        binding.statusText.text = "Loading..."

        lifecycleScope.launch {
            try {
                val items = withContext(Dispatchers.IO) { parseDirectory(currentUrl) }
                adapter.submitList(items)
                binding.statusText.text =
                    if (items.isEmpty()) "No links found on this page." else "${items.size} items"
            } catch (e: Exception) {
                adapter.submitList(emptyList())
                binding.statusText.text = e.message ?: "Could not load server."
                Toast.makeText(this@MainActivity, "Server load failed", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = android.view.View.GONE
            }
        }
    }

    private fun parseDirectory(url: String): List<RemoteEntry> {
        val document = Jsoup.connect(url)
            .userAgent("AXTV/1.0")
            .timeout(15000)
            .get()

        val baseUri = URI(url)
        val seen = linkedSetOf<String>()
        val result = mutableListOf<RemoteEntry>()

        document.select("a[href]").forEach { element ->
            val rawHref = element.attr("href").trim()
            if (rawHref.isBlank()) return@forEach
            if (rawHref.startsWith("#")) return@forEach
            if (rawHref.startsWith("javascript:", true)) return@forEach
            if (rawHref.startsWith("mailto:", true)) return@forEach
            if (rawHref == "../") return@forEach

            val resolved = baseUri.resolve(rawHref).toString()
            val resolvedUri = URI(resolved)

            if (resolvedUri.scheme !in listOf("http", "https")) return@forEach
            if (resolvedUri.host != baseUri.host) return@forEach
            if (!seen.add(resolved)) return@forEach

            val visibleName = element.text().trim()
            val name = when {
                visibleName.isNotBlank() -> visibleName
                resolvedUri.path.isNotBlank() ->
                    resolvedUri.path.substringAfterLast('/').ifBlank { rawHref }
                else -> rawHref
            }

            if (name.equals("Parent Directory", true)) return@forEach

            result += RemoteEntry(
                name = name,
                url = resolved,
                type = detectType(resolved, rawHref)
            )
        }

        return result.sortedWith(
            compareBy<RemoteEntry> {
                when (it.type) {
                    EntryType.FOLDER -> 0
                    EntryType.VIDEO -> 1
                    EntryType.FILE -> 2
                }
            }.thenBy { it.name.lowercase() }
        )
    }

    private fun detectType(url: String, rawHref: String): EntryType {
        val lower = url.lowercase()
        if (rawHref.endsWith("/") || lower.endsWith("/")) return EntryType.FOLDER
        if (videoExtensions.any { lower.endsWith(it) }) return EntryType.VIDEO
        val lastPart = URI(url).path.substringAfterLast('/')
        if (lastPart.isNotBlank() && !lastPart.contains(".")) return EntryType.FOLDER
        return EntryType.FILE
    }

    private fun openPlayer(entry: RemoteEntry) {
        startActivity(Intent(this, PlayerActivity::class.java).apply {
            putExtra(PlayerActivity.EXTRA_URL, entry.url)
            putExtra(PlayerActivity.EXTRA_TITLE, entry.name)
        })
    }

    private fun goUp() {
        if (currentUrl == BASE_URL) return
        loadDirectory(URI(currentUrl).resolve("../").toString())
    }

    private fun normalizeFolderUrl(url: String): String =
        if (url.endsWith("/")) url else "$url/"

    companion object {
        const val BASE_URL = "http://172.16.50.4/"
    }
}
