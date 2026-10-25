package com.prayagi.netratrikaal.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import androidx.core.content.pm.PackageInfoCompat
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest

/** A newer release described by latest.json (published with every release by the release workflow). */
data class AppRelease(
    val tag: String,
    val versionName: String,
    val versionCode: Long,
    val notes: String,
    val apkUrl: String,
    val sha256: String,
    val size: Long
)

/**
 * In-app updater. Reads latest.json from the repo's latest GitHub release, runs only when the user taps "Check for update",
 * downloads app-release.apk, verifies its sha256 and size, then hands it to the Android package installer
 * (the user confirms with one system tap). Android only installs it if it is signed with the same key as the installed app.
 */
object AppUpdater {
    const val REPO = "prayagi-store-and-services/netra-trikaal"
    const val APP_LABEL = "Trikaal"
    private const val PREFS = "netra_app_update"
    private const val KEY_LAST_CHECK = "last_check_ms"
    private val TAG_RE = Regex("^v[0-9]+\\.[0-9]+\\.[0-9]+$")
    private val SHA_RE = Regex("^[0-9a-f]{64}$")

    fun latestJsonUrl() = "https://github.com/$REPO/releases/latest/download/latest.json"

    /** Returns the release only when it is newer than [installedCode] and every field passes validation. */
    fun parse(json: String, installedCode: Long): AppRelease? {
        return try {
            val o = JSONObject(json)
            val tag = o.getString("tag")
            val code = o.getLong("versionCode")
            val sha = o.getString("sha256").lowercase()
            val size = o.getLong("size")
            if (!TAG_RE.matches(tag) || !SHA_RE.matches(sha) || size <= 0L || code <= installedCode) return null
            AppRelease(
                tag = tag,
                versionName = o.optString("versionName", tag.removePrefix("v")),
                versionCode = code,
                notes = o.optString("notes", "").trim(),
                apkUrl = "https://github.com/$REPO/releases/download/$tag/app-release.apk",
                sha256 = sha,
                size = size
            )
        } catch (e: Exception) {
            null
        }
    }

    /** Result of a manual check: on the newest version, a newer release exists, or the check could not be done. */
    sealed class CheckResult {
        object Latest : CheckResult()
        data class Newer(val release: AppRelease) : CheckResult()
        object Failed : CheckResult()
    }

    /** Pure decision for a latest.json text: newer release, already latest, or unreadable (Failed). */
    fun classify(json: String, installedCode: Long): CheckResult {
        parse(json, installedCode)?.let { return CheckResult.Newer(it) }
        return try {
            val code = JSONObject(json).getLong("versionCode")
            if (code <= installedCode) CheckResult.Latest else CheckResult.Failed
        } catch (e: Exception) {
            CheckResult.Failed
        }
    }

    /** Manual "Check for update": always asks now (ignores the once-a-day limit). Call off the main thread. */
    fun checkNow(context: Context): CheckResult {
        return try {
            val c = open(latestJsonUrl())
            if (c.responseCode != 200) return CheckResult.Failed
            val text = c.inputStream.use { it.readBytes() }
            if (text.size > 1024 * 1024) return CheckResult.Failed
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putLong(KEY_LAST_CHECK, System.currentTimeMillis()).apply()
            classify(String(text, Charsets.UTF_8), installedCode(context))
        } catch (e: Exception) {
            CheckResult.Failed
        }
    }

    fun installedVersionName(context: Context): String =
        try { context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "Unavailable" } catch (e: Exception) { "Unavailable" }

    fun installedCode(context: Context): Long =
        PackageInfoCompat.getLongVersionCode(context.packageManager.getPackageInfo(context.packageName, 0))

    private fun open(url: String): HttpURLConnection {
        val c = URL(url).openConnection() as HttpURLConnection
        c.connectTimeout = 10000
        c.readTimeout = 20000
        c.instanceFollowRedirects = true
        c.setRequestProperty("User-Agent", "$APP_LABEL-updater")
        return c
    }

    /** Deletes every file in the installer download folder and returns how many were removed. */
    fun cleanDir(dir: File?): Int {
        var n = 0
        dir?.listFiles()?.forEach { if (it.delete()) n++ }
        return n
    }

    /**
     * Called when the app starts. After an in-app update installs, Android restarts the app, so this
     * removes the downloaded installer file and nothing is left in storage.
     */
    fun cleanLeftovers(context: Context) {
        try {
            val installed = installedVersionName(context)
            val now = System.currentTimeMillis()
            File(context.cacheDir, "updates").listFiles()?.forEach { if (!PreDownload.keepFile(it.name, installed, now - it.lastModified())) it.delete() }
        } catch (_: Exception) {}
    }

    /** Called when the app comes back to the front. Removes installer files older than one hour (cancelled or finished installs). */
    fun cleanStale(context: Context) {
        try {
            val now = System.currentTimeMillis()
            val installed = installedVersionName(context)
            File(context.cacheDir, "updates").listFiles()?.forEach { if (!PreDownload.keepFile(it.name, installed, now - it.lastModified())) it.delete() }
        } catch (_: Exception) {}
    }

    /** Rehash every cached update and validate the owning application before reuse. */
    fun savedFile(context: Context, release: AppRelease): File? {
        val file = File(File(context.cacheDir, "updates"), "$APP_LABEL-${release.tag}.apk")
        return try {
            check(context.getSharedPreferences("verified_update_artifacts",Context.MODE_PRIVATE).contains(file.name)) { "Legacy cache requires a new download." }
            verifyArchive(context, file, release); file
        } catch (_: Exception) { file.delete(); null }
    }

    @Suppress("DEPRECATION")
    internal fun verifyArchive(context: Context, file: File, release: AppRelease) {
        check(file.isFile && file.length() == release.size) { "Update file is missing or incomplete." }
        val md = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input -> val buffer=ByteArray(16384); while(true) { val n=input.read(buffer);if(n<0)break;md.update(buffer,0,n) } }
        check(md.digest().joinToString("") { "%02x".format(it) } == release.sha256) { "Update checksum changed. Download again." }
        val flags = if(Build.VERSION.SDK_INT>=28) android.content.pm.PackageManager.GET_SIGNING_CERTIFICATES else android.content.pm.PackageManager.GET_SIGNATURES
        val pm=context.packageManager
        val archive=pm.getPackageArchiveInfo(file.path,flags) ?: error("Unreadable update APK.")
        val current=pm.getPackageInfo(context.packageName,flags)
        fun code(p:android.content.pm.PackageInfo)=if(Build.VERSION.SDK_INT>=28)p.longVersionCode else p.versionCode.toLong()
        fun signers(p:android.content.pm.PackageInfo):Set<String> = (if(Build.VERSION.SDK_INT>=28)p.signingInfo?.apkContentsSigners else p.signatures)?.map { sig ->
            MessageDigest.getInstance("SHA-256").digest(sig.toByteArray()).joinToString("") { "%02x".format(it) }
        }?.toSet().orEmpty()
        check(SelfUpdateIdentity.matches(context.packageName,archive.packageName,code(current),code(archive),release.versionCode,
            release.versionName,archive.versionName,signers(current),signers(archive))) { "Update package, version or signer did not match. Nothing was installed." }
    }

    /** Downloads the APK and verifies size and sha256. Deletes the file and throws if anything is off. */
    fun download(context: Context, release: AppRelease, onProgress: ((Long, Long) -> Unit)? = null): File {
        val dir = File(context.cacheDir, "updates").apply { mkdirs() }
        dir.listFiles()?.filter { !it.name.endsWith("-${release.tag}.apk") }?.forEach { it.delete() }
        val file = File(dir, "$APP_LABEL-${release.tag}.apk")
        val c = open(release.apkUrl)
        if (c.responseCode != 200) throw IllegalStateException("Download failed (server answered ${c.responseCode}).")
        val md = MessageDigest.getInstance("SHA-256")
        var total = 0L
        var lastReport = 0L
        c.inputStream.use { input ->
            file.outputStream().use { out ->
                val buf = ByteArray(16384)
                while (true) {
                    val n = input.read(buf)
                    if (n < 0) break
                    total += n
                    if (total > release.size) { file.delete(); throw IllegalStateException("Downloaded file is larger than expected.") }
                    md.update(buf, 0, n)
                    out.write(buf, 0, n)
                    if (onProgress != null) { val now = System.currentTimeMillis(); if (now - lastReport >= 250L) { lastReport = now; onProgress(total, release.size) } }
                }
            }
        }
        val actual = md.digest().joinToString("") { "%02x".format(it) }
        if (total != release.size || actual != release.sha256) {
            file.delete()
            throw IllegalStateException("The downloaded file did not match its checksum, so it was not installed.")
        }
        try {
            verifyArchive(context,file,release)
            check(context.getSharedPreferences("verified_update_artifacts",Context.MODE_PRIVATE).edit().putString(file.name,
                JSONObject().put("tag",release.tag).put("name",release.versionName).put("code",release.versionCode).put("sha",release.sha256).put("size",release.size).toString()).commit())
        } catch(e:Exception) {file.delete();throw e}
        return file
    }

    /** Opens the system installer. Throws a readable message when the user must first allow installs from this app. */
    fun install(context: Context, file: File) {
        check(file.canonicalFile.parentFile==File(context.cacheDir,"updates").canonicalFile) { "Unknown update file." }
        try {
            val raw=context.getSharedPreferences("verified_update_artifacts",Context.MODE_PRIVATE).getString(file.name,null) ?: error("Cached update needs fresh verification. Download again.")
            val p=JSONObject(raw)
            verifyArchive(context,file,AppRelease(p.getString("tag"),p.getString("name"),p.getLong("code"),"","",p.getString("sha"),p.getLong("size")))
        } catch(e:Exception) {file.delete();throw e}
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !context.packageManager.canRequestPackageInstalls()) {
            context.startActivity(
                Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + context.packageName))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            throw IllegalStateException("Allow installs from this app, then tap Install again.")
        }
        val uri = FileProvider.getUriForFile(context, context.packageName + ".updates", file)
        context.startActivity(
            Intent(Intent.ACTION_VIEW)
                .setDataAndType(uri, "application/vnd.android.package-archive")
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}

internal object SelfUpdateIdentity {
    fun matches(expectedPackage:String,archivePackage:String?,installedCode:Long,archiveCode:Long,
        expectedCode:Long,expectedName:String,archiveName:String?,installedSigners:Set<String>,archiveSigners:Set<String>):Boolean =
        archivePackage==expectedPackage && expectedCode>installedCode && archiveCode==expectedCode &&
        archiveName==expectedName && installedSigners.isNotEmpty() && archiveSigners==installedSigners
}
